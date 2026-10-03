package com.dailytrack.repository;
import com.dailytrack.entity.Goal; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface GoalRepo extends JpaRepository<Goal,Long> { List<Goal> findByUserId(Long u); Optional<Goal> findByIdAndUserId(Long id,Long u); void deleteByTrackerId(Long t); }
