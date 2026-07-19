package com.medvault.blockchain;

import com.medvault.entity.BlockEntity;
import com.medvault.repository.BlockRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Re-implementation of the project's original Blockchain.java, adapted to
 * persist each block as a row so the chain survives restarts and can be
 * audited from the database, while keeping the same SHA-256 chaining logic
 * (index + timestamp + data + previousHash -> hash).
 */
@Service
public class BlockchainService {

    private final BlockRepository blockRepository;

    public BlockchainService(BlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }

    /** Adds a new block containing the given data and returns its hash. */
    public synchronized String addBlock(String data) {
        List<BlockEntity> chain = blockRepository.findAllByOrderByBlockIndexAsc();
        long nextIndex = chain.isEmpty() ? 0 : chain.get(chain.size() - 1).getBlockIndex() + 1;
        String previousHash = chain.isEmpty() ? "0" : chain.get(chain.size() - 1).getHash();

        String hash = computeHash(nextIndex, data, previousHash);
        BlockEntity block = new BlockEntity(nextIndex, data, previousHash, hash);
        blockRepository.save(block);
        return hash;
    }

    /** Recomputes every hash in the chain and confirms nothing was tampered with. */
    public boolean isChainValid() {
        List<BlockEntity> chain = blockRepository.findAllByOrderByBlockIndexAsc();
        String expectedPrev = "0";
        for (BlockEntity b : chain) {
            if (!b.getPreviousHash().equals(expectedPrev)) return false;
            String recomputed = computeHash(b.getBlockIndex(), b.getData(), b.getPreviousHash());
            if (!recomputed.equals(b.getHash())) return false;
            expectedPrev = b.getHash();
        }
        return true;
    }

    public List<BlockEntity> getChain() {
        return blockRepository.findAllByOrderByBlockIndexAsc();
    }

    private String computeHash(long index, String data, String previousHash) {
        String input = index + data + previousHash;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
