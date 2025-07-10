// AuthenticationService
public class AuthenticationService {
    private DataAccess dataAccess;

    public AuthenticationService(DataAccess dataAccess) {
    this.dataAccess = dataAccess;
    }

    public boolean authenticate(String userId, String pin) {
        User user = dataAccess.getUser(userId);
        return user != null && user.getPin().equals(pin);
    }
}
