import java.util.InputMismatchException;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    //TODO add configuration files to this project
    private final static int LEAVE = 0;
    private final static int CREATE_ACCOUNT = 1;
    private final static int LOG_IN = 2;
    private final static int TRANSFER = 1;
    private final static int WITHDRAWAL = 2;
    private final static int DEPOSIT = 3;
    private final static int BLOCK_CARD = 4;
    private final static int CARD_INFO = 5;
    private final static String BANK_ACCOUNTS_FILE_PATH = "resources/BankAccounts.ser";
    private final static String TRANSACTIONS_FILE_PATH = "resources/AllTransactions.ser";
    private final static BankDataHandler bankAccountsHandler = new BankDataHandler(
            BANK_ACCOUNTS_FILE_PATH,
            TRANSACTIONS_FILE_PATH
    );
    private static BankAccount loggedAccount;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        BankAccount fabio = new BankAccount("fabio");
        DepositOperation depositOperation = ((bankAccount, amount) -> BankAccount.createAccountWithUpdatedBalance(bankAccount, bankAccount.getBalance() + amount));
        fabio = fabio.deposit(depositOperation, 102);

        WithdrawalOperation withdrawalOperation = ((bankAccount, amount) -> BankAccount.createAccountWithUpdatedBalance(bankAccount, bankAccount.getBalance() - amount));
        fabio = fabio.withdrawal(withdrawalOperation, 10);
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
        bankAccountsHandler.createAccount(newAccount);
    }

    private static void logIn(Scanner scan) {
        try {
            Log.printYourAccountNumberQuestion();
            int accountNumber = scan.nextInt();
            Log.printPasswordQuestion();
            int password = scan.nextInt();
            Optional<BankAccount> account = bankAccountsHandler.logIn(accountNumber, password);
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
        if (bankAccountsHandler.isAccountNumberValid(accountNumber) && loggedAccount.getBalance() >= amountToTransfer) {
            bankAccountsHandler.transfer(loggedAccount, accountNumber, amountToTransfer);
            loggedAccount = BankAccount.createAccountWithUpdatedBalance(loggedAccount, loggedAccount.getBalance() - amountToTransfer);
        } else {
            Log.printAccountTransferError();
            Transaction transaction = new Transaction(loggedAccount.getAccountNumber(), accountNumber, amountToTransfer, false);
            bankAccountsHandler.documentTransaction(transaction);
        }
    }

    private static void deposit(Scanner scan) {
        Log.printAmountQuestion();
        int amount = scan.nextInt();
        bankAccountsHandler.deposit(loggedAccount.getAccountNumber(), amount);
        loggedAccount = BankAccount.createAccountWithUpdatedBalance(loggedAccount, loggedAccount.getBalance() + amount);
        Log.printSuccessfulDeposit();
    }

    private static void withdrawal(Scanner scan) {
        Log.printAmountQuestion();
        int amount = scan.nextInt();
        if (amount > loggedAccount.getBalance()) {
            Log.printNotEnoughBalance();
        } else {
            bankAccountsHandler.withdrawal(loggedAccount.getAccountNumber(), amount);
            loggedAccount = BankAccount.createAccountWithUpdatedBalance(loggedAccount, loggedAccount.getBalance() - amount);
            Log.printSuccessfulWithdrawal();
        }
    }
}