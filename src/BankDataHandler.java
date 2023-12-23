import java.io.*;
import java.util.ArrayList;
import java.util.Optional;

public class BankDataHandler implements Serializable {
    private final String BANK_ACCOUNTS_FILE_PATH;
    private final String TRANSACTIONS_FILE_PATH;
    private final ArrayList<BankAccount> temporaryBankAccounts = new ArrayList<>();

    public BankDataHandler(String bankAccountsFilePath, String transactionsFilePath) {
        BANK_ACCOUNTS_FILE_PATH = bankAccountsFilePath;
        TRANSACTIONS_FILE_PATH = transactionsFilePath;
    }

    public void createAccount(BankAccount bankAccount) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(BANK_ACCOUNTS_FILE_PATH);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(bankAccount);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public Optional<BankAccount> logIn(int accountNumber, int password) {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(BANK_ACCOUNTS_FILE_PATH))) {
            BankAccount bankAccount;
            while ((bankAccount = (BankAccount) objectInputStream.readObject()) != null) {
                if (bankAccount.getAccountNumber() == accountNumber && bankAccount.getPassword() == password) {
                    return Optional.of(bankAccount);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public void transfer(BankAccount loggedAccount, int accountNumber, int amountToTransfer) {
        temporaryBankAccounts.clear();
        try (FileInputStream fileInputStream = new FileInputStream(BANK_ACCOUNTS_FILE_PATH);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            BankAccount bankAccount;
            boolean wasSuccessful = false;
            while (objectInputStream.available() > 0) {
                bankAccount = (BankAccount) objectInputStream.readObject();
                if (bankAccount.getAccountNumber() == accountNumber && loggedAccount.getBalance() > amountToTransfer) {
                    double newBalance = bankAccount.getBalance() + amountToTransfer;
                    BankAccount updatedBankAccount = updateBankAccount(bankAccount, newBalance);
                    temporaryBankAccounts.add(updatedBankAccount);
                    wasSuccessful = true;
                }
                temporaryBankAccounts.add(bankAccount);
            }
            if (wasSuccessful) {
                updateBankAccountsFile(temporaryBankAccounts);
            }
            Transaction transaction = new Transaction(loggedAccount.getAccountNumber(), accountNumber, amountToTransfer, wasSuccessful);
            documentTransaction(transaction);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private BankAccount updateBankAccount(BankAccount bankAccount, double newBalance) {
        return new BankAccountBuilder()
                .accountHoldersName(bankAccount.getAccountHoldersName())
                .accountNumber(bankAccount.getAccountNumber())
                .balance(newBalance)
                .password(bankAccount.getPassword())
                .transactionHistory(bankAccount.getTransactionHistory())
                .accountOpeningDate(bankAccount.getAccountOpeningDate())
                .accountStatus(bankAccount.getAccountStatus())
                .build();
    }

    private void updateBankAccountsFile(ArrayList<BankAccount> temporaryBankAccounts) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(BANK_ACCOUNTS_FILE_PATH);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            temporaryBankAccounts
                    .forEach(bankAccountToWrite -> {
                        try {
                            objectOutputStream.writeObject(bankAccountToWrite);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void documentTransaction(Transaction transaction) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(TRANSACTIONS_FILE_PATH, true);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)
        ) {
            objectOutputStream.writeObject(transaction);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
