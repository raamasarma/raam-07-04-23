package com.raithanna.dairy.RaithannaDairy.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Data
public class saleOrder {
    @Id
    @GeneratedValue
    private int Id;

    private String branch;
    private String custCode;
    private Date date;
    private String shift;
    private int orderNo;
    private double disc;
    private double comm;
    private double amount;
    private double unitRate;
    private double netAmount;
    private double OsAmount;
    private double WAmount;
    private String remove;
    private String remarks;
    private String entryUser;
    private String updUser;
    private Date updatedd;
    private Date createdd;
    private String recptNo;
    private Date recDate;
    private String customerName;
    private String sourcedata;

}
