package bank;

import java.util.HashMap;

/**
 * This object has an instance for each agent or auction house
 * entity. This is where values for the money and available balance is
 * stored, as well as methods to retrieve them.
 * @author Isaac Heflin
 * @Date 5/5/2019
 */

public class Account {

    private final int MAX_NUM = 10000;


    private int money, blockFunds;
    private int accountNum;
    String name;

    public Account(int money, HashMap<Integer, Account> allAccounts) {
        this.money = money;

        setUniqueID(allAccounts);
    }

    public void depositMoney(int money){
        this.money += money;
    }

    /**
     * Because the ID must be unique, the list of all account numbers is passed
     * to this method to confirm the ID doesn't already exist.
     * @param allAccounts: List of all accounts which exist at the moment the
     *                     method is called
     */
    private void setUniqueID(HashMap<Integer, Account> allAccounts) {
        int idNum;

        do idNum = (int)(Math.random()* MAX_NUM);
        while(allAccounts.containsKey(idNum));

        this.accountNum = idNum;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setBlockFunds(int blockFunds){
        this.blockFunds += blockFunds;
        if(blockFunds < 0){
            this.blockFunds = 0;
        }
    }

    public void setUnBlockFunds(int unBlockFunds){
        this.blockFunds -= unBlockFunds;
        if(blockFunds < 0){
            this.blockFunds = 0;
        }
    }

    public void setNewBalance() {
        this.money = this.money - this.blockFunds;
        this.blockFunds = 0;
    }

    public int getUsableMoney() {

        return (money - blockFunds);
    }

    public int getMoney() {
        return this.money;
    }

    public int getHeld() {
        return this.blockFunds;
    }


    public int getAccountNum() { return this.accountNum; }

}
