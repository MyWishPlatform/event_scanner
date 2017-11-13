package io.mywish.dream.contoller;

import io.mywish.dream.dto.CreateContract;
import io.mywish.dream.dto.Result;
import io.mywish.dream.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDateTime;
import java.util.concurrent.CompletionStage;

@Controller
@RequestMapping(path = "ticket-sale")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @RequestMapping(method = RequestMethod.POST)
    public CompletionStage<Result> create() {
        return ticketService
                .deploy(LocalDateTime.now().plusDays(1))
                .thenApply(address -> new Result(true, new CreateContract(address)));
    }
}
