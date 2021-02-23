package com.nency.note.detail;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nency.note.R;
import com.nency.note.interfaces.OnAudioItemClickListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.AUDIO_SERVICE;

public class AudioRecordFragment extends BottomSheetDialogFragment
        implements OnAudioItemClickListener {

    private static final String LOG_TAG = "AudioRecordTest";

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    private static String newAudioFileName = null;

    private RecordButton recordButton = null;
    private MediaRecorder recorder = null;

    private PlayButton playButton = null;
    private MediaPlayer player = null;
    private SeekBar scrubSeekBar;
    Timer seekTimer;

    private ArrayList<String> audioList;
    RecyclerView recyclerView;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    private AudioRecordFragment() {
    }

    public static AudioRecordFragment newInstance(ArrayList<String> audioList) {
        AudioRecordFragment fragment = new AudioRecordFragment();
        fragment.audioList = audioList;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_audio_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setPeekHeight();
        recyclerView = view.findViewById(R.id.audioList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new AudioAdapter(audioList, this));
        scrubSeekBar = view.findViewById(R.id.scruber);
        setAudioListener();
    }

    private void setAudioListener() {
        recordButton = ((RecordButton) getView().findViewById(R.id.btnRecord));
        recordButton.setOnRecordListener(new RecordButton.OnRecordListener() {
            @Override
            public void onRecord(boolean start) {
                AudioRecordFragment.this.onRecord(start);
            }
        });
        playButton = ((PlayButton) getView().findViewById(R.id.btnPlay));
        playButton.setOnPlayListener(new PlayButton.OnPlayListener() {
            @Override
            public void onPlay(boolean start) {
                AudioRecordFragment.this.onPlay(start);
            }
        });
    }

    private void setPeekHeight() {
        Dialog baseDialog = getDialog();
        if (baseDialog instanceof BottomSheetDialog) {
            BottomSheetDialog dialog = (BottomSheetDialog) baseDialog;
            BottomSheetBehavior<?> behavior = dialog.getBehavior();
            behavior.setPeekHeight(300);
        }
    }

    @Override
    public void onItemSelect(int position) {
        fileName = audioList.get(position);
        playButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemRemove(int position) {
        audioList.remove(position);
        recyclerView.getAdapter().notifyDataSetChanged();
        fileName = null;
        playButton.setVisibility(View.GONE);
        recordButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) {
            dismiss();
        }

    }

    private void onRecord(boolean start) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    REQUEST_RECORD_AUDIO_PERMISSION);
            recordButton.setRecording(false);
            return;
        }
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();

            AudioManager audioManager = (AudioManager) getContext().getSystemService(AUDIO_SERVICE);

            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
            // set audio progress

            scrubSeekBar.setMax(player.getDuration());
            scrubSeekBar.setVisibility(View.VISIBLE);
            scrubSeekBar.setProgress(0);

            scrubSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    player.seekTo(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            seekTimer= new Timer();
            seekTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    scrubSeekBar.setProgress(player.getCurrentPosition());
                }
            }, 0, 300);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    AudioRecordFragment.this.stopPlaying();
                }
            });
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        if(player != null) {
            player.release();
            player = null;
            scrubSeekBar.setVisibility(View.GONE);
            playButton.stopPlaying();
            seekTimer.cancel();
        }
    }

    private String createAudioFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Audio_" + timeStamp;
        String storageDir =
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        String audio = storageDir + "/" +
                imageFileName+  /* prefix */
                ".3gp";         /* suffix */

        // Save a file: path for use with ACTION_VIEW intents
        return audio;
    }

    private void startRecording() {
        try {
            recorder = new MediaRecorder();
            newAudioFileName = createAudioFile();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(newAudioFileName);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.prepare();

            playButton.setVisibility(View.GONE);
            recyclerView.setClickable(false);
        } catch (IOException e) {
            recordButton.setRecording(false);
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        audioList.add(newAudioFileName);
        recyclerView.getAdapter().notifyDataSetChanged();
        newAudioFileName = null;
        recorder.stop();
        recorder.release();
        recorder = null;
        recyclerView.setClickable(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }


    private static class AudioViewHolder extends RecyclerView.ViewHolder {
        int position = 0;
        final TextView text;

        AudioViewHolder(LayoutInflater inflater, ViewGroup parent,
                final OnAudioItemClickListener onItemClickListener) {
            // TODO: Customize the item layout
            super(inflater.inflate(R.layout.item_audio,
                    parent,
                    false));
            text = itemView.findViewById(R.id.txtAudioFileName);
            itemView.findViewById(R.id.btnRemove)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onItemClickListener.onItemRemove(position);
                        }
                    });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemSelect(position);
                }
            });
        }
    }

    private static class AudioAdapter extends RecyclerView.Adapter<AudioViewHolder> {

        private final ArrayList<String> audioList;
        private final OnAudioItemClickListener onItemClickListener;

        AudioAdapter(ArrayList<String> audioList, OnAudioItemClickListener onItemClickListener) {
            this.audioList = audioList;
            this.onItemClickListener = onItemClickListener;
        }

        @NonNull
        @Override
        public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AudioViewHolder(LayoutInflater.from(parent.getContext()),
                    parent,
                    onItemClickListener);
        }

        @Override
        public void onBindViewHolder(AudioViewHolder holder, int position) {
            String filePath = audioList.get(position);
            holder.text.setText(filePath.substring(filePath.lastIndexOf("/") + 1));
            holder.position = position;
        }

        @Override
        public int getItemCount() {
            return audioList.size();
        }

    }
}