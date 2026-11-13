package com.hrm.repository.document;

import com.hrm.Entity.document.Document;
import com.hrm.enums.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {
    @Query("SELECT d FROM Document d WHERE " +
            "(:type IS NULL OR d.type = :type) AND " +
            "(:name IS NULL OR d.name LIKE %:name%)")
    Page<Document> findByFilters(@Param("type") String type,
                                 @Param("name") String name,
                                 Pageable pageable);

    Page<Document> findByType(String type, Pageable pageable);

    long countByStatus(DocumentStatus status);

    boolean existsByName(String name);
}

