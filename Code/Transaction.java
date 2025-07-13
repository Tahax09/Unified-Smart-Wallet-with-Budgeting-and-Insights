import java.util.Date;

public class Transaction {
    private String transactionId;
    private double amount;
    private Date date;
    private String status;
    private Account senderAccount;
    private Account recipientAccount;
    private String transactionType; // "sent" or "received"

    public Transaction(String transactionId, double amount, Date date, String status,
                       Account sender, Account recipient, String transactionType) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.date = date;
        this.status = status;
        this.senderAccount = sender;
        this.recipientAccount = recipient;
        this.transactionType = transactionType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public double getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public Account getSenderAccount() {
        return senderAccount;
    }

    public Account getRecipientAccount() {
        return recipientAccount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getRecipientId() {
        return recipientAccount.getOwner().getUserId();
    }
}
