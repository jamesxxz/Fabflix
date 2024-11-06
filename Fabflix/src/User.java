/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */

public class User {

    private final String username;
    private final String userId;

    public User(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    // Getter for userId
    public String getUserId() {
        return userId;
    }

    // Getter for username
    public String getUsername() {
        return username;
    }
}
