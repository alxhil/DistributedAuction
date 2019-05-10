package bank;

import java.io.Serializable;

/**
 * This object is used for storing the account number and
 * string ID for each auction house, which is stored in
 * a hash map inside the bank class.
 *
 */

public class AuctionInfo implements Serializable {
    int accountNum;
    String auctionID;

    public AuctionInfo(int accountNum, String auctionID) {
        this.accountNum = accountNum;
        this.auctionID = auctionID;
    }


    public int getAccountNum() {
        return accountNum;
    }


    public String getAuctionID() {
        return auctionID;
    }
}