package com.dailytrack.dto;
import jakarta.validation.constraints.*;
public record TrackerReq(@NotBlank(message="Give your tracker a name") @Size(max=60,message="Name is too long") String name,
 @NotBlank String category,@Pattern(regexp="count|duration|distance|value|completion",message="Choose a tracking type") String type,@Size(max=20) String unit,Boolean archived){}
