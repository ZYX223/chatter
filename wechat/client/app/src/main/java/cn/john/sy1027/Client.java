package cn.john.sy1027;

import android.app.Application;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends Application {
    public Socket sk;
    public Socket getSocket(){
        return sk;
    }
    public void setSocket(Socket socket){
        this.sk = socket;
    }
}

