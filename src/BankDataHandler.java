import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BankDataHandler implements Serializable {
    //TODO ask if I can use the try with resources without declaring and initializing the same thing everytime
    private final String BANK_ACCOUNTS_FILE_PATH;
    private final String TRANSACTIONS_FILE_PATH;
    private final ArrayList<BankAccount> temporaryBankAccounts = new ArrayList<>();

    public BankDataHandler(String bankAccountsFilePath, String transactionsFilePath) {
        BANK_ACCOUNTS_FILE_PATH = bankAccountsFilePath;
        TRANSACTIONS_FILE_PATH = transactionsFilePath;
    }

    public void createAccount(BankAccount bankAccount) {
        try (MyObjectOutputStream myObjectOutputStream = new MyObjectOutputStream(new FileOutputStream(BANK_ACCOUNTS_FILE_PATH, true))) {
            myObjectOutputStream.writeObject(bankAccount);
        } catch (IOException e) {
            System.err.println(Arrays.toString(e.getStackTrace()));
        }
    }

    public Optional<BankAccount> logIn(int accountNumber, int password) {
        return readFromBankFile(BANK_ACCOUNTS_FILE_PATH)
                .stream()
                .filter(account -> account.getAccountNumber() == accountNumber &&
                        account.getPassword() == password &&
                        account.getAccountStatus().equals(AccountStatus.ACTIVE))
                .findFirst();
    }

    public void processTransfer(BankAccount loggedAccount, int accountNumber, int amountToTransfer) {
        List<BankAccount> accounts = readFromBankFile(BANK_ACCOUNTS_FILE_PATH);

        if (!isAccountNumberValid(accountNumber) || !(loggedAccount.getBalance() >= amountToTransfer)) {
            Transaction transaction = new Transaction(loggedAccount.getAccountNumber(), accountNumber, amountToTransfer, false);
            documentTransaction(transaction);
            updateLoggedAccountTransactionHistory(loggedAccount.getAccountNumber(), transaction);
            return;
        }

        List<BankAccount> updatedAccounts = accounts.stream()
                .map(account -> {
                    if (account.getAccountNumber() == accountNumber) {
                        double newBalance = account.getBalance() + amountToTransfer;
                        return updateBankAccount(account, newBalance);
                    }
                    if (account.getAccountNumber() == loggedAccount.getAccountNumber()) {
                        double newBalance = account.getBalance() - amountToTransfer;
                        Transaction transaction = new Transaction(loggedAccount.getAccountNumber(), accountNumber, amountToTransfer, true);
                        updateLoggedAccountTransactionHistory(loggedAccount.getAccountNumber(), transaction);
                        return updateBankAccount(account, newBalance, transaction);
                    }
                    return account;
                })
                .toList();

        updateBankAccountsFile(updatedAccounts);
    }

    private BankAccount updateBankAccount(BankAccount bankAccount, double newBalance, Transaction transaction) {
        BankAccount updatedBankAccount = updateBankAccount(bankAccount, newBalance);
        updatedBankAccount.getTransactionHistory().add(transaction);
        return updatedBankAccount;
    }

    private void updateLoggedAccountTransactionHistory(int loggedAccountNumber, Transaction transaction) {
        List<BankAccount> temporaryBankAccounts = new ArrayList<>();
        try (MyObjectInputStream myObjectInputStream = new MyObjectInputStream(new FileInputStream(BANK_ACCOUNTS_FILE_PATH))) {
            BankAccount bankAccount;
            while (true) {
                try {
                    bankAccount = (BankAccount) myObjectInputStream.readObject();
                } catch (EOFException error) {
                    break;
                }
                if (bankAccount.getAccountNumber() == loggedAccountNumber) {
                    bankAccount.getTransactionHistory().add(transaction);
                    temporaryBankAccounts.add(bankAccount);
                    continue;
                }
                temporaryBankAccounts.add(bankAccount);
            }
            updateBankAccountsFile(temporaryBankAccounts);
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

    private BankAccount updateBankAccountStatus(BankAccount bankAccount) {
        return new BankAccountBuilder()
                .accountHoldersName(bankAccount.getAccountHoldersName())
                .accountNumber(bankAccount.getAccountNumber())
                .balance(bankAccount.getBalance())
                .password(bankAccount.getPassword())
                .transactionHistory(bankAccount.getTransactionHistory())
                .accountOpeningDate(bankAccount.getAccountOpeningDate())
                .accountStatus(AccountStatus.BLOCKED)
                .build();
    }

    private void updateBankAccountsFile(List<BankAccount> temporaryBankAccounts) {
        try (MyObjectOutputStream objectOutputStream = new MyObjectOutputStream(new FileOutputStream(BANK_ACCOUNTS_FILE_PATH))) {
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

    public void documentTransaction(Transaction transaction) {
        try (MyObjectOutputStream objectOutputStream = new MyObjectOutputStream(new FileOutputStream(TRANSACTIONS_FILE_PATH, true))) {
            objectOutputStream.writeObject(transaction);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isAccountNumberValid(int accountNumber) {
        try (MyObjectInputStream myObjectInputStream = new MyObjectInputStream(new FileInputStream(BANK_ACCOUNTS_FILE_PATH))) {
            BankAccount bankAccount;
            while (true) {
                try {
                    bankAccount = (BankAccount) myObjectInputStream.readObject();
                } catch (EOFException error) {
                    break;
                }
                if (bankAccount.getAccountNumber() == accountNumber) {
                    return true;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public void deposit(int loggedAccountNumber, int amount) {
        List<BankAccount> temporaryBankAccounts = new ArrayList<>();
        try (MyObjectInputStream myObjectInputStream = new MyObjectInputStream(new FileInputStream(BANK_ACCOUNTS_FILE_PATH))) {
            BankAccount bankAccount;
            while (true) {
                try {
                    bankAccount = (BankAccount) myObjectInputStream.readObject();
                } catch (EOFException error) {
                    break;
                }
                if (bankAccount.getAccountNumber() == loggedAccountNumber) {
                    double newBalance = bankAccount.getBalance() + amount;
                    bankAccount = updateBankAccount(bankAccount, newBalance);
                    temporaryBankAccounts.add(bankAccount);
                    continue;
                }
                temporaryBankAccounts.add(bankAccount);
            }
            updateBankAccountsFile(temporaryBankAccounts);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void withdrawal(int loggedAccountNumber, int amount) {
        List<BankAccount> temporaryBankAccounts = new ArrayList<>();
        try (MyObjectInputStream myObjectInputStream = new MyObjectInputStream(new FileInputStream(BANK_ACCOUNTS_FILE_PATH))) {
            BankAccount bankAccount;
            while (true) {
                try {
                    bankAccount = (BankAccount) myObjectInputStream.readObject();
                } catch (EOFException error) {
                    break;
                }
                if (bankAccount.getAccountNumber() == loggedAccountNumber) {
                    double newBalance = bankAccount.getBalance() - amount;
                    bankAccount = updateBankAccount(bankAccount, newBalance);
                    temporaryBankAccounts.add(bankAccount);
                    continue;
                }
                temporaryBankAccounts.add(bankAccount);
            }
            updateBankAccountsFile(temporaryBankAccounts);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void blockCard(int loggedAccountNumber) {
        List<BankAccount> temporaryBankAccounts = new ArrayList<>();
        try (MyObjectInputStream myObjectInputStream = new MyObjectInputStream(new FileInputStream(BANK_ACCOUNTS_FILE_PATH))) {
            BankAccount bankAccount;
            while (true) {
                try {
                    bankAccount = (BankAccount) myObjectInputStream.readObject();
                } catch (EOFException error) {
                    break;
                }
                if (bankAccount.getAccountNumber() == loggedAccountNumber) {
                    bankAccount = updateBankAccountStatus(bankAccount);
                    temporaryBankAccounts.add(bankAccount);
                    continue;
                }
                temporaryBankAccounts.add(bankAccount);
            }
            updateBankAccountsFile(temporaryBankAccounts);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private List<BankAccount> readFromBankFile(String filePath) {
        try (MyObjectInputStream objectInputStream = new MyObjectInputStream(new FileInputStream(filePath))) {
            List<BankAccount> accounts = new ArrayList<>();
            while (true) {
                try {
                    BankAccount account = (BankAccount) objectInputStream.readObject();
                    accounts.add(account);
                } catch (EOFException e) {
                    break;
                }
            }
            return accounts;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
