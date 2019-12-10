package com.dadatop.cd.firemonitor;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

public class Mp3Activity extends Activity{
    private  MediaPlayer mediaPlayer=null;

    private String mp3 = "0.mp3";
    private AudioManager audioManager;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.addActivity(this);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        setContentView(R.layout.activity_mp3);
        mp3 = getIntent().getStringExtra("mp3");
        if(mp3==null)mp3 = "0.mp3";
        play();

    }

    private void play() {
        if(mediaPlayer==null){
            //创建播放实例
            if(mp3.contains("0.mp3")){
                mediaPlayer=MediaPlayer.create(this, R.raw.zero);
            } else if(mp3.contains("1.mp3")){
                mediaPlayer=MediaPlayer.create(this, R.raw.one);
            }else if(mp3.contains("2.mp3")){
                mediaPlayer=MediaPlayer.create(this, R.raw.two);
            }else if(mp3.contains("3.mp3")){
                mediaPlayer=MediaPlayer.create(this, R.raw.three);
            }else if(mp3.contains("4.mp3")){
                mediaPlayer=MediaPlayer.create(this, R.raw.four);
            }else if(mp3.contains("5.mp3")){
                mediaPlayer=MediaPlayer.create(this, R.raw.five);
            }else if(mp3.contains("6.mp3")){
                mediaPlayer=MediaPlayer.create(this, R.raw.six);
            }
        }

        if(mediaPlayer==null){
            mediaPlayer=MediaPlayer.create(this, R.raw.zero);
        }

        changeToReceiver();

        mediaPlayer.setLooping(false);
        mediaPlayer.seekTo(0);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });
    }

    /**
     * 切换到听筒
     */
    public void changeToReceiver(){
        audioManager.setSpeakerphoneOn(true);
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } else {
            audioManager.setMode(AudioManager.MODE_IN_CALL);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
