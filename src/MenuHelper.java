class MenuHelper {
    private MenuHelper() {
    }

    public static void printInitialMenu(){
        System.out.println("0. Exit");
        System.out.println("1. Create Account");
        System.out.println("2. Log In");
    }

    public static void printLoggedMenu(){
        System.out.println("0. Go Back");
        System.out.println("1. Transfer");
        System.out.println("2. Withdrawal");
        System.out.println("3. Deposit");
        System.out.println("4. Block Card");
        System.out.println("5. Card Info");
    }
}
