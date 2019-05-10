package bank;

import auctionHouse.AuctionHouse;
import constants.Message;

import java.io.*;
import java.net.Socket;

    /*

    Connection Agent <-> Bank

     */

    /**
     * An instance of this class runs on its own thread, instantiated by
     * Bank, and communicates with a specific agent. It handles and generates
     * all the messages used in correspondence and stores some values that
     * belong to the agent as well.
     * @author Isaac Heflin
     * @Date 5/5/2019
     */

    public class AgentHandler extends Thread {

        private Socket socket;
        private Account account;


        public AgentHandler(Socket socket) {
            this.socket = socket;
        }

        /**
         * Each time a message is sent from the agent, this method is
         * called. Several possibilities for messages exist, and this handles
         * every case for what the agent will potentially send using
         * a switch statement, allowing the bank to respond appropriately.
         *
         * @param message: The message from the agent
         * @throws IOException
         * @throws ClassNotFoundException
         */
        private void processMessage(Message message) throws IOException, ClassNotFoundException {

            switch (message.getCommand()) {

                case "CreateNewAccount":
                    int initialFunds = (Integer) message.splitCommand(1);
                    String nameOfAgent = (String) message.splitCommand(2);
                    this.account = new Account(initialFunds, Bank.accountHashMap);
                    this.account.setName(nameOfAgent);

                    Bank.accountHashMap.put(account.getAccountNum(), account);
                    sendMessageToAgent(new Message("", account.getAccountNum()));
                    break;

                case "SendBlockedMoneyToAuction":
                    int accountNumOfAuction = (Integer) message.splitCommand(1);
                    Account auctionAccount =
                            Bank.accountHashMap.get(accountNumOfAuction);

                    auctionAccount.depositMoney(account.getHeld());
                    account.setNewBalance();

                    break;

                case "ViewCurrentAuctions":
                    sendMessageToAgent(new Message(
                            "", Bank.auctionHouseAddresses));
                    break;

                case "availableBalance":
                    sendMessageToAgent(new Message("", account.getUsableMoney()));
                    break;


                case "totalBalance":
                    sendMessageToAgent(new Message("", account.getMoney()));
                    break;

                case "Terminates":
                    Bank.accountHashMap.remove(account.getAccountNum());
                    break;
            }
        }


        /**
         * A method to compose a message that will be sent to the agent using an
         * object output stream and a Message object.
         *
         * @param message: Message to be sent to the agent
         * @throws IOException
         */
        private void sendMessageToAgent(Message message) throws IOException {
            ObjectOutputStream objOutput = null;

            try {
                objOutput = new ObjectOutputStream(socket.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }

            objOutput.writeObject(message);
        }


        /**
         * This method waits for a message to be sent by an agent, then,
         * once the message is sent, calls the processMessage method to
         * properly deal with the contents. If the message is of termination,
         * the thread dies (peacefully).
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

