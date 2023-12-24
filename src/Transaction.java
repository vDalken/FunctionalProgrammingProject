import java.io.Serial;
import java.io.Serializable;

public class Transaction implements Serializable {
    private final int loggedAccountNumber;
    private final int accountNumber;
    private final int amountToTransfer;
    private final boolean wasSuccessful;
    @Serial
    private static final long serialVersionUID = 3901330131467928625L;

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

    public String wasSuccessful(boolean wasSuccessful){
        return wasSuccessful ? "yes" : "no";
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "Account Number=" + accountNumber +
                ", Amount To Transfer=" + amountToTransfer +
                ", Was Successful?=" + wasSuccessful(wasSuccessful) +
                '}';
    }
}
