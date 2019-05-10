package auctionHouse;

import constants.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * This class creates an Auctionhouse which interacts with the bank and the agent using ports
 * @author Alex Hill
 * @date 5/5/2019
 */
public class AuctionHouse {

    public static Socket socketForBank;
    private static Socket client;
    private static ArrayList<AuctionedItems> possibleAuctionItems = new ArrayList<>();
    public static ArrayList<AuctionedItems> ItemsBeingAuctioned = new ArrayList<>();
    public static HashMap<AuctionedItems, ArrayList<BiddingAgents>> biddingAgentMessage;
    private static String IP_ADDRESS = "";
    private static String BANK_IP_ADDRESS = "";
    private static int BANK_PORT = 0;
    public static int accountNumber;
    private static final boolean LOCAL = false;
    private static ServerSocket serverSocket;


    public static AuctionedItems getItem(String itemName) {
        for (AuctionedItems item : ItemsBeingAuctioned) {
            if (item.getName().equals(itemName)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Initializes messages for bidding agents
     */
    public static void intializeBiddingAgentMessage() {

        biddingAgentMessage = new HashMap<>();
        for (AuctionedItems item : ItemsBeingAuctioned) {
            ArrayList<BiddingAgents> biddingAgents = new ArrayList<>();
            biddingAgentMessage.put(item, biddingAgents);
        }


    }

    public AuctionHouse() throws IOException {


    }

    /**
     * Gets the auction list and starts the first messages which gets sent to the GUI
     * @throws IOException
     */
    public static void startAuction() throws IOException {

        AuctionList AL = new AuctionList();
        possibleAuctionItems = AL.generateAuctions();
        auctionItems();
        intializeBiddingAgentMessage();
    }

    /**
     * Main
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 4 && !LOCAL) {
            System.out.println("Wrong arguments!\n" +
                    "Usage: java -jar AuctionHouse.jar AUCTION_PORT AUCTION_IP BANK_IP BANK_PORT");
            System.exit(0);
        }


        if (LOCAL) {
            IP_ADDRESS = "localhost";
            BANK_IP_ADDRESS = "localhost";
            BANK_PORT = 8080;
            startAuction(9088);
        } else {
            IP_ADDRESS = args[1].toString();
            BANK_IP_ADDRESS = args[2].toString();
            BANK_PORT = Integer.parseInt(args[3]);
            startAuction(Integer.parseInt(args[0]));
        }
        AuctionHouse auctionHouse = new AuctionHouse();
        auctionHouse.openBankAccount();
        replaceIDs();
        if (LOCAL) {
            auctionHouse.sendAddress(9088);
        } else {
            auctionHouse.sendAddress(Integer.parseInt(args[0]));
        }
        auctionHouse.acceptAgentRequest();
    }


    public static void startAuction(int port) throws IOException {

        AuctionList AL = new AuctionList();
        possibleAuctionItems = AL.generateAuctions();
        auctionItems();
        intializeBiddingAgentMessage();
        socketForBank = new Socket(BANK_IP_ADDRESS, BANK_PORT);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socketForBank.getOutputStream());
        objectOutputStream.writeObject(new Message("NewAuctionHouse"));
        serverSocket = new ServerSocket(port);
    }


    /**
     * Replaces IDs of auction items so that when multiple auctions are running they can find out which
     * auction ID is which because they are all unique
     */
    public static void replaceIDs() {
        for(int i = 0; i < possibleAuctionItems.size(); i++){
            possibleAuctionItems.get(i).setName(possibleAuctionItems.get(i).getName()+accountNumber);
        }


    }

    /**
     * Waits for an agent to connect
     * @throws IOException
     */
    public void acceptAgentRequest() throws IOException {
        while (true) {

            Socket socket = serverSocket.accept();
            AgentHandler agentHandler = new AgentHandler(socket);
            agentHandler.start();
        }
    }


    /**
     * Opens a new account and generates a unique ID
     * @throws IOException
     */
    public void openBankAccount() throws IOException {
        ObjectOutputStream objOutForBank = new ObjectOutputStream(socketForBank.getOutputStream());
        objOutForBank.writeObject(new Message("CreateNewAccount"));
        ObjectInputStream objectInputStream = new ObjectInputStream(socketForBank.getInputStream());
        Message message = null;
        try {
            message = (Message) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        accountNumber = (Integer) message.splitCommand(1);
        System.out.println("Bank Account ID: " + accountNumber);
        checkBalance();
    }


    /**
     * Sends the address of the auction house to other agents so that they can find every auction house
     * @param portNumber
     * @throws IOException
     */
    public void sendAddress(int portNumber) throws IOException {
        ObjectOutputStream objOutForBank = new ObjectOutputStream(socketForBank.getOutputStream());
        objOutForBank.writeObject(new Message(
                "AuctionAddress", IP_ADDRESS, portNumber));
    }

    /*
        This method generates the items to auction. It randomly picks
        3 items from the possible items list.
     */

    /**
     * Makes the initial three auction items
     */
    public static void auctionItems() {
        Random random = new Random();
        while (ItemsBeingAuctioned.size() < 3) {
            int selectRandomItem = random.nextInt(possibleAuctionItems.size());
            AuctionedItems item = possibleAuctionItems.get(selectRandomItem);
            if (!ItemsBeingAuctioned.contains(item)) {
                ItemsBeingAuctioned.add(item);
            }
        }

    }

    /**
     * Checks balance of the auctionhouse and outputs it to the console
     * @throws IOException
     */
    public static void checkBalance() throws IOException {
        ObjectOutputStream objOutForBank = new ObjectOutputStream(socketForBank.getOutputStream());
        objOutForBank.writeObject(new Message(
                "CheckBalance", accountNumber));
        ObjectInputStream objInput = new ObjectInputStream(socketForBank.getInputStream());
        Message msg = null;
        try {
            msg = (Message) objInput.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Auction Account Balance: $" + (int) (msg.splitCommand(1)));
    }


    /**
     * Replaces an auction item if it is bidded on and won/lost
     * @throws IOException
     */
    public static void replaceAuctionedItem() throws IOException {
        Random random = new Random();
        while (ItemsBeingAuctioned.size() < 3) {

            int selectRandomItem = random.nextInt(possibleAuctionItems.size());
            AuctionedItems item = possibleAuctionItems.get(selectRandomItem);
            if (!ItemsBeingAuctioned.contains(item)) {
                biddingAgentMessage.put(item, new ArrayList<>());
                ItemsBeingAuctioned.add(possibleAuctionItems.get(selectRandomItem));
            }
        }
        //Whenever an item gets replaced it will print out the account balance because that is when
        //A bid is won.
    }


}
