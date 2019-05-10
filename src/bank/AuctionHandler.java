package bank;

import constants.AuctionHouseAddress;
import constants.Message;
import java.io.*;
import java.net.Socket;
import java.util.Random;

/**
 * An instance of this class runs on its own thread, instantiated by
 * Bank, and communicates with a specific auction house. It handles and generates
 * all the messages used in correspondence and stores some values that
 * belong to the auction house as well.
 * @author Isaac Heflin
 * @Date 5/5/2019
 */

public class AuctionHandler extends Thread {

    private Socket socket;
    private Account account;
    private Account agentAccount;
    private AuctionHouseAddress auctionHouseAddress;

    public AuctionHandler(Socket socket) {
        this.socket = socket;
    }

    /**
     * Each time a message is sent from the auction house, this method is
     * called. Several possibilities for messages exist, and this handles
     * every case for what the auction house will potentially send using
     * a switch statement, allowing the bank to respond appropriately.
     * @param message: The message from the auction house
     * @throws IOException
     */
    private void processMessage(Message message)
            throws IOException {

        switch (message.getCommand()) {

            case "CheckBalance":
                ObjectOutputStream checkBalance =
                        new ObjectOutputStream(socket.getOutputStream());
                int balance = (Bank.accountHashMap.get(
                        message.splitCommand(1)).getMoney());

                checkBalance.writeObject(new Message("", balance));
                break;

            case "CreateNewAccount":
                this.account = new Account(0, Bank.accountHashMap);
                Bank.accountHashMap.put(account.getAccountNum(), account);
                ObjectOutputStream sender = new ObjectOutputStream(
                        socket.getOutputStream());

                sender.writeObject(new Message(
                        "AccountNum", account.getAccountNum()));
                break;

            case "AuctionAddress":
                Random random = new Random();
                String auctionID = "AuctionHouse" + random.nextInt(10000);
                this.auctionHouseAddress = new AuctionHouseAddress(
                        (String) message.splitCommand(1),
                        (Integer) message.splitCommand(2));

                Bank.auctionHouseAddresses.put(
                        new AuctionInfo(account.getAccountNum(), auctionID),
                        auctionHouseAddress);
                break;

            case "BlockFunds":
                this.agentAccount = Bank.accountHashMap.get(
                        message.splitCommand(1));
                this.agentAccount = Bank.accountHashMap.get(message.splitCommand(1));
                System.out.println("Block funds of account number "+this.agentAccount.getAccountNum()+" with amount "+message.splitCommand(2));

                this.agentAccount.setBlockFunds((int) message.splitCommand(2));
                break;

            case "UnblockFunds":

                this.agentAccount = Bank.accountHashMap.get(
                        message.splitCommand(1));
                this.agentAccount.setUnBlockFunds((Integer) message.splitCommand(2));
                System.out.println("Unblock funds of account number "+this.agentAccount.getAccountNum()+" with amount "+message.splitCommand(2));
                this.agentAccount.setUnBlockFunds(
                        (Integer) message.splitCommand(2));

                break;

            case "Terminates":
                Bank.accountHashMap.remove(account.getAccountNum());
                Bank.auctionHouseAddresses.remove(auctionHouseAddress);
                break;

        }
    }

    /**
     * This method waits for a message to be sent by an auction house, then,
     * once the message is sent, calls the processMessage method to
     * properly deal with the contents. If the message is of termination,
     * the thread dies (peacefully).
     */
    public void run() {

        while (true) {
            ObjectInputStream objInput = null;

            try {
                objInput = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Message message = (Message) objInput.readObject();

                if (message.getCommand().equals("Terminates")) {
                    processMessage(message);
                    break;
                } else {


                    processMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
