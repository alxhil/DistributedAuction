package constants;

import java.io.Serializable;

/**
 * This object is used for storing the physical address of the
 * auction house for later reference to access the other program
 * using the IP address and the port number the program is using.
 */

public class AuctionHouseAddress implements Serializable {
    private String ipAddress;
    private int portNumber;

    public AuctionHouseAddress(String ipAddress, int portNumber) {
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPortNumber() {
        return portNumber;
    }
}