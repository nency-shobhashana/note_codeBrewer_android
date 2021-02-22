package com.nency.note.detail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.nency.note.R;

public class RecordButton extends AppCompatImageView {

    private boolean recording = false;
    private OnRecordListener onRecordListener;

    public boolean isRecording() {
        return recording;
    }

    public void setRecording(boolean isRecording) {
        if (isRecording) {
            setImageResource(R.drawable.ic_stop);
        } else {
            setImageResource(R.drawable.ic_record);
        }
        recording = isRecording;
    }

    public void setOnRecordListener(OnRecordListener onRecordListener) {
        this.onRecordListener = onRecordListener;
    }

    OnClickListener clicker = new OnClickListener() {
        public void onClick(View v) {
            if (onRecordListener != null) {
                setRecording(!recording);
                onRecordListener.onRecord(recording);
            }
        }
    };

    public RecordButton(Context context) {
        this(context, null);
    }

    public RecordButton(@NonNull Context context,
            @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordButton(@NonNull Context context,
            @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageResource(R.drawable.ic_record);
        setOnClickListener(clicker);
    }

    interface OnRecordListener{
        void onRecord(boolean startRecording);
    }
}