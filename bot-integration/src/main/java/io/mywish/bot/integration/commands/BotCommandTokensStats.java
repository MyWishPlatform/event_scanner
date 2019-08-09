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
import java.util.Objects;

@Component
public class BotCommandTokensStats implements BotCommand {
    @Getter
    private final String name = "/tokens";
    @Getter
    private final String usage = "[days count]";
    @Getter
    private final String description = "Print information about created tokens for last month";

    @Autowired
    private BotUserRepository botUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ContractRepository contractRepository;

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

        List<ProductTokenCommon> products = new ArrayList<>();
        products.addAll(productRepository.findEthTokensFromDate(from, NetworkType.ETHEREUM_MAINNET));
        products.addAll(productRepository.findEthIcoFromDate(from, NetworkType.ETHEREUM_MAINNET));
        products.addAll(productRepository.findEosTokensFromDate(from, NetworkType.EOS_MAINNET));
        products.addAll(productRepository.findEosTokensExtFromDate(from, NetworkType.EOS_MAINNET));
        products.addAll(productRepository.findEosIcoFromDate(from, NetworkType.EOS_MAINNET));
        products.addAll(productRepository.findTronTokensFromDate(from, NetworkType.TRON_MAINNET));
        products.addAll(productRepository.findWavesStoFromDate(from, NetworkType.WAVES_MAINNET));

        if (products.isEmpty()) {
            context.sendMessage("No contracts");
            return;
        }

        StringBuilder messageBuilder = new StringBuilder();
        products.stream()
                .sorted(Comparator.comparing(Product::getCreatedDate))
                .forEach(product -> append(messageBuilder, product));

        context.sendMessage(messageBuilder.toString());
    }

    private StringBuilder append(StringBuilder messageBuilder, ProductTokenCommon product) {
        appendId(messageBuilder, product);
        appendType(messageBuilder, product);
        appendDate(messageBuilder, product);
        appendEmailOrId(messageBuilder, product);
        appendSymbol(messageBuilder, product);
        appendName(messageBuilder, product);
        appendAddress(messageBuilder, product);
        appendLineBreak(messageBuilder);
        return messageBuilder;
    }

    private StringBuilder appendId(StringBuilder messageBuilder, ProductTokenCommon product) {
        return messageBuilder
                .append("\nid: ")
                .append(product.getId());
    }

    private StringBuilder appendType(StringBuilder messageBuilder, ProductTokenCommon product) {
        int contractType = product.getContractType();
        String typename = ProductStatistics.PRODUCT_TYPES.getOrDefault(contractType, "unknown");
        return messageBuilder
                .append("\ncontract_type: ")
                .append(typename)
                .append(" (")
                .append(contractType)
                .append(")");
    }

    private StringBuilder appendDate(StringBuilder messageBuilder, ProductTokenCommon product) {
        return messageBuilder
                .append("\ndate: ")
                .append(product.getCreatedDate());
    }

    private StringBuilder appendEmailOrId(StringBuilder messageBuilder, ProductTokenCommon product) {
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

    private StringBuilder appendSymbol(StringBuilder messageBuilder, ProductTokenCommon product) {
        return messageBuilder
                .append("\nsymbol: ")
                .append(product.getSymbol());
    }

    private StringBuilder appendName(StringBuilder messageBuilder, ProductTokenCommon product) {
        if (product instanceof ProductNameable) {
            ProductNameable nameable = (ProductNameable) product;
            return messageBuilder
                    .append("\nname: ")
                    .append(nameable.getName());
        }

        return messageBuilder;
    }

    private StringBuilder appendAddress(StringBuilder messageBuilder, ProductTokenCommon product) {
        Contract contract = contractRepository
                .findByProduct(product)
                .stream()
                .filter(c -> Objects.nonNull(c.getTxHash()))
                .findFirst()
                .orElse(null);

        if (contract == null) {
            return messageBuilder;
        }

        return messageBuilder
                .append("\naddress: ")
                .append(contract.getAddress());
    }

    private StringBuilder appendLineBreak(StringBuilder messageBuilder) {
        return messageBuilder.append("\n");
    }
}
