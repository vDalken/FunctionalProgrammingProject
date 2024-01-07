import java.util.logging.LogManager;

public final class Log {
    private Log() {
    }
    public static void printChoiceErrorMessage(){
        System.out.println("You chose a option that doesn't exist");
    }
    public static void printLeavingMessage(){
        System.out.println("\nYou're leaving the app\n");
    }
    public static void printLoggingOutMessage(){System.out.println("\nYou're logging out");}

    public static void printAccountsHoldersNameQuestion(){
        System.out.println("Which name you would like to have as your account holder name?");
    }

    public static void printYourAccountNumberQuestion(){
        System.out.println("Write your account number");
    }

    public static void printAccountNumberQuestion(){
        System.out.println("Write the account number you want to transfer the money to");
    }

    public static void printPasswordQuestion(){
        System.out.println("Write the respective password of your account");
    }

    public static void printNewAccountInformation(BankAccount bankAccount){
        System.out.println("Account Number: "+ bankAccount.getAccountNumber());
        System.out.println("Password: "+ bankAccount.getPassword());
        System.out.println("Account Opening Date: "+ bankAccount.getAccountOpeningDate());
    }

    public static void printLogInError(){
        System.err.println("\nYou didn't put the right details. Log In unsuccessful\n");
    }

    public static void printLogInSuccess(){
        System.out.println("\nYou are now logged in\n");
    }

    public static void printAmountQuestion(){
        System.out.println("Write the amount");
    }

    public static void printAccountTransferError(){
        System.out.println("\nThe details you provided were wrong.\nMake sure everything you provided is true\n");
    }

    public static void printNotEnoughBalance(){
        System.out.println("Not enough balance to withdraw that amount");
    }

    public static void printSuccessfulWithdrawal(){
        System.out.println("Successful withdrawal");
    }

    public static void printSuccessfulDeposit(){
        System.out.println("Successful deposit");
    }

    public static void printBlockBankAccountQuestion(){
        System.out.println("Are you sure you want to block your bank account? Y for yes and n for no");
        System.out.println("After this, your bank account will be inaccessible");
    }

    public static void printBlockedBankAccountMessage(){
        System.out.println("Your bank account was blocked");
    }
}
