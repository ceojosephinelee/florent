package com.florent.support;

import com.florent.fake.FakeSaveNotificationPort;
import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class DatabaseCleaner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FakeSaveNotificationPort fakeNotification;

    @Before(order = 0)
    public void clean() {
        jdbcTemplate.execute(
                "TRUNCATE TABLE proposal, curation_request, flower_shop, seller, buyer, \"user\" CASCADE");
        fakeNotification.clear();
    }
}
