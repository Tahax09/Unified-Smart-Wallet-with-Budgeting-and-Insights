import java.util.ArrayList;
import java.util.List;

// TransactionService
public class TransactionService {
    private List<String> transactions = new ArrayList<>();

    public String createTransaction(String senderId, String recipientId, double amount) {
        String record = "From: " + senderId + ", To: " + recipientId + ", Amount: " + amount;
        transactions.add(record);
        return "Transaction successful";
    }
}
