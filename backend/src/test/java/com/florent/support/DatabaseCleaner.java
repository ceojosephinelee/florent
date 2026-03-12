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
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        jdbcTemplate.execute("TRUNCATE TABLE curation_request");
        jdbcTemplate.execute("TRUNCATE TABLE flower_shop");
        jdbcTemplate.execute("TRUNCATE TABLE seller");
        jdbcTemplate.execute("TRUNCATE TABLE buyer");
        jdbcTemplate.execute("TRUNCATE TABLE \"user\"");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
        fakeNotification.clear();
    }
}
