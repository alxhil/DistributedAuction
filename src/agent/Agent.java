
package agent;

import auctionHouse.AuctionedItems;
import bank.AuctionInfo;
import constants.AuctionHouseAddress;
import constants.Message;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The agent class has all the method that is needed while communicating with the auction house
 * and the bank
 *
 * @author Anmol Baniya
 * @Date 5/4/2019
 */
public class Agent {

    private int accountNumber;
    private String agentName;
    private int initialBalance;
    private Socket socketForBank;

    /**
     * The constructor for the agent class
     *
     * @param socketForBank  socket for communicating with the bank
     * @param agentName      name of the agent
     * @param initialBalance initial balance agent has
     * @throws IOException
     */
    public Agent(Socket socketForBank, String agentName, int initialBalance) throws IOException {

        this.agentName = agentName;
        this.socketForBank = socketForBank;
        this.initialBalance = initialBalance;

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socketForBank.getOutputStream());

        objectOutputStream.writeObject(new Message("NewAgent"));


    }

    public String getAgentName() {
        return this.agentName;
    }

    /**
     * This method open the bank account of the agent by communicating with the bank
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */

    public void openBankAccount() throws IOException, ClassNotFoundException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socketForBank.getOutputStream());


        objectOutputStream.writeObject(new Message("CreateNewAccount", initialBalance, agentName));
        ObjectInputStream objectInputStream = new ObjectInputStream(socketForBank.getInputStream());

        Message message = (Message) objectInputStream.readObject();
        this.accountNumber = (Integer) message.splitCommand(1);


    }

    /**
     * @return total Balance of agent
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public int getTotalBalance() throws IOException, ClassNotFoundException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socketForBank.getOutputStream());
        objectOutputStream.writeObject(new Message("totalBalance"));
        ObjectInputStream objectInputStream = new ObjectInputStream(socketForBank.getInputStream());
        Message message = (Message) objectInputStream.readObject();
        return (Integer) message.splitCommand(1);
    }

    /**
     * @return total Available balance
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public int getAvailableBalance() throws IOException, ClassNotFoundException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socketForBank.getOutputStream());
        objectOutputStream.writeObject(new Message("availableBalance"));
        ObjectInputStream objectInputStream = new ObjectInputStream(socketForBank.getInputStream());
        Message message = (Message) objectInputStream.readObject();
        return (Integer) message.splitCommand(1);
    }


    /**
     * @return auction house list
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public HashMap<AuctionInfo, AuctionHouseAddress> getAuctionHousesList() throws IOException, ClassNotFoundException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socketForBank.getOutputStream());
        objectOutputStream.writeObject(new Message("ViewCurrentAuctions"));
        ObjectInputStream objectInputStream = new ObjectInputStream(socketForBank.getInputStream());
        Message message = (Message) objectInputStream.readObject();

        return (HashMap) message.splitCommand(1);

    }

    /**
     * It notifies bank to transfer the blocked funds to the auction house when the
     * agent wins the item
     *
     * @param accountNumOfAuction account num of auction house
     * @throws IOException
     */
    public void notifiesBankToTransferBlockedFunds(int accountNumOfAuction) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socketForBank.getOutputStream());
        objectOutputStream.writeObject(new Message("SendBlockedMoneyToAuction", accountNumOfAuction));


    }

    /**
     * @param socket socket for communication with the bank
     * @return gives the list of auctioned items by the auction house
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public ArrayList<AuctionedItems> getListOfAuctionedItems(Socket socket) throws IOException, ClassNotFoundException {

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(new Message("ViewCurrentAuctionItems"));
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        Message message = (Message) (objectInputStream.readObject());
        return (ArrayList<AuctionedItems>) message.splitCommand(1);

    }

    /**
     * This method places the bid on the item
     *
     * @param item      item on which bid was placed
     * @param bidAmount bid amount
     * @param socket    socket for communication with the auction house
     * @throws IOException
     */
    public void placeBid(AuctionedItems item, int bidAmount, Socket socket) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(new Message("Bid", item.getName(), bidAmount, accountNumber, getAgentName()));
    }

    /**
     * This method get the bid status message
     *
     * @param socket socket for communication with the auction house
     * @return arraylist of messages of bid status of each of the items
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public ArrayList<Message> getStatusOfBid(Socket socket) throws IOException, ClassNotFoundException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(new Message("GetBidStatus", accountNumber));

        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        Message message = (Message) (objectInputStream.readObject());

        return (ArrayList<Message>) message.splitCommand(1);


    }


}
