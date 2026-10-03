package com.dailytrack.entity;
import jakarta.persistence.*; import java.time.LocalDate;
@Entity @Table(name="daily_records",indexes={@Index(columnList="userId"),@Index(columnList="trackerId"),@Index(columnList="recordDate")})
public class DailyRecord { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; public Long userId,trackerId;
 public LocalDate recordDate; @Column(name="val") public double value; @Column(length=500) public String notes; }
