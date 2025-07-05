import java.util.*;

// Domain Class: Wallet
class Wallet {
    private String userId;
    private double balance;

    public Wallet(String userId, double balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public String getUserId() {
        return userId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}

// AuthenticationService
class AuthenticationService {
    private Map<String, String> userPins = new HashMap<>();

    public void registerUser(String userId, String pin) {
        userPins.put(userId, pin);
    }

    public boolean authenticate(String userId, String pin) {
        return pin != null && pin.equals(userPins.get(userId));
    }
}

// WalletService
class WalletService {
    private Map<String, Wallet> wallets = new HashMap<>();

    public void addWallet(Wallet wallet) {
        wallets.put(wallet.getUserId(), wallet);
    }

    public String checkBalance(String userId, double amount) {
        Wallet wallet = wallets.get(userId);
        if (wallet != null && wallet.getBalance() >= amount) {
            return "Sufficient";
        }
        return "Insufficient";
    }

    public double getBalance(String userId) {
    Wallet wallet = wallets.get(userId);
    if (wallet != null) {
        return wallet.getBalance();
    }
    return 0.0;
    }

    public boolean debit(String userId, double amount) {
        Wallet wallet = wallets.get(userId);
        if (wallet != null && wallet.getBalance() >= amount) {
            wallet.setBalance(wallet.getBalance() - amount);
            return true;
        }
        return false;
    }

    public boolean credit(String userId, double amount) {
        Wallet wallet = wallets.get(userId);
        if (wallet != null) {
            wallet.setBalance(wallet.getBalance() + amount);
            return true;
        }
        return false;
    }

    // Check if recipient exists $need professor's approval first
    //public boolean recipientExists(String recipientId) {
    //return wallets.containsKey(recipientId);
    //}
}

// TransactionService
class TransactionService {
    private List<String> transactions = new ArrayList<>();

    public String createTransaction(String senderId, String recipientId, double amount) {
        String record = "From: " + senderId + ", To: " + recipientId + ", Amount: " + amount;
        transactions.add(record);
        return "Transaction successful";
    }
}

// NotificationService
class NotificationService {
    public boolean sendNotification(String userId, String message) {
        System.out.println("Notification to " + userId + ": " + message);
        return true;
    }
}

// Controller
class SendMoneyController {
    private AuthenticationService authService;
    private WalletService walletService;
    private TransactionService transactionService;
    private NotificationService notificationService;

    public SendMoneyController(AuthenticationService authService, WalletService walletService,
                               TransactionService transactionService, NotificationService notificationService) {
        this.authService = authService;
        this.walletService = walletService;
        this.transactionService = transactionService;
        this.notificationService = notificationService;
    }

    public String sendMoney(String userId, String pin, String recipientId, double amount) {
        boolean authResult = authService.authenticate(userId, pin);
        if (!authResult) {
            return "Authentication failed";
        }

        String balanceStatus = walletService.checkBalance(userId, amount);
        if (!"Sufficient".equals(balanceStatus)) {
            return "Insufficient funds";
        }

        boolean debitResult = walletService.debit(userId, amount);
        boolean creditResult = walletService.credit(recipientId, amount);

        if (!debitResult || !creditResult) {
            return "Transaction failed";
        }

        String transactionResult = transactionService.createTransaction(userId, recipientId, amount);

        notificationService.sendNotification(userId, "You sent $" + amount + " to " + recipientId);
        notificationService.sendNotification(recipientId, "You received $" + amount + " from " + userId);

        return transactionResult;
    }
}

// Main class
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Set up services
        AuthenticationService authService = new AuthenticationService();
        WalletService walletService = new WalletService();
        TransactionService transactionService = new TransactionService();
        NotificationService notificationService = new NotificationService();

        // Register user and recipient
        authService.registerUser("tahax09", "1999");
        walletService.addWallet(new Wallet("tahax09", 500.0));
        walletService.addWallet(new Wallet("prof_alessia", 0.0));

        // Set up controller
        SendMoneyController controller = new SendMoneyController(
            authService, walletService, transactionService, notificationService
        );

        System.out.println("=== Unified Smart Wallet: Send Money ===");
        System.out.println("Welcome to the Unified Smart Wallet System!");
        
        String userId, pin;
        boolean loginSuccess = false;

        do {
            System.out.print("Enter user name: ");
            userId = scanner.nextLine();

            System.out.print("Enter PIN code or password: ");
            pin = scanner.nextLine();

            loginSuccess = authService.authenticate(userId, pin);
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
        String result = controller.sendMoney(userId, pin, recipientId, amount);

        System.out.println("Transaction status: " + result);
    }
}