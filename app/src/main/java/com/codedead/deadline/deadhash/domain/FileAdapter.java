package com.codedead.deadline.deadhash.domain;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.codedead.deadline.deadhash.R;

import java.util.ArrayList;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileDataHolder> {
    private ArrayList<FileData> fileDataList;

    public FileAdapter(ArrayList<FileData> fileDataList) {
        this.fileDataList = fileDataList;
    }

    @Override
    public FileDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.filerecycler_item_row, parent, false);
        return new FileDataHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(FileDataHolder holder, int position) {
        FileData file = fileDataList.get(position);
        holder.bindFileData(file);
    }

    @Override
    public int getItemCount() {
        return fileDataList.size();
    }

    public static class FileDataHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView encryptionName;
        private TextView encryptionData;
        private ImageButton copyData;

        public FileDataHolder(View v) {
            super(v);

            encryptionName = (TextView) v.findViewById(R.id.Encryption_title);
            encryptionData = (TextView) v.findViewById(R.id.Encryption_data);
            copyData = (ImageButton) v.findViewById(R.id.Copy_Data);

            copyData.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(v.getContext().CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(encryptionName.getText(), encryptionData.getText());
            clipboard.setPrimaryClip(clip);

            Toast.makeText(v.getContext(), R.string.toast_data_copied, Toast.LENGTH_SHORT).show();
        }

        public void bindFileData(FileData data) {
            encryptionName.setText(data.getEncryption_name());
            encryptionData.setText(data.getEncryption_data());
        }
    }
}


