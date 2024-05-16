package org.bchain.model;

public class BlockData {

    private final int amount;
    private final String senderAddress;
    private final String recipientAddress;

    public BlockData(int amount, String senderAddress, String recipientAddress) {
        this.amount = amount;
        this.senderAddress = senderAddress;
        this.recipientAddress = recipientAddress;
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
}
