package com.syslab.caddyless.filemanager.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.syslab.caddyless.filemanager.R;
import com.syslab.caddyless.filemanager.adpter.FileAdapter;
import com.syslab.caddyless.filemanager.utils.FileSortFactory;
import com.syslab.caddyless.filemanager.utils.QueryAsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by caddyless on 2017/5/22.
 */

public class FileListActivity extends AppCompatActivity implements FileAdapter.OnCopyFileListener {
    private TextView filePath;
    private ListView lv;
    private ArrayList<File> data=new ArrayList<>();
    private File[] files;
    private FileAdapter fileAdapter;
    private String rootPath;
    private Stack<String> nowPathStack;
    private ImageButton back;
    private ImageButton menuButton;
    private SearchView searchBox;
    @Override
    public void doCopy(File file) {
        watingCopyFile=file;
        Toast.makeText(FileListActivity.this,file.getName()+"被添加到粘贴板",Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filelist);
        Log.d("FileListActivity", "onCreate: success enter fileclass");
        initView();
        back= (ImageButton) findViewById(R.id.comeback);
        searchBox= (SearchView) findViewById(R.id.searchBox);
        searchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                doSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        this.registerForContextMenu(lv);
    }
    private void initView(){
        rootPath= Environment.getExternalStorageDirectory().toString();
        nowPathStack=new Stack<>();
        lv= (ListView) findViewById(R.id.file_list);
        filePath= (TextView) findViewById(R.id.filePath);
        files=Environment.getExternalStorageDirectory().listFiles();
        nowPathStack.push(rootPath);
        for(File f:files){
            data.add(f);
        }
        filePath.setText(getPathString());
        fileAdapter=new FileAdapter(this,data);
        fileAdapter.setOnCopyListener(this);
        lv.setAdapter(fileAdapter);
        lv.setOnItemClickListener(new FileItemClickListener());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("文件操作");
        menu.add(0,1,Menu.NONE,"复制");
        menu.add(0,2,Menu.NONE,"删除");
        menu.add(0,3,Menu.NONE,"移动");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        //这个view就是当前的View
        View view = info.targetView;
        ContextMenu.ContextMenuInfo menuInfo=item.getMenuInfo();
        int position= ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
        File files=new File(getPathString());
        File file=files.listFiles()[position];
        switch (item.getItemId()){
            case 1:
                doCopy(file);
                break;
            case 2:
                file.delete();
                showChanged(getPathString());
                break;
            case 3:
                doCopy(file);
                file.delete();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    static File watingCopyFile;
    class FileItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            File file=files[position];
            if(file.isFile()){
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri data=Uri.fromFile(file);
                int index=file.getName().lastIndexOf(".");
                String suffix=file.getName().substring(index+1);
                String type= MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
                intent.setDataAndType(data,type);
                startActivity(intent);
            }else{
                nowPathStack.push("/"+file.getName());
                showChanged(getPathString());
            }
        }
    }
    private void showChanged(String path){
        filePath.setText(path);
        files=new File(path).listFiles();
        data.clear();
        for(File f:files){
            data.add(f);
        }
        files=fileAdapter.setfiledata(data);
    }
    private String getPathString(){
        Stack<String> temp=new Stack<>();
        temp.addAll(nowPathStack);
        String result="";
        while (temp.size()!=0){
            result=temp.pop()+result;
        }
        return result;
    }
    @Override
    public void onBackPressed() {
        if(ifSearching){
            ifSearching=false;
            showChanged(getPathString());
        }else{
            if(nowPathStack.peek()==rootPath){
                super.onBackPressed();
            }else{
                nowPathStack.pop();
                showChanged(getPathString());
            }
        }
    }

    MenuItem sortItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        sortItem=menu.findItem(R.id.action_sort);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_new_folder:
                doCreateNewFolder();
                break;
            case R.id.action_copy_paste:
                doPaste();
                break;
            case R.id.action_sort_date:
                fileAdapter.setSortWay(FileSortFactory.SORT_BY_FOLDER_AND_TIME);
                fileAdapter.notifyDataSetChanged();
                break;
            case R.id.action_sort_size:
                fileAdapter.setSortWay(FileSortFactory.SORT_BY_FOLDER_AND_SIZE);
                fileAdapter.notifyDataSetChanged();
                break;
            case R.id.action_sort_name:
                fileAdapter.setSortWay(FileSortFactory.SORT_BY_FOLDER_AND_NAME);
                fileAdapter.notifyDataSetChanged();
                break;
            default:
                files = fileAdapter.setfiledata();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    boolean isCopy=true;

    private void doPaste(){
        File newFile=new File(getPathString()+"/"+watingCopyFile.getName());
        if(watingCopyFile.equals(null)){
            //Snackbar.make(findViewById(R.id.main_view), "当前粘贴板为空，不能粘贴", Snackbar.LENGTH_SHORT).show();
        }else{
            if(watingCopyFile.isFile()&&watingCopyFile.exists()){
                try {
                    FileInputStream fis = new FileInputStream(watingCopyFile);
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len = -1;
                    long contentSize = watingCopyFile.length();
                    long readed = 0;
                    byte[] buff = new byte[8192];
                    while ((len=fis.read(buff))!=-1){
                        //写文件
                        fos.write(buff,0,len);
                        readed+=len;
                        //发布进度
                    }
                    fos.flush();
                    fis.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {

                }
            }
        }
        if (newFile.exists()) {
            Toast.makeText(FileListActivity.this,"复制" + newFile.getName() + "成功",Toast.LENGTH_SHORT).show();
            fileAdapter.notifyDataSetChanged();
        }
    }
    AlertDialog mydialog;
    EditText newfloder_name;
    private void doCreateNewFolder(){
        mydialog = new AlertDialog.Builder(FileListActivity.this).create();
        mydialog.show();
        mydialog.getWindow().setContentView(R.layout.newfloder_dialog);
        mydialog.setView(new EditText(FileListActivity.this));
        //加入下面两句以后即可弹出输入法
        mydialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        mydialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        newfloder_name = (EditText) mydialog.getWindow().findViewById(R.id.newfloder_name);

        mydialog.getWindow()
                .findViewById(R.id.newfloder_cancle)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mydialog.dismiss();
                    }
                });
        mydialog.getWindow()
                .findViewById(R.id.newfloder_create)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name =  newfloder_name.getText().toString();
                        if (name != null) {
                            File folder = new File(getPathString() + "/" + name);
                            folder.mkdirs();
                            if (folder.exists()) {
                                Toast.makeText(FileListActivity.this,"文件："+name + " 创建成功",Toast.LENGTH_SHORT).show();
                                showChanged(getPathString());
                                mydialog.dismiss();
                            }
                        }

                    }
                });
    }
    boolean ifSearching=false;
    AlertDialog searchDialog;
    TextView querytv;
    private void doSearch(String query) {
        ifSearching = true;
        searchDialog = new AlertDialog.Builder(FileListActivity.this).create();
        searchDialog.show();
        searchDialog.getWindow().setContentView(R.layout.query_dialog);
        querytv = (TextView) searchDialog.getWindow().findViewById(R.id.query_tv);
        new QueryAsyncTask(querytv,getPathString(),query,fileAdapter,searchDialog).execute();
    }
}
