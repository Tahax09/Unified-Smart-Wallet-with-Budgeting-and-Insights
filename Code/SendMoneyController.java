// Controller
public class SendMoneyController {
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

    // Separate authentication method
    public boolean authenticate(String userId, String pin) {
        return authService.authenticate(userId, pin);
    }
    
    // Method to send money
    public String sendMoney(String userId, String recipientId, double amount) {
        
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