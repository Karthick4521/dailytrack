package com.dailytrack.entity;
import jakarta.persistence.*;
@Entity @Table(name="goals",indexes=@Index(columnList="userId"))
public class Goal { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; public Long userId,trackerId;
 public String title,period; public double target; }
