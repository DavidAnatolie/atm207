package phase2;

import java.util.Observable;
import java.util.Observer;

abstract class Account_Student extends Account implements Account_Transferable {
    int transactions;
    int maxTransactions;
    int transferLimit;
    int transferTotal;

    // Transactions, student account has maximum 20 transfers that they can have
    // TODO: Interest, age, email
    // Default 20 transactions, 250 transferTotal
    Account_Student(double balance, SystemUser_Customer owner) {
        super(balance, owner);
        this.transactions = 20;
        this.transferTotal = 250;
    }

    Account_Student(double balance, SystemUser_Customer owner1, SystemUser_Customer owner2) {
        super(balance, owner1, owner2);
    }

    public void update(Observable o, Object arg) {
        if ((boolean) arg) {
            transferLimit = 0;
            transactions = 0;
        }
    }


    private boolean validWithdrawal(double withdrawalAmount) {
        return withdrawalAmount > 0 && withdrawalAmount % 5 == 0 && balance > 0 &&
                Cash.isThereEnoughBills(withdrawalAmount) && (transactions < maxTransactions);
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
            transactions += 1;
            updateMostRecentTransaction("Withdrawal", withdrawalAmount, null);
        }
    }
    /**
     * Sets the monthly Transactions that a Student Account has.
     *
     * @param transactionsAmount the set amount of transactions
     */
    public void setMaxTransactions(int transactionsAmount) {
        maxTransactions = transactionsAmount;
    }

    /**
     * Sets the monthly amount that a Student Account has for allowance of transfers.
     *
     * @param transferLimitAmount the set amount of transfers
     */
     public void setTransferLimit(int transferLimitAmount) {
        transferLimit = transferLimitAmount;
    }


    @Override
    void undoWithdrawal(double withdrawalAmount) {
        balance += withdrawalAmount;
        transactions -= 1;
        Cash.undoCashWithdrawal(withdrawalAmount);
    }

    @Override
    void deposit(double depositAmount) {
        if ((depositAmount > 0) && (transactions < maxTransactions)) {
            balance += depositAmount;
            transactions += 1;
            updateMostRecentTransaction("Deposit", depositAmount, null);
        } else {
            System.out.println("invalid deposit");
        }
    }

    @Override
    void undoDeposit(double depositAmount) {
        balance -= depositAmount;
        transactions -= 1;
    }

    /**
     * Transfer money between accounts the user owns
     *
     * @param transferAmount the amount to be transferred
     * @param account        another account the user owns
     * @return true if transfer was successful
     */
    public boolean transferBetweenAccounts(double transferAmount, Account account) {
        transactions += 1;
        transferTotal += transferAmount;
        return transferToAnotherUser(transferAmount, getOwner(), account);

    }

    /**
     * Transfer money from this account to another user's account (this will decrease their balance)
     *
     * @param transferAmount amount to transfer
     * @param user           receives transferAmount
     * @param account        of user
     * @return true iff transfer is valid
     */
    public boolean transferToAnotherUser(double transferAmount, SystemUser_Customer user, Account account) {
        if (validTransfer(transferAmount, user, account)) {
            balance -= transferAmount;
            if (account instanceof Account_Asset) {
                account.balance += transferAmount;
            } else {
                account.balance -= transferAmount;
            }
            if (user == getOwner()) {
                updateMostRecentTransaction("TransferBetweenAccounts", transferAmount, account);
            } else {
                updateMostRecentTransaction("TransferToAnotherUser", transferAmount, account);
            }
            transactions += 1;
            return true;
        }
        return false;
    }

    private void undoTransfer(double transferAmount, Account account) {
        balance += transferAmount;
        transactions -= 1;
        transferTotal -= transferAmount;
        if (account instanceof Account_Asset) {
            account.balance -= transferAmount;
        } else {
            account.balance += transferAmount;
        }

    }

    private boolean validTransfer(double transferAmount, SystemUser_Customer user, Account account) {
        return transferAmount > 0 && (balance - transferAmount) >= 0 && user.hasAccount(account) &&
                (transactions < maxTransactions) && (transferAmount + transferTotal < transferLimit);
    }

    @Override
    void undoMostRecentTransaction() {
        super.undoMostRecentTransaction();
        transactions += 1;
        if (getMostRecentTransaction().get("Type").equals("TransferBetweenAccounts") ||
                getMostRecentTransaction().get("Type").equals("TransferToAnotherUser")) {
            undoTransfer((Double) getMostRecentTransaction().get("Amount"), (Account) getMostRecentTransaction().get("Account"));
        }
    }
    public String toString() {
        String mostRecentTransactionString;

        if (getMostRecentTransaction().get("Type") == "Withdrawal") {
            mostRecentTransactionString = "$" + getMostRecentTransaction().get("Amount") + " withdrawn.";
        } else if (getMostRecentTransaction().get("Type") == "Deposit") {
            mostRecentTransactionString = "$" + getMostRecentTransaction().get("Amount") + " deposited.";
        } else {
            mostRecentTransactionString = "n/a";
        }

        return "Student\t\t\t" + dateOfCreation + "\t" + balance + "\t\t" + mostRecentTransactionString;
    }
}