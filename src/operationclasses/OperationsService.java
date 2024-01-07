package operationclasses;

import datahandling.BankDataHandler;
import loggingclasses.Log;
import modelclasses.BankAccount;
import modelclasses.Transaction;
import java.util.Optional;
import java.util.Scanner;

public class OperationsService {
    private final BankDataHandler BANK_ACCOUNTS_HANDLER;

    public OperationsService(BankDataHandler BANK_ACCOUNTS_HANDLER) {
        this.BANK_ACCOUNTS_HANDLER = BANK_ACCOUNTS_HANDLER;
    }

    public void createAccount(String accountHolderName) {
        BankAccount newAccount = new BankAccount(accountHolderName);
        Log.printNewAccountInformation(newAccount);
        BANK_ACCOUNTS_HANDLER.createAccount(newAccount);
    }

    public Optional<BankAccount> logIn(int accountNumber, int password) {
        return BANK_ACCOUNTS_HANDLER.logIn(accountNumber, password);
    }

    public void transfer(Scanner scan, BankAccount loggedAccount) {
        Log.printAccountNumberQuestion();
        int accountNumber = scan.nextInt();
        Log.printAmountQuestion();
        int amountToTransfer = scan.nextInt();
        if (BANK_ACCOUNTS_HANDLER.isAccountNumberValid(accountNumber) && loggedAccount.getBalance() >= amountToTransfer) {
            BANK_ACCOUNTS_HANDLER.processTransfer(loggedAccount, accountNumber, amountToTransfer);
            loggedAccount = BankAccount.createAccountWithUpdatedBalance(loggedAccount, loggedAccount.getBalance() - amountToTransfer);
        } else {
            Log.printAccountTransferError();
            BANK_ACCOUNTS_HANDLER.processTransfer(loggedAccount, accountNumber, amountToTransfer);
            Transaction transaction = new Transaction(loggedAccount.getAccountNumber(), accountNumber, amountToTransfer, false);
            loggedAccount.getTransactionHistory().add(transaction);
        }
    }

    public void withdrawal(final Scanner scan, BankAccount loggedAccount) {
        Log.printAmountQuestion();
        int amount = scan.nextInt();
        if (amount > loggedAccount.getBalance()) {
            Log.printNotEnoughBalance();
        } else {
            BANK_ACCOUNTS_HANDLER.withdrawal(loggedAccount.getAccountNumber(), amount);
            loggedAccount = BankAccount.createAccountWithUpdatedBalance(loggedAccount, loggedAccount.getBalance() - amount);
            Log.printSuccessfulWithdrawal();
        }
    }

    public void deposit(final Scanner scan, BankAccount loggedAccount) {
        Log.printAmountQuestion();
        int amount = scan.nextInt();
        BANK_ACCOUNTS_HANDLER.deposit(loggedAccount.getAccountNumber(), amount);
        loggedAccount = BankAccount.createAccountWithUpdatedBalance(loggedAccount, loggedAccount.getBalance() + amount);
        Log.printSuccessfulDeposit();
    }

    public void blockCard(final Scanner scan, BankAccount loggedAccount) {
        Log.printBlockBankAccountQuestion();
        scan.nextLine();
        String choice = scan.nextLine();
        if (choice.trim().equals("Y")) {
            BANK_ACCOUNTS_HANDLER.blockCard(loggedAccount.getAccountNumber());
            loggedAccount = null;
            Log.printBlockedBankAccountMessage();
        }
    }

}
