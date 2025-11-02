package org.example.skills.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    private String code;
    private  String name;
    private Date birth;
    private  String tel;
    private  String address;
    private  String company;

}
