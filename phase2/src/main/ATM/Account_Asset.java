package ATM;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.EmptyStackException;

/**
 * Asset accounts include Chequing and Savings Accounts.
 */
abstract class Account_Asset extends Account implements Account_Transferable {

    Account_Asset(String id, double balance, User_Customer owner) {
        super(id, balance, owner);
    }

    Account_Asset(String id, double balance, User_Customer owner1, User_Customer owner2) {
        super(id, balance, owner1, owner2);
    }

    /**
     * Pay a bill by transferring money to a non-user's account
     *
     * @param amount      transfer amount
     * @param accountName non-user's account name
     * @return true if bill has been payed successfully
     */
    public boolean payBill(double amount, String accountName) throws IOException {
        if (amount > 0 && (balance - amount) >= 0) {
            String message = "\nUser " + this.getPrimaryOwner() + " paid $" + amount + " to " + accountName + " on " +
                    LocalDateTime.now();
            // Open the file for writing and write to it.
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFilePath, true)))) {
                out.println(message);
            }
            balance -= amount;
            updateMostRecentTransaction("PayBill", amount, null);
            transactionHistory.push(new Transaction("PayBill", amount, null));
            return true;
        }
        return false;
    }

    public void undoPaybill(double amount) {
        balance += amount;
    }

    private boolean validWithdrawal(double withdrawalAmount) {
        return withdrawalAmount > 0 && withdrawalAmount % 5 == 0 && balance > 0 &&
                Cash.isThereEnoughBills(withdrawalAmount);
    }

    /**
     * Withdraw money from an account (This will decrease <balance>)
     *
     * @param withdrawalAmount amount to be withdrawn
     * @param condition        additional condition in order to successfully withdraw
     */
    void withdraw(double withdrawalAmount, boolean condition) {
        if (validWithdrawal(withdrawalAmount) && (condition)) {
            balance -= withdrawalAmount;
            Cash.cashWithdrawal(withdrawalAmount);

            updateMostRecentTransaction("Withdrawal", withdrawalAmount, null);
            transactionHistory.push(new Transaction("Withdrawal", withdrawalAmount, null));
        }
    }

    @Override
    void undoWithdrawal(double withdrawalAmount) {
        balance += withdrawalAmount;
        Cash.undoCashWithdrawal(withdrawalAmount);
    }

    @Override
    void deposit(double depositAmount) {
        if (depositAmount > 0) {
            balance += depositAmount;
            updateMostRecentTransaction("Deposit", depositAmount, null);
            transactionHistory.push(new Transaction("Deposit", depositAmount, null));
        } else {
            System.out.println("invalid deposit");
        }
    }

    @Override
    void undoDeposit(double depositAmount) {
        balance -= depositAmount;
    }

    /**
     * Transfer money between accounts the user owns
     *
     * @param transferAmount the amount to be transferred
     * @param account        another account the user owns
     * @return true if transfer was successful
     */
    public boolean transferBetweenAccounts(double transferAmount, Account account) {
        return transferToAnotherUser(transferAmount, (User_Customer) UserManager.getAccount(getPrimaryOwner()), account);
    }

    /**
     * Transfer money from this account to another user's account (this will decrease their balance)
     *
     * @param transferAmount amount to transfer
     * @param user           receives transferAmount
     * @param account        of user
     * @return true iff transfer is valid
     */
    public boolean transferToAnotherUser(double transferAmount, User_Customer user, Account account) {
        if (validTransfer(transferAmount, user, account)) {
            balance -= transferAmount;
            if (account instanceof Account_Asset) {
                account.balance += transferAmount;
            } else {
                account.balance -= transferAmount;
            }
            if (user == UserManager.getAccount(getPrimaryOwner())) {
                updateMostRecentTransaction("TransferBetweenAccounts", transferAmount, account);
                transactionHistory.push(new Transaction("TransferBetweenAccounts", transferAmount, account));
            } else {
                updateMostRecentTransaction("TransferToAnotherUser", transferAmount, account);
                transactionHistory.push(new Transaction("TransferToAnotherUser", transferAmount, account));
            }
            return true;
        }
        return false;
    }

    private void undoTransfer(double transferAmount, Account account) {
        balance += transferAmount;
        if (account instanceof Account_Asset) {
            account.balance -= transferAmount;
        } else {
            account.balance += transferAmount;
        }

    }

    private boolean validTransfer(double transferAmount, User_Customer user, Account account) {
        //TODO: any login user should have a hasAccount method
        return transferAmount > 0 && (balance - transferAmount) >= 0 && user.hasAccount(account);
    }

    @Override
    void undoMostRecentTransaction() {
        super.undoMostRecentTransaction();
        if (getMostRecentTransaction().get("Type").equals("TransferBetweenAccounts") ||
                getMostRecentTransaction().get("Type").equals("TransferToAnotherUser")) {
            undoTransfer((Double) getMostRecentTransaction().get("Amount"), (Account) getMostRecentTransaction().get("Account"));
        }
        //TODO: how about pay bill?
    }

    @Override
    void undoTransactions(int n) {
        if (n > 0) {
            for (int i = 0; i < n; i++) {
                try {
                    Transaction transaction = transactionHistory.pop();
                    String type = transaction.getType();

                    if (transaction.getType().equals("Withdrawal")) {
                        undoWithdrawal(transaction.getAmount());
                    } else if (transaction.getType().equals("Deposit")) {
                        undoDeposit(transaction.getAmount());
                    } else if (transaction.getType().equals("TransferBetweenAccounts") ||
                            transaction.getType().equals("TransferToAnotherUser")) {
                        undoTransfer(transaction.getAmount(), transaction.getAccount());
                    } else if (type.equals("PayBill")) {
                        undoPaybill(transaction.getAmount());
                    }

                } catch (EmptyStackException e) {
                    System.out.println("All transactions on this account have been undone");
                }
            }
        }
    }
}
