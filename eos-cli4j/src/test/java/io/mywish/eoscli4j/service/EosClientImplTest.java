package io.mywish.eoscli4j.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.mywish.eoscli4j.model.response.BlockResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

public class EosClientImplTest {
    private ObjectMapper objectMapper;

    @Before
    public void init() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void getBlock() throws Exception {
        String json = readJson("block.json");
        BlockResponse response = objectMapper.readValue(json, BlockResponse.class);
        assertNotNull(response);
    }

    private String readJson(String resourceName) throws URISyntaxException, IOException {
        URL url = getClass().getClassLoader().getResource("eos/responses/" + resourceName);
        Path path = Paths.get(url.toURI());
        boolean exists = Files.exists(path, LinkOption.NOFOLLOW_LINKS);
        assertTrue(String.format("File '%s' not available", resourceName), exists);
        List<String> strings = Files.readAllLines(path);
        return String.join("\n", strings);
    }

}