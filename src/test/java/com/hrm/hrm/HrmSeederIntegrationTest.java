package com.hrm.hrm;

import com.hrm.Service.hrm.HrmSeeder;
import com.hrm.repository.hrm.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/** Kiểm tra khởi tạo seed lần đầu, chạy lại nhiều lần và không trùng lặp. */
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HrmSeederIntegrationTest {

    @Autowired HrmSeeder seeder;
    @Autowired HrmReferenceDataRepository referenceDataRepository;
    @Autowired HrmSeedLogRepository seedLogRepository;
    @Autowired HrmEmployeeRepository employeeRepository;
    @Autowired HrmAttendanceRepository attendanceRepository;
    @Autowired HrmLeaveRequestRepository leaveRequestRepository;
    @Autowired HrmCandidateRepository candidateRepository;
    @Autowired HrmGrowthRepository growthRepository;

    @Test
    @Order(1)
    void firstRun_importsReferenceAndSampleData() {
        assertThat(referenceDataRepository.count()).isPositive();
        assertThat(referenceDataRepository.findByCategoryOrderBySortOrderAsc("departments"))
                .extracting("code").contains("Engineering", "Finance");
        assertThat(employeeRepository.count()).isEqualTo(8);
        assertThat(attendanceRepository.count()).isEqualTo(112);
        assertThat(leaveRequestRepository.count()).isEqualTo(6);
        assertThat(candidateRepository.count()).isEqualTo(7);
        assertThat(growthRepository.count()).isEqualTo(7);
        assertThat(seedLogRepository.existsById("HRM-TEST-SAMPLE")).isTrue();
    }

    @Test
    @Order(2)
    void rerunSeed_isIdempotent_noDuplicates() {
        long employeesBefore = employeeRepository.count();
        long attendanceBefore = attendanceRepository.count();
        long referenceBefore = referenceDataRepository.count();

        seeder.run(null);
        seeder.run(null);

        assertThat(employeeRepository.count()).isEqualTo(employeesBefore);
        assertThat(attendanceRepository.count()).isEqualTo(attendanceBefore);
        assertThat(referenceDataRepository.count()).isEqualTo(referenceBefore);
    }
}
