package com.assignment.james.maybank.fileloader.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class Transaction {
    private String accountNumber;
    private double trxAmount;
    private String description;
    private LocalDate trxDate;
    private LocalTime trxTime;
    private int customerId;

    public void setTrxDate (String trxDate){
        if (trxDate == null) {
           this.trxDate = null;
        }
        this.trxDate = LocalDate.parse(trxDate,  DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public void setTrxTime (String trxTime){
        if (trxTime == null) {
            this.trxTime = null;
        }
        this.trxTime = LocalTime.parse(trxTime,  DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
