package com.example.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class AuditLog {
    private int id;
    private int userId;
    private String userName;
    private String actionType;
    private String description;
    private Timestamp actionTime;
}