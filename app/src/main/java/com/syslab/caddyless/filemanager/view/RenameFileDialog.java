package com.syslab.caddyless.filemanager.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.syslab.caddyless.filemanager.R;
import com.syslab.caddyless.filemanager.utils.AlertUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by caddyless on 2017/5/21.
 */

public class RenameFileDialog {
    Context context;
    List<File> webFiles;
    AlertDialog dialog;
    Button btnRename;
    Button btnCancel;
    EditText etFileName;
    TextView tvFileType;
    int position;

    public RenameFileDialog(Context context, List<File> files, int position) {
        this.context = context;
        this.webFiles = files;
        this.position = position;
        initDialog();
    }

    private void initDialog() {
        dialog = new AlertDialog.Builder(context).create();
    }

    private boolean renameFile() {
        String dirName = etFileName.getText().toString();
        if (dirName.equals("")) {
            AlertUtil.toastMess(context, "文件名不能为空");
            return false;
        } else {
            if (checkSameName(dirName)) {
                AlertUtil.toastMess(context, "文件名已存在");
                return false;
            }
        }
        File file = webFiles.get(position);
        return file.renameTo(new File(file.getParentFile().getAbsolutePath() + "/" + dirName));
    }

    private boolean checkSameName(String name) {
        boolean result = false;
        List<String> tempList = new ArrayList<>();
        for (File webFile : webFiles) {
            if (!webFile.isFile()) {
                tempList.add(webFile.getName());
            }
        }
        if (tempList.contains(name)) {
            result = true;
        }
        return result;
    }

    public interface OnFileRenameListener {
        public void onFileRenamed(boolean success);
    }

    OnFileRenameListener onFileRenameListener;

    public void setOnFileRenameListener(OnFileRenameListener onFileRenameListener) {
        this.onFileRenameListener = onFileRenameListener;
    }

    public void show() {
        if (dialog == null) {
            return;
        }
        dialog.show();
        dialog.getWindow().setContentView(R.layout.dialog_rename_file);
        btnRename = (Button) dialog.getWindow().findViewById(R.id.btn_dlg_rename_confirm);
        btnCancel = (Button) dialog.getWindow().findViewById(R.id.btn_dlg_rename_cancle);
        etFileName = (EditText) dialog.getWindow().findViewById(R.id.et_dlg_rename);
        tvFileType = (TextView) dialog.getWindow().findViewById(R.id.tv_dlg_file_type);
        if (webFiles.get(position).isFile()) {
            tvFileType.setText("." +getTailName(webFiles.get(position).getName()));
        }else{
            tvFileType.setText("");
        }
        btnRename.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                dialog.dismiss();;
                if(onFileRenameListener!=null){
                    onFileRenameListener.onFileRenamed(renameFile());
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    public static String getTailName(String name) {
        String type=null;
        if(name.contains(".")){
            String[] splits=name.split("\\.");
            type=splits[splits.length-1].toLowerCase();
        }
        return type;
    }

}
