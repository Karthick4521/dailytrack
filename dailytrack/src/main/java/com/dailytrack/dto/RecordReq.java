package com.dailytrack.dto;
import jakarta.validation.constraints.*; import java.time.LocalDate;
public record RecordReq(@NotNull Long trackerId,LocalDate date,@Positive(message="Value must be greater than zero") double value,@Size(max=500) String notes){}
