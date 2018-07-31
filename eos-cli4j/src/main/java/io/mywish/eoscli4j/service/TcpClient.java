package io.mywish.eoscli4j.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class TcpClient {
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public TcpClient(String host, int port) throws Exception {
        this.socket = new Socket(host, port);
        this.dataInputStream = new DataInputStream(this.socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
    }

    public void write(String data) throws Exception {
        this.dataOutputStream.writeBytes(data);
    }

    public int readInt() throws Exception {
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res += (this.dataInputStream.readByte() & 0xFF) << (8*i);
        }
        return res;
    }

    public String readString(int length) throws Exception {
        byte[] res = new byte[length];
        this.dataInputStream.readFully(res);
        return new String(res);
    }
}
