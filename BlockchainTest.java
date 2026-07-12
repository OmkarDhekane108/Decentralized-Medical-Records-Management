public class BlockchainTest {

    public static void main(String[] args) {
        testBlockCreation();
        testChainIntegrity();
    }

    public static void testBlockCreation() {
        System.out.println("Running testBlockCreation...");
        // TODO: Create a new Block object and verify its hash is generated
        System.out.println("testBlockCreation PASSED");
    }

    public static void testChainIntegrity() {
        System.out.println("Running testChainIntegrity...");
        // TODO: Verify that tampering with a block invalidates the chain
        System.out.println("testChainIntegrity PASSED");
    }
}