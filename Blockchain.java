import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

// Represents a single block in the chain
class Block {
    private String previousHash;
    private String data;       // e.g. medical record info
    private long timestamp;
    private String hash;

    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timestamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String input = previousHash + Long.toString(timestamp) + data;
        return applySHA256(input);
    }

    public static String applySHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getHash() { return hash; }
    public String getPreviousHash() { return previousHash; }
    public String getData() { return data; }
}

// Manages the chain of blocks
public class Blockchain {
    private List<Block> chain = new ArrayList<>();

    public Blockchain() {
        // Genesis block (first block, no previous hash)
        chain.add(new Block("Genesis Block", "0"));
    }

    public void addBlock(String data) {
        Block previousBlock = chain.get(chain.size() - 1);
        Block newBlock = new Block(data, previousBlock.getHash());
        chain.add(newBlock);
        System.out.println("Block added: " + newBlock.getHash());
    }

    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            // Check if current block's hash is still correct
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                System.out.println("Tampering detected at block " + i);
                return false;
            }

            // Check if current block points to correct previous hash
            if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                System.out.println("Chain broken at block " + i);
                return false;
            }
        }
        return true;
    }

    public void printChain() {
        for (int i = 0; i < chain.size(); i++) {
            Block b = chain.get(i);
            System.out.println("Block " + i + " | Data: " + b.getData() + " | Hash: " + b.getHash());
        }
    }

    // Demo/test run
    public static void main(String[] args) {
        Blockchain medicalChain = new Blockchain();

        medicalChain.addBlock("Patient: Ravi Kumar | Diagnosis: Fever | Doctor: Dr. Sharma");
        medicalChain.addBlock("Patient: Anita Joshi | Diagnosis: Fracture | Doctor: Dr. Patil");

        medicalChain.printChain();

        System.out.println("Is chain valid? " + medicalChain.isChainValid());
    }
}