package phase1;

class Account_Debt_LineOfCredit extends Account_Debt {
    /*
     * A line of credit account allows you to transfer money in our out. But it also displays a positive balance
     * when the user owes money and a negative balance when the user overpays
     */

    /**
     * Balance is set to 0.00 as default if an initial balance is not provided.
     */
    Account_Debt_LineOfCredit(Login_Customer owner, double amount) {
        super(amount, owner);
    }
    /** LineOfCreditAccount Balance */
    private double accountBalance;

    public Account_Debt_LineOfCredit(Login_Customer owner) {
        this(owner, 0);
    }

    public double withdraw(double withdrawalAmount) {
        accountBalance -= withdrawalAmount;
        return withdrawalAmount;
    }

    public void deposit(double depositAmount) {
        accountBalance += depositAmount;
    }

    public String viewBalance() {
        String stringBalance = Double.toString(-accountBalance);
        stringBalance = "$" + stringBalance;
        return stringBalance;
    }
    public Account_Debt_LineOfCredit(double balance, Login_Customer owner) {
        super(balance, owner);
    }


    void transfer(double transferAmount, Account transferAccount, Login_Customer transferUser) {
        for (Account i : transferUser.getAccounts()) {
            if (i == transferAccount) {
                balance -= transferAmount;
                i.deposit(transferAmount);
            }
        }
    }

//    public void transferOut(double transferAmount, Account transferAccount, Login_Customer transferUser) {
//        for (int i : (transferUser.getAccounts())) {
//            if (transferUser.accounts(i) == transferAccount) {
//                accountBalance -= transferAmount;
//                (transferUser.accounts(i)).accountBalance += transferAmount;
//            }
//        }
//    }
//
//    public void transferBetween(double transferAmount, Account transferAccount) {
//        for (int i : ((this.user).accounts).length) {
//            if ((this.user).accounts(i) == transferAccount) {
//                accountBalance -= transferAmount;
//                ((this.user).accounts(i)).accountBalance += transferAmount;
//            }
//        }
}
