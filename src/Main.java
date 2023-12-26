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
    private static BankAccount loggedAccount;

    public static void main(final String[] args) {
        Scanner scan = new Scanner(System.in);
        int choice;
        do {
            Log.printInitialMenu();
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
        BankAccount newAccount = new BankAccount(accountHoldersName);
        Log.printNewAccountInformation(newAccount);
        BANK_ACCOUNTS_HANDLER.createAccount(newAccount);
    }

    private static void logIn(Scanner scan) {
        try {
            Log.printYourAccountNumberQuestion();
            int accountNumber = scan.nextInt();
            Log.printPasswordQuestion();
            int password = scan.nextInt();
            Optional<BankAccount> account = BANK_ACCOUNTS_HANDLER.logIn(accountNumber, password);
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
            Log.printLoggedMenu();
            choice = scan.nextInt();
            switch (choice) {
                case LEAVE:
                    Log.printLoggingOutMessage();
                    break;
                case TRANSFER:
                    transfer(scan);
                    break;
                case WITHDRAWAL:
                    withdrawal(scan);
                    break;
                case DEPOSIT:
                    deposit(scan);
                    break;
                case BLOCK_CARD:
                    blockCard(scan);
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

    private static void transfer(Scanner scan) {
        Log.printAccountNumberQuestion();
        int accountNumber = scan.nextInt();
        Log.printAmountQuestion();
        int amountToTransfer = scan.nextInt();
        if (BANK_ACCOUNTS_HANDLER.isAccountNumberValid(accountNumber) && loggedAccount.getBalance() >= amountToTransfer) {
            BANK_ACCOUNTS_HANDLER.processTransfer(loggedAccount, accountNumber, amountToTransfer);
            loggedAccount = BankAccount.createAccountWithUpdatedBalance(loggedAccount, loggedAccount.getBalance() - amountToTransfer);
        } else {
            Log.printAccountTransferError();
            BANK_ACCOUNTS_HANDLER.processTransfer(loggedAccount, accountNumber, amountToTransfer);
            Transaction transaction = new Transaction(loggedAccount.getAccountNumber(), accountNumber, amountToTransfer, false);
            loggedAccount.getTransactionHistory().add(transaction);
        }
    }

    private static void deposit(final Scanner scan) {
        Log.printAmountQuestion();
        int amount = scan.nextInt();
        BANK_ACCOUNTS_HANDLER.deposit(loggedAccount.getAccountNumber(), amount);
        loggedAccount = BankAccount.createAccountWithUpdatedBalance(loggedAccount, loggedAccount.getBalance() + amount);
        Log.printSuccessfulDeposit();
    }

    private static void withdrawal(final Scanner scan) {
        Log.printAmountQuestion();
        int amount = scan.nextInt();
        if (amount > loggedAccount.getBalance()) {
            Log.printNotEnoughBalance();
        } else {
            BANK_ACCOUNTS_HANDLER.withdrawal(loggedAccount.getAccountNumber(), amount);
            loggedAccount = BankAccount.createAccountWithUpdatedBalance(loggedAccount, loggedAccount.getBalance() - amount);
            Log.printSuccessfulWithdrawal();
        }
    }

    private static void blockCard(final Scanner scan) {
        Log.printBlockBankAccountQuestion();
        scan.nextLine();
        String choice = scan.nextLine();
        if (choice.trim().equals("Y")) {
            BANK_ACCOUNTS_HANDLER.blockCard(loggedAccount.getAccountNumber());
            loggedAccount = null;
            Log.printBlockedBankAccountMessage();
        }
    }
}
