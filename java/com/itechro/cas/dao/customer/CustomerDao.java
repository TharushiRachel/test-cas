package com.itechro.cas.dao.customer;

import com.itechro.cas.commons.constants.AppsConstants;
import com.itechro.cas.model.domain.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerDao extends JpaRepository<Customer, Integer> {

    Customer findByCustomerFinancialIDAndStatus(String finacleID, AppsConstants.Status status);

    List<Customer> findAllByCustomerFinancialIDAndStatus(String finacleID, AppsConstants.Status status);

    //Customer findByCustomerFinancialID(String customerFinancialID);

//    @Query(value = "SELECT c FROM t_customer c WHERE c.status='ACT' c.CUSTOMER_FINANCIAL_ID = :customerFinancialID", nativeQuery = true)
//    Customer findCustomerByFinancialID(@Param("customerFinancialID") String customerFinancialID);

    @Query(value = "SELECT * FROM t_customer WHERE status = 'ACT' AND CUSTOMER_FINANCIAL_ID = :customerFinancialID", nativeQuery = true)
    Customer findCustomerByFinancialID(@Param("customerFinancialID") String customerFinancialID);

}
