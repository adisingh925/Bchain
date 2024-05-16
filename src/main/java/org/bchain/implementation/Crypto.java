package org.bchain.implementation;

import org.bchain.model.Block;
import org.bchain.model.BlockData;
import org.bchain.model.BroadcastTransaction;
import org.bchain.model.Coinbase;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

import static org.web3j.utils.Numeric.hexStringToByteArray;

public class Crypto implements org.bchain.interfaces.Crypto {

    private static final Logger LOGGER = Logger.getLogger(Crypto.class.getName());
    private final KeyPair keyPair;
    private final String publicKey;
    private final Coinbase coinbase;
    private static final ArrayList<Block> blockchain = new ArrayList<>();
    private final String address;
    private static final String FIRST_BLOCK_PREV_HASH = "0000000000000000000000000000000000000000000000000000000000000000";

    {
        keyPair = generateECDSAKeyPair();
        publicKey = bytesToHex(keyPair.getPublic().getEncoded());
        address = generateAddressFromPublicKey();
        coinbase = new Coinbase("100", address);
    }

    public static KeyPair generateECDSAKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
            keyGen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());
            return keyGen.generateKeyPair();
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.SEVERE, e.toString(), e);
        }

        return null;
    }

    private byte[] signMessage(PrivateKey privateKey, byte[] message) {
        try {
            Signature ecdsa = Signature.getInstance("SHA256withECDSA");
            ecdsa.initSign(privateKey);
            ecdsa.update(message);
            return ecdsa.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Error signing message", e);
        }
        return null;
    }

    private boolean verifySignature(byte[] message, byte[] signature, PublicKey publicKey) {
        try {
            Signature ecdsa = Signature.getInstance("SHA256withECDSA");
            ecdsa.initVerify(publicKey);
            ecdsa.update(message);
            return ecdsa.verify(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Error verifying signature", e);
        }
        return false;
    }

    private String getSha256(String blockData) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(blockData.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte aHash : hash) {
                String hex = Integer.toHexString(0xff & aHash);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.SEVERE, e.toString(), e);
        }
        return null;
    }

    private void mineBlock(ArrayList<BlockData> transactions) {
        StringBuilder transactionData = new StringBuilder();

        for (BlockData transaction : transactions) {
            transactionData.append(transaction.getAmount()).append(transaction.getSenderAddress()).append(transaction.getRecipientAddress());
        }

        Long timeStamp = System.currentTimeMillis();
        String previousHash = blockchain.isEmpty() ? FIRST_BLOCK_PREV_HASH : blockchain.get(blockchain.size() - 1).getHash();
        String blockData = timeStamp + previousHash + coinbase.getAmount() + coinbase.getRecipientAddress() + transactionData;

        Long nonce = 0L;
        String hash = getSha256(blockData + nonce);

        while (true) {
            assert hash != null;
            if (hash.startsWith("00000")) break;
            nonce++;
            hash = getSha256(blockData + nonce);
        }

        Block block = new Block(previousHash, hash, coinbase, transactions, nonce);
        blockchain.add(block);
        System.out.println("Block mined successfully!");
    }

    @Override
    public Integer checkBalance() {
        int balance = 0;
        for (Block block : blockchain) {
            for (org.bchain.model.BlockData transaction : block.getTransactions()) {
                if (transaction.getSenderAddress().equals(address)) {
                    balance -= transaction.getAmount();
                }

                if (transaction.getRecipientAddress().equals(address)) {
                    balance += transaction.getAmount();
                }
            }

            if (block.getCoinbase().getRecipientAddress().equals(address)) {
                balance += Integer.parseInt(block.getCoinbase().getAmount());
            }
        }
        return balance;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    public BroadcastTransaction createTransaction(int amount, String recipientAddress) {
        if (checkBalance() >= amount) {
            return new BroadcastTransaction(amount, address, recipientAddress, signMessage(keyPair.getPrivate(), (amount + address + recipientAddress).getBytes()), publicKey);
        } else {
            System.out.println("Insufficient funds.");
        }

        return null;
    }

    public void verifyReceivedFunds(BroadcastTransaction transaction) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (verifyAddress(transaction.getRecipientAddress())) {
            if (verifyAddress(transaction.getSenderAddress())) {
                if (verifySignature((transaction.getAmount() + transaction.getSenderAddress() + transaction.getRecipientAddress()).getBytes(), transaction.getSignature(), KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(hexStringToByteArray(transaction.getPublicKey()))))) {
                    ArrayList<BlockData> transactions = new ArrayList<>();
                    transactions.add(new BlockData(transaction.getAmount(), transaction.getSenderAddress(), transaction.getRecipientAddress()));
                    mineBlock(transactions);
                } else {
                    System.out.println("Invalid transaction signature.");
                }
            } else {
                System.out.println("Invalid sender address.");
            }
        } else {
            System.out.println("Invalid recipient address.");
        }
    }

    @Override
    public void mineEmptyBlock() {
        mineBlock(new ArrayList<>());
    }

    @Override
    public String getPublicKey() {
        return publicKey;
    }

    @Override
    public ArrayList<Block> getBlockchain() {
        return blockchain;
    }

    private String generateAddressFromPublicKey() {
        String address = Objects.requireNonNull(getSha256(publicKey)).substring(0, 40);
        String checksum = Objects.requireNonNull(getSha256(address)).substring(0, 8);
        return address + checksum;
    }

    public boolean verifyAddress(String address) {
        String addressWithoutChecksum = address.substring(0, 40);
        String checksum = address.substring(40);
        return Objects.requireNonNull(getSha256(addressWithoutChecksum)).substring(0, 8).equals(checksum);
    }

    public String getAddress() {
        return address;
    }
}
