package auctionHouse;

import java.util.ArrayList;
import java.util.Collections;


public class AuctionList {

    /**
     * List of all possible auctions
     */
    private String[][] auctions = {{"chair", "50"}, {"mattress", "100"}, {"computer", "200"},
            {"phone", "150"}, {"refrigerator", "200"}, {"headphones", "75"},
            {"microphone", "100"}, {"monitor", "150"}, {"peanutbutter", "300"},
            {"apple", "50"}, {"tomato", "75"}, {"detergent", "55"},
            {"waterbottle", "70"}, {"febreeze", "50"}, {"microwave", "125"},
            {"lamp", "50"}, {"tv", "50"}, {"cable", "75"},
            {"ipad", "150"}, {"gloves", "75"}, {"shirt", "80"},
            {"jeans", "50"}, {"lumberaxe", "50"}, {"pickaxe", "50"},
            {"sword", "50"}, {"brick", "100"}, {"cement", "80"},
            {"shoes", "90"}, {"book", "50"}, {"trinity_force", "40"}, {"blue_shell", "40"}, {"gravity_gun", "50"},
            {"metal_gear", "50"}};

    public AuctionList() {

    }

    /**
     *Generates all possible auctions using the auctions [][] array
     * @return
     */
    public ArrayList<AuctionedItems> generateAuctions() {
        ArrayList<AuctionedItems> list = new ArrayList<>();

        for (int i = 0; i < auctions.length; i++) {
            list.add(new AuctionedItems(auctions[i][0], Integer.parseInt(auctions[i][1])));
            System.out.println("Successfully generated auction of '" + auctions[i][0] +
                    "' with bid '" + auctions[i][1] + "'");
        }

        Collections.shuffle(list);
        return list;


    }

}
