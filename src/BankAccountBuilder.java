import java.util.ArrayList;

public class BankAccountBuilder {
    private String accountHoldersName;
    private int accountNumber;
    private int password;
    private double balance;
    private ArrayList<String> transactionHistory;
    private String accountOpeningDate;
    private AccountStatus accountStatus;

    public BankAccountBuilder accountHoldersName(String accountHoldersName) {
        this.accountHoldersName = accountHoldersName;
        return this;
    }

    public BankAccountBuilder accountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
        return this;
    }

    public BankAccountBuilder password(int password) {
        this.password = password;
        return this;
    }

    public BankAccountBuilder balance(double balance) {
        this.balance = balance;
        return this;
    }

    public BankAccountBuilder transactionHistory(ArrayList<String> transactionHistory) {
        this.transactionHistory = transactionHistory;
        return this;
    }

    public BankAccountBuilder accountOpeningDate(String accountOpeningDate) {
        this.accountOpeningDate = accountOpeningDate;
        return this;
    }

    public BankAccountBuilder accountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
        return this;
    }

    public String getAccountHoldersName() {
        return accountHoldersName;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public int getPassword() {
        return password;
    }

    public double getBalance() {
        return balance;
    }

    public ArrayList<String> getTransactionHistory() {
        return transactionHistory;
    }

    public String getAccountOpeningDate() {
        return accountOpeningDate;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public BankAccount build() {
        return new BankAccount(this);
    }
}
