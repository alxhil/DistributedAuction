# Distributed Auction
##### JDK-8
##### Isaac Heflin, Alex Hill, Anmol Baniya

### Command Line Arguments
Bank : java -jar Bank.jar BANK_PORT

Auction House: java -jar Auction.jar AUCTION_PORT AUCTION_IP BANK_IP BANK_PORT

Agent: java -jar Agent.jar BANK_IP_ADDRESS BANK_PORT

#### Who did what ?

#### Anmol Baniya
He did the agent application.
#### Alex Hill
 He did the auction house.
#### Isaac Heflin
He did the bank and the messaging thing.

### Packages

#### Bank
A server for the Auction House and Agent clients.

###### Auction/Agent Handler
Handles communication between the bank and the auction house and the agents
respectively.

###### Account
Holds the account information for each agent as well as the methods for
accessing and manipulating the account values.

###### Auction Info
Convenience object for the information on the auction instances under the
specific auction handler.

#### Auction House
A server for the Agent clients and a client of the Banks.

###### Agent Handler
Handles communication between the auction house and the agent.

###### Auctioned Items
A class to keep track of the bidding items and their statuses.

###### Auction List
Used to read the auctionitems.txt file in this package which contains the
configuration information about the items that the auction houses can use
for bidding.

###### Bidding Agents
Convenience object for the information on all agents bidding on specific items
in a collection.

#### Agent
A client that uses both the bank and auction houses as servers to
communicate with. The Agent class contains all the methods needed for
the communication with the servers.

###### Agent Graphics Handler
The main thread of the package which also handles the GUI for the program.

#### Constants
###### Message
A serializable object to send content from one program to another over
object to keep messaging consistent. Contains documentation of messages
that will be sent across the sockets for what the expected behavior is for
individual commands sent or received by the program.

###### AuctionHouseAddress
A small class which contains information on the AuctionHouseAddress, storing
the port and ID information for a particular auction house.