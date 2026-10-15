package com.carrental.modules.user.repository;

import com.carrental.modules.user.entity.IdentityDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IdentityDocumentRepository extends JpaRepository<IdentityDocument, Long> {
    List<IdentityDocument> findByUserIdOrderByCreatedAtDesc(Long userId);
}
