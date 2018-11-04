package io.mywish.scanner.services;

import io.lastwill.eventscan.model.NetworkType;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;

@Slf4j
public class LastBlockFilePersister implements LastBlockPersister {
    private final NetworkType networkType;
    private final String startBlockFileDir;
    private Long lastBlock;

    private FileOutputStream lastOutputStream;

    public LastBlockFilePersister(@NonNull NetworkType networkType, @NonNull String startBlockFileDir, Long lastBlock) {
        this.networkType = networkType;
        this.startBlockFileDir = startBlockFileDir;
        this.lastBlock = lastBlock;
    }

    @Override
    public void open() {
        if (startBlockFileDir == null) {
            log.warn("Start block dir was not specified. Only im memory mode available.");
            return;
        }
        File file = Paths.get(startBlockFileDir, networkType.name()).toFile();
        if (lastBlock == null && file.exists() && file.canRead()) {
            try (FileReader fileReader = new FileReader(file)) {
                BufferedReader reader = new BufferedReader(fileReader);
                lastBlock = Long.parseLong(reader.readLine());
            }
            catch (Throwable e) {
                log.warn("Impossible to read last block from {}.", file, e);
            }
        }

        File dir = new File(startBlockFileDir);
        if (!dir.exists()) {
            try {
                if (!dir.mkdirs()) {
                    throw new Exception("Dir was not created.");
                }
            }
            catch (Exception e) {
                log.error("Impossible to create dir {} to store last block.", dir, e);
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
            if (lastBlock == null) {
                return;
            }
            saveLastBlock(lastBlock);
        }
        catch (IOException e) {
            log.error("Error on creating file writer.", e);
        }
    }

    @Override
    public void close() {
        if (lastOutputStream == null) {
            return;
        }

        try {
            lastOutputStream.close();
            lastOutputStream = null;
        }
        catch (IOException e) {
            log.error("Error on closing stream.", e);
        }
    }

    @Override
    public Long getLastBlock() {
        return lastBlock;
    }

    @Override
    public void saveLastBlock(long blockNumber) {
        lastBlock = blockNumber;

        if (lastOutputStream == null) {
            return;
        }
        try {
            FileChannel fileChannel = lastOutputStream.getChannel();
            fileChannel.position(0L);
            ByteBuffer buf =  ByteBuffer.wrap(Long.toString(blockNumber).getBytes());
            fileChannel.write(buf);
            fileChannel.truncate(buf.position());
        }
        catch (IOException e) {
            log.error("Error on saving last block no.", e);
        }
    }
}
