package io.mywish.bot.integration.commands;

import io.lastwill.eventscan.model.*;
import io.lastwill.eventscan.repositories.*;
import io.mywish.bot.service.BotCommand;
import io.mywish.bot.service.ChatContext;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class BotCommandTokensStats implements BotCommand {
    @Getter
    private final String name = "/tokens";
    @Getter
    private final String usage = "/tokens [days count]";
    @Getter
    private final String description = "Print information about created tokens for last month";

    @Autowired
    private BotUserRepository botUserRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public void execute(ChatContext context, List<String> args) {
        if (!botUserRepository.existsByChatId(context.getChatId())) {
            context.sendMessage("You don't have access to this function.");
            return;
        }

        int daysCount = 30;
        if (!args.isEmpty()) {
            try {
                daysCount = Integer.parseInt(args.get(0));
            } catch (NumberFormatException ignored) {
            }
        }

        LocalDateTime from = LocalDateTime.now().minusDays(daysCount);

        List<Product> products = new ArrayList<>();
        products.addAll(productRepository.findEthTokensFromDate(from));
        products.addAll(productRepository.findEthIcoFromDate(from));
        products.addAll(productRepository.findEosTokensFromDate(from));
        products.addAll(productRepository.findEosTokensExtFromDate(from));
        products.addAll(productRepository.findEosIcoFromDate(from));
        products.addAll(productRepository.findTronTokensFromDate(from));
        products.addAll(productRepository.findWavesStoFromDate(from));

        StringBuilder messageBuilder = new StringBuilder();
        products.stream()
                .sorted(Comparator.comparing(Product::getCreatedDate))
                .forEach(product -> append(messageBuilder, product));
    }

    private StringBuilder append(StringBuilder messageBuilder, Product product) {
        appendId(messageBuilder, product);
        appendType(messageBuilder, product);
        appendDate(messageBuilder, product);
        appendEmailOrId(messageBuilder, product);

        if (product instanceof ProductToken) {
            append(messageBuilder, (ProductToken) product);
        } else if (product instanceof ProductCrowdsale) {
            append(messageBuilder, (ProductCrowdsale) product);
        } else if (product instanceof ProductTokenEos) {
            append(messageBuilder, (ProductTokenEos) product);
        } else if (product instanceof ProductTokenExtEos) {
            append(messageBuilder, (ProductTokenExtEos) product);
        } else if (product instanceof ProductCrowdsaleEos) {
            append(messageBuilder, (ProductCrowdsaleEos) product);
        } else if (product instanceof ProductTokenTron) {
            append(messageBuilder, (ProductTokenTron) product);
        } else if (product instanceof ProductStoWaves) {
            append(messageBuilder, (ProductStoWaves) product);
        }

        return messageBuilder;
    }

    private StringBuilder appendId(StringBuilder messageBuilder, Product product) {
        return messageBuilder
                .append("\nid: ")
                .append(product.getId());
    }

    private StringBuilder appendType(StringBuilder messageBuilder, Product product) {
        int contractType = product.getContractType();
        String typename = ProductStatistics.PRODUCT_TYPES.getOrDefault(contractType, "unknown");
        return messageBuilder
                .append("\ncontract_type: ")
                .append(typename)
                .append(" (")
                .append(contractType)
                .append(")");
    }

    private StringBuilder appendDate(StringBuilder messageBuilder, Product product) {
        return messageBuilder
                .append("\ndate: ")
                .append(product.getCreatedDate());
    }

    private StringBuilder appendEmailOrId(StringBuilder messageBuilder, Product product) {
        User user = userRepository.findOne(product.getUserId());
        String email = user.getEmail();
        int id = user.getId();

        if (email == null || email.trim().isEmpty()) {
            return messageBuilder
                    .append("\nuser_id: ")
                    .append(id);
        }

        return messageBuilder
                .append("\nemail: ")
                .append(email);
    }

    private void append(StringBuilder messageBuilder, ProductToken product) {
    }

    private void append(StringBuilder messageBuilder, ProductCrowdsale product) {
    }

    private void append(StringBuilder messageBuilder, ProductTokenEos product) {
    }

    private void append(StringBuilder messageBuilder, ProductTokenExtEos product) {
    }

    private void append(StringBuilder messageBuilder, ProductCrowdsaleEos product) {
    }

    private void append(StringBuilder messageBuilder, ProductTokenTron product) {
    }

    private void append(StringBuilder messageBuilder, ProductStoWaves product) {
    }
}
