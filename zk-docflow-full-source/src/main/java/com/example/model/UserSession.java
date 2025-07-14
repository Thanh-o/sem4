package com.example.model;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class UserSession {
    private String sessionId;
    private String ipAddress;
    private String userAgent;
    private Date lastAccessed;
}
