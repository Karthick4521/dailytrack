package com.dailytrack.entity;
import jakarta.persistence.*; import java.time.LocalDate;
@Entity @Table(name="users")
public class AppUser { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; public String fullName;
 @Column(unique=true,nullable=false) public String email; public String passwordHash; public LocalDate createdAt; }
