package com.syslab.caddyless.filemanager.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Files.FileColumns;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by caddyless on 2017/5/26.
 */

public class FileSort {
    public static ArrayList<String> getSpecificTypeOfFile(Context context,String[] extension){
        ArrayList<String> filePaths=new ArrayList<String>();
        Uri fileUri= MediaStore.Files.getContentUri("external");
        String[] projection=new String[]{
                FileColumns.DATA,FileColumns.TITLE
        };
        String selection="";
        for(int i=0;i<extension.length;i++){
            if(i!=0){
                selection=selection+" OR ";
            }
            selection=selection+FileColumns.DATA+" LIKE '%"+extension[i]+"'";
        }
        String sortOrder=FileColumns.DATE_MODIFIED;
        ContentResolver resolver=context.getContentResolver();
        Cursor cursor=resolver.query(fileUri,projection,selection,null,sortOrder);
        if(cursor==null){
            return filePaths;
        }
        if(cursor.moveToLast()){
            do {
                String data=cursor.getString(0);
                filePaths.add(data);
                Log.d("tag",data);
            }while (cursor.moveToPrevious());
        }
        cursor.close();
        return filePaths;
    }
}