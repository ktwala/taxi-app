package com.taxiservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "membership_application_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipApplicationDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long documentId;

    @Column(name = "application_id", nullable = false)
    private Long applicationId;

    @NotBlank(message = "Document type is required")
    @Column(name = "document_type", nullable = false, length = 50)
    private String documentType;

    @NotBlank(message = "Document path is required")
    @Column(name = "document_path", nullable = false, length = 255)
    private String documentPath;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt = LocalDateTime.now();
}
