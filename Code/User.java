import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private String name;
    private String email;
    private String pin;
    private Account account;

    // Add these for Request Money use case:
    private List<MoneyRequest> sentRequests = new ArrayList<>();
    private List<MoneyRequest> receivedRequests = new ArrayList<>();

    public User(String userId, String name, String email, String pin, String accountId, double balance) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.pin = pin;
        // Pass 'this' as owner to Account
        this.account = new Account(accountId, balance, this);
    }

    public boolean checkPin(String inputPin) {
        return this.pin.equals(inputPin);
    }

    public Account getAccount() {
        return this.account;
    }

    public String getUserId() {
        return userId;
    }

    public String getPin() {
        return pin;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    // --- Request Money support ---

    public List<MoneyRequest> getSentRequests() {
        return sentRequests;
    }

    public List<MoneyRequest> getReceivedRequests() {
        return receivedRequests;
    }

    public void addSentRequest(MoneyRequest request) {
        sentRequests.add(request);
    }

    public void addReceivedRequest(MoneyRequest request) {
        receivedRequests.add(request);
    }
}
