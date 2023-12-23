import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

public final class BankAccount implements Serializable {
    private String accountHoldersName;
    private int accountNumber;
    private int password;
    private double balance;
    private static final String CURRENCY = "€";
    private ArrayList<Transaction> transactionHistory;
    private String accountOpeningDate;
    private AccountStatus accountStatus;
    private final static String dateFormat = "dd-MM-yyyy";
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(dateFormat);

    public BankAccount() {
    }

    public BankAccount(String accountHoldersName) {
        Random random = new Random();
        this.accountHoldersName = accountHoldersName;
        accountNumber = generateAccountNumber(random);
        password = generatePassword(random);
        balance = 0;
        transactionHistory = new ArrayList<>();
        accountOpeningDate = getDate();
        accountStatus = AccountStatus.ACTIVE;
    }

    public BankAccount(BankAccountBuilder bankAccount){
        this.accountHoldersName =bankAccount.getAccountHoldersName();
        this.accountNumber = bankAccount.getAccountNumber();
        this.password = bankAccount.getPassword();;
        this.balance = bankAccount.getBalance();
        this.transactionHistory = bankAccount.getTransactionHistory();
        this.accountOpeningDate = bankAccount.getAccountOpeningDate();
        this.accountStatus = bankAccount.getAccountStatus();
    }

    private int generatePassword(Random random) {
        return random.nextInt(1000, 9999);
    }

    private int generateAccountNumber(Random random) {
        return random.nextInt(10000000, 99999999);
    }

    private String getDate() {
        return LocalDate
                .now()
                .format(DATE_TIME_FORMATTER);
    }

    public String getAccountHoldersName() {
        return accountHoldersName;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public int getPassword() {
        return password;
    }

    public ArrayList<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    public String getAccountOpeningDate() {
        return accountOpeningDate;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public BankAccount deposit(DepositOperation depositOperation, double amount){
        return depositOperation.perform(this,amount);
    }

    public BankAccount withdrawal(WithdrawalOperation withdrawalOperation, double amount){
        return withdrawalOperation.perform(this,amount);
    }

    public static BankAccount createAccountWithUpdatedBalance(BankAccount account, double newBalance) {
        return new BankAccountBuilder()
                .accountHoldersName(account.getAccountHoldersName())
                .accountNumber(account.getAccountNumber())
                .balance(newBalance)
                .password(account.getPassword())
                .transactionHistory(account.getTransactionHistory())
                .accountOpeningDate(account.getAccountOpeningDate())
                .accountStatus(account.getAccountStatus())
                .build();
    }

    @Override
    public String toString() {
        return "Your bank account info" +
                "\nAccount Holders Name: " + accountHoldersName +
                "\nAccount Number: " + accountNumber +
                "\nPassword=" + password +
                "\nBalance=" + balance +
                "\nTransaction History=" + transactionHistory +
                "\nAccount Opening Date='" + accountOpeningDate + '\'' +
                "\nAccount Status=" + accountStatus;
    }
}
