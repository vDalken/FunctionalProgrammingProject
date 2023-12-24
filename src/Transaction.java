import java.io.Serializable;

public class Transaction implements Serializable {
    private final int loggedAccountNumber;
    private final int accountNumber;
    private final int amountToTransfer;
    private final boolean wasSuccessful;

    public Transaction(int loggedAccountNumber, int accountNumber, int amountToTransfer, boolean wasSuccessful) {
        this.loggedAccountNumber = loggedAccountNumber;
        this.accountNumber = accountNumber;
        this.amountToTransfer = amountToTransfer;
        this.wasSuccessful = wasSuccessful;
    }

    public int getLoggedAccountNumber() {
        return loggedAccountNumber;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public int getAmountToTransfer() {
        return amountToTransfer;
    }

    public boolean isWasSuccessful() {
        return wasSuccessful;
    }
}
