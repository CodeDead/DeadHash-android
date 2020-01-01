package com.codedead.deadhash.domain;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.codedead.deadhash.R;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataHolder> {

    private final ArrayList<EncryptionData> encryptionDataList;

    public DataAdapter(ArrayList<EncryptionData> encryptionDataList) {
        this.encryptionDataList = encryptionDataList;
    }

    @NonNull
    @Override
    public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.filerecycler_item_row, parent, false);
        return new DataHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull DataHolder holder, int position) {
        EncryptionData file = encryptionDataList.get(position);
        holder.bindData(file);
    }

    @Override
    public int getItemCount() {
        return encryptionDataList.size();
    }

    static class DataHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView encryptionName;
        private final TextView encryptionData;

        private final ImageButton compareData;

        private String originalCompare;

        DataHolder(View v) {
            super(v);

            encryptionName = v.findViewById(R.id.Encryption_title);
            encryptionData = v.findViewById(R.id.Encryption_data);
            compareData = v.findViewById(R.id.Compare_check_image);

            final ImageButton copyData = v.findViewById(R.id.Copy_Data);

            copyData.setOnClickListener(this);
            compareData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (originalCompare == null || originalCompare.length() == 0) return;
                    if (originalCompare.equals(encryptionData.getText().toString())) {
                        Toast.makeText(v.getContext(), R.string.toast_hash_match, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(v.getContext(), R.string.toast_hash_mismatch, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            final ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                final ClipData clip = ClipData.newPlainText(encryptionName.getText(), encryptionData.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(v.getContext(), R.string.toast_data_copied, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(v.getContext(), R.string.string_no_clipboard_access, Toast.LENGTH_SHORT).show();
            }
        }

        void bindData(EncryptionData data) {
            encryptionName.setText(data.getEncryption_name());
            encryptionData.setText(data.getEncryption_data());

            if (data.getCompareCheck() != null && data.getCompareCheck().length() != 0) {
                originalCompare = data.getCompareCheck();
                if (data.getEncryption_data().equals(data.getCompareCheck())) {
                    compareData.setImageResource(R.drawable.ic_compare_check);
                } else {
                    compareData.setImageResource(R.drawable.ic_compare_uncheck);
                }
            } else {
                compareData.setVisibility(View.INVISIBLE);
            }
        }
    }
}


