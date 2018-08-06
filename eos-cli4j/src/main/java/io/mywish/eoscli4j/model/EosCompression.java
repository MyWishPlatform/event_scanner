package io.mywish.eoscli4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EosCompression {
    @JsonProperty("none")
    None,
    @JsonProperty("zlib")
    ZLib,
}
