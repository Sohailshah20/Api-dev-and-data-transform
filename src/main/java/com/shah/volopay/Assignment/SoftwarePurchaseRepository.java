package com.shah.volopay.Assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SoftwarePurchaseRepository extends JpaRepository<SoftwarePurchase,Integer> {


    public List<SoftwarePurchase> findAllByDepartmentAndDateBetween(String department,Date startDate,Date endDate);
    public List<SoftwarePurchase> findAllByDateBetween(Date startDate,Date endDate);
    public List<SoftwarePurchase> findAllBySoftwareAndDateBetween(String software,Date date1, Date date2);


}