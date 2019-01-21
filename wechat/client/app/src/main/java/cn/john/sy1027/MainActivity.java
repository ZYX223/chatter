package cn.john.sy1027;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<ChatContent> contents=new ArrayList<ChatContent>();
    private ChatContentAdapter adapter;
    private ListView chatLv;
    private EditText et;

    private String name;
    private OutputStream out;
    private Socket sk;
    private BufferedReader reader;
    private String words;
    private Intent intent;

    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        intent=getIntent();
        name=intent.getStringExtra("name");
        name=name.replace("/n","");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et=findViewById(R.id.sendMsg);
        chatLv=findViewById(R.id.chatListLv);

        adapter=new ChatContentAdapter(this,R.layout.chat_layout,contents);
        chatLv.setAdapter(adapter);

        receive();
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        adapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }
        };
    }
    public void send(View view) {
        ChatContent cc=new ChatContent();
        cc.setContent(et.getText().toString());
        words=et.getText().toString();
        cc.setMe(true);
        et.setText("");
        contents.add(cc);
        adapter.notifyDataSetChanged();
        sendWord();
    }
    public void sendWord(){
        new Thread(){
            @Override
            public void run() {
                try {
                    name=intent.getStringExtra("name");
                    words=name+':'+words+"\n";
                    Log.d("I",name);
                    sk=((Client)getApplication()).getSocket();
                    out=sk.getOutputStream();
                    out.write(words.getBytes());
                    Log.d("I",words);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void receive(){
        new Thread(){
            @Override
            public void run() {
                String message=null;
                try{
                    sk=((Client)getApplication()).getSocket();
                    out=sk.getOutputStream();
                    reader=new BufferedReader(new InputStreamReader(sk.getInputStream()));
                    while((message=reader.readLine())!=null){
                        message+="\n";
                        ChatContent cc=new ChatContent();
                        cc.setMe(false);
                        cc.setContent(message);
                        contents.add(cc);
                        handler.sendEmptyMessage(0);
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("I", "I am die");
    }
}
