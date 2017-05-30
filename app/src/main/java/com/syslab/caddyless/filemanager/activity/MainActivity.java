package com.syslab.caddyless.filemanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.syslab.caddyless.filemanager.R;
import com.syslab.caddyless.filemanager.adpter.MessageAdapter;
import com.syslab.caddyless.filemanager.utils.FileSort;
import com.syslab.caddyless.filemanager.utils.Message;

import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity {
    private View leftmenu_file;
    private ImageButton music;
    private ImageButton video;
    private ImageButton picture;
    private TextView musicText;
    private TextView videoText;
    private TextView pictureText;
    private ArrayList<String> musicFilePaths;
    private ArrayList<String> videoFilePaths;
    private ArrayList<String> pictureFilePaths;
    //
    private List<Message> messageList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        music= (ImageButton) findViewById(R.id.music);
        musicText= (TextView) findViewById(R.id.music_text);
        musicFilePaths=FileSort.getSpecificTypeOfFile(MainActivity.this,new String[]{".mp3",".wma"});
        musicText.setText("音乐"+"("+musicFilePaths.size()+")");
        videoText= (TextView) findViewById(R.id.video_text);
        videoFilePaths=FileSort.getSpecificTypeOfFile(MainActivity.this,new String[]{".mp4",".avi",".rmvb",".flash"});
        videoText.setText("视频"+"("+videoFilePaths.size()+")");
        pictureText= (TextView) findViewById(R.id.picture_text);
        pictureFilePaths=FileSort.getSpecificTypeOfFile(MainActivity.this,new String[]{".gif",".jpeg",".bmp",".jpg"});
        pictureText.setText("图片"+"("+pictureFilePaths.size()+")");

        Log.d("Main", "onCreate: start initalize");
        intiMessage();
        Log.d("Main", "onCreate: finish initalize");
        RecyclerView recyclerView= (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        MessageAdapter messageAdapter=new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        leftmenu_file=findViewById(R.id.leftmenu_file);
        Log.d("MainActivity", "onCreate: success compelete main");
        leftmenu_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "onClick: success receive");
                try{
                    Intent intent=new Intent(MainActivity.this,FileListActivity.class);
                    startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(MainActivity.this,"进入activity异常",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void intiMessage(){
        Message message1=new Message(R.mipmap.phone,"by:Mike|Creative Mints","1 minute ago","Mechanical Grasshopper");
        messageList.add(message1);
        Message message2=new Message(R.mipmap.cloud,"by:Dash","21 minutes ago","Assistant App-Weather Module");
        messageList.add(message2);
    }
}
