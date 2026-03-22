package controller;
import model.PeerClient;
import model.CallRecord;
import model.Message;
import java.util.List;

public class ManagerClient extends PeerClient {
    private String privateKey;

    public ManagerClient(String peerId, String publicKey, String username, String privateKey) {
        super(peerId, publicKey, username);
        this.privateKey = privateKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public List<PeerClient> getActivePeers() {
        // TODO: return active peers
        return java.util.Collections.emptyList();
    }

    public List<CallRecord> getActiveCalls() {
        // TODO: return active calls
        return java.util.Collections.emptyList();
    }

    public List<Message> getTranscript(String peerId) {
        // TODO: return transcript messages for peerId
        return java.util.Collections.emptyList();
    }

    public List<CallRecord> requestTranscript(String peerId) {
        // TODO: request transcript for peerId
        return java.util.Collections.emptyList();
    }

    public List<CallRecord> requestCallLogs(String peerId) {
        // TODO: request call logs for peerId
        return java.util.Collections.emptyList();
    }
}
