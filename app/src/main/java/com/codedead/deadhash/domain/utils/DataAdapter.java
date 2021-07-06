package com.codedead.deadhash.domain.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.codedead.deadhash.R;
import com.codedead.deadhash.domain.objects.hashgenerator.HashData;

import java.util.List;

public final class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataHolder> {

    private final List<HashData> hashDataList;

    /**
     * Initialize a new DataAdapter
     *
     * @param hashDataList The List of EncryptionData objects
     */
    public DataAdapter(final List<HashData> hashDataList) {
        this.hashDataList = hashDataList;
    }

    @NonNull
    @Override
    public DataHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.filerecycler_item_row, parent, false);
        return new DataHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull final DataHolder holder, final int position) {
        final HashData file = hashDataList.get(position);
        holder.bindData(file);
    }

    @Override
    public int getItemCount() {
        return hashDataList.size();
    }

    static class DataHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView encryptionName;
        private final TextView encryptionData;
        private final ImageButton compareData;

        private String originalCompare;

        /**
         * Initialize a new DataHolder
         *
         * @param v The View that holds certain information
         */
        DataHolder(final View v) {
            super(v);

            encryptionName = v.findViewById(R.id.Encryption_title);
            encryptionData = v.findViewById(R.id.Encryption_data);
            compareData = v.findViewById(R.id.Compare_check_image);

            final ImageButton copyData = v.findViewById(R.id.Copy_Data);

            copyData.setOnClickListener(this);
            compareData.setOnClickListener(v1 -> {
                if (originalCompare == null || originalCompare.length() == 0) return;
                if (originalCompare.equals(encryptionData.getText().toString())) {
                    Toast.makeText(v1.getContext(), R.string.toast_hash_match, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v1.getContext(), R.string.toast_hash_mismatch, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onClick(final View v) {
            final ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                final ClipData clip = ClipData.newPlainText(encryptionName.getText(), encryptionData.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(v.getContext(), R.string.toast_data_copied, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(v.getContext(), R.string.string_no_clipboard_access, Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * Bind an EncryptionData object to the view
         *
         * @param data The EncryptionData that should be bound to the view
         */
        void bindData(final HashData data) {
            encryptionName.setText(data.getHashName());
            encryptionData.setText(data.getHashData());

            if (data.getCompareCheck() != null && data.getCompareCheck().length() != 0) {
                originalCompare = data.getCompareCheck();
                if (data.getHashData().equalsIgnoreCase(data.getCompareCheck())) {
                    compareData.setImageResource(R.drawable.ic_compare_check);
                    compareData.setBackgroundTintList(ContextCompat.getColorStateList(compareData.getContext(), R.color.green));
                } else {
                    compareData.setImageResource(R.drawable.ic_compare_uncheck);
                    compareData.setBackgroundTintList(ContextCompat.getColorStateList(compareData.getContext(), R.color.red));
                }
            } else {
                compareData.setVisibility(View.INVISIBLE);
            }
        }
    }
}
