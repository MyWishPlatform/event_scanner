package io.mywish.binance.blockchain.model;

import java.util.List;

import com.binance.dex.api.client.domain.BlockMeta;
import com.binance.dex.api.client.domain.broadcast.Transaction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BinanceBlock {
    private final BlockMeta blockMeta;
    private final List<Transaction> transactions;
}
