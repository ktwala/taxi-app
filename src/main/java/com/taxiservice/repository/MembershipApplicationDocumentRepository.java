package com.taxiservice.repository;

import com.taxiservice.entity.MembershipApplicationDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembershipApplicationDocumentRepository extends JpaRepository<MembershipApplicationDocument, Long> {

    List<MembershipApplicationDocument> findByApplicationId(Long applicationId);

    List<MembershipApplicationDocument> findByDocumentType(String documentType);

    void deleteByApplicationId(Long applicationId);
}
