package io.mywish.eoscli4j.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TcpClient {
    private final Socket socket;
    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;

    public TcpClient(String host, int port) throws Exception {
        this.socket = new Socket(host, port);
        this.dataInputStream = new DataInputStream(this.socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
    }

    public void write(String data) throws Exception {
        dataOutputStream.writeBytes(data);
        dataOutputStream.flush();
    }

    public int readInt() throws Exception {
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res |= (dataInputStream.readByte() & 0xFF) << (8*i);
        }
        return res;
    }

    public String readString(int length) throws Exception {
        byte[] res = new byte[length];
        dataInputStream.readFully(res);
        return new String(res);
    }

    public void close() throws IOException {
        dataInputStream.close();
        dataOutputStream.close();
        socket.close();
    }
}
