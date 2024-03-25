package com.example.helloapp;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.example.helloapp.R;

public class MusicPlayerActivity extends Activity {

    private MediaPlayer mediaPlayer;
    private Button playButton;
    private Button pauseButton;
    private Button selectButton;
    private SeekBar seekBar;
    private Handler handler;

    private static final int PICK_AUDIO_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = new MediaPlayer();
        handler = new Handler();
        selectButton = findViewById(R.id.selectButton);
        seekBar = findViewById(R.id.seekBar);
        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);
                mediaPlayer.start();
                updateSeekBar();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseButton.setVisibility(View.GONE);
                playButton.setVisibility(View.VISIBLE);
                mediaPlayer.pause();
            }
        });

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                seekBar.setProgress(0);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, PICK_AUDIO_REQUEST);
    }

    private void updateSeekBar() {
        seekBar.setMax(mediaPlayer.getDuration());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    handler.postDelayed(this, 1000); // Обновляем каждую секунду
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK) {
            Uri audioUri = data.getData();
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(getApplicationContext(), audioUri);
                mediaPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        mediaPlayer = null;
    }
}
