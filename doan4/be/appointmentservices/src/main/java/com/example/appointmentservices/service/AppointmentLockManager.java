package com.example.appointmentservices.service;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class AppointmentLockManager {
    private final Map<String, Map<String, Map<String, Boolean>>> lockMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void lockSlot(String doctorId, String date, String time) {
        lockMap.computeIfAbsent(doctorId, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(date, k -> new ConcurrentHashMap<>())
                .put(time, true);

        scheduler.schedule(() -> unlockSlotIfNotPaid(doctorId, date, time), 5, TimeUnit.MINUTES);
    }

    public boolean isSlotLocked(String doctorId, String date, String time) {
        return lockMap.getOrDefault(doctorId, Collections.emptyMap())
                .getOrDefault(date, Collections.emptyMap())
                .getOrDefault(time, false);
    }

    public void unlockSlot(String doctorId, String date, String time) {
        lockMap.getOrDefault(doctorId, Collections.emptyMap())
                .getOrDefault(date, Collections.emptyMap())
                .remove(time);
    }

    public void unlockSlotIfNotPaid(String doctorId, String date, String time) {
        unlockSlot(doctorId, date, time);
    }

    public void confirmPayment(String doctorId, String date, String time) {
        unlockSlot(doctorId, date, time);
    }
}