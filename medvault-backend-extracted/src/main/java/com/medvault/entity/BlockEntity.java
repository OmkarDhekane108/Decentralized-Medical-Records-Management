package com.medvault.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "blockchain")
public class BlockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long blockIndex;
    private long timestamp;

    @Column(length = 4000)
    private String data; // the medical record payload that was hashed

    private String previousHash;
    private String hash;

    public BlockEntity() {}

    public BlockEntity(long blockIndex, String data, String previousHash, String hash) {
        this.blockIndex = blockIndex;
        this.timestamp = Instant.now().toEpochMilli();
        this.data = data;
        this.previousHash = previousHash;
        this.hash = hash;
    }

    public Long getId() { return id; }
    public long getBlockIndex() { return blockIndex; }
    public long getTimestamp() { return timestamp; }
    public String getData() { return data; }
    public String getPreviousHash() { return previousHash; }
    public String getHash() { return hash; }
}
