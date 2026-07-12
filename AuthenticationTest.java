public class AuthenticationTest {

    public static void main(String[] args) {
        testLogin();
        testLogout();
    }

    public static void testLogin() {
        System.out.println("Running testLogin...");
        Authentication.login();
        System.out.println("testLogin PASSED: login() executed successfully");
    }

    public static void testLogout() {
        System.out.println("Running testLogout...");
        Authentication.logout();
        System.out.println("testLogout PASSED: logout() executed successfully");
    }
}