package io.lastwill.eventscan.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
@Component
public class LastBlockPersister {
    @Value("${io.lastwill.eventscan.start-block-file}")
    private String startBlockFilePath;

    @Value("${io.lastwill.eventscan.start-block:#{null}}")
    private Long lastBlock;

    private FileOutputStream lastOutputStream;

    @PostConstruct
    protected void init() {
        if (startBlockFilePath == null) {
            log.warn("Start block file was not specified. Only im memory mode available.");
            return;
        }
        File file = new File(startBlockFilePath);
        if (lastBlock == null && file.exists() && file.canRead()) {
            try (FileReader fileReader = new FileReader(file)) {
                BufferedReader reader = new BufferedReader(fileReader);
                lastBlock = Long.parseLong(reader.readLine());
            }
            catch (Throwable e) {
                log.warn("Impossible to read last block from {}.", file, e);
            }
        }

        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    throw new Exception("File was not created.");
                }
            }
            catch (Exception e) {
                log.error("Impossible to create new file {} to store last block.", file, e);
            }
        }

        if (!file.exists() || !file.canWrite()) {
            log.error("Impossible to write to file {}.", file);
            return;
        }

        try {
            lastOutputStream = new FileOutputStream(file, false);
        }
        catch (IOException e) {
            log.error("Error on creating file writer.", e);
        }
    }

    @PreDestroy
    public void close() {
        if (lastOutputStream != null) {
            try {
                lastOutputStream.close();
            }
            catch (IOException e) {
                log.error("Error on closing stream.", e);
            }
        }
    }

    public Long getLastBlock() {
        return lastBlock;
    }

    public void saveLastBlock(long blockNumber) {
        lastBlock = blockNumber;
        FileChannel fileChannel = lastOutputStream.getChannel();
        try {
            fileChannel.position(0L);
            ByteBuffer buf =  ByteBuffer.wrap(Long.toString(blockNumber).getBytes());
            fileChannel.write(buf);
            fileChannel.truncate(buf.position());
        }
        catch (IOException e) {
            log.error("Error on saving last block no.");
        }
    }
}
