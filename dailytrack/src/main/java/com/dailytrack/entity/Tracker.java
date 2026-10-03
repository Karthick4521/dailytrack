package com.dailytrack.entity;
import jakarta.persistence.*; import java.time.LocalDate;
@Entity @Table(name="trackers",indexes=@Index(columnList="userId"))
public class Tracker { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; public Long userId;
 public String name,category,type,unit; public boolean archived; public LocalDate createdAt; }
