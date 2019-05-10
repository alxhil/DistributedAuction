package auctionHouse;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import constants.Message;

/**
 * This class handles the agent
 * @author Alex Hill
 * @date 5/5/2019
 */
public class AgentHandler extends Thread {

    private Socket socket;
    private ArrayList<Message> messageForEachItems = new ArrayList<>();
    private AuctionedItems wonItem;
    private boolean agentWon = false;
    private boolean checkBalance = false;


    public AgentHandler(Socket socket) {
        this.socket = socket;

    }

    public void processMessage(Message message) throws IOException, ClassNotFoundException {


        switch (message.getCommand()) {


            case "ViewCurrentAuctionItems":
                sendMessageToAgent(new Message("", AuctionHouse.ItemsBeingAuctioned));
                break;

            case "GetBidStatus":
                int accountNumOfAgent = (Integer) message.splitCommand(1);
                sendBidStatus(accountNumOfAgent);
                break;


            /*

            Example: bid String(ItemName) bidamount AgentID

            When an agent bids on an item, it will send a string to the bank to block
            the funds to the agent account and it will reset the bidding timer.

             */

            case "Bid":
                String itemName = (String) message.splitCommand(1);
                AuctionedItems bidItem = AuctionHouse.getItem(itemName);
                if (!bidItem.isBidStarted()) {
                    bidItem.setBidStarted(true);
                    bidItem.scheduleTimeTicking();
                }
                if (bidItem.isBidStarted()) {
                    bidItem.resetTime();
                }
                int bidAmount = (Integer) message.splitCommand(2);
                int agentAccountNum = (Integer) message.splitCommand(3);
                String accName = (String) message.splitCommand(4);

                if (getBiddingAgent(agentAccountNum, bidItem) == null) {
                    BiddingAgents biddingAgents = new BiddingAgents(agentAccountNum,accName);
                    AuctionHouse.biddingAgentMessage.get(bidItem).add(biddingAgents);

                }
                sendMessageToBank(new Message("CheckBalance", agentAccountNum));
                ObjectInputStream objectInputStream = new ObjectInputStream(AuctionHouse.socketForBank.getInputStream());
                Message messageFromBank = (Message) objectInputStream.readObject();

                int agentFund = (Integer) messageFromBank.splitCommand(1);

                if (bidAmount > agentFund) {
                    BiddingAgents biddingAgents = getBiddingAgent(agentAccountNum, bidItem);
                    biddingAgents.setMessage("Hey, You have insufficient fund");
                } else if (bidAmount > bidItem.getCurrentBid() && bidAmount > bidItem.getMinimumBid()) {
                    BiddingAgents biddingAgents = getBiddingAgent(agentAccountNum, bidItem);
                    biddingAgents.setBlockedFunds(bidAmount);
                    bidItem.setCurrentBid(bidAmount);
                    biddingAgents.setMessage("You are the highest bidder");

                    System.out.println(biddingAgents.getAccName()+" has bid $" + bidAmount + " on "+bidItem.getName());

                    sendMessageToBank(new Message("BlockFunds", agentAccountNum, bidAmount));


                    biddingAgents.setHighestBidder(true);
                    setMessageForOtherBidders(bidItem, "You have been outbid", biddingAgents);
                } else if (bidAmount < bidItem.getMinimumBid()) {
                    getBiddingAgent(agentAccountNum, bidItem).setMessage("Your bid amount is too low");
                } else if (bidAmount <= bidItem.getCurrentBid()) {
                    getBiddingAgent(agentAccountNum, bidItem).setMessage("Your bid is invalid");
                }

                break;

        }
    }

    /**
     *
     * @param accountNum
     * @param item
     * @return BiddingAgent
     */
    public BiddingAgents getBiddingAgent(int accountNum, AuctionedItems item) {
        ArrayList<BiddingAgents> biddingAgents = AuctionHouse.biddingAgentMessage.get(item);
        for (BiddingAgents biddingAgent : biddingAgents) {
            if (biddingAgent.getAccountNum() == accountNum) {
                return biddingAgent;
            }
        }
        return null;
    }

    /**
     * Sends bid status
     * @param accountNum
     * @throws IOException
     */
    public synchronized void sendBidStatus(int accountNum) throws IOException {
        for (AuctionedItems item : AuctionHouse.biddingAgentMessage.keySet()) {
            ArrayList<BiddingAgents> biddingAgents = AuctionHouse.biddingAgentMessage.get(item);
            for (BiddingAgents biddingAgent : biddingAgents) {
                if (biddingAgent.getAccountNum() == accountNum) {
                    if (item.getTime() == 0) {
                        if (biddingAgent.isHighestBidder()) {
                            biddingAgent.setMessage("You Won this Item");
                            setMessageForOtherBidders(item, "You lost the item", biddingAgent);
                            System.out.println(biddingAgent.getAccName()+" has won the item "+item.getName());
                            this.wonItem = item;
                            this.agentWon = true;




                        }
                    }
                    AuctionHouse.replaceAuctionedItem();
                    messageForEachItems.add(new Message("", item.getName(), item.getCurrentBid(), biddingAgent.getMessage()));
                }
            }
        }

        if (agentWon) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            AuctionHouse.biddingAgentMessage.remove(wonItem);
            AuctionHouse.ItemsBeingAuctioned.remove(wonItem);
            AuctionHouse.replaceAuctionedItem();
            agentWon = false;
            checkBalance = true;



        }
        sendMessageToAgent(new Message("", messageForEachItems));
        messageForEachItems.clear();
        if(checkBalance) {
            try{
                Thread.sleep(1500);
            } catch (Exception e){
                e.printStackTrace();
            }
            AuctionHouse.checkBalance();
            checkBalance = false;
        }

    }


    /**
     *Sets messages on GUI for other bidders if they have been outbid/lost
     * @param item
     * @param message
     * @param agentThatIsBidding
     * @throws IOException
     */
    private void setMessageForOtherBidders(AuctionedItems item, String message, BiddingAgents agentThatIsBidding) throws IOException {
        ArrayList<BiddingAgents> biddingAgents = AuctionHouse.biddingAgentMessage.get(item);
        BiddingAgents agent = agentThatIsBidding;
        for (BiddingAgents biddingAgent : biddingAgents) {


            if (((agent.getAccountNum()) == biddingAgent.getAccountNum())) {

            } else {

                if (!((agent.getAccountNum()) == biddingAgent.getAccountNum())) {
                    sendMessageToBank(new Message("UnblockFunds", biddingAgent.getAccountNum(),
                            biddingAgent.getBlockedFunds()));
                }
                biddingAgent.setMessage(message);
                biddingAgent.setHighestBidder(false);
            }
        }

    }


    /**
     *Sends message to Agent through ObjectOutputStream using agent socket
     * @param message
     * @throws IOException
     */
    private void sendMessageToAgent(Message message) throws IOException {

        ObjectOutputStream objOut = null;

        try {
            objOut = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        objOut.writeObject(message);


    }

    /**
     *Sends message to bank through ObjectOutputStream using socketForBank
     * @param message
     * @throws IOException
     */
    public void sendMessageToBank(Message message) throws IOException {
        ObjectOutputStream objOut = null;

        try {
            objOut = new ObjectOutputStream(AuctionHouse.socketForBank.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        objOut.writeObject(message);
    }

    /**
     * Thread
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
