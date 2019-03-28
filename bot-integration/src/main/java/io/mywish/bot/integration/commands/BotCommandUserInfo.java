package io.mywish.bot.integration.commands;

import io.lastwill.eventscan.model.*;
import io.lastwill.eventscan.repositories.*;
import io.mywish.bot.service.BotCommand;
import io.mywish.bot.service.ChatContext;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BotCommandUserInfo implements BotCommand {
    @Getter
    private final String name = "/user";
    @Getter
    private final String usage = "";
    @Getter
    private final String description = "Print information about user";

    @Autowired
    private BotUserRepository botUserRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserSiteBalanceRepository userSiteBalanceRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private InternalPaymentRepository internalPaymentRepository;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public void execute(ChatContext context, List<String> args) {
        if (!botUserRepository.existsByChatId(context.getChatId())) {
            context.sendMessage("You don't have access to this function.");
            return;
        }

        if (args.size() != 1) {
            context.sendMessage("Please specify user id or e-mail");
            return;
        }

        String idOrEmail = args.get(0);
        List<User> users = new ArrayList<>();
        try {
            Integer id = Integer.parseInt(idOrEmail);
            User user = userRepository.findOne(id);
            if (user != null) {
                users.add(user);
            }
        } catch (NumberFormatException ignored) {
            users.addAll(userRepository.findAllByEmail(idOrEmail));
        }

        if (users.isEmpty()) {
            context.sendMessage("User not found");
            return;
        }

        StringBuilder messageBuilder = new StringBuilder();
        users.forEach(user -> {
            messageBuilder.append("User: ")
                    .append("\nid: ")
                    .append(user.getId())
                    .append("\nemail: ")
                    .append(user.getEmail());

            UserProfile userProfile = userProfileRepository.findByUser(user);
            messageBuilder.append("\n\nUserProfile")
                    .append("\ntotp_key: ")
                    .append(userProfile.getTotpKey())
                    .append("\nuse_totp: ")
                    .append(userProfile.getUseTotp())
                    .append("\nis_social: ")
                    .append(userProfile.getIsSocial())
                    .append("\nlang: ")
                    .append(userProfile.getLang())
                    .append("\nlast_used_totp: ")
                    .append(userProfile.getLastUsedTotp());


            List<UserSiteBalance> balances = userSiteBalanceRepository.findAllByUser(user);
            if (!balances.isEmpty()) {
                messageBuilder.append("\n\nBalances:");
                balances.forEach(userSiteBalance -> {
                    messageBuilder
                            .append("\n\n")
                            .append(userSiteBalance.getSite().getSiteName())
                            .append(" (")
                            .append(userSiteBalance.getSite().getId())
                            .append("): ")
                            .append("\nbalance: ")
                            .append(userSiteBalance.getBalance())
                            .append("\nbtc_address: ")
                            .append(userSiteBalance.getBtcAddress())
                            .append("\neth_address: ")
                            .append(userSiteBalance.getEthAddress())
                            .append("\ntron_address: ")
                            .append(userSiteBalance.getTronAddress())
                            .append("\nmemo: ")
                            .append(userSiteBalance.getMemo());

                    List<InternalPayment> payments = internalPaymentRepository
                            .findAllByUserAndSite(user, userSiteBalance.getSite());
                    if (!payments.isEmpty()) {
                        messageBuilder.append("\nPayments: ");
                        payments.forEach(payment -> messageBuilder
                                .append("\nid: ")
                                .append(payment.getId())
                                .append("\noriginal_currency: ")
                                .append(payment.getOriginalCurrency())
                                .append("\ntx_hash: ")
                                .append(payment.getTxHash())
                                .append("\ndatetime: ")
                                .append(payment.getDatetime())
                                .append("\ndelta: ")
                                .append(payment.getDelta())
                                .append("\noriginal_delta: ")
                                .append(payment.getOriginalDelta())
                        );
                    }
                });
            }

            List<Product> userContracts = productRepository.findAllByUserId(user.getId());
            if (!userContracts.isEmpty()) {
                messageBuilder.append("\n\nContracts: ");
                userContracts.forEach(product -> messageBuilder
                        .append("\nid: ")
                        .append(product.getId())
                        .append("\ncreated_date: ")
                        .append(product.getCreatedDate())
                        .append("\ncontract_type: ")
                        .append(product.getContractType())
                        .append("\nnetwork_type: ")
                        .append(product.getNetwork().getType())
                        .append("\nstate: ")
                        .append(product.getState())
                );
            }
        });

        context.sendMessage(messageBuilder.toString());
    }
}
