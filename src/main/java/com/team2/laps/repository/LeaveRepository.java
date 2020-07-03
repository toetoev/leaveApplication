package com.team2.laps.repository;

import java.util.List;

import com.team2.laps.model.Leave;
import com.team2.laps.model.LeaveType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, String> {
    @Query(value = "SELECT * FROM leaves WHERE YEAR(start_date) = YEAR(CURDATE()) AND YEAR(end_date) = YEAR(CURDATE()) AND user_id = :id ORDER BY start_date", nativeQuery = true)
    List<Leave> findCurrentYearLeaveByUserOrderByStartDate(@Param("id") String id);

    @Query(value = "SELECT leaves.* FROM leaves, users WHERE leaves.user_id = users.id AND start_date >= CURDATE() AND report_to = :id ORDER BY start_date", nativeQuery = true)
    List<Leave> findLeaveForApprovalBySubordinatesOrderByStartDate(@Param("id") String id);

    @Query(value = "SELECT COALESCE(SUM(DATEDIFF(end_date, start_date)), 0) FROM leaves WHERE leaves.user_id = :id AND leave_type = :leave_type AND status = '4' AND YEAR(start_date) = YEAR(CURDATE()) AND YEAR(end_date) = YEAR(CURDATE())", nativeQuery = true)
    int countCurrentYearLeaveUsed(@Param("id") String id, @Param("leave_type") LeaveType leaveType);
}
