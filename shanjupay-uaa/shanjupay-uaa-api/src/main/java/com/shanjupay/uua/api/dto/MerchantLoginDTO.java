package com.shanjupay.uua.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class MerchantLoginDTO implements Serializable {

    //@RequestParam("name")String name,
    //@RequestBody Map<String,Object> payload,
    //@RequestParam("effectiveTime")int effectiveTime
    private String mobile;

    private String name;

    private Map<String,Object> payload;

    private int effectiveTime;
}
