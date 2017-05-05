package com.codedead.deadline.deadhash.domain;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileDialog {

    private static final String PARENT_DIR = "...";
    private String[] fileList;
    private File currentPath;

    public interface FileSelectedListener {
        void fileSelected(File file);
    }

    private ListenerList<FileSelectedListener> fileListenerList = new ListenerList<>();
    private final Activity activity;
    private String fileEndsWith;

    public FileDialog(Activity activity, File initialPath, String fileEndsWith) {
        this.activity = activity;
        setFileEndsWith(fileEndsWith);
        if (!initialPath.exists()) initialPath = Environment.getExternalStorageDirectory();
        loadFileList(initialPath);
    }

    /**
     * @return file dialog
     */
    private Dialog createFileDialog() {
        Dialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(currentPath.getPath());

        builder.setItems(fileList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String fileChosen = fileList[which];
                File chosenFile = getChosenFile(fileChosen);
                if (chosenFile.isDirectory()) {
                    loadFileList(chosenFile);
                    dialog.cancel();
                    dialog.dismiss();
                    showDialog();
                } else fireFileSelectedEvent(chosenFile);
            }
        });

        dialog = builder.show();
        return dialog;
    }


    public void addFileListener(FileSelectedListener listener) {
        fileListenerList.add(listener);
    }

    /**
     * Show file dialog
     */
    public void showDialog() {
        createFileDialog().show();
    }

    private void fireFileSelectedEvent(final File file) {
        fileListenerList.fireEvent(new ListenerList.FireHandler<FileSelectedListener>() {
            public void fireEvent(FileSelectedListener listener) {
                listener.fileSelected(file);
            }
        });
    }

    private void loadFileList(File path) {
        this.currentPath = path;
        List<String> r = new ArrayList<>();
        if (path.exists()) {
            if (path.getParentFile() != null) r.add(PARENT_DIR);
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    if (!sel.canRead()) return false;
                    else {
                        boolean endsWith = fileEndsWith == null || filename.toLowerCase().endsWith(fileEndsWith);
                        return endsWith || sel.isDirectory();
                    }
                }
            };
            String[] fileList1 = path.list(filter);
            if (fileList1 != null) {
                Collections.addAll(r, fileList1);
            }
        }
        fileList = r.toArray(new String[]{});
    }

    private File getChosenFile(String fileChosen) {
        if (fileChosen.equals(PARENT_DIR)) return currentPath.getParentFile();
        else return new File(currentPath, fileChosen);
    }

    private void setFileEndsWith(String fileEndsWith) {
        this.fileEndsWith = fileEndsWith != null ? fileEndsWith.toLowerCase() : null;
    }
}

class ListenerList<L> {
    private List<L> listenerList = new ArrayList<>();

    interface FireHandler<L> {
        void fireEvent(L listener);
    }

    void add(L listener) {
        listenerList.add(listener);
    }

    void fireEvent(FireHandler<L> fireHandler) {
        List<L> copy = new ArrayList<>(listenerList);
        for (L l : copy) {
            fireHandler.fireEvent(l);
        }
    }
}