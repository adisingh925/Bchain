package org.bchain.interfaces;

import org.bchain.model.Block;
import org.bchain.model.BroadcastTransaction;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

public interface Crypto {
    Integer checkBalance();
    String getPublicKey();
    ArrayList<Block> getBlockchain();
    BroadcastTransaction createTransaction(int amount, String recipientAddress);
    void mineEmptyBlock();
    String getAddress();
    boolean verifyAddress(String address);
    void verifyReceivedFunds(BroadcastTransaction transaction) throws NoSuchAlgorithmException, InvalidKeySpecException;
}
