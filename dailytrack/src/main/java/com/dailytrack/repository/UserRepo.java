package com.dailytrack.repository;
import com.dailytrack.entity.AppUser; import org.springframework.data.jpa.repository.JpaRepository; import java.util.Optional;
public interface UserRepo extends JpaRepository<AppUser,Long> { Optional<AppUser> findByEmail(String e); boolean existsByEmail(String e); }
