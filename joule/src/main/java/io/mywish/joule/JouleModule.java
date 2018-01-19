package io.mywish.joule;

import io.mywish.joule.contracts.JouleAPI;
import io.mywish.joule.service.RemoteTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.web3j.protocol.Web3j;
import org.web3j.tx.TransactionManager;

import java.math.BigInteger;

@AutoConfigurationPackage
@SpringBootConfiguration
@ComponentScan
@PropertySource("classpath:joule.properties")
@EntityScan
@EnableScheduling
public class JouleModule implements SchedulingConfigurer {
    private final BigInteger defaultGasPrice = BigInteger.valueOf(20000000000L);
    private final BigInteger defaultGasLimit = BigInteger.valueOf(300000L);
    @Bean
    public JouleAPI jouleAPI(
            @Value("${io.mywish.joule.contract.address}") String address,
            Web3j web3j,
            TransactionManager transactionManager
    ) {
        return JouleAPI.load(address, web3j, transactionManager, defaultGasPrice, defaultGasLimit);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

        threadPoolTaskScheduler.setPoolSize(10);
        threadPoolTaskScheduler.setThreadNamePrefix("joule-scheduled-");
        threadPoolTaskScheduler.initialize();

        taskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
    }
}
