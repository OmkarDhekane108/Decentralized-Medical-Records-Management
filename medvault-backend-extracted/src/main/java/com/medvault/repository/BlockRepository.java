package com.medvault.repository;

import com.medvault.entity.BlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BlockRepository extends JpaRepository<BlockEntity, Long> {
    List<BlockEntity> findAllByOrderByBlockIndexAsc();
}
