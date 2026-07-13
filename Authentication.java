import java.util.HashMap;
import java.util.Map;

public class Authentication {

    // Hardcoded demo credentials: username -> [password, role]
    private static final Map<String, String[]> USERS = new HashMap<>();

    static {
        USERS.put("patient1", new String[]{"patient123", "Patient"});
        USERS.put("doctor1", new String[]{"doctor123", "Doctor"});
        USERS.put("admin1", new String[]{"admin123", "Admin"});
    }

    // Original simple methods (kept for existing tests)
    public static void login() {
        System.out.println("User Login");
    }

    public static void logout() {
        System.out.println("User Logout");
    }

    // New: real credential + role check
    public static boolean authenticate(String username, String password, String role) {
        String[] record = USERS.get(username);
        if (record == null) {
            System.out.println("Login Failed: Username not found");
            return false;
        }
        if (!record[0].equals(password)) {
            System.out.println("Login Failed: Incorrect password");
            return false;
        }
        if (!record[1].equalsIgnoreCase(role)) {
            System.out.println("Login Failed: Role mismatch");
            return false;
        }
        System.out.println("Login Successful: " + username + " logged in as " + role);
        return true;
    }

    // Demo run
    public static void main(String[] args) {
        System.out.println("--- Testing valid logins ---");
        authenticate("patient1", "patient123", "Patient");
        authenticate("doctor1", "doctor123", "Doctor");
        authenticate("admin1", "admin123", "Admin");

        System.out.println("--- Testing invalid login ---");
        authenticate("patient1", "wrongpassword", "Patient");
    }
}