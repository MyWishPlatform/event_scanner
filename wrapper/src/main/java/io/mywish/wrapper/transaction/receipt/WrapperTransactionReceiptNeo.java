package io.mywish.wrapper.transaction.receipt;

import io.mywish.wrapper.WrapperLog;
import io.mywish.wrapper.WrapperTransactionReceipt;
import io.mywish.wrapper.transaction.WrapperTransactionNeo;
import java.util.List;

public class WrapperTransactionReceiptNeo extends WrapperTransactionReceipt {
    public WrapperTransactionReceiptNeo(WrapperTransactionNeo transaction, List<WrapperLog> logs) {
        super(
                transaction.getHash(),
                transaction.getContracts(),
                logs,
                true
        );
    }
}
