package com.base.services.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class TenantResponse {

    private UUID id;
    private String name;
    private String mobileNumber;
    private String emailId;
    private String driver;
    private Integer port;
    private String dbUrl;
    private String dbName;
    private String dbUsername;
    private String dbPassword;

}
