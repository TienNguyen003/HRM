package com.hrm.repository.job;

import com.hrm.Entity.job.Disqualified;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisqualifiedRepository extends JpaRepository<Disqualified, Integer> {
    boolean existsByEmailAndPhone(String email, String phone);
}
