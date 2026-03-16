package com.florent.support;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockSellerSecurityContextFactory.class)
public @interface WithMockSeller {
    long userId() default 1L;
    long sellerId() default 10L;
}
