import java.util.Date;
import java.util.List;

public class TransactionController {
    private DataManager dataManager;
    private User currentUser;

    public TransactionController(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    // Authenticate user by userId and pin
    public boolean authenticate(String userId, String pin) {
        User user = dataManager.findUser(userId);
        if (user != null && user.checkPin(pin)) {
            this.currentUser = user;
            return true;
        }
        return false;
    }

    // Expose the currently authenticated user (for balance, etc.)
    public User getCurrentUser() {
        return this.currentUser;
    }

    // Send money from current user to recipientId
    public String sendMoney(String recipientId, double amount) {
        if (currentUser == null) return "Not authenticated";

        Account senderAccount = currentUser.getAccount();
        if (!senderAccount.checkBalance(amount)) {
            return "Insufficient funds";
        }

        User recipientUser = dataManager.findUser(recipientId);
        if (recipientUser == null) {
            return "Recipient not found";
        }

        Account recipientAccount = recipientUser.getAccount();
        senderAccount.debit(amount);
        recipientAccount.credit(amount);

        String txnId = generateTransactionId();
        Date txnDate = new Date();

        // Create two transaction records: one for sender, one for recipient
        Transaction sentTxn = new Transaction(
            txnId, amount, txnDate, "success", senderAccount, recipientAccount, "sent"
        );
        Transaction receivedTxn = new Transaction(
            txnId, amount, txnDate, "success", senderAccount, recipientAccount, "received"
        );

        senderAccount.addTransaction(sentTxn);
        recipientAccount.addTransaction(receivedTxn);

        dataManager.saveTransaction(sentTxn);
        dataManager.saveTransaction(receivedTxn);

        dataManager.updateAccounts(senderAccount, recipientAccount);

        return "Transaction successful";
    }

    // View transactions for the current user
    public List<Transaction> viewTransactions() {
        if (currentUser == null) return null;
        return currentUser.getAccount().getTransactions();
    }

    // Helper method to generate unique transaction IDs
    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis();
    }
}
