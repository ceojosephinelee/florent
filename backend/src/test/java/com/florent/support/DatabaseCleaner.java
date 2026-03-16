package com.florent.support;

import com.florent.fake.FakeSaveNotificationUseCase;
import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class DatabaseCleaner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FakeSaveNotificationUseCase fakeNotification;

    @Before(order = 0)
    public void clean() {
        jdbcTemplate.execute(
                "TRUNCATE TABLE outbox_event, notification, user_device, "
                        + "payment, reservation, proposal, curation_request, "
                        + "flower_shop, seller, buyer, \"user\" CASCADE");
        fakeNotification.clear();
    }
}
