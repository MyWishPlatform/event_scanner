package io.mywish.troncli4j.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.mywish.troncli4j.model.response.BlockResponse;
import io.mywish.troncli4j.model.response.NodeInfoResponse;
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

public class TronClientImplTest {
    private ObjectMapper objectMapper;

    @Before
    public void init() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //
    }

    @Test
    public void getNodeInfo() throws Exception {
        String json = readJson("node-info.json");
        NodeInfoResponse response = objectMapper.readValue(json, NodeInfoResponse.class);
        assertNotNull(response);

        assertNotNull(response.getBlock());
        assertEquals(5093704L, (long) response.getBlock().getNum());
        assertEquals("00000000004db948270a7183abe55c55c7f13aaa43f04f2b8ac938e8e0ed88c5",
                response.getBlock().getId());

        assertNotNull(response.getSolidityBlock());
        assertEquals(5093684L, (long) response.getSolidityBlock().getNum());
        assertEquals("00000000004db9345bea257ac7fde664ca33aab49b3f6892ab46550a88feb30d",
                response.getSolidityBlock().getId());
    }

    @Test
    public void getBlock() throws Exception {
        String json = readJson("block.json");
        BlockResponse response = objectMapper.readValue(json, BlockResponse.class);
        assertNotNull(response);
//        assertNotNull(response.getTransactions().get(0).getId());
//        assertEquals(3, response.getTransactions().get(0).getActions().size());
//
//        Transaction transaction = response.getTransactions().get(0);
//        EosAction action = transaction.getActions().get(0);
//        byte[] data = DatatypeConverter.parseHexBinary(action.getData());
//        ByteBuffer buffer = ByteBuffer.wrap(data);
//
//        String sender = abiParser.parseName(buffer);
//        String newName = abiParser.parseName(buffer);
//        assertEquals("mywishtoken3", sender);
//        assertEquals("zannanananan", newName);
    }

    @Test
    public void getNoTrxBlock() throws Exception {
        String json = readJson("no-trx-block.json");
        BlockResponse response = objectMapper.readValue(json, BlockResponse.class);
        assertNotNull(response);
//        assertNotNull(response.getTransactions().get(0).getId());
//        assertEquals(0, response.getTransactions().get(0).getActions().size());
////
////        Transaction transaction = response.getTransactions().get(0);
////        EosAction action = transaction.getActions().get(0);
////        byte[] data = DatatypeConverter.parseHexBinary(action.getData());
////        ByteBuffer buffer = ByteBuffer.wrap(data);
////
////        String sender = abiParser.parseName(buffer);
////        String newName = abiParser.parseName(buffer);
////        assertEquals("mywishtoken3", sender);
////        assertEquals("zannanananan", newName);
    }

    private String readJson(String resourceName) throws URISyntaxException, IOException {
        URL url = getClass().getClassLoader().getResource("tron/responses/" + resourceName);
        Path path = Paths.get(url.toURI());
        boolean exists = Files.exists(path, LinkOption.NOFOLLOW_LINKS);
        assertTrue(String.format("File '%s' not available", resourceName), exists);
        List<String> strings = Files.readAllLines(path);
        return String.join("\n", strings);
    }

}