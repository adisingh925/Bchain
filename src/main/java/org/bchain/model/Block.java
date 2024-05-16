package org.bchain.model;

import java.util.List;

public class Block {

    private final String hash;
    private final String previousHash;
    private final Coinbase coinbase;
    private final List<BlockData> transactions;
    private final long timeStamp;
    private final Long nonce;            //start with 4 zeros

    public Block(String previousHash, String blockHash, Coinbase coinbase, List<BlockData> transactions, Long nonce) {
        this.coinbase = coinbase;
        this.transactions = transactions;
        this.previousHash = previousHash;
        this.timeStamp = System.currentTimeMillis();
        this.hash = blockHash;
        this.nonce = nonce;
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public List<BlockData> getTransactions() {
        return transactions;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public long getNonce() {
        return nonce;
    }

    public Coinbase getCoinbase() {
        return coinbase;
    }
}
