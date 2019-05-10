package bank;

import constants.AuctionHouseAddress;
import constants.Message;
import java.net.*;
import java.io.*;
import java.util.HashMap;

/**
 * This class is the main thread for the bank that runs on startup.
 * It waits for an auction house or an agent to contact it and starts
 * a new thread through the use of an agent or auction handler to
 * manage each entity.
 * @author Isaac Heflin
 * @Date 5/5/2019
 */

public class Bank extends Thread {
    public static HashMap<Integer, Account> accountHashMap = new HashMap<>();
    public static HashMap<AuctionInfo, AuctionHouseAddress>
            auctionHouseAddresses = new HashMap();
    private static final boolean LOCAL = false;
    private ServerSocket bankSocket;


    public static void main(String[] args)
            throws IOException, ClassNotFoundException {

        Bank bank = new Bank();
        if(args.length != 1 && !LOCAL){
            System.out.println("Wrong Arguments! \n" +
                    " Usage: java -jar Bank.jar PORT_NUMBER_OF_BANK ");

            System.exit(0);
        }
        if (LOCAL) {
            bank.start(8080);
        } else {
            bank.start(Integer.parseInt(args[0]));
        }

    }


    /**
     * An Agent of an AuctionHouse will start a connection with the
     * Bank and send what type of client they are so the Bank can start
     * a new handler based on that information.
     *
     * @param port: The bank's port
     * @throws IOException, ClassNotFoundException
     */
    public void start(int port) throws IOException, ClassNotFoundException {
        Socket client;
        Message message;
        bankSocket = new ServerSocket(port);

        while (true) {
            try {
                client = bankSocket.accept();
            } catch (UnknownHostException u) {
                System.out.println("Invalid client port/IP sent to bank");
                break;
            }

            message = (Message) (new ObjectInputStream(
                    client.getInputStream())).readObject();


            if (message.getCommand().equals("NewAgent")) {

                new Thread(new AgentHandler(client)).start();
                System.out.println("Connected to agent");
            } else if(message.getCommand().equals("NewAuctionHouse")) {

                new Thread(new AuctionHandler(client)).start();
                System.out.println("Connected to auctionhouse");
            }
        }
    }
}
