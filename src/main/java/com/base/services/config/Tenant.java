package com.base.services.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tenant {

    private String id;
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
