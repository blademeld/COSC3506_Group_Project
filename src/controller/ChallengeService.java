package controller;

import model.PeerProfile;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

// Handles RSA challenge-response authentication and username exchange between peers
public class ChallengeService {

    // Simple interface so auth steps can log back to the UI
    public interface AuthLogger {
        void log(String message);
    }

    // Run by the Host — returns "Name (keyId)" of the connecting peer, or null if
    // auth fails
    public String authenticateAsHost(ConnectionHandler handler, PeerProfile profile, String localUsername,
            AuthLogger logger) {
        try {
            logger.log("Generating challenge...");
            String challenge = UUID.randomUUID().toString();
            handler.send("CHALLENGE:" + challenge);

            logger.log("Waiting for peer response...");
            String pubKeyLine = handler.readLine();
            String sigLine = handler.readLine();
            String usernameLine = handler.readLine();

            if (pubKeyLine == null || sigLine == null || usernameLine == null)
                return null;

            String peerUsername = usernameLine.replace("USERNAME:", "");
            logger.log("Received public key from " + peerUsername + ". Verifying signature...");

            byte[] pubKeyBytes = Base64.getDecoder().decode(pubKeyLine.replace("PUBKEY:", ""));
            byte[] sigBytes = Base64.getDecoder().decode(sigLine.replace("SIG:", ""));

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey peerPublicKey = keyFactory.generatePublic(new X509EncodedKeySpec(pubKeyBytes));

            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(peerPublicKey);
            sig.update(challenge.getBytes());
            boolean valid = sig.verify(sigBytes);

            if (!valid) {
                logger.log("Signature verification failed.");
                handler.send("AUTH_FAIL");
                return null;
            }

            logger.log("Signature verified. Sending confirmation...");
            handler.send("AUTH_OK");
            handler.send("USERNAME:" + localUsername);
            handler.send("PUBKEY:" + Base64.getEncoder().encodeToString(profile.getPublicKey().getEncoded()));

            String keyId = shortId(peerPublicKey);
            logger.log("Peer identity confirmed: " + peerUsername + " (" + keyId + ")");
            return peerUsername + " (" + keyId + ")";

        } catch (Exception e) {
            logger.log("Auth error: " + e.getMessage());
            return null;
        }
    }

    // Run by the Client — returns "Name (keyId)" of the host, or null if auth fails
    public String authenticateAsClient(ConnectionHandler handler, PeerProfile profile, String localUsername,
            AuthLogger logger) {
        try {
            logger.log("Waiting for challenge from host...");
            String challengeLine = handler.readLine();
            if (challengeLine == null || !challengeLine.startsWith("CHALLENGE:"))
                return null;

            String challenge = challengeLine.replace("CHALLENGE:", "");
            logger.log("Challenge received. Signing with private key...");

            byte[] pubKeyBytes = profile.getPublicKey().getEncoded();
            handler.send("PUBKEY:" + Base64.getEncoder().encodeToString(pubKeyBytes));

            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(profile.getPrivateKey());
            sig.update(challenge.getBytes());
            byte[] signature = sig.sign();
            handler.send("SIG:" + Base64.getEncoder().encodeToString(signature));
            handler.send("USERNAME:" + localUsername);

            logger.log("Response sent. Waiting for host confirmation...");
            String response = handler.readLine();
            if (!"AUTH_OK".equals(response)) {
                logger.log("Host rejected the connection.");
                return null;
            }

            String hostUsernameLine = handler.readLine();
            String hostPubKeyLine = handler.readLine();

            String hostUsername = hostUsernameLine != null ? hostUsernameLine.replace("USERNAME:", "") : "host";

            String keyId = "unknown";
            if (hostPubKeyLine != null) {
                byte[] hostKeyBytes = Base64.getDecoder().decode(hostPubKeyLine.replace("PUBKEY:", ""));
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PublicKey hostPublicKey = keyFactory.generatePublic(new X509EncodedKeySpec(hostKeyBytes));
                keyId = shortId(hostPublicKey);
            }

            logger.log("Host identity confirmed: " + hostUsername + " (" + keyId + ")");
            return hostUsername + " (" + keyId + ")";

        } catch (Exception e) {
            logger.log("Auth error: " + e.getMessage());
            return null;
        }
    }

    public static String shortId(PublicKey publicKey) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(publicKey.getEncoded());
            return Base64.getEncoder().encodeToString(hash).substring(0, 8);
        } catch (Exception e) {
            return "unknown";
        }
    }
}
