package com.hrm.repository.user;

import com.hrm.Entity.user.Bank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepository extends JpaRepository<Bank, Integer> {
    @Query("SELECT COUNT(*) > 0 FROM Bank WHERE nameBank = :nameBank AND numberBank = :numberBank")
    boolean existsByNameAndAccount(String nameBank, String numberBank);

    @Query("SELECT b FROM Bank b WHERE" +
            "(:name IS NULL OR b.employee.name LIKE %:name%) AND" +
            "(:status IS NULL OR b.status = :status) AND" +
            "(:priority IS NULL OR b.priority = :priority) AND" +
            "(:nameBank IS NULL OR b.nameBank LIKE %:nameBank%)")
    Page<Bank> findBankByNameAndNameBank
            (String name, Integer status, Integer priority, String nameBank, Pageable pageable);
}
