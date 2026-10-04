package com.pharmacy.inventory.scheduler;

import com.pharmacy.inventory.service.StockAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * NOTE: @EnableScheduling is already declared on PharmacyInventoryApplication
 * (member1's main class) in the shared repo, so nothing extra is needed for
 * this scheduler to run.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StockAlertScheduler {

    private final StockAlertService stockAlertService;

    /**
     * Runs every hour on the hour. Checks all medicines for low stock
     * (quantity < reorderLevel) and near expiry (within 30 days) and
     * creates StockAlert records for anything not already flagged.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void checkStockAndExpiry() {
        log.info("Running scheduled stock & expiry check...");
        stockAlertService.runStockAndExpiryCheck();
        log.info("Scheduled stock & expiry check completed.");
    }
}
