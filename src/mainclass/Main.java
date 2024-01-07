package mainclass;

import datahandling.BankDataHandler;
import loggingclasses.Log;
import loggingclasses.MenuHelper;
import modelclasses.BankAccount;
import operationclasses.OperationsService;

import java.util.InputMismatchException;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    //TODO add configuration files to this project
    private static final int LEAVE = 0;
    private static final int CREATE_ACCOUNT = 1;
    private static final int LOG_IN = 2;
    private static final int TRANSFER = 1;
    private static final int WITHDRAWAL = 2;
    private static final int DEPOSIT = 3;
    private static final int BLOCK_CARD = 4;
    private static final int CARD_INFO = 5;
    private static final String BANK_ACCOUNTS_FILE_PATH = "resources/BankAccounts.ser";
    private static final String TRANSACTIONS_FILE_PATH = "resources/AllTransactions.ser";
    private static final BankDataHandler BANK_ACCOUNTS_HANDLER = new BankDataHandler(
            BANK_ACCOUNTS_FILE_PATH,
            TRANSACTIONS_FILE_PATH
    );
    private static final OperationsService OPERATIONS_SERVICE = new OperationsService(BANK_ACCOUNTS_HANDLER);
    private static BankAccount loggedAccount;

    public static void main(final String[] args) {
        Scanner scan = new Scanner(System.in);
        int choice;
        do {
            MenuHelper.printInitialMenu();
            choice = scan.nextInt();
            switch (choice) {
                case LEAVE:
                    Log.printLeavingMessage();
                    break;
                case CREATE_ACCOUNT:
                    createAccount(scan);
                    break;
                case LOG_IN:
                    logIn(scan);
                    loggedMenu(scan);
                    break;
                default:
                    Log.printChoiceErrorMessage();
                    break;
            }
        } while (choice != LEAVE);
    }

    private static void createAccount(Scanner scan) {
        Log.printAccountsHoldersNameQuestion();
        scan.nextLine();
        String accountHoldersName = scan.nextLine();
        OPERATIONS_SERVICE.createAccount(accountHoldersName);
    }

    private static void logIn(Scanner scan) {
        try {
            Log.printYourAccountNumberQuestion();
            int accountNumber = scan.nextInt();
            Log.printPasswordQuestion();
            int password = scan.nextInt();
            Optional<BankAccount> account = OPERATIONS_SERVICE.logIn(accountNumber,password);
            loggedAccount = account.orElse(null);
            if (loggedAccount == null) {
                Log.printLogInError();
            } else {
                Log.printLogInSuccess();
            }
        } catch (InputMismatchException error) {
            System.err.println("You need to write a valid account NUMBER");
        }
    }
    private static void loggedMenu(Scanner scan) {
        int choice;
        do {
            if (loggedAccount == null) {
                break;
            }
            MenuHelper.printLoggedMenu();
            choice = scan.nextInt();
            switch (choice) {
                case LEAVE:
                    Log.printLoggingOutMessage();
                    break;
                case TRANSFER:
                    OPERATIONS_SERVICE.transfer(scan, loggedAccount);
                    break;
                case WITHDRAWAL:
                    OPERATIONS_SERVICE.withdrawal(scan, loggedAccount);
                    break;
                case DEPOSIT:
                    OPERATIONS_SERVICE.deposit(scan, loggedAccount);
                    break;
                case BLOCK_CARD:
                    OPERATIONS_SERVICE.blockCard(scan, loggedAccount);
                    break;
                case CARD_INFO:
                    System.out.println(loggedAccount.toString());
                    break;
                default:
                    Log.printChoiceErrorMessage();
                    break;
            }
        } while (choice != LEAVE);
    }
}
