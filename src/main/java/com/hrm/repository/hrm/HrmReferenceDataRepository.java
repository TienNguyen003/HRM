package com.hrm.repository.hrm;

import com.hrm.Entity.hrm.HrmReferenceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HrmReferenceDataRepository extends JpaRepository<HrmReferenceData, String> {
    List<HrmReferenceData> findByCategoryOrderBySortOrderAsc(String category);

    @Modifying
    @Query("delete from HrmReferenceData r where r.category = :category")
    void deleteByCategory(@Param("category") String category);
}
