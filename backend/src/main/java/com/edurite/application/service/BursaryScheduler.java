package com.edurite.application.service;

import com.edurite.domain.model.Enums.BursaryStatus;
import com.edurite.domain.repo.BursaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component @RequiredArgsConstructor @Slf4j
public class BursaryScheduler {
    private final BursaryRepository repo;
    @Scheduled(cron = "0 0 1 * * *")
    public void archiveEnded(){
        var ended = repo.findByStatusAndEndDateBefore(BursaryStatus.ACTIVE, LocalDate.now());
        ended.forEach(b->b.setStatus(BursaryStatus.ARCHIVED));
        repo.saveAll(ended);
        if(!ended.isEmpty()) log.info("Archived {} bursaries", ended.size());
    }
}
