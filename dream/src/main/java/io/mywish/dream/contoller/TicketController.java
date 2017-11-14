package io.mywish.dream.contoller;

import io.mywish.dream.dto.Result;
import io.mywish.dream.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.concurrent.CompletionStage;

@RestController
@RequestMapping(path = "ticket-sale")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @RequestMapping(method = RequestMethod.POST)
    public CompletionStage<Result> create() {
        return ticketService
                .deploy(LocalDateTime.now().plusDays(1))
                .thenApply(contract -> new Result(true, contract));
    }

    @RequestMapping(method = RequestMethod.GET)
    public Result list() {
        return new Result(true, ticketService.getContracts());
    }

    @RequestMapping(path = "{address}/players", method = RequestMethod.GET)
    public CompletionStage<Result> getPlayers(@PathVariable("address") String address) {
        return ticketService
                .getPlayers(address)
                .thenApply(players -> new Result(true, players));
    }

    @RequestMapping(path = "{address}/finish", method = RequestMethod.POST)
    public CompletionStage<Result> finish(@PathVariable("address") String address) {
        return ticketService
                .finish(address)
                .thenApply(aVoid -> new Result(true, null));
    }

    @RequestMapping(path = "{address}/finish", method = RequestMethod.GET)
    public CompletionStage<Result> isFinished(@PathVariable("address") String address) {
        return ticketService
                .isFinished(address)
                .thenApply(value -> new Result(true, value));
    }

    @RequestMapping(path = "{address}/winners/{id}", method = RequestMethod.POST)
    public CompletionStage<Result> finish(@PathVariable("address") String address, @PathVariable("id") int index) {
        return ticketService
                .setWinner(address, index)
                .thenApply(aVoid -> new Result(true, null));
    }
}
