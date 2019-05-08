package io.mywish.wavescli4j;

import io.mywish.wavescli4j.model.Block;
import io.mywish.wavescli4j.model.Height;

public interface WavesClient {
    Height getHeight() throws Exception;
    Block getBlock(Long number) throws Exception;
}
