import java.io.*;
import java.util.*;

public class DataAccess {
    private String filename;
    private Map<String, User> users = new HashMap<>();

    public DataAccess(String filename) {
        this.filename = filename;
        loadUsers();
    }

    // Load users from CSV
    private void loadUsers() {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String userId = parts[0].trim();
                String pin = parts[1].trim();
                double balance = Double.parseDouble(parts[2].trim());
                users.put(userId, new User(userId, pin, balance));
            }
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    // Save users to CSV
    public void saveUsers() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("userid,pin,balance\n");
            for (User user : users.values()) {
                bw.write(user.getUserId() + "," + user.getPin() + "," + user.getBalance() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    public void updateUserBalance(String userId, double newBalance) {
        User user = users.get(userId);
        if (user != null) {
            user.setBalance(newBalance);
            saveUsers();
        }
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }
}
