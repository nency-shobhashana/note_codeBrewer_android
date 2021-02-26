package com.nency.note.detail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.nency.note.R;

public class PlayButton extends AppCompatImageView {
    private boolean mStartPlaying = true;

    private OnPlayListener onPlayListener;

    public boolean isPlaying() {
        return !mStartPlaying;
    }

    public void stopPlaying(){
        mStartPlaying = true;
        setImageResource(R.drawable.ic_play);
    }

    public void setOnPlayListener(OnPlayListener onPlayListener) {
        this.onPlayListener = onPlayListener;
    }

    OnClickListener clicker = new OnClickListener() {
        public void onClick(View v) {
            if(onPlayListener != null) {
                if (mStartPlaying) {
                    setImageResource(R.drawable.ic_pause);
                } else {
                    setImageResource(R.drawable.ic_play);
                }
                mStartPlaying = !mStartPlaying;
                onPlayListener.onPlay(!mStartPlaying);
            }
        }
    };

    public PlayButton(Context context) {
        this(context, null);
    }

    public PlayButton(@NonNull Context context,
            @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayButton(@NonNull Context context,
            @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageResource(R.drawable.ic_play);
        setOnClickListener(clicker);
    }

    interface OnPlayListener{
        void onPlay(boolean startPlaying);
    }
}