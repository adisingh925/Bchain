package org.bchain;

import org.bchain.interfaces.Crypto;
import org.bchain.model.Block;
import org.bchain.model.BroadcastTransaction;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

/**
 * Main class to run the application.
 */
public class App {

    private static final Crypto crypto = new org.bchain.implementation.Crypto();

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n\n**********************************************");
            System.out.println("****** Welcome to Bcoin Blockchain App ******");
            System.out.println("**********************************************");
            System.out.println("Wallet Address : " + crypto.getAddress());
            System.out.println("----------------------------------------------");
            System.out.println("1. Transfer Bcoin");
            System.out.println("2. Mine Empty Block");
            System.out.println("3. Check Balance");
            System.out.println("4. Display Blockchain");
            System.out.println("5. Exit");
            System.out.print("Choose an option (1-5): ");

            int option = scanner.nextInt();

            switch (option) {
                case 1: {
                    System.out.println("----------------------------------------------");
                    System.out.print("Enter recipient address: ");
                    String recipientAddress = scanner.next();
                    System.out.print("Enter amount: ");
                    int amount = scanner.nextInt();
                    BroadcastTransaction broadcastTransaction = crypto.createTransaction(amount, recipientAddress);
                    crypto.verifyReceivedFunds(broadcastTransaction);
                    break;
                }
                case 2: {
                    crypto.mineEmptyBlock();
                    break;
                }
                case 3: {
                    int balance = crypto.checkBalance();
                    System.out.println("----------------------------------------------");
                    System.out.println("Your balance is: " + balance);
                    System.out.println("----------------------------------------------");
                    break;
                }
                case 4:
                    System.out.println("----------------------------------------------");
                    System.out.println("Displaying Blockchain...");
                    System.out.println("----------------------------------------------");
                    for (Block b : crypto.getBlockchain()) {
                        System.out.println("Hash: " + b.getHash());
                        System.out.println("Previous Hash: " + b.getPreviousHash());
                        System.out.println("Coinbase Amount: " + b.getCoinbase().getAmount());
                        System.out.println("Coinbase Recipient Address: " + b.getCoinbase().getRecipientAddress());
                        System.out.println("Transactions: ");
                        for (org.bchain.model.BlockData transaction : b.getTransactions()) {
                            System.out.println("\tAmount: " + transaction.getAmount());
                            System.out.println("\tSender Address: " + transaction.getSenderAddress());
                            System.out.println("\tRecipient Address: " + transaction.getRecipientAddress());
                        }
                        System.out.println("Timestamp: " + b.getTimeStamp());
                        System.out.println("Nonce: " + b.getNonce());
                        System.out.println("----------------------------------------------");
                    }
                    break;
                case 5: {
                    System.out.println("----------------------------------------------");
                    System.out.println("Exiting...");
                    System.out.println("----------------------------------------------");
                    System.exit(0);
                    break;
                }
                default: {
                    System.out.println("----------------------------------------------");
                    System.out.println("Invalid option. Please choose a valid option (1-5).");
                    System.out.println("----------------------------------------------");
                }
            }
        }
    }
}
