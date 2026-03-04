package com.example.demo.scheduler;

import com.example.demo.services.ViewingScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ViewingScheduleReminderScheduler {

    @Autowired
    private ViewingScheduleService viewingScheduleService;

    @Scheduled(fixedRate = 60000) // mỗi 1 phút
    public void triggerViewingScheduleReminders() {
        viewingScheduleService.sendReminders();
    }
}
