package com.florent.support;

import com.florent.fake.FakeSaveNotificationPort;
import com.florent.domain.notification.SaveNotificationUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestNotificationConfig {

    @Bean
    public FakeSaveNotificationPort fakeSaveNotificationPort() {
        return new FakeSaveNotificationPort();
    }

    @Bean
    @Primary
    public SaveNotificationUseCase saveNotificationPort(FakeSaveNotificationPort fake) {
        return fake;
    }
}