package phase2;

import java.io.Serializable;
import java.util.*;

/**
 * An ATM that allows customers and employees to interact with their login accounts.
 * It will display options on the screen and the user will select an option by typing the corresponding number
 * on the keyboard.
 */
class ATM extends Observable implements Serializable {
    /**
     * Allow user to login by entering username and password.
     * <p>
     * It will return the SystemUser account if the login is valid; otherwise, it'll consistently asking user to
     * enter username and password.
     */
    private static SystemUser loginPrompt() {
        System.out.println("Welcome to CSC207 Banking Service.");

        Scanner reader = new Scanner(System.in);

        SystemUser systemUser = null;
        while (systemUser == null) {
            System.out.print("Please enter your username: ");
            String username = reader.next();
            System.out.print("Please enter your password: ");
            String password = reader.next();

            systemUser = LoginManager.verifyLogin(username, password);

        }
        System.out.println("\nSystemUser success. Hi " + systemUser.getUsername() + "!");
        return systemUser;
    }

    /**
     * Preloaded bank manage account: {username: jen, password: 1234}
     * Preloaded customer account: {username: steve, password: 1234}
     */
    public static void main(String[] args) {
        // Load the back up of SystemUser account lists after restarting the ATM.
        LoginManagerBackup load_backup = new LoginManagerBackup();
        LoginManagerBackup backup = load_backup.returnFileBackup();
        LoginManager.login_map = new HashMap<>();
        // Instantiate an Employee account here for basic functions here.
        SystemUser_Employee_BankManager jen = new SystemUser_Employee_BankManager("jen", "1234");
        LoginManager.addLogin(jen);
        if (backup.deleted == 0) {
            LoginManager.login_map = load_backup.returnFileBackup().login_map;

        } else {
            // If the backup was deleted, recreate the default state here.

            jen.createLogin("Customer", "steve", "1234");
            jen.addAccount("Chequing", ((SystemUser_Customer) LoginManager.getLogin("steve")), 1234);
            jen.addAccount("LineOfCredit", ((SystemUser_Customer) LoginManager.getLogin("steve")), 4321);
            jen.addAccount("Saving", ((SystemUser_Customer) LoginManager.getLogin("steve")), 1000);
            jen.addAccount("CreditCard", ((SystemUser_Customer) LoginManager.getLogin("steve")), 420);
        }


        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        int now = (calendar.get(Calendar.MONTH));

        // The ATM should displays SystemUser interface all the time, until it is being shut down.
        //noinspection InfiniteLoopStatement
        while (true) {
            // Constantly checking if now is the start of the month.
            now = new ATMTime().checkMonth(now);


            // A login session.
            SystemUser systemUser = loginPrompt();
            new Options(systemUser);
        }

    }
}