package com.dailytrack.dto;
import jakarta.validation.constraints.*;
public record GoalReq(@NotBlank(message="Give your goal a title") String title,Long trackerId,@Positive(message="Target must be greater than zero") double target,@Pattern(regexp="daily|weekly|monthly") String period){}
