package cn.john.sy1027;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Main2Activity extends AppCompatActivity {
    private String name;
    private EditText word;
    private OutputStream out;
    private Socket sk;
    private BufferedReader reader;
    private String words;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        intent=getIntent();
        name=intent.getStringExtra("name");
        name=name.replace("/n","");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sss);

        word=findViewById(R.id.Et);
        words=word.getText().toString();
    }
    public void setWord(View view){
        new Thread(){
            @Override
            public void run() {
                try {
                    name=intent.getStringExtra("name");
                    words=word.getText().toString();
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
}
