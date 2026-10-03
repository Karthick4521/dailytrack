package com.dailytrack.repository;
import com.dailytrack.entity.Tracker; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface TrackerRepo extends JpaRepository<Tracker,Long> { List<Tracker> findByUserId(Long u); List<Tracker> findByUserIdAndArchivedFalse(Long u); Optional<Tracker> findByIdAndUserId(Long id,Long u); }
