package com.example.insurance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.insurance.entity.ClaimDocument;

public interface ClaimDocumentRepository
        extends JpaRepository<ClaimDocument, Long> {

    List<ClaimDocument> findByClaim_IdOrderByUploadedAtDesc(
            Long claimId);
}