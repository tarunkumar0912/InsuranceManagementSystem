package com.example.insurance.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.insurance.entity.ClaimDocument;
import com.example.insurance.entity.InsuranceClaim;
import com.example.insurance.repository.ClaimDocumentRepository;
import com.example.insurance.repository.InsuranceClaimRepository;

@Service
public class ClaimDocumentService {

    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png"
    );

    private final ClaimDocumentRepository documentRepository;
    private final InsuranceClaimRepository claimRepository;
    private final Path uploadDirectory;

    public ClaimDocumentService(
            ClaimDocumentRepository documentRepository,
            InsuranceClaimRepository claimRepository,
            @Value("${insurance.file.upload-dir:uploads/claim-documents}")
            String uploadDirectory) {

        this.documentRepository = documentRepository;
        this.claimRepository = claimRepository;
        this.uploadDirectory = Paths.get(uploadDirectory)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.uploadDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not initialize document storage", exception);
        }
    }

    @Transactional
    public ClaimDocument upload(Long claimId, MultipartFile file) {

        InsuranceClaim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Claim not found"));

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "A file is required");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.CONTENT_TOO_LARGE,
                    "File size must not exceed 5 MB");
        }

        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only PDF, JPEG and PNG files are allowed");
        }

        String originalFilename =
                StringUtils.cleanPath(
                        file.getOriginalFilename() == null
                                ? "document"
                                : file.getOriginalFilename());

        if (originalFilename.isBlank()
                || originalFilename.contains("..")
                || originalFilename.contains("/")
                || originalFilename.contains("\\")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid filename");
        }

        String storedFilename = UUID.randomUUID().toString();
        Path destination = uploadDirectory.resolve(storedFilename)
                .normalize();

        if (!destination.startsWith(uploadDirectory)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid storage path");
        }

        try {
            Files.copy(
                    file.getInputStream(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not store the uploaded file");
        }

        try {
            ClaimDocument document = new ClaimDocument();
            document.setClaim(claim);
            document.setOriginalFilename(originalFilename);
            document.setStoredFilename(storedFilename);
            document.setContentType(contentType);
            document.setFileSize(file.getSize());
            document.setUploadedAt(LocalDateTime.now());

            return documentRepository.save(document);

        } catch (RuntimeException exception) {
            try {
                Files.deleteIfExists(destination);
            } catch (IOException ignored) {
                // Keep the original database error.
            }
            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public List<ClaimDocument> getDocuments(Long claimId) {

        if (!claimRepository.existsById(claimId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Claim not found");
        }

        return documentRepository
                .findByClaim_IdOrderByUploadedAtDesc(claimId);
    }

    @Transactional(readOnly = true)
    public ClaimDocument getDocumentMetadata(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Document not found"));
    }

    public Resource download(ClaimDocument document) {

        Path path = uploadDirectory
                .resolve(document.getStoredFilename())
                .normalize();

        if (!path.startsWith(uploadDirectory)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid document path");
        }

        try {
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Document file not found");
            }

            return resource;

        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not read document");
        }
    }

    public MediaType getMediaType(ClaimDocument document) {
        try {
            return MediaType.parseMediaType(document.getContentType());
        } catch (Exception exception) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}