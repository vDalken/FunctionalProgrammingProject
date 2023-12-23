public final class Log {
    private Log() {
    }

    public static void printInitialMenu(){
        System.out.println("0. Exit");
        System.out.println("1. Create Account");
        System.out.println("2. Log In");
    }

    public static void printUserMenu(){
        System.out.println("0. Go Back");
        System.out.println("1. Transfer");
        System.out.println("2. Withdrawal");
        System.out.println("3. Deposit");
        System.out.println("4. Block Card");
        System.out.println("5. Card Info");
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

    public static void printAccountNumberQuestion(){
        System.out.println("Write your account number");
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

    public static void printLoggedMenu(){
        System.out.println("0. Go Back");
        System.out.println("1. Transfer");
        System.out.println("2. Withdrawal");
        System.out.println("3. Deposit");
        System.out.println("4. Block Card");
        System.out.println("5. Card Info");
    }

    public static void printAmountQuestion(){
        System.out.println("Write the amount you want to transfer");
    }
}
