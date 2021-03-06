package phase1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class for handling cash storage, withdrawal, deposit of $5, $10, $20, and $50 bills.
 * This is a utility/helper class.
 */
final class Cash {

    /**
     * Map denomination to quantity
     * Cash initially starts with fifty bills of every denomination
     */
    private static final HashMap<String, Integer> bills = new HashMap<String, Integer>() {
        {
            put("five", 50);
            put("ten", 50);
            put("twenty", 50);
            put("fifty", 50);
        }
    };
    private static final String outputFilePath = "phase1/alerts.txt";

    /**
     * Check the quantity of denominations
     *
     * @return true iff amount of any denomination goes below 20
     */
    static private boolean isAmountBelowTwenty() {
        for (int n : bills.values()) {
            if (n < 20) {
                return true;
            }
        }
        return false;
    }

    /**
     * Send an alert to alerts.txt iff isAmountBelowTwenty
     */
    static private void checkDenom() {
        if (isAmountBelowTwenty()) {
            try {
                sendAlert();
            } catch (IOException e) {
                // do nothing?
            }
        }
    }

    static private void sendAlert() throws IOException {
        // Open the file for writing and write to it.
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFilePath, true)))) {
            out.println(bills);
        }
    }

    /*
    When bank manager restocks the machine
    cashList: [fives, tens, twenties, fifties]
     */
    static void cashDeposit(ArrayList<Integer> cashList) {
        bills.put("five", bills.get("five") + cashList.get(0));
        bills.put("ten", bills.get("ten") + cashList.get(1));
        bills.put("twenty", bills.get("twenty") + cashList.get(2));
        bills.put("fifty", bills.get("fifty") + cashList.get(3));
    }

    /**
     * return a List of cash that contains the number of bills that will be withdrawn
     * according to the withdrawal amount and the inventory.
     * [fifty, twenty, ten, five]
     */
    static private ArrayList<Integer> verifyCashWithdrawal(double amount) {
        double remainder = amount;
        ArrayList<Integer> cashList = new ArrayList<>();

        int fiftyWithdrawn = Math.min((int) Math.floor(remainder / 50), bills.get("fifty"));
        cashList.add(fiftyWithdrawn);
        remainder -= fiftyWithdrawn * 50;

        int twentyWithdrawn = Math.min((int) Math.floor(remainder / 20), bills.get("twenty"));
        cashList.add(twentyWithdrawn);
        remainder -= twentyWithdrawn * 20;

        int tenWithdrawn = Math.min((int) Math.floor(remainder / 10), bills.get("ten"));
        cashList.add(tenWithdrawn);
        remainder -= tenWithdrawn * 10;

        int fiveWithdrawn = Math.min((int) Math.floor(remainder / 5), bills.get("five"));
        cashList.add(fiveWithdrawn);

        return cashList;
    }

    /**
     * In order for a withdrawal to take place, there must be enough bills to give out.
     *
     * @param amount withdrawal amount
     * @return true iff there is enough bills for amount
     */
    static boolean isThereEnoughBills(double amount) {
        ArrayList<Integer> numberOfBills = verifyCashWithdrawal(amount);
        double total = numberOfBills.get(0) * 50 + numberOfBills.get(1) * 20 + numberOfBills.get(2) * 10 +
                numberOfBills.get(3) * 5;
        return amount == total;
    }

    /**
     * Cash withdrawal. The number of different bills are used in
     * withdrawal depending on the withdrawal amount and the inventory.
     * Update quantity of denominations.
     */
    static void cashWithdrawal(double amount) {
        double remainder = amount;

        // The number of a specific bill withdrawn should be the smaller integer of either the amount of the
        // specific bill that needed to be withdrawn or the inventory of that bill.
        int fiftyWithdrawn = Math.min((int) Math.floor(remainder / 50), bills.get("fifty"));
        bills.put("fifty", bills.get("fifty") - fiftyWithdrawn);
        remainder -= fiftyWithdrawn * 50;

        int twentyWithdrawn = Math.min((int) Math.floor(remainder / 20), bills.get("twenty"));
        bills.put("twenty", bills.get("twenty") - twentyWithdrawn);
        remainder -= twentyWithdrawn * 20;

        int tenWithdrawn = Math.min((int) Math.floor(remainder / 10), bills.get("ten"));
        bills.put("ten", bills.get("ten") - tenWithdrawn);
        remainder -= tenWithdrawn * 10;

        int fiveWithdrawn = Math.min((int) Math.floor(remainder / 5), bills.get("five"));
        bills.put("five", bills.get("five") - fiveWithdrawn);

        checkDenom();
        int totalAmount = fiftyWithdrawn * 50 + twentyWithdrawn * 20 + tenWithdrawn * 10 + fiveWithdrawn * 5;
        System.out.println("\nTotal amount of $" + totalAmount + ": " + fiftyWithdrawn + " fifty-dollar bills, " +
                twentyWithdrawn + " twenty-dollar bills, " + tenWithdrawn + " ten-dollar bills, " + fiveWithdrawn +
                " five-dollar bills have be withdrawn. ");
        System.out.println("Please note that the actual withdrawal amount may be differ " +
                "due to the fact that five-dollar note is the lowest denomination");
    }

    static void undoCashWithdrawal(double amount) {
        double remainder = amount;

        // The number of a specific bill withdrawn should be the smaller integer of either the amount of the
        // specific bill that needed to be withdrawn or the inventory of that bill.
        int fiftyWithdrawn = Math.min((int) Math.floor(remainder / 50), bills.get("fifty"));
        bills.put("fifty", bills.get("fifty") + fiftyWithdrawn);
        remainder -= fiftyWithdrawn * 50;

        int twentyWithdrawn = Math.min((int) Math.floor(remainder / 20), bills.get("twenty"));
        bills.put("twenty", bills.get("twenty") + twentyWithdrawn);
        remainder -= twentyWithdrawn * 20;

        int tenWithdrawn = Math.min((int) Math.floor(remainder / 10), bills.get("ten"));
        bills.put("ten", bills.get("ten") + tenWithdrawn);
        remainder -= tenWithdrawn * 10;

        int fiveWithdrawn = Math.min((int) Math.floor(remainder / 5), bills.get("five"));
        bills.put("five", bills.get("five") + fiveWithdrawn);

    }
}
