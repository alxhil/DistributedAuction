package auctionHouse;


import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;
/**
 * It represents the item that is being auctioned
 *
 */
public class AuctionedItems implements Serializable {

    private int minimumBid;
    private String name;
    private int currentBid;
    private int time;
    private String bidStatus;
    private Boolean isBidding;
    private boolean hasBidStarted;


    public void setBidStarted(boolean hasBidStarted) {
        this.hasBidStarted = hasBidStarted;
    }

    public boolean isBidStarted() {
        return hasBidStarted;
    }

    public void scheduleTimeTicking() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                timeTick();
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    public int getTime() {
        return time;
    }


    public AuctionedItems(String name, int minimumBid) {
        this.minimumBid = minimumBid;
        this.name = name;
        this.time = 10;
        this.isBidding = false;
        this.hasBidStarted = false;
        /*
        No bidding
        Bidding
        Won

         */
        this.bidStatus = "No Bidding";
        this.currentBid = minimumBid;
    }

    public int getMinimumBid() {
        return minimumBid;
    }

    public String getName() {
        return name;
    }

    public void setIsBidding(Boolean b) {
        this.isBidding = b;
    }

    public Boolean IsBidding() {
        return this.isBidding;
    }

    public int getCurrentBid() {
        return currentBid;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setCurrentBid(int currentBid) {
        this.currentBid = currentBid;
    }

    public void timeTick() {
        this.time--;
    }

    public void resetTime() {
        this.time = 30;
    }

    public String getBidStatus() {
        return bidStatus;
    }

    public void setBidStatus(String bidStatus) {
        this.bidStatus = bidStatus;
    }

}
