package com.example.Todo.schedule;

import com.example.Todo.mail.MailService;
import com.example.Todo.user.User;
import com.example.Todo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@EnableScheduling
@RequiredArgsConstructor
public class ScheduledTasks {
    private final MailService mailService;
    private final UserRepository userRepository;

    // Every day at 6AM
    @Scheduled(cron = "0 0 6 * * ?")
    public void notifyAboutTasks() {
        log.info("Send notify about tasks to users");

        List<User> users = userRepository.findAllByTodayItemsExist();

        for (User user : users) {
            mailService.sendSimpleMessage(user.getEmail(), "Daily reminder", "Hi! You have a tasks");
        }
    }
}
