package cn.john.sy1027;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {
    private EditText name;
    private EditText password;
    public List<User> users=new ArrayList<User>();

    public Socket socket;
    public InetAddress address;

    public OutputStream out;
    public BufferedReader reader;

    private String username;
    private String Password;

    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        name=findViewById(R.id.et_name);
        password=findViewById(R.id.password_text);


        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        name.setText("");
                        password.setText("");
                        break;
                    default:
                        break;
                }
            }
        };
    }
    public void Login (View view) {
        new Thread() {
            @Override
            public void run() {
                String addr="https://api.myjson.com/bins/5zn86";
                String info=new Fetcher(addr).fetch();
                try {
                    JSONArray jsa=new JSONArray(info);
                    for (int i = 0; i < jsa.length(); i++) {
                        JSONObject jso = jsa.getJSONObject(i);
                        User u = new User();
                        u.setUname(jso.getString("name"));
                        Log.d("I",u.getUname());
                        u.setPassword(jso.getString("password"));
                        Log.d("I",u.getPassword());
                        users.add(u);
                    }
                    username = name.getText().toString();
                    Password = password.getText().toString();
                    int flag = 0;
                    for (int i=0;i<users.size();i++) {
                        User u=users.get(i);
                        System.out.println(i);
                        if (u.getUname().equals(username) && u.getPassword().equals(Password)) {
                            flag = 1;
                            break;
                        }
                    }
                    if(flag==1){
                        Log.d("I","victory");
                        this.interrupt();
                        Log.d("I","victory!!");
                        connect();
                    }
                    else{
                        handler.sendEmptyMessage(0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void connect() {
        new Thread(){
            @Override
            public void run() {
                try{
                    address=InetAddress.getByName("192.168.21.57");
                    socket=new Socket(address,9000);
                    ((Client)getApplication()).setSocket(socket);
                    out=socket.getOutputStream();
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out.write((username + "\n").getBytes());
                }catch (IOException e){
                    e.printStackTrace();
                }
                Intent intent = new Intent(Login.this, SSS.class);
                startActivity(intent);
            }
        }.start();
    }
    public void clearUIData(View view){
        name.setText("");
        password.setText("");
    }

}
