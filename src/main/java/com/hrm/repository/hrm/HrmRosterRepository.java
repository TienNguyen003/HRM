package com.hrm.repository.hrm;

import com.hrm.Entity.hrm.HrmRoster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HrmRosterRepository extends JpaRepository<HrmRoster, String> {
    List<HrmRoster> findAllByMonth(String month);
}
