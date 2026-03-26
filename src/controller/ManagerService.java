package controller;

import model.CallRecord;
import model.Message;
import model.PeerClient;
import model.PeerProfile;
import model.TranscriptStore;

import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ManagerService extends PeerService {

    private final TranscriptStore store;
    private PeerProfile profile;

    public ManagerService(TranscriptStore store) {
        this.store = store;
    }

    public void setProfile(PeerProfile profile) {
        this.profile = profile;
    }

    // Creates a PeerClient representing the local user
    public PeerClient generatePeer() {
        if (profile == null) return null;
        String keyId = ChallengeService.shortId(profile.getPublicKey());
        String pubKey = Base64.getEncoder().encodeToString(profile.getPublicKey().getEncoded());
        return new PeerClient(keyId, pubKey, profile.getUsername());
    }

    // Confirms the local keypair is valid by signing and verifying test data
    public boolean validatePeer() {
        if (profile == null) return false;
        try {
            byte[] testData = "validate".getBytes();
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(profile.getPrivateKey());
            sig.update(testData);
            byte[] signature = sig.sign();
            sig.initVerify(profile.getPublicKey());
            sig.update(testData);
            return sig.verify(signature);
        } catch (Exception e) {
            return false;
        }
    }

    // Returns all distinct participants found in the transcript store
    public List<PeerClient> getActivePeers() {
        Map<String, PeerClient> peers = new LinkedHashMap<>();
        for (Message msg : store.getMessages()) {
            String sender = msg.getSenderId();
            if (!peers.containsKey(sender)) {
                peers.put(sender, new PeerClient(sender, "", sender));
            }
            String receiver = msg.getReceiverId();
            if (receiver != null && !peers.containsKey(receiver)) {
                peers.put(receiver, new PeerClient(receiver, "", receiver));
            }
        }
        return new ArrayList<>(peers.values());
    }

    // Returns messages sent to or from the given peer ID
    public List<Message> getMessagesFor(String peerId) {
        List<Message> result = new ArrayList<>();
        for (Message msg : store.getMessages()) {
            if (msg.getSenderId().equals(peerId) || msg.getReceiverId().equals(peerId)) {
                result.add(msg);
            }
        }
        return result;
    }

    public List<CallRecord> getActiveCalls() {
        return store.getCallRecords();
    }

    public List<CallRecord> requestTranscript(String peerId) {
        return store.getCallRecords();
    }

    public List<CallRecord> requestCallLogs(String peerId) {
        return store.getCallRecords();
    }
}
