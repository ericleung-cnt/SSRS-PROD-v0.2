package org.mardep.ssrs.service;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class SpringAsyncConfig {

	@Bean(name = "dnsAsnycExecutor")
    public Executor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor tpe = new ThreadPoolTaskExecutor();
		tpe.setMaxPoolSize(1);
        return tpe;
    }
}
