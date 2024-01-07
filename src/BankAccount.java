import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public final class BankAccount implements Serializable {
    //TODO ask if I can have a history of all transactions, withdrawals and deposits and sorted by date ArrayList<Object> history
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

    public BankAccount(BankAccountBuilder bankAccount) {
        this.accountHoldersName = bankAccount.getAccountHoldersName();
        this.accountNumber = bankAccount.getAccountNumber();
        this.password = bankAccount.getPassword();
        ;
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

    public BankAccount deposit(DepositOperation depositOperation, double amount) {
        return depositOperation.perform(this, amount);
    }

    public BankAccount withdrawal(WithdrawalOperation withdrawalOperation, double amount) {
        return withdrawalOperation.perform(this, amount);
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

    private String showTransactionHistory(){
        return getTransactionHistory()
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String toString() {
        return "\nYour bank account info" +
                "\nAccount Holders Name: " + accountHoldersName +
                "\nAccount Number: " + accountNumber +
                "\nPassword=" + password +
                "\nBalance=" + balance +
                "\n\nTransaction History:\n" + showTransactionHistory() +
                "\n\nAccount Opening Date='" + accountOpeningDate + '\'' +
                "\nAccount Status=" + accountStatus + "\n";
    }

    public static class BankAccountBuilder {
        private String accountHoldersName;
        private int accountNumber;
        private int password;
        private double balance;
        private ArrayList<Transaction> transactionHistory;
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

        public BankAccountBuilder transactionHistory(ArrayList<Transaction> transactionHistory) {
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

        public ArrayList<Transaction> getTransactionHistory() {
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

}
