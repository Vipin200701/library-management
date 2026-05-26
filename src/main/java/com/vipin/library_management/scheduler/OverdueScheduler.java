package com.vipin.library_management.scheduler;

import com.vipin.library_management.service.BorrowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OverdueScheduler {

    private final BorrowService borrowService;

    // Runs every day at midnight  →  "0 0 0 * * *"
    // For testing use every minute →  "0 * * * * *"
    @Scheduled(cron = "0 0 0 * * *")
    public void markOverdueBooks() {
        log.info("⏰ Overdue scheduler started at {}", LocalDateTime.now());

        int count = borrowService.markOverdueRecords();

        if (count > 0) {
            log.warn("📌 {} borrow record(s) marked as OVERDUE", count);
        } else {
            log.info("✅ No overdue records found");
        }
    }
}