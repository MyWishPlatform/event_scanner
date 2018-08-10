package io.mywish.eoscli4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * More details here: https://github.com/EOSIO/eos/blob/master/libraries/chain/include/eosio/chain/block.hpp
 */
public enum TransactionStatus {
    /**
     * succeed, no error handler executed
     */
    @JsonProperty("executed")
    Executed,
    /**
     * objectively failed (not executed), error handler executed
     */
    @JsonProperty("soft_fail")
    SoftFail,
    /**
     * objectively failed and error handler objectively failed thus no state change
     */
    @JsonProperty("hard_fail")
    HardFail,
    /**
     * transaction delayed/deferred/scheduled for future execution
     */
    @JsonProperty("delayed")
    Delayed,
    /**
     * transaction expired and storage space refuned to user
     */
    @JsonProperty("expired")
    Expired,
}
