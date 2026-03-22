package model;

public class PeerClient {
    private String peerId;
    private String publicKey;
    private String username;
    private String status;

    public PeerClient(String peerId, String publicKey, String username) {
        this.peerId = peerId;
        this.publicKey = publicKey;
        this.username = username;
        this.status = "offline";
    }

    public String getPeerId() {
        return peerId;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}