package com.hrm.repository.user;

import com.hrm.Entity.user.Contracts;
import com.hrm.Entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractsRepository extends JpaRepository<Contracts, Integer>{
	@Query("SELECT c FROM Contracts c WHERE" +
			"(:status IS NULL OR c.status = :status) AND" +
			"(:id IS NULL OR c.employee.id = :id) AND"+
			"(:name IS NULL OR c.employee.name LIKE %:name%)")
	Page<Contracts> findByName
			(String name, Integer status, Integer id, Pageable pageable);

	// hợp đồng sắp đến ngày kết thúc (dùng cho nhắc việc)
	@Query("SELECT c FROM Contracts c WHERE c.dismissal_date IS NOT NULL AND c.dismissal_date BETWEEN :from AND :to")
	List<Contracts> findExpiring(@org.springframework.data.repository.query.Param("from") String from,
								 @org.springframework.data.repository.query.Param("to") String to);
}
