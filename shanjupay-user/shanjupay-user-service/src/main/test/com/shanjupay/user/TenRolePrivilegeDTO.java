package com.shanjupay.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class TenRolePrivilegeDTO implements Serializable {

    private Long tenantId;
    private String roleCode;
    private String privilegeCode;
}
