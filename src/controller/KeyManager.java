package controller;

import model.PeerProfile;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

// Generates and manages the local RSA keypair
public class KeyManager {

    private static final int KEY_SIZE = 2048;

    // Generate a new RSA keypair and return a PeerProfile for the given username
    public PeerProfile generateProfile(String username) throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(KEY_SIZE);
        KeyPair keyPair = generator.generateKeyPair();

        //
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        return new PeerProfile(username, publicKey, privateKey);
    }
}
