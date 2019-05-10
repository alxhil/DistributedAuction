package auctionHouse;

/**
 * This class represent the agent that is bidding on the item
 */
public class BiddingAgents {
    private int accountNum;
    private String message;
    private boolean isHighestBidder;
    private int blockedFunds;
    private String accName;


    public BiddingAgents(int accountNum, String accName) {
        this.accountNum = accountNum;
        this.accName = accName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAccName(){
        return this.accName;
    }


    public int getAccountNum() {
        return accountNum;
    }

    public String getMessage() {
        return message;
    }

    public int getBlockedFunds() {
        return blockedFunds;
    }

    public void setBlockedFunds(int blockedFunds) {
        this.blockedFunds = blockedFunds;
    }

    public boolean isHighestBidder() {
        return isHighestBidder;
    }

    public void setHighestBidder(boolean highestBidder) {
        isHighestBidder = highestBidder;
    }
}
