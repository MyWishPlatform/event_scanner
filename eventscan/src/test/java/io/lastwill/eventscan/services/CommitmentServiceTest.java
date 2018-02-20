package io.lastwill.eventscan.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CommitmentServiceTest {
    private class BlockStub extends EthBlock.Block {
        BlockStub(String hash, String blockNo, String parent) {
            setHash(hash);
            setNumber(blockNo);
            setParentHash(parent);
        }
    }

    final AtomicLong committedBlockNo = new AtomicLong(0);
    final AtomicLong rejectedBlockNo = new AtomicLong(0);
    final AtomicInteger committedCount = new AtomicInteger(0);
    final AtomicInteger rejectedCount = new AtomicInteger(0);
    final CommitmentService target = new CommitmentService(5);

    final CommitmentService.Handler<Integer> handler = new CommitmentService.Handler<Integer>() {
        @Override
        public void committed(long blockNumber, Integer payload, int chainLength) {
            committedBlockNo.set(blockNumber);
            committedCount.incrementAndGet();
        }

        @Override
        public void rejected(long blockNumber, Integer payload, int chainLength) {
            rejectedBlockNo.set(blockNumber);
            rejectedCount.incrementAndGet();
        }
    };

    @Before
    public void init() {
        target.waitCommitment("0x1", 1, 1, handler);
        target.waitCommitment("0x2", 1, 2, handler);
        target.waitCommitment("0x3", 2, 3, handler);
        target.waitCommitment("0x4", 2, 4, handler);
    }

    @Test
    public void commonLogicTest() {
        target.waitCommitment("0x1", 1, 7, handler);
        target.addBlock(new BlockStub("0x1", "0x1", "0x0"));
        Assert.assertEquals("no committed",0, committedBlockNo.get());
        Assert.assertEquals("no rejected",0, rejectedBlockNo.get());

        target.addBlock(new BlockStub("0x2", "0x2", "0x1"));
        Assert.assertEquals("no committed",0, committedBlockNo.get());
        Assert.assertEquals("no rejected",0, rejectedBlockNo.get());


        target.addBlock(new BlockStub("0x3", "0x3", "0x2"));
        target.addBlock(new BlockStub("0x4", "0x4", "0x3"));
        target.addBlock(new BlockStub("0x5", "0x5", "0x4"));
        target.addBlock(new BlockStub("0x6", "0x6", "0x5"));

        Assert.assertEquals("1 must be committed",1, committedBlockNo.get());
        Assert.assertEquals("1 must be rejected",1, rejectedBlockNo.get());
        Assert.assertEquals("committed count", 2, committedCount.get());

    }

    @Test
    public void forkTest() {
        target.waitCommitment("0x10", 1, 7, handler);
        target.addBlock(new BlockStub("0x1", "0x1", "0x0"));
        target.addBlock(new BlockStub("0x2", "0x2", "0x1"));
        // parallel
        target.addBlock(new BlockStub("0x10", "0x1", "0x0"));
        target.addBlock(new BlockStub("0x20", "0x2", "0x10"));
        target.addBlock(new BlockStub("0x3", "0x3", "0x20"));
        target.addBlock(new BlockStub("0x4", "0x4", "0x3"));
        target.addBlock(new BlockStub("0x5", "0x5", "0x4"));
        target.addBlock(new BlockStub("0x6", "0x6", "0x5"));

        Assert.assertEquals("1 must be rejected",1, rejectedBlockNo.get());
        Assert.assertEquals("committed count", 2, rejectedCount.get());

        Assert.assertEquals("1 must be committed",1, committedBlockNo.get());
        Assert.assertEquals("committed count", 1, committedCount.get());

    }
}