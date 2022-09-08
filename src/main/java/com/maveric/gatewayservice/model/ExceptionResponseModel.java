package com.maveric.gatewayservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ExceptionResponseModel {
    private String code ;
    private String message;
    private String description;
    private Date  date;
}
