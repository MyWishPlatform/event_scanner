package io.mywish.bot.integration.services;

import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.ProductStatistics;
import io.lastwill.eventscan.model.UserStatistics;
import io.lastwill.eventscan.repositories.NetworkRepository;
import io.lastwill.eventscan.repositories.ProductRepository;
import io.lastwill.eventscan.repositories.UserRepository;
import io.mywish.bot.service.InformationProvider;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class BotStatisticProvider implements InformationProvider {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NetworkRepository networkRepository;

    private final Map<NetworkType, String> networkNames = new HashMap<NetworkType, String>() {{
        put(NetworkType.ETHEREUM_ROPSTEN, "Ropsten");
        put(NetworkType.ETHEREUM_MAINNET, "Ethereum");
        put(NetworkType.RSK_MAINNET, "RSK");
        put(NetworkType.RSK_TESTNET, "RSK Testnet");
    }};

    @Override
    public SendMessage getInformation(String userName) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Users\n");
        long totalUsers = userRepository.getUserStatistics()
                .stream()
                .peek(userStatistics -> {
                    if (userStatistics.isRegistered()) {
                        stringBuilder.append("  Registered: ");
                    }
                    else {
                        stringBuilder.append("  Anonymous: ");
                    }
                    stringBuilder.append("*").append(userStatistics.getUserCount()).append("*\n");
                })
                .map(UserStatistics::getUserCount)
                .reduce(Long::sum)
                .orElse(0L);

        stringBuilder.append("*Total: ").append(totalUsers).append("*\n");

        networkRepository.findAll()
                .forEach(network -> {
                    stringBuilder.append("\n*")
                            .append(networkNames.getOrDefault(network.getType(), "Unknown Network"))
                            .append("*\n");

                    val grouped = productRepository.getProductStatistics(network.getId())
                            .stream()
                            .collect(Collectors.groupingBy(
                                    ProductStatistics::getContractType,
                                    Collectors.groupingBy(
                                            ProductStatistics::getContractState,
                                            Collectors.summingInt(ProductStatistics::getContractCount)
                                    )
                            ));
                    AtomicInteger overallContracts = new AtomicInteger();
                    grouped
                            .forEach((type, states) -> {
                                stringBuilder.append("Contact *").append(type).append("*:\n");
                                states.forEach((state, count) -> {
                                    stringBuilder.append("  ").append(state.replaceAll("_", "\\\\_")).append(": *").append(count).append("*\n");
                                });
                                int total = states.values().stream().reduce(Integer::sum).orElse(0);
                                overallContracts.addAndGet(total);
                                stringBuilder.append("  *Total: ").append(total).append("*\n");
                            });
                    stringBuilder.append("*Total contracts: ").append(overallContracts.get()).append("*\n");
                });


        return new SendMessage().setText(stringBuilder.toString()).enableMarkdown(true);
    }

    @Override
    public boolean isAvailable(String userName) {
        return true;
    }
}
