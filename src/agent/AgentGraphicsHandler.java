package agent;

import auctionHouse.AuctionedItems;
import bank.AuctionInfo;
import constants.AuctionHouseAddress;
import constants.Message;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

/**
 * This class creates the graphical user interface for the agent so agent
 * can interact with bank and the auction house
 *
 * @author Anmol Baniya
 * @Date 5/4/2019
 */
public class AgentGraphicsHandler extends Application {

    private Agent agent;
    private static String agentName;
    private ListView<HBox> listViewOfItems;
    private ListView listViewOfAddresses;
    private Stage stage;
    private Text agentText;
    private Button auctionHouseList;
    private Scene scene;
    private Button totalBalance;
    private Button totalAvailableBalance;
    private Text totalBalanceText;
    private Text availableBalanceText;
    private static final boolean LOCAL = false;
    private boolean auctionListClicked;
    private boolean auctionHouseClicked;
    private ArrayList<Integer> indexOfhboxInListView;
    private static String host = "";
    private static int port;

    /**
     * This method starts the application and intitalizes the member
     * variables
     *
     * @param primaryStage stage(Window) of the application
     * @throws Exception
     */
    public void start(Stage primaryStage) throws Exception {
        this.auctionListClicked = false;
        this.auctionHouseClicked = false;
        this.listViewOfItems = new ListView();
        this.indexOfhboxInListView = new ArrayList<>();
        this.listViewOfAddresses = new ListView();
        this.listViewOfAddresses.setPrefSize(200, 438);

        this.listViewOfItems = new ListView();
        this.listViewOfItems.setPrefSize(1000, 500);
        this.stage = primaryStage;

        Random random = new Random();
        String[] agents = new String[]{"Alex", "Hrithik", "Isaac"};

        int randomNum = random.nextInt(1000);
        this.agentName = agents[random.nextInt(agents.length)] + randomNum;

        this.agentText = new Text(agentName);
        this.agentText.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        this.agentText.setFill(Color.WHITE);

        this.stage.setOnCloseRequest((WindowEvent t) -> {
                    try {
                        if (agent.getAvailableBalance() != agent.getTotalBalance()) {
                            t.consume();
                        } else {

                            Platform.exit();
                            System.exit(0);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        );

        int initialBalance = random.nextInt(1000) * 100;

        Socket socket = new Socket(host, port);
        System.out.println("Anmol");
        displayUserInterface();
        agent = new Agent(socket, agentName, initialBalance);

    }

    /**
     * It updates the scence of the application whenever any new changes happen
     * It does not creates a new scence. It just reset new anchor pane object
     * to the same scene
     *
     * @param anchorPane
     */
    public void updateScene(AnchorPane anchorPane) {

        anchorPane.getChildren().addAll(agentText, auctionHouseList, totalBalance,
                totalAvailableBalance);

        AnchorPane.setTopAnchor(agentText, 0.0);
        AnchorPane.setLeftAnchor(agentText, 650.0);
        AnchorPane.setTopAnchor(auctionHouseList, 80.0);
        AnchorPane.setRightAnchor(auctionHouseList, 80.0);


        AnchorPane.setBottomAnchor(totalBalance, 30.0);
        AnchorPane.setLeftAnchor(totalBalance, 150.0);
        AnchorPane.setLeftAnchor(totalAvailableBalance, 400.0);
        AnchorPane.setBottomAnchor(totalAvailableBalance, 30.0);

        anchorPane.setStyle("-fx-background-color: red;");

        this.scene.setRoot(anchorPane);
        this.stage.setScene(scene);
        this.stage.show();


    }


    /**
     * It updates the list of auction houses and list of items being auctioned
     *
     * @param anchorPane new root with update list of auction houses and items
     */
    public void updateAuctionListsAndItems(AnchorPane anchorPane) {
        if (auctionHouseClicked) {
            anchorPane.getChildren().add(listViewOfItems);
            AnchorPane.setLeftAnchor(listViewOfItems, 40.0);
            AnchorPane.setTopAnchor(listViewOfItems, 80.0);
        }
        if (auctionListClicked) {
            AnchorPane.setRightAnchor(listViewOfAddresses, 40.0);
            AnchorPane.setTopAnchor(listViewOfAddresses, 140.0);
            anchorPane.getChildren().add(listViewOfAddresses);
        }
    }

    /**
     * It styles the button
     *
     * @param button to be styled
     */
    public void styleButton(Button button) {

        button.setStyle("-fx-background-color: red;" + "-fx-text-fill: white;");

    }

    /**
     * It styles the text
     *
     * @param text text to be styled
     */
    public void styleText(Text text) {
        text.setFill(Color.BLACK);
        text.setFont(Font.font("Arial", FontWeight.BOLD, 12));
    }

    /**
     * This method opens the bank account of the agent when clicked
     *
     * @param openBankAccount button to be clicked
     */
    public void executeOpenAccountButton(Button openBankAccount) {


        openBankAccount.setOnAction(e -> {

            try {
                agent.openBankAccount();

            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
            AnchorPane newAnchorPane = new AnchorPane();
            updateScene(newAnchorPane);
            this.agentText.setText("Agent: "+agent.getAgentName());

        });

    }

    /**
     * This method gives the total balance agent has when clicked on total balance button
     */
    public void executeTotalBalanceButton() {
        this.totalBalance.setOnAction(e -> {

            try {
                totalBalanceText = new Text("$" + agent.getTotalBalance());
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }

            totalBalanceText.setFill(Color.WHITE);
            totalBalanceText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            AnchorPane anchorPane1 = new AnchorPane();
            AnchorPane.setLeftAnchor(totalBalanceText, 250.0);
            AnchorPane.setBottomAnchor(totalBalanceText, 30.0);
            updateAuctionListsAndItems(anchorPane1);
            anchorPane1.getChildren().addAll(totalBalanceText);
            updateScene(anchorPane1);

        });

    }

    /**
     * This method gives the actual available balance agent has, when clicked in
     * available balance button
     */
    public void executeAvailableBalanceButton() {
        this.totalAvailableBalance.setOnAction(e -> {
            try {
                availableBalanceText = new Text("$" + agent.getAvailableBalance());
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
            availableBalanceText.setFill(Color.WHITE);
            availableBalanceText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            AnchorPane anchorPane2 = new AnchorPane();
            AnchorPane.setBottomAnchor(availableBalanceText, 30.0);
            AnchorPane.setLeftAnchor(availableBalanceText, 600.0);
            updateAuctionListsAndItems(anchorPane2);

            anchorPane2.getChildren().addAll(availableBalanceText);
            updateScene(anchorPane2);
        });

    }

    /**
     * This method gives the list of item that auction house is auctioning
     *
     * @param getItem             button that gives the list of items being auctioned
     * @param auctionAcct         account number of auction house
     * @param auctionHouseAddress address of the auction
     */
    public void executeGetItemButton(Button getItem, int auctionAcct, AuctionHouseAddress auctionHouseAddress) {


        getItem.setOnAction(e -> {


            String ipAddress = auctionHouseAddress.getIpAddress();
            int portNumber = auctionHouseAddress.getPortNumber();


            try {
                Socket socket = new Socket(ipAddress, portNumber);


                ArrayList<AuctionedItems> listOfAuctionedItems = agent.getListOfAuctionedItems(socket);
                int indexValue = resetItem(auctionAcct);
                if (indexValue == -1) {
                    for (AuctionedItems item : listOfAuctionedItems) {
                        System.out.println("Anmol " + item.getName());
                    }


                    createItemInformation(getItem.getText(), auctionAcct, -1,
                            listOfAuctionedItems, socket);
                } else {
                    for (AuctionedItems item : listOfAuctionedItems) {
                        System.out.println("Alex " + item.getName());
                    }

                    createItemInformation(getItem.getText(), auctionAcct, indexValue + 1,
                            listOfAuctionedItems, socket);
                }


                this.auctionHouseClicked = true;

                AnchorPane anchorPaneForList = new AnchorPane();
                AnchorPane.setLeftAnchor(listViewOfItems, 40.0);

                AnchorPane.setTopAnchor(listViewOfItems, 80.0);
                AnchorPane.setRightAnchor(listViewOfAddresses, 40.0);
                AnchorPane.setTopAnchor(listViewOfAddresses, 140.0);
                anchorPaneForList.getChildren().addAll(listViewOfAddresses, listViewOfItems);

                updateScene(anchorPaneForList);

            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            } catch (ClassNotFoundException e3) {
                e3.printStackTrace();
            }

        });
    }

    /**
     * It executes the place bid button when the user clicks on the place bid button
     *
     * @param auctionedItems Item on which bid was placed
     * @param auctionAcct    account number of the auction house that is auctioning the item
     * @param bidField       bid field that gives the bid amount
     * @param placeBid       button to place the bid
     * @param socket         socket to communicate with the auction house
     */
    public void executePlaceBidButton(AuctionedItems auctionedItems, int auctionAcct, TextField bidField,
                                      Button placeBid, Socket socket) {
        placeBid.setOnAction(e -> {
            try {
                agent.placeBid(auctionedItems, Integer.parseInt(bidField.getText()), socket);
                AnchorPane anchorPane = new AnchorPane();
                updateScene(anchorPane);
                updateAuctionListsAndItems(anchorPane);
                updateBidStatus(socket, auctionAcct, placeBid);

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }


    /**
     * It display the user interface of the application
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void displayUserInterface() throws IOException, ClassNotFoundException {

        AnchorPane anchorPane = new AnchorPane();
        Button openBankAccount = new Button("Open Bank Account");


        this.totalBalance = new Button("Total Balance");
        this.totalAvailableBalance = new Button("Total Available Balance");


        executeOpenAccountButton(openBankAccount);
        executeTotalBalanceButton();
        executeAvailableBalanceButton();


        this.auctionHouseList = new Button("AuctionHouses List");

        this.auctionHouseList.setOnAction(e -> {


            HashMap<AuctionInfo, AuctionHouseAddress> houseAddressHashMap = null;
            try {
                houseAddressHashMap = agent.getAuctionHousesList();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
            Text id = new Text("ID");
            styleText(id);

            Text accountNum = new Text("Account Number");
            styleText(accountNum);
            this.listViewOfAddresses = new ListView();
            this.listViewOfAddresses.setPrefSize(200, 438);
            HBox hBox = new HBox(70);
            hBox.getChildren().addAll(id, accountNum);
            this.listViewOfAddresses.getItems().add(hBox);

            for (AuctionInfo auctionInfo : houseAddressHashMap.keySet()) {

                Button buttonId = new Button(auctionInfo.getAuctionID());
                Text accountNumb = new Text("" + auctionInfo.getAccountNum());
                styleText(accountNumb);
                styleButton(buttonId);
                HBox hboxIDAndAcct = new HBox(50);
                hboxIDAndAcct.getChildren().addAll(buttonId, accountNumb);
                listViewOfAddresses.getItems().add(hboxIDAndAcct);

                executeGetItemButton(buttonId, auctionInfo.getAccountNum(), houseAddressHashMap.get(auctionInfo));
                AnchorPane anchorPaneForList = new AnchorPane();

                if (auctionListClicked && auctionHouseClicked) {

                    AnchorPane.setLeftAnchor(listViewOfItems, 40.0);
                    AnchorPane.setTopAnchor(listViewOfItems, 80.0);
                    anchorPaneForList.getChildren().add(listViewOfItems);
                }


                AnchorPane.setRightAnchor(listViewOfAddresses, 40.0);
                AnchorPane.setTopAnchor(listViewOfAddresses, 140.0);
                anchorPaneForList.getChildren().add(listViewOfAddresses);


                updateScene(anchorPaneForList);
                auctionListClicked = true;
            }
        });

        anchorPane.getChildren().addAll(agentText, openBankAccount);


        AnchorPane.setTopAnchor(agentText, 0.0);
        AnchorPane.setLeftAnchor(agentText, 650.0);
        AnchorPane.setTopAnchor(auctionHouseList, 100.0);
        AnchorPane.setRightAnchor(auctionHouseList, 80.0);
        AnchorPane.setTopAnchor(openBankAccount, 300.0);
        AnchorPane.setLeftAnchor(openBankAccount, 630.0);

        anchorPane.setStyle("-fx-background-color: red;");


        this.scene = new Scene(anchorPane, 1350, 680, Color.RED);
        this.stage.setScene(scene);
        this.stage.show();


    }

    private int resetItem(int acctNum) {

        return indexOfhboxInListView.indexOf(acctNum);
    }


    /**
     * This method creates information about the item that auction is auctioning
     * It creates information like current bid, minimum bid, item name, and bid status
     *
     * @param auctionId   the id of the auction that is auctioning the item
     * @param auctionAcct the account of the auction that is auctioning the item
     * @param i           Index value of the item in the listView
     * @param listOfItems List of item being auctioned. It will always be 3.
     * @param socket      Socket for communicating with the auction house
     */

    public void createItemInformation(String auctionId, int auctionAcct, int i, ArrayList<AuctionedItems> listOfItems,
                                      Socket socket) {
        int index = listViewOfItems.getItems().size();
        Text id = new Text(auctionId);
        id.setFill(Color.RED);
        id.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        HBox hBoxText = new HBox();
        hBoxText.getChildren().add(id);
        hBoxText.setId("Text");

        if (i == -1) {
            listViewOfItems.getItems().add(index, hBoxText);
        }


        for (AuctionedItems auctionedItems : listOfItems) {

            HBox hBox = new HBox(15);
            Text item = new Text("Item Name");
            styleText(item);


            Text itemName = new Text(auctionedItems.getName());
            styleText(itemName);

            Text minimum = new Text("Minimum Bid");
            styleText(minimum);
            Text minimumBid = new Text(auctionedItems.getMinimumBid() + "");
            styleText(minimumBid);

            Text current = new Text("Current Bid");
            styleText(current);

            Text currentBid = new Text(auctionedItems.getCurrentBid() + "");
            styleText(currentBid);

            Text bidAmount = new Text("Bid Amount");
            styleText(bidAmount);

            TextField bidField = new TextField();


            Button placeBid = new Button("Place Bid");
            styleButton(placeBid);

            executePlaceBidButton(auctionedItems, auctionAcct, bidField, placeBid, socket);

            Text bidStatus = new Text("Bid Status");
            styleText(bidStatus);

            Text statusMessage = new Text(auctionedItems.getBidStatus());
            styleText(statusMessage);

            hBox.getChildren().addAll(item, itemName, minimum, minimumBid, current, currentBid, bidAmount,
                    bidField, placeBid, bidStatus, statusMessage);
            hBox.setId(auctionedItems.getName());

            if (indexOfhboxInListView.contains(auctionAcct)) {
                listViewOfItems.getItems().set(i, hBox);
                i++;
            } else {
                listViewOfItems.getItems().add(index + 1, hBox);
                index++;


            }


        }

        if (i == -1) {
            for (int j = 0; j < 4; j++) {
                indexOfhboxInListView.add(auctionAcct);
            }
        }


    }

    /**
     * This method updates the bid status of each of items auction house is
     * auctioning
     *
     * @param socket         socket for communication with the auction house
     * @param auctionAcctNum account number of the auction house
     * @param bidButton      bid button that was clicked
     */
    public void updateBidStatus(Socket socket, int auctionAcctNum, Button bidButton) {

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {

            try {
                ArrayList<Message> arrayListMessage = agent.getStatusOfBid(socket);
                for (Message message : arrayListMessage) {
                    for (HBox hBox : listViewOfItems.getItems()) {

                        if (message.splitCommand(1).equals(hBox.getId())) {
                            if (message.splitCommand(3).equals("You Won this Item")) {
                                bidButton.setDisable(true);
                                agent.notifiesBankToTransferBlockedFunds(auctionAcctNum);

                            }
                            if (message.splitCommand(3).equals("You lost the item")) {
                                bidButton.setDisable(true);
                            }
                            Text currentBid = new Text(message.splitCommand(2) + "");
                            styleText(currentBid);
                            hBox.getChildren().set(5, currentBid);
                            Text statusMessage = new Text(message.splitCommand(3) + "");
                            styleText(statusMessage);
                            hBox.getChildren().set(10, statusMessage);


                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }

    /**
     * This is the first method that gets executed
     *
     * @param args Bank_ip address and port
     */
    public static void main(String[] args) {
        if (args.length != 2 && !LOCAL) {
            System.out.println("Wrong Arguments! \n" +
                    "Usage: java -jar Agent.jar BANK_IP_ADDRESS BANK_PORT");
            System.exit(0);
        }
        if (LOCAL) {
            host = "localhost";
            port = 8080;
        } else {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }
        launch(args);
    }


}
