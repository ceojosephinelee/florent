package com.florent.support;

import com.florent.fake.FakeSaveNotificationUseCase;
import com.florent.domain.notification.SaveNotificationUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestNotificationConfig {

    @Bean
    public FakeSaveNotificationUseCase fakeSaveNotificationPort() {
        return new FakeSaveNotificationUseCase();
    }

    @Bean
    @Primary
    public SaveNotificationUseCase saveNotificationPort(FakeSaveNotificationUseCase fake) {
        return fake;
    }
}