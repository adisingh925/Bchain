package org.bchain.model;

public class BroadcastTransaction {

    private final int amount;
    private final String senderAddress;
    private final String recipientAddress;
    private final byte[] signature;
    private final String publicKey;

    public BroadcastTransaction(int amount, String senderAddress, String recipientAddress, byte[] signature, String publicKey) {
        this.amount = amount;
        this.senderAddress = senderAddress;
        this.recipientAddress = recipientAddress;
        this.signature = signature;
        this.publicKey = publicKey;
    }

    public int getAmount() {
        return amount;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public byte[] getSignature() {
        return signature;
    }

    public String getPublicKey() {
        return publicKey;
    }
}
