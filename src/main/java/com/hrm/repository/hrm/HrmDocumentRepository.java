package com.hrm.repository.hrm;

import com.hrm.Entity.hrm.HrmDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HrmDocumentRepository extends JpaRepository<HrmDocument, String> {
    List<HrmDocument> findAllByEmployee_Id(String employeeId);

    void deleteAllByEmployee_Id(String employeeId);
}
