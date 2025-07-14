import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DataManager dataManager = new DataManager("users.csv");
        TransactionController controller = new TransactionController(dataManager);
        RequestController requestController = new RequestController(dataManager);

        System.out.println("Welcome to Unified Smart Wallet");

        // Step 1: User login
        System.out.print("Enter user ID: ");
        String userId = scanner.nextLine();
        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine();

        boolean accessGranted = controller.authenticate(userId, pin);
        if (!accessGranted) {
            System.out.println("Authentication failed. Exiting.");
            scanner.close();
            return;
        }
        System.out.println("Login successful.");

        // Set current user in request controller
        requestController.setCurrentUser(controller.getCurrentUser());

        // Step2: Display user balance
        double balance = controller.getCurrentUser().getAccount().getBalance();
        System.out.println("Welcome, " + controller.getCurrentUser().getName() + "!");
        System.out.println("Your balance: " + balance + " EUR");
        
        // Step 3: Operation selector loop
        while (true) {
            System.out.println("\nSelect operation:");
            System.out.println("1. Send Money");
            System.out.println("2. Request Money");
            System.out.println("3. View Transaction History");
            System.out.println("4. View/Respond to Money Requests");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            String choiceInput = scanner.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(choiceInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            if (choice == 1) {
                // Send Money
                System.out.print("Enter recipient ID: ");
                String recipientId = scanner.nextLine();
                System.out.print("Enter amount to send: ");
                double amount;
                try {
                    amount = Double.parseDouble(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid amount entered.");
                    continue;
                }
                String status = controller.sendMoney(recipientId, amount);
                System.out.println("Transaction status: " + status);

            } else if (choice == 2) {
                // Request Money
                System.out.print("Enter recipient ID: ");
                String recipientId = scanner.nextLine();
                System.out.print("Enter amount to request: ");
                double amount;
                try {
                    amount = Double.parseDouble(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid amount entered.");
                    continue;
                }
                String status = requestController.requestMoney(recipientId, amount);
                System.out.println("Request status: " + status);

            } else if (choice == 3) {
                // View Transaction History
                List<Transaction> transactions = controller.viewTransactions();
                if (transactions == null || transactions.isEmpty()) {
                    System.out.println("No transactions found.");
                } else {
                    System.out.println("TxnID | Amount | Status | Date | RecipientID | Type");
                    for (Transaction txn : transactions) {
                        System.out.println(
                            txn.getTransactionId() + " | " +
                            txn.getAmount() + " | " +
                            txn.getStatus() + " | " +
                            txn.getDate() + " | " +
                            txn.getRecipientId() + " | " +
                            txn.getTransactionType()
                        );
                    }
                }

            } else if (choice == 4) {
                // View/Respond to Money Requests
                System.out.println("Received Requests:");
                List<MoneyRequest> received = requestController.viewReceivedRequests();
                if (received == null || received.isEmpty()) {
                    System.out.println("No received requests.");
                } else {
                    for (MoneyRequest r : received) {
                        System.out.println(r.getRequestId() + " | From: " + r.getRequester().getUserId() +
                                " | Amount: " + r.getAmount() + " | Status: " + r.getStatus());
                    }
                    System.out.print("Enter request ID to respond (or blank to skip): ");
                    String reqId = scanner.nextLine();
                    if (!reqId.isBlank()) {
                        System.out.print("Accept or Decline? ");
                        String action = scanner.nextLine();
                        String resp = requestController.respondToRequest(reqId, action);
                        System.out.println(resp);
                    }
                }

                System.out.println("Sent Requests:");
                List<MoneyRequest> sent = requestController.viewSentRequests();
                if (sent == null || sent.isEmpty()) {
                    System.out.println("No sent requests.");
                } else {
                    for (MoneyRequest r : sent) {
                        System.out.println(r.getRequestId() + " | To: " + r.getRecipient().getUserId() +
                                " | Amount: " + r.getAmount() + " | Status: " + r.getStatus());
                    }
                }

            } else if (choice == 5) {
                // Exit
                System.out.println("Goodbye!");
                break;
            } else {
                System.out.println("Invalid choice. Please select a valid operation.");
            }
        }

        scanner.close();
    }
}
