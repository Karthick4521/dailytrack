package com.dailytrack.repository;
import com.dailytrack.entity.DailyRecord; import org.springframework.data.jpa.repository.JpaRepository; import java.time.LocalDate; import java.util.*;
public interface RecordRepo extends JpaRepository<DailyRecord,Long> {
 List<DailyRecord> findByUserId(Long u); Optional<DailyRecord> findByIdAndUserId(Long id,Long u);
 List<DailyRecord> findByUserIdAndRecordDateBetweenOrderByRecordDateDesc(Long u,LocalDate f,LocalDate t);
 List<DailyRecord> findByUserIdAndTrackerIdAndRecordDateBetween(Long u,Long t,LocalDate f,LocalDate to); void deleteByTrackerId(Long t); }
