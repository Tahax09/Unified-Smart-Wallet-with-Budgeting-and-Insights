// NotificationService
public class NotificationService {
    public boolean sendNotification(String userId, String message) {
        System.out.println("Notification to " + userId + ": " + message);
        return true;
    }
}