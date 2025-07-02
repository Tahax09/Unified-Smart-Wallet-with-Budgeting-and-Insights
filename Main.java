import java.util.Date;
import java.util.UUID;

// Domain Classes
class User {
    private String userId;
    private String name;
    private String email;
    private String phone;

    public User(String userId, String name, String email, String phone) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}

class Wallet {
    private String walletId;
    private double balance;
    private String currency;
    

    public Wallet(String walletId, double balance, String currency) {
        this.walletId = walletId;
        this.balance = balance;
        this.currency = currency;
    }

    public boolean debit(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public boolean credit(double amount) {
        if (amount > 0) {
            balance += amount;
            return true;
        }
        return false;
    }

    public double getBalance() {
        return balance;
    }
}

class Transaction {
    private String transactionId;
    private Wallet sender;
    private Wallet recipient;
    private double amount;
    private String status;
    private Date timestamp;

    public Transaction(String transactionId, Wallet sender, Wallet recipient, double amount) {
        this.transactionId = transactionId;
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.status = "PENDING";
        this.timestamp = new Date();
    }

    public boolean process() {
        if (sender.debit(amount)) {
            if (recipient.credit(amount)) {
                status = "COMPLETED";
                return true;
            }
        }
        status = "FAILED";
        return false;
    }
}

class Authentication {
    private String method;
    private Date timestamp;

    public boolean verify() {
        // Authentication logic
        return true;
    }
}

class Notification {
    private String notificationId;
    private String message;
    private Date timestamp;

    public void send() {
        // Notification sending logic
    }
}

// Service Classes
class AuthenticationService {
    public boolean authenticate(String credentials) {
        // Authentication logic
        return true;
    }
}

class WalletService {
    public boolean checkBalance(Wallet wallet, double amount) {
        return wallet.getBalance() >= amount;
    }

    public boolean debit(Wallet wallet, double amount) {
        return wallet.debit(amount);
    }

    public boolean credit(Wallet wallet, double amount) {
        return wallet.credit(amount);
    }
}

class TransactionService {
    public Transaction createTransaction(Wallet sender, Wallet recipient, double amount) {
        return new Transaction(UUID.randomUUID().toString(), sender, recipient, amount);
    }
}

class NotificationService {
    public void sendNotification(User user, String message) {
        Notification notification = new Notification();
        notification.send();
    }
}

// Controller Class
class SendMoneyController {
    private AuthenticationService authService = new AuthenticationService();
    private WalletService walletService = new WalletService();
    private TransactionService transactionService = new TransactionService();
    private NotificationService notificationService = new NotificationService();

    public void sendMoney(String senderId, String recipientId, double amount, String credentials) {
        // 1. Authenticate
        if (!authService.authenticate(credentials)) {
            return;
        }

        // 2. Retrieve wallets (simplified)
        Wallet senderWallet = getWallet(senderId);
        Wallet recipientWallet = getWallet(recipientId);

        // 3. Check balance
        if (!walletService.checkBalance(senderWallet, amount)) {
            return;
        }

        // 4. Process transaction
        Transaction transaction = transactionService.createTransaction(senderWallet, recipientWallet, amount);
        if (!transaction.process()) {
            return;
        }

        // 5. Send notifications
        notificationService.sendNotification(getUser(senderId), "Money sent");
        notificationService.sendNotification(getUser(recipientId), "Money received");
    }

    // Helper methods (simplified)
    private Wallet getWallet(String userId) {
        // Actual implementation would fetch from database
        return new Wallet("wallet_" + userId, 1000.0, "USD");
    }

    private User getUser(String userId) {
        // Actual implementation would fetch from database
        return new User(userId, "User " + userId, "user@example.com", "123456789");
    }
}

// Usage Example
public class Main {
    public static void main(String[] args) {
        SendMoneyController controller = new SendMoneyController();
        controller.sendMoney("user1", "user2", 150.0, "valid_credentials");

         // Add print statements to see results!
        System.out.println("Send money operation completed.");
    }
}
