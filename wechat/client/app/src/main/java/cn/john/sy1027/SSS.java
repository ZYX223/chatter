package cn.john.sy1027;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static android.R.*;
import static android.R.layout.simple_list_item_1;

public class SSS extends AppCompatActivity {
    private EditText word;
    private ListView friendlist;
    private TextView textView;
    private OutputStream out;
    private Socket sk;
    private BufferedReader reader;

    private List<User> users=new ArrayList<User>();

    private List<String> list;
    private ArrayAdapter<String> adapter;
    private Handler handler;

    private String ms;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent=getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firend_list);

        word=findViewById(R.id.Et);
        friendlist=findViewById(R.id.fl);
        textView=findViewById(R.id.tv);

        list=new ArrayList<String>();
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);

        setFriendlist();

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        adapter.notifyDataSetChanged();
                        friendlist.setAdapter(adapter);
                        break;
                    case 1:
                        textView.setText(ms);
                        break;
                    default:
                        break;
                }
            }
        };
        friendlist.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String user= list.get(i);
                Intent intent=new Intent(SSS.this,MainActivity.class);
                intent.putExtra("name",user.replace("*",""));
                startActivity(intent);

            }
        });
    }
    public void setFriendlist() {
        new Thread() {
            @Override
            public void run() {
                    String addr = "https://api.myjson.com/bins/5zn86";
                    String info = new Fetcher(addr).fetch();
                    try {
                        JSONArray jsa = new JSONArray(info);
                        for (int i = 0; i < jsa.length(); i++) {
                            JSONObject jso = jsa.getJSONObject(i);
                            User u = new User();
                            u.setUname(jso.getString("name"));
                            Log.d("I", u.getUname());
                            u.setPassword(jso.getString("password"));
                            Log.d("I", u.getPassword());
                            users.add(u);
                        }
                        for (int i = 0; i < users.size(); i++) {
                            User u = users.get(i);
                            Log.d("I", u.getUname());
                            list.add(u.getUname());
                        }
                        sk = ((Client) getApplication()).getSocket();
                        out = sk.getOutputStream();
                        reader = new BufferedReader(new InputStreamReader(sk.getInputStream()));
                        out.write(("list\n").getBytes());
                        String firend = null;
                        int i;
                        while ((firend = reader.readLine()) != null) {
                            firend = firend + "\n";
                            if (firend.trim().equals("over!")) {
                                break;
                            }
                            i = list.indexOf(firend.replace("\n", ""));
                            list.set(i, "*" + firend.replace("\n", ""));
                        }
                        handler.sendEmptyMessage(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
    }
    public void update(View view) {
        list.clear();
        new Thread() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < users.size(); i++) {
                        User u = users.get(i);
                        Log.d("I", u.getUname());
                        list.add(u.getUname());
                    }
                    sk = ((Client) getApplication()).getSocket();
                    out = sk.getOutputStream();
                    reader = new BufferedReader(new InputStreamReader(sk.getInputStream()));
                    out.write(("list\n").getBytes());
                    String firend = null;
                    int i;
                    while ((firend = reader.readLine()) != null) {
                        firend = firend + "\n";
                        if (firend.trim().equals("over!")) {
                            break;
                        }
                        i = list.indexOf(firend.replace("\n", ""));
                        list.set(i, "*" + firend.replace("\n", ""));
                    }
                    handler.sendEmptyMessage(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void exit(View view){
        new Thread()
        {
            @Override
            public void run() {
                try {
                    out.write(("bye\n").getBytes());
                    ms=reader.readLine();
                    handler.sendEmptyMessage(1);
                    reader.close();
                    out.close();
                    sk.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent=new Intent(SSS.this,MainActivity.class);
                startActivity(intent);
            }
        }.start();
    }
}
