package cn.john.sy1027;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Fetcher {
    private String address;
    public Fetcher(String address){
        this.address=address;
    }
    public String fetch(){
        String data=null;
        try {
            URL url=new URL(address);
            HttpURLConnection con=(HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            InputStream input=con.getInputStream();
            int len=con.getContentLength();
            byte[] buf=new byte[len];
            input.read(buf);
            data=new String(buf);
            input.close();
            con.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
