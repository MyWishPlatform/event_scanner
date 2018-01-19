package io.mywish.joule.service;

import io.mywish.joule.repositories.AddressLockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Component
public class LockService {
    private final static int lockedBy = 0x7fffffff;
    @Autowired
    private AddressLockRepository lockRepository;
    private Queue<Request> waiter = new ConcurrentLinkedQueue<>();
    @Value("${io.mywish.joule.tx.sign.lock-acquire-timeout}")
    private long lockAcquireTimeoutSec;

    @Scheduled(fixedDelay = 1000)
    protected void checkLock() {
        if (waiter.isEmpty()) {
            return;
        }

        Request request = waiter.peek();
        if (LocalDateTime.now(ZoneOffset.UTC).isAfter(request.expiredAt)) {
            log.warn("Waiting for acquire lock was timed out at {} for address {}.", request.expiredAt, request.address);
            waiter.remove().future.complete(false);
        }

        int affected = lockRepository.updateLockedBy(request.address, lockedBy);
        if (affected == 0) {
            return;
        }
        if (affected != 1) {
            log.warn("updateLockedBy returned wrong affected rows value {}.", affected);
            return;
        }

        waiter.remove();
        try {
            request.future.complete(true);
        }
        finally {
            int releaseAffected = lockRepository.updateLockedByToNull(request.address, lockedBy);
            if (releaseAffected != 1) {
                log.error("update lockeBy to null was return wrong affected rows number {} it must be 1", releaseAffected);
            }
        }
    }

    public CompletableFuture<Boolean> acquireLock(final String address) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        waiter.add(new Request(future, address, LocalDateTime.now(ZoneOffset.UTC).plusSeconds(lockAcquireTimeoutSec)));
        return future;
    }

    private static class Request {
        final CompletableFuture<Boolean> future;
        final String address;
        final LocalDateTime expiredAt;

        private Request(CompletableFuture<Boolean> future, String address, LocalDateTime expiredAt) {
            this.future = future;
            this.address = address;
            this.expiredAt = expiredAt;
        }
    }
}
