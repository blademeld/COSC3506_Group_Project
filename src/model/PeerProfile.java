package model;

import java.security.PrivateKey;
import java.security.PublicKey;

// Represents the local user's identity and keypair
public class PeerProfile {

    private final String username;
    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    public PeerProfile(String username, PublicKey publicKey, PrivateKey privateKey) {
        this.username = username;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public String getUsername() {
        return username;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
