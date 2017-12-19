package io.mywish.bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ChatPersister {
    @Value("${io.mywish.bot.file:#{null}}")
    private String chatsFilePath;

    private FileOutputStream lastOutputStream;
    private final Set<Long> chats = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Object saveLock = new Object();
    private final Runnable saver = () -> {
        log.info("Saver thread was started.");
        try {
            while (true) {
                synchronized (saveLock) {
                    saveLock.wait();
                }
                writeToStream();
            }

        }
        catch (InterruptedException e) {
            log.error("Saver thread was interrupted.", e);
        }
    };

    @PostConstruct
    protected void init() {
        if (chatsFilePath == null) {
            log.warn("File for persisting bot chats is not specified.");
            return;
        }
        File file = new File(chatsFilePath);
        log.info("Persist chats to {}.", file.getAbsolutePath());
        if (file.exists() && file.canRead()) {
            readFromFile(file);
        }
        else if (!file.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            }
            catch (IOException e) {
                log.warn("Impossible to create file {}. Chats were not persisted!", file.getAbsolutePath(), e);
                return;
            }
        }
        if (file.canWrite()) {
            try {
                lastOutputStream = new FileOutputStream(file, false);
            }
            catch (FileNotFoundException e) {
                log.error("Error on creating file {} writer.", file, e);
                return;
            }
            Thread thread = new Thread(saver);
            thread.setDaemon(true);
            thread.start();
        }
        else {
            log.warn("Chats file not writable. Chats were not persisted!");
        }
    }

    public boolean tryAdd(long charId) {
        boolean result = chats.add(charId);
        if (result) {
            notifySaver();
        }
        return result;
    }

    public Iterable<Long> getChats() {
        return chats;
    }

    public int getCount() {
        return chats.size();
    }

    public void remove(long chatId) {
        chats.remove(chatId);
        notifySaver();
    }

    private void notifySaver() {
        synchronized (saveLock) {
            saveLock.notify();
        }
    }

    private void readFromFile(File file) {
        try (FileReader fileReader = new FileReader(file)) {
            BufferedReader reader = new BufferedReader(fileReader);
            int lineNo = 1;
            String line = reader.readLine();
            while (line != null) {
                try {
                    chats.add(Long.parseLong(line));
                }
                catch (Exception e) {
                    log.error("Error on reading line {}.", lineNo, e);
                }
                lineNo ++;
                line = reader.readLine();
            }
            if (chats.isEmpty()) {
                log.warn("File {} has no chats.", file);
            }
        }
        catch (Exception e) {
            log.warn("Impossible to read file {}.", file, e);
        }
    }

    private void writeToStream() {
        FileChannel fileChannel = lastOutputStream.getChannel();
        try {
            fileChannel.position(0L);
            int length = 0;
            for (long chatId: chats) {
                ByteBuffer buf =  ByteBuffer.wrap(Long.toString(chatId).getBytes());
                fileChannel.write(buf);
                length += buf.position();
            }
            fileChannel.truncate(length);
        }
        catch (IOException e) {
            log.error("Error on saving last block no.");
        }

    }
}
