package com.raithanna.dairy.RaithannaDairy.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Data
public class customer {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    private String code;
    private Integer custno;
    private String mobileNo;
     private String Email;


    //MobileNo and Email



}
