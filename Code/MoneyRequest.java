import java.util.Date;

public class MoneyRequest {
    private String requestId;
    private double amount;
    private String status; // "pending", "accepted", "declined"
    private Date createdDate;
    private User requester;
    private User recipient;

    public MoneyRequest(String requestId, double amount, String status, Date createdDate, User requester, User recipient) {
        this.requestId = requestId;
        this.amount = amount;
        this.status = status;
        this.createdDate = createdDate;
        this.requester = requester;
        this.recipient = recipient;
    }

    // Getters and setters
    public String getRequestId() { return requestId; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreatedDate() { return createdDate; }
    public User getRequester() { return requester; }
    public User getRecipient() { return recipient; }
}
