import java.util.Date;
import java.util.List;

public class RequestController {
    private DataManager dataManager;
    private User currentUser;

    public RequestController(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    // Request money from another user
    public String requestMoney(String recipientId, double amount) {
        if (currentUser == null) return "Not authenticated";
        User recipient = dataManager.findUser(recipientId);
        if (recipient == null) return "Recipient not found";
        String requestId = "REQ" + System.currentTimeMillis();
        MoneyRequest request = new MoneyRequest(
            requestId, amount, "pending", new Date(), currentUser, recipient
        );
        currentUser.addSentRequest(request);
        recipient.addReceivedRequest(request);
        dataManager.saveMoneyRequest(request);
        return "Request created";
    }

    public List<MoneyRequest> viewSentRequests() {
        if (currentUser == null) return null;
        return currentUser.getSentRequests();
    }

    public List<MoneyRequest> viewReceivedRequests() {
        if (currentUser == null) return null;
        return currentUser.getReceivedRequests();
    }

    // Respond to a received request (accept/decline)
    public String respondToRequest(String requestId, String action) {
        if (currentUser == null) return "Not authenticated";
        MoneyRequest request = dataManager.findMoneyRequest(requestId);
        if (request == null) return "Request not found";
        if (!request.getRecipient().equals(currentUser)) return "Not authorized";
        if (!request.getStatus().equals("pending")) return "Request already processed";
        if (action.equalsIgnoreCase("accept")) {
            // Check balance and update accounts (reuse your SendMoney logic)
            if (!currentUser.getAccount().checkBalance(request.getAmount())) {
                return "Insufficient funds";
            }
            currentUser.getAccount().debit(request.getAmount());
            request.getRequester().getAccount().credit(request.getAmount());
            // Optionally, create a Transaction here
            request.setStatus("accepted");
        } else if (action.equalsIgnoreCase("decline")) {
            request.setStatus("declined");
        } else {
            return "Invalid action";
        }
        dataManager.updateMoneyRequest(request);
        return "Request " + request.getStatus();
    }
}
