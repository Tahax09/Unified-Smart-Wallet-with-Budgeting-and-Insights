import java.util.ArrayList;
import java.util.List;

public class Account {
    private String accountId;
    private double balance;
    private User owner; // Reference to the owner User
    private List<Transaction> transactions = new ArrayList<>();

    public Account(String accountId, double balance, User owner) {
        this.accountId = accountId;
        this.balance = balance;
        this.owner = owner;
    }

    public String getAccountId() {
        return accountId;
    }

    public double getBalance() {
        return balance;
    }

    public void debit(double amount) {
        this.balance -= amount;
    }

    public void credit(double amount) {
        this.balance += amount;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void addTransaction(Transaction txn) {
        transactions.add(txn);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    // The missing method needed by TransactionController
    public boolean checkBalance(double amount) {
        return this.balance >= amount;
    }
}
