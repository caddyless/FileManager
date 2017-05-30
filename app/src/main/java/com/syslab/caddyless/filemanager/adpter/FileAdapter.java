package com.syslab.caddyless.filemanager.adpter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.syslab.caddyless.filemanager.R;
import com.syslab.caddyless.filemanager.utils.FileSortFactory;
import com.syslab.caddyless.filemanager.view.RenameFileDialog;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by caddyless on 2017/5/15.
 */

public class FileAdapter extends BaseAdapter {
    ArrayList<File> filedata;
    Context context;
    //排序方法
    int sortWay = FileSortFactory.SORT_BY_FOLDER_AND_NAME;

    public void setSortWay(int sortWay) {
        this.sortWay = sortWay;
    }

    public FileAdapter(Context context, ArrayList<File> data) {
        this.context = context;
        this.filedata = data;
        fileItemListener=new FileListItemListener();
    }

    public File[] setfiledata(ArrayList<File> data) {
        this.filedata = data;
        sort();
        this.notifyDataSetChanged();
        File[] files = new File[filedata.size()];
        int j=0;
        for (int i = 0; i < files.length; i++) {
            File file=filedata.get(i);
            if(!file.isHidden()){
                files[j]=file;
                j++;
            }
        }
        return files;
    }

    public File[] setfiledata(){
        File[] files=new File[filedata.size()];
        int j=0;
        for(int i=0;i<files.length;i++){
            File file=filedata.get(i);
            if(!file.isHidden()){
                files[j]=file;
                j++;
            }
        }
        return files;
    }

    private void sort() {
        Collections.sort(this.filedata, FileSortFactory.getWebFileQueryMethod(sortWay));
    }

    @Override
    public void notifyDataSetChanged() {
        //重新排序
        sort();
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filedata.size();
    }

    @Override
    public Object getItem(int position) {
        return filedata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    FileListItemListener fileItemListener;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        File file=filedata.get(position);
        fileItemListener=new FileListItemListener();
        ViewHolder viewHolder;
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.file_item,null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        if(file.isDirectory()){
            viewHolder.fileImage.setImageResource(R.mipmap.folder);
            viewHolder.fileContent.setText(String.valueOf(file.listFiles().length));
        }else {
            viewHolder.fileImage.setImageResource(R.mipmap.file);
        }
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        viewHolder.fileName.setText(file.getName());
        viewHolder.fileTime.setText(format.format(new Date(file.lastModified())));
        return convertView;
    }
    public static class ViewHolder{
        ImageView fileImage;
        TextView fileName;
        TextView fileTime;
        TextView fileContent;
        public ViewHolder(View v){
            fileImage=(ImageView) v.findViewById(R.id.file_bitmap);
            fileName= (TextView) v.findViewById(R.id.file_name);
            fileTime= (TextView) v.findViewById(R.id.file_date);
            fileContent= (TextView) v.findViewById(R.id.fileNumber);
        }
    }

    public class FileListItemListener implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        Integer position;

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.more_rename:
                    doRename();
                    break;
                case R.id.more_copy:
                    doCopy();
                    break;
                case R.id.more_remove:
                    doRemove();;
                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        public void onClick(final View v) {
            position = (Integer) v.getTag();
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.inflate(R.menu.file_list_popup_menu);
            popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                @Override
                public void onDismiss(PopupMenu menu) {
                    RotateAnimation rotateAnimation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    rotateAnimation.setDuration(300);
                    rotateAnimation.setFillAfter(true);
                    v.startAnimation(rotateAnimation);
                }
            });
            popupMenu.setOnMenuItemClickListener(this);
            if (filedata.get(position).isDirectory()) {
                popupMenu.getMenu().findItem(R.id.more_copy).setVisible(false);
            } else {
                popupMenu.getMenu().findItem(R.id.more_copy).setVisible(true);
            }
            RotateAnimation rotateAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(200);
            rotateAnimation.setFillAfter(true);
            v.startAnimation(rotateAnimation);
            popupMenu.show();
        }

        private void doRemove() {
            final File file = filedata.get(position);
            judgeAlertDialog(context, "提醒", "你确认删除" + file.getName() + "吗？", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteDir(file);
                    filedata.remove(file);
                    notifyDataSetChanged();
                    showToast(file.getName()+"删除成功");
                }
            },null);
        }

        private void showToast(String message){
            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
        }

        private void doCopy(){
            if(onCopyFileListener!=null){
                onCopyFileListener.doCopy(filedata.get(position));
            }
        }
        private void doRename(){
            showToast("重命名"+position);
            RenameFileDialog dialog=new RenameFileDialog(context,filedata,position);
            dialog.setOnFileRenameListener(new RenameFileDialog.OnFileRenameListener() {
                @Override
                public void onFileRenamed(boolean success) {
                    String message=null;
                    if(filedata.get(position).isFile()){
                        message="文件";
                    }else{
                        message="文件夹";
                    }
                    if(success){
                        message+="重命名成功";
                    }else{
                        message+="重命名失败";
                    }
                    showToast(message);
                }
            });
            dialog.show();
            setfiledata(filedata);
        }
    }

    public static AlertDialog judgeAlertDialog(Context context, String title, String message,
                                               DialogInterface.OnClickListener okListener,
                                               DialogInterface.OnClickListener cancelListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("取消", cancelListener)
                .setPositiveButton("确定", okListener)
                .show();
        return alertDialog;
    }

    public static void deleteDir(File file) {
        if (file.isFile()) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                deleteDir(f);
            }
        }
        file.delete();
    }

    public interface OnCopyFileListener {
        void doCopy(File file);
    }

    OnCopyFileListener onCopyFileListener;

    public void setOnCopyListener(OnCopyFileListener onCopyListener) {
        this.onCopyFileListener = onCopyFileListener;
    }
}

