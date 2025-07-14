import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataManager {
    private String filePath;
    private Map<String, User> users;
    private Map<String, MoneyRequest> moneyRequests = new HashMap<>();
    private List<Transaction> transactions = new ArrayList<>();

    public DataManager(String filePath) {
        this.filePath = filePath;
        this.users = new HashMap<>();
        loadUsers();
        loadTransactions();
        loadMoneyRequests(); // <-- Now loads requests from file
    }

    // --- USER MANAGEMENT ---

    private void loadUsers() {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String userId = parts[0];
                String pin = parts[1];
                String name = parts[2];
                String email = parts[3];
                String accountId = parts[4];
                double balance = Double.parseDouble(parts[5]);
                User user = new User(userId, name, email, pin, accountId, balance);
                users.put(userId, user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User findUser(String userId) {
        return users.get(userId);
    }

    public Account findAccountById(String accountId) {
        for (User user : users.values()) {
            if (user.getAccount().getAccountId().equals(accountId)) {
                return user.getAccount();
            }
        }
        return null;
    }

    // --- TRANSACTION MANAGEMENT ---

    public void loadTransactions() {
        File txnFile = new File("transactions.csv");
        if (!txnFile.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(txnFile))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String transactionId = parts[0];
                double amount = Double.parseDouble(parts[1]);
                Date date = parseDate(parts[2]);
                String status = parts[3];
                String senderAccountId = parts[4];
                String recipientAccountId = parts[5];
                String transactionType = (parts.length > 6) ? parts[6] : "sent"; // fallback

                Account sender = findAccountById(senderAccountId);
                Account recipient = findAccountById(recipientAccountId);

                Transaction txn = new Transaction(transactionId, amount, date, status, sender, recipient, transactionType);

                // Add to the correct account's transaction list
                if ("sent".equals(transactionType) && sender != null) {
                    sender.addTransaction(txn);
                } else if ("received".equals(transactionType) && recipient != null) {
                    recipient.addTransaction(txn);
                }
                transactions.add(txn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Date parseDate(String dateStr) {
        try {
            return new Date(Long.parseLong(dateStr));
        } catch (NumberFormatException e) {
            try {
                String cleaned = dateStr.replaceAll(" [A-Z]{3,4} ", " ");
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
                return sdf.parse(cleaned);
            } catch (ParseException ex) {
                ex.printStackTrace();
                return new Date();
            }
        }
    }

    public void saveTransaction(Transaction transaction) {
        boolean writeHeader = !new File("transactions.csv").exists();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("transactions.csv", true))) {
            if (writeHeader) {
                bw.write("transactionId,amount,date,status,senderAccountId,recipientAccountId,transactionType");
                bw.newLine();
            }
            bw.write(
                transaction.getTransactionId() + "," +
                transaction.getAmount() + "," +
                transaction.getDate().getTime() + "," +
                transaction.getStatus() + "," +
                transaction.getSenderAccount().getAccountId() + "," +
                transaction.getRecipientAccount().getAccountId() + "," +
                transaction.getTransactionType()
            );
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        transactions.add(transaction);
    }

    public List<Transaction> getTransactionsForUser(User user) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getSenderAccount().equals(user.getAccount()) || t.getRecipientAccount().equals(user.getAccount())) {
                result.add(t);
            }
        }
        return result;
    }

    public void updateAccounts(Account senderAccount, Account recipientAccount) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("userId,pin,name,email,accountId,balance");
            bw.newLine();
            for (User user : users.values()) {
                Account account = user.getAccount();
                bw.write(
                    user.getUserId() + "," +
                    user.getPin() + "," +
                    user.getName() + "," +
                    user.getEmail() + "," +
                    account.getAccountId() + "," +
                    account.getBalance()
                );
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- MONEY REQUEST MANAGEMENT ---

    // Load from file at startup
    public void loadMoneyRequests() {
        File reqFile = new File("moneyrequests.csv");
        if (!reqFile.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(reqFile))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String requestId = parts[0];
                double amount = Double.parseDouble(parts[1]);
                String status = parts[2];
                Date createdDate = new Date(Long.parseLong(parts[3]));
                String requesterId = parts[4];
                String recipientId = parts[5];
                User requester = findUser(requesterId);
                User recipient = findUser(recipientId);
                if (requester != null && recipient != null) {
                    MoneyRequest request = new MoneyRequest(requestId, amount, status, createdDate, requester, recipient);
                    moneyRequests.put(requestId, request);
                    requester.addSentRequest(request);
                    recipient.addReceivedRequest(request);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save all requests to file (called after any change)
    private void saveAllMoneyRequestsToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("moneyrequests.csv"))) {
            bw.write("requestId,amount,status,createdDate,requesterId,recipientId");
            bw.newLine();
            for (MoneyRequest req : moneyRequests.values()) {
                bw.write(
                    req.getRequestId() + "," +
                    req.getAmount() + "," +
                    req.getStatus() + "," +
                    req.getCreatedDate().getTime() + "," +
                    req.getRequester().getUserId() + "," +
                    req.getRecipient().getUserId()
                );
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMoneyRequest(MoneyRequest request) {
        moneyRequests.put(request.getRequestId(), request);
        saveAllMoneyRequestsToFile();
    }

    public MoneyRequest findMoneyRequest(String requestId) {
        return moneyRequests.get(requestId);
    }

    public void updateMoneyRequest(MoneyRequest request) {
        moneyRequests.put(request.getRequestId(), request);
        saveAllMoneyRequestsToFile();
    }

    public List<MoneyRequest> getRequestsForUser(User user) {
        List<MoneyRequest> result = new ArrayList<>();
        for (MoneyRequest req : moneyRequests.values()) {
            if (req.getRequester().equals(user) || req.getRecipient().equals(user)) {
                result.add(req);
            }
        }
        return result;
    }
}
