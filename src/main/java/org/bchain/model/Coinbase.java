package org.bchain.model;

public class Coinbase {

    private final String amount;
    private final String recipientAddress;

    public Coinbase(String amount, String recipientAddress) {
        this.amount = amount;
        this.recipientAddress = recipientAddress;
    }

    public String getAmount() {
        return amount;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }
}
