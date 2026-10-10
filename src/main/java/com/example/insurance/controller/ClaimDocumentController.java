package com.example.insurance.controller;

import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.insurance.entity.ClaimDocument;
import com.example.insurance.service.ClaimDocumentService;

@RestController
@RequestMapping("/api/claims/{claimId}/documents")
public class ClaimDocumentController {

    private final ClaimDocumentService documentService;

    public ClaimDocumentController(
            ClaimDocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> upload(
            @PathVariable Long claimId,
            @RequestParam("file") MultipartFile file) {

        ClaimDocument document =
                documentService.upload(claimId, file);

        return ResponseEntity.status(201).body(Map.of(
                "documentId", document.getId(),
                "claimId", claimId,
                "filename", document.getOriginalFilename(),
                "contentType", document.getContentType(),
                "size", document.getFileSize(),
                "message", "Document uploaded successfully"
        ));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> list(
            @PathVariable Long claimId) {

        List<Map<String, Object>> result =
                documentService.getDocuments(claimId)
                        .stream()
                        .map(document -> Map.<String, Object>of(
                                "documentId", document.getId(),
                                "filename", document.getOriginalFilename(),
                                "contentType", document.getContentType(),
                                "size", document.getFileSize(),
                                "uploadedAt", document.getUploadedAt()
                        ))
                        .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{documentId}/download")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> download(
            @PathVariable Long claimId,
            @PathVariable Long documentId) {

        ClaimDocument document =
                documentService.getDocumentMetadata(documentId);

        if (!document.getClaim().getId().equals(claimId)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = documentService.download(document);

        return ResponseEntity.ok()
                .contentType(documentService.getMediaType(document))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(document.getOriginalFilename())
                                .build()
                                .toString())
                .header("X-Content-Type-Options", "nosniff")
                .body(resource);
    }
}