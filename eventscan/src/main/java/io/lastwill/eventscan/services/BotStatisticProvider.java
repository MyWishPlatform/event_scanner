package io.lastwill.eventscan.services;

import io.lastwill.eventscan.model.ProductStatistics;
import io.lastwill.eventscan.repositories.ProductRepository;
import io.mywish.bot.service.InformationProvider;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class BotStatisticProvider implements InformationProvider {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public SendMessage getInformation(String userName) {
        StringBuilder stringBuilder = new StringBuilder();
        val grouped = productRepository.getProductStatistics()
                .stream()
                .collect(Collectors.groupingBy(
                        ProductStatistics::getContractType,
                        Collectors.groupingBy(
                                ProductStatistics::getContractState,
                                Collectors.summingInt(ProductStatistics::getContractCount)
                        )
                ));
        AtomicInteger overall = new AtomicInteger();
        grouped
                .forEach((type, states) -> {
                    stringBuilder.append("Contact ").append(type).append(":\n");
                    states.forEach((state, count) -> {
                        stringBuilder.append("\t").append(state).append(": ").append(count).append("\n");
                    });
                    int total = states.values().stream().reduce(Integer::sum).orElse(0);
                    overall.addAndGet(total);
                    stringBuilder.append("\tTotal: ").append(total).append("\n");
                });
        stringBuilder.append("Total contracts: ").append(overall.get());
        return new SendMessage().setText(stringBuilder.toString());
    }

    @Override
    public boolean isAvailable(String userName) {
        return true;
    }
}
