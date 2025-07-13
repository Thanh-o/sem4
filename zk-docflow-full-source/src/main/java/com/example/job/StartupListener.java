package com.example.job;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Timer;
import java.util.TimerTask;

@WebListener
public class StartupListener implements ServletContextListener {

    private Timer timer;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                DeadlineChecker.checkAndMarkOverdue();
            }
        }, 0, 10 * 60 * 1000); // chạy mỗi 10 phút
        System.out.println("✅ [StartupListener] Khởi động job kiểm tra quá hạn.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (timer != null) {
            timer.cancel();
            System.out.println("🛑 [StartupListener] Dừng job kiểm tra quá hạn.");
        }
    }
}
