import java.io.*;
import java.util.ArrayList;
import java.util.Optional;

public class BankAccountsHandler implements Serializable {
    private final String PATH_OF_BANK_ACCOUNTS_FILE;
    private final ArrayList<BankAccount> temporaryBankAccounts = new ArrayList<>();


    public BankAccountsHandler(String pathOfBankAccountsFile) {
        PATH_OF_BANK_ACCOUNTS_FILE = pathOfBankAccountsFile;
    }

    public void createAccount(BankAccount bankAccount) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(PATH_OF_BANK_ACCOUNTS_FILE);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(bankAccount);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public Optional<BankAccount> logIn(int accountNumber, int password) {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(PATH_OF_BANK_ACCOUNTS_FILE))) {
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
        try (FileInputStream fileInputStream = new FileInputStream(PATH_OF_BANK_ACCOUNTS_FILE);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            BankAccount bankAccount;
            while (objectInputStream.available() > 0) {
                bankAccount = (BankAccount) objectInputStream.readObject();
                if (bankAccount.getAccountNumber() == accountNumber) {
                    double newBalance = bankAccount.getBalance() + amountToTransfer;
                    BankAccount updatedBankAccount = updateBankAccount(bankAccount, newBalance);
                    temporaryBankAccounts.add(updatedBankAccount);
                }
                temporaryBankAccounts.add(bankAccount);
            }
            updateBankAccountsFile(temporaryBankAccounts);
            documentTransaction(loggedAccount,accountNumber,amountToTransfer);
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
        try (FileOutputStream fileOutputStream = new FileOutputStream(PATH_OF_BANK_ACCOUNTS_FILE);
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

    private void documentTransaction(BankAccount loggedAccount, int accountNumber, int amountToTransfer){
        try(FileOutputStream fileOutputStream = new FileOutputStream()){

        }
    }
}
