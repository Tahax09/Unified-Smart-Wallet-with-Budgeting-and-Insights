// WalletService
public class WalletService {
    private DataAccess dataAccess;

    public WalletService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public String checkBalance(String userId, double amount) {
        User user = dataAccess.getUser(userId);
        if (user != null && user.getBalance() >= amount) {
            return "Sufficient";
        }
        return "Insufficient";
    }

    public boolean debit(String userId, double amount) {
        User user = dataAccess.getUser(userId);
        if (user != null && user.getBalance() >= amount) {
            user.setBalance(user.getBalance() - amount);
            dataAccess.saveUsers();
            return true;
        }
        return false;
    }

    public boolean credit(String userId, double amount) {
        User user = dataAccess.getUser(userId);
        if (user != null) {
            user.setBalance(user.getBalance() + amount);
            dataAccess.saveUsers();
            return true;
        }
        return false;
    }

    public double getBalance(String userId) {
        User user = dataAccess.getUser(userId);
        return (user != null) ? user.getBalance() : 0.0;
    }

    // Check if recipient exists $need professor's approval first
    //public boolean recipientExists(String recipientId) {
    //return wallets.containsKey(recipientId);
    //}
}