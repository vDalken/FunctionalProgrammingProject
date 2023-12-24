import java.io.*;
import java.util.ArrayList;
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
        temporaryBankAccounts.clear();
        try (MyObjectOutputStream myObjectOutputStream = new MyObjectOutputStream(new FileOutputStream(BANK_ACCOUNTS_FILE_PATH, true))) {
            myObjectOutputStream.writeObject(bankAccount);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public Optional<BankAccount> logIn(int accountNumber, int password) {
        try (MyObjectInputStream myObjectInputStream = new MyObjectInputStream(new FileInputStream(BANK_ACCOUNTS_FILE_PATH))) {
            BankAccount bankAccount;
            while (true) {
                try {
                    bankAccount = (BankAccount) myObjectInputStream.readObject();
                } catch (EOFException error) {
                    break;
                }
                if (bankAccount.getAccountNumber() == accountNumber && bankAccount.getPassword() == password && bankAccount.getAccountStatus().equals(AccountStatus.ACTIVE)) {
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
        try (MyObjectInputStream myObjectInputStream = new MyObjectInputStream(new FileInputStream(BANK_ACCOUNTS_FILE_PATH))) {
            BankAccount bankAccount;
            while (true) {
                try {
                    bankAccount = (BankAccount) myObjectInputStream.readObject();
                } catch (EOFException error) {
                    break;
                }
                if (bankAccount.getAccountNumber() == accountNumber) {
                    double newBalance = bankAccount.getBalance() + amountToTransfer;
                    BankAccount updatedBankAccount = updateBankAccount(bankAccount, newBalance);
                    temporaryBankAccounts.add(updatedBankAccount);
                    continue;
                }
                if (bankAccount.getAccountNumber() == loggedAccount.getAccountNumber()) {
                    double newBalance = bankAccount.getBalance() - amountToTransfer;
                    BankAccount updatedBankAccount = updateBankAccount(loggedAccount, newBalance);
                    temporaryBankAccounts.add(updatedBankAccount);
                    continue;
                }
                temporaryBankAccounts.add(bankAccount);
            }

            updateBankAccountsFile(temporaryBankAccounts);
            Transaction transaction = new Transaction(loggedAccount.getAccountNumber(), accountNumber, amountToTransfer, true);
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

    private void updateBankAccountsFile(ArrayList<BankAccount> temporaryBankAccounts) {
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
        temporaryBankAccounts.clear();
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
        temporaryBankAccounts.clear();
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
        temporaryBankAccounts.clear();
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
}
