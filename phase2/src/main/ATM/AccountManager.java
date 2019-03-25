package ATM;

import java.util.ArrayList;

/**
 * A static class to manage all existing bank accounts.
 */
final class AccountManager {
    // A list of existing bank accounts
    static ArrayList<Account> account_list = new ArrayList<>();

    private static final ATMSystem myATM = new ATMSystem();

    static void addAccount(Account account) {
        account_list.add(account);
    }

    static Account createAccount(String accountType, User_Customer owner, double initialAmount) {

        if (accountType == null) {
            System.out.println("Invalid account type. Account cannot be created.");
            return null;
        }
        if (accountType.equalsIgnoreCase("CHEQUING")) {
            Account_Asset_Chequing newAccount = new Account_Asset_Chequing(initialAmount, owner);
            addAccount(newAccount);
            return newAccount;

        } else if (accountType.equalsIgnoreCase("SAVINGS")) {
            Account_Asset_Saving newAccount = new Account_Asset_Saving(initialAmount, owner);
            myATM.addObserver(newAccount);
            addAccount(newAccount);
            return newAccount;

        } else if (accountType.equalsIgnoreCase("CREDITCARD")) {
            Account_Debt_CreditCard newAccount = new Account_Debt_CreditCard(initialAmount, owner);
            addAccount(newAccount);
            return newAccount;

        } else if (accountType.equalsIgnoreCase("LINEOFCREDIT")) {
            Account_Debt_LineOfCredit newAccount = new Account_Debt_LineOfCredit(initialAmount, owner);
            addAccount(newAccount);
            return newAccount;

        } else if (accountType.equalsIgnoreCase("STUDENT")) {
            Account_Student newAccount = new Account_Student(initialAmount, owner);
            addAccount(newAccount);
            return newAccount;
        }

        return null;
    }

    static ArrayList<Account> getAccount(String username) {
        ArrayList<Account> accountsOwned = new ArrayList<>();
        for (Account ac : account_list) {
            if (ac.getOwners().contains(UserManager.getUser(username))) {
                accountsOwned.add(ac);
            }
        }
        return accountsOwned;
    }

    static void addAccount(String accountType, User_Customer username, @SuppressWarnings("SameParameterValue") double amount) {
        Account newAccount = AccountManager.createAccount(accountType, username, amount);
        if (newAccount != null) {
            username.addAccount(newAccount);
            System.out.println("A " + accountType + " account with $" + amount + " balance is successfully created for "
                    + username.getUsername() + ". ");
        }

    }

    /**
     * Create an account for a Customer. Amount is not initialized here.
     */
    static void addAccount(String accountType, User_Customer username) {
        AccountManager.addAccount(accountType, username, 0);
    }


}