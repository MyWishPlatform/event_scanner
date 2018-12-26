package io.mywish.troncli4j.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeInfoResponse extends Response {
    private final BlockNum block;
    private final BlockNum solidityBlock;

    @Getter
    public static class BlockNum {
        private final Long num;
        private final String id;

        public BlockNum(String numAndId) {
            String[] split = numAndId.split(",");
            this.num = Long.valueOf(split[0].split(":")[1]);
            this.id = split[1].split(":")[1];
        }
    }
}
