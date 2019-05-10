package constants;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This object is used for ease of communication between programs.
 * Any amount serializable objects can be passed to the constructor
 * and the Message object resulting will contain each object in the
 * order which they were passed. The command is the first item passed,
 * which is to signify what the recipient of the message is to do with
 * the arguments, which are given after that.
 */

public class Message implements Serializable {

    /**
     * MESSAGE OPTIONS
     * [String command][args]:Sender/Receiver
     * Description
     * <p>
     * A MESSAGE SENT WITH NO COMMAND SHOULD USE "" AS ITS FIRST ARGUMENT
     * <p>
     *   Description
     *
     *   A MESSAGE SENT WITH NO COMMAND SHOULD USE "" AS ITS FIRST ARGUMENT
     *
     * [Terminates][]: Bank/Agent or AuctionHouse
     *   The bank is notified to end the connection with the agent or
     *     auction house as they are closing
     *
     * [CheckBalance][int accountNum]: Bank/AuctionHouse
     *   The sender requests to see their total balance
     *   The bank sends back the balance integer
     *
     * [availableBalance][]: Bank/Agent
     *   Checks the available balance of the agent
     *   Bank sends back the integer of the available balance
     *
     * [totalBalance][]: Bank/Agent
     *   Checks the total balance of the agent
     *   Bank sends back the integer of the available balance
     *

     * [NewAgent][]: Bank or AuctionHouse/Agent
     * Establishes connection with agent
     * <p>
     * [NewAuctionHouse][]: Bank/AuctionHouse
     * Establishes connection with auction house
     * <p>
     * [CreateNewAccount][]:Bank/AuctionHouse or Agent
     * Creates a new account, the bank sends back the account number
     * <p>
     * [AuctionAddress][Integer ipAddress, Integer portNum]:Bank/AuctionHouse
     * Gives the bank a location for the auction house
     * <p>
     * [BlockFunds][Integer agentAccountNum, Integer fundsToBlock]:Bank/AuctionHouse
     * Blocks specified funds from the given agent account
     * <p>
     * [UnblockFunds][Integer agentAccountNum, Integer fundsToUnblock]:Bank/AuctionHouse
     * Unblocks specified funds from the given agent account
     * <p>
     * [AccountNum][Integer accountNum]:AuctionHouse/Bank
     * Sends the account number to the auction house when it is created
     * <p>
     * [ViewCurrentAuctions][]:Agent/Bank

     * Requests auctionhouse info from bank
     * Bank sends back HashMap<AuctionInfo, AuctionHouseAddress>
     * <p>
     *   Requests auctionhouse info from bank
     *   Bank sends back HashMap<AuctionInfo, AuctionHouseAddress>
     *
     * [ViewCurrentAuctionItems][]: Agent/AuctionHouse
     *   Requests item info from the auction house
     *   Auction house sends back the ItemsBeingAuctioned collection
     *
     * [BlockFunds][Integer bidAmount, Integer agentAccountNum]:Bank/AuctionHouse
     * Blocks an amount of funds from what the bidAmount was
     * <p>
     * [Bid][String item, Integer bidAmount, Integer agentAccountNum]:AuctionHouse/Agent
     * Sends the bidding information to the auction house so it can process an attempted bid
     * <p>
     * [UnblockFunds][Integer amount, Integer agentAccountNum]:Bank/AuctionHouse
     * Gets the account to unblock the funds so it is available to the agent
     * <p>
     * [ProcessBlockedFunds][Integer agentAccountNum, Integer auctionAccountNum]: Bank/AuctionHouse
     * Upon an auction ending, the blocked funds are transferred to the auction house
     * account from the agent's account
     *   Sends the bidding information to the auction house so it can process an attempted bid
     *
     * [GetBidStatus][Integer accountNumAgent]: Agent/AuctionHouse
     *   Agent requests the bid status of their current auctions
     *   AuctionHouse calls sendBidStatus, which sends the info to Agent
     *
     * [UnblockFunds][Integer amount, Integer agentAccountNum]:Bank/AuctionHouse
     *   Gets the account to unblock the funds so it is available to the agent
     *
     * [SendBlockedMoneyToAuction][Integer auctionAccountNum]: Bank/AuctionHouse
     *   Upon an auction ending, the blocked funds are transferred to the auction house
     *   account from the agent's account
     */

    ArrayList<Object> args = new ArrayList<>();

    public Message(Object... args) {
        for (Object arg : args) {
            this.args.add(arg);
        }
    }

    /**
     * Takes n amount of args that are sent to the
     * constructor and changes them toStrings for
     * testing, printing, etc.
     *
     * Takes n amount of args that are sent to the constructor and changes
     * them to Strings for testing purposes. This is not used in the final
     * implementation of the program.
     * @return String representation of args
     */
    public String toStrings() {
        String returnString = new String();
        for (Object arg : args) {
            returnString += arg.toString();
            returnString += ",";
        }
        return returnString;
    }

    public String getCommand() {
        return this.args.get(0).toString();
    }

    public Object splitCommand(int arg) {
        return this.args.get(arg);
    }

}
