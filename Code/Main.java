import java.util.Scanner;

// Main class
public class Main {
    public static void main(String[] args) {
        // Initialize DataAccess with your CSV file
        DataAccess dataAccess = new DataAccess("users.csv");

        // Set up services using DataAccess
        AuthenticationService authService = new AuthenticationService(dataAccess);
        WalletService walletService = new WalletService(dataAccess);
        TransactionService transactionService = new TransactionService();
        NotificationService notificationService = new NotificationService();

        // Register user and recipient
        /*authService.registerUser("tahax09", "1999");
        walletService.addWallet(new Wallet("tahax09", 500.0));
        walletService.addWallet(new Wallet("prof_alessia", 0.0));*/

        // Set up controller
        SendMoneyController controller = new SendMoneyController(
            authService, walletService, transactionService, notificationService
        );

        System.out.println("=== Unified Smart Wallet: Send Money ===");
        System.out.println("Welcome to the Unified Smart Wallet System!");
        
        Scanner scanner = new Scanner(System.in);
        String userId, pin;
        boolean loginSuccess = false;
        
        // Authenticate user
        do {
            System.out.print("Enter user name: ");
            userId = scanner.nextLine();

            System.out.print("Enter PIN code or password: ");
            pin = scanner.nextLine();

            loginSuccess = controller.authenticate(userId, pin);
            if (loginSuccess) {
                System.out.println("Login successful. Welcome, " + userId + "!");
            } else {
                System.out.println("Login failed. Please check your username and PIN and try again.\n");
            }
        } while (!loginSuccess);

        // Display current balance
        System.out.println("\n--- Send Money ---");
        System.out.println("Your current balance: €" + walletService.getBalance(userId));
        System.out.print("Enter recipient name: ");
        String recipientId = scanner.nextLine();

        // Check if recipient exists $need professor's approval first
        /*String recipientId;
        do {
            System.out.print("Enter recipient name: ");
            recipientId = scanner.nextLine();

            if (!walletService.recipientExists(recipientId)) {
            System.out.println("Recipient not found. Please enter a valid recipient.");
            } else {
            break;
            }
        } while (true);*/

        System.out.print("Enter amount to send: ");
        double amount = scanner.nextDouble();

        System.out.println("\nProcessing transaction...");
        String result = controller.sendMoney(userId, recipientId, amount);

        System.out.println("Transaction status: " + result);
    }
}