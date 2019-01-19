package com.example.administrator.qmusic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.playlib.PlayJniProxy;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int PLAYSTATE_INIT = -1;
    private static final int PLAYSTATE_START = 0;
    private static final int PLAYSTATE_RESUME = 1;
    private static final int PLAYSTATE_PAUSE = 2;
    private static final int PLAYSTATE_STOP = 3;
    private static final int PLAYSTATE_NEXT = 4;

    PlayJniProxy mPlayJniProxy;
    private String playUrl = "http://mpge.5nd.com/2015/2015-11-26/69708/1.mp3";
    private String nextUrl = "http://ngcdn004.cnr.cn/live/dszs/index.m3u8";
    private SeekBar mVolumeBar;
    private SeekBar mTimeBar;
    private boolean mPlayNext = false;
    private int mPlayState = PLAYSTATE_INIT;
    private int mVolume = 85;
    private int mChannelLayout = PlayJniProxy.PLAY_CHANNEL_STEREO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initAudioPlay();
        mVolumeBar = (SeekBar) findViewById(R.id.seek_volume);
        mVolumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPlayJniProxy.setVolume(progress);
                Log.i(TAG, "mVolumeBar progress : " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mVolumeBar.setProgress(mVolume);
        mTimeBar = (SeekBar) findViewById(R.id.seek_time);
        mTimeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPlayJniProxy.seek(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initAudioPlay() {
        mPlayJniProxy = new PlayJniProxy();
        mPlayJniProxy.setPlayProgressCallBack(new PlayJniProxy.PlayProgressCallBack() {
            @Override
            public void onPrepared() {
                mPlayJniProxy.start();
            }

            @Override
            public void onStarted() {
                mPlayState = PLAYSTATE_START;
            }

            @Override
            public void onResumed() {
                mPlayState = PLAYSTATE_RESUME;
            }

            @Override
            public void onPaused() {
                mPlayState = PLAYSTATE_PAUSE;
            }

            @Override
            public void onStopped() {
                if (mPlayNext) {
                    mPlayJniProxy.prepare();
                    mPlayNext = false;
                } else {
                    mPlayState = PLAYSTATE_STOP;
                }
            }

            @Override
            public void onSeeked(int progress) {
                mTimeBar.setProgress(progress);
            }

            @Override
            public void onVolumeModified(int percent) {
                mVolume = percent;
            }

            @Override
            public void onChannelLayoutModify(int layout) {

            }

            @Override
            public void onPitchModified(float pitch) {

            }

            @Override
            public void onSpeedModified(float speed) {

            }

            @Override
            public void onError(final int code, final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "error code : " + code + " msg : " + msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void onStart(View view) {
        if (mPlayState == PLAYSTATE_START) {
            return;
        }
        mPlayJniProxy.prepare(nextUrl,  mVolume, mChannelLayout);
    }

    public void onPause(View view) {
        if (mPlayState == PLAYSTATE_PAUSE) {
            return;
        }
        mPlayJniProxy.pause();
    }

    public void onResume(View view) {
        if (mPlayState == PLAYSTATE_RESUME) {
            return;
        }
        mPlayJniProxy.resume();
    }

    public void onStop(View view) {
        if (mPlayState == PLAYSTATE_STOP) {
            return;
        }
        mPlayJniProxy.stop();
    }

    public void onLeft(View view) {
        mPlayJniProxy.switchChannel(PlayJniProxy.PLAY_CHANNEL_LEFT);
    }

    public void onStereo(View view) {
        mPlayJniProxy.switchChannel(PlayJniProxy.PLAY_CHANNEL_STEREO);
    }

    public void onRight(View view) {
        mPlayJniProxy.switchChannel(PlayJniProxy.PLAY_CHANNEL_RIGHT);
    }

    public void onPitch(View view) {
        mPlayJniProxy.setPitch(1.5f);
    }

    public void onNormal(View view) {
        mPlayJniProxy.setPitch(1.0f);
        mPlayJniProxy.setSpeed(1.0f);
    }

    public void onSpeed(View view) {
        mPlayJniProxy.setSpeed(1.5f);
    }

    public void onNext(View view) {
        mPlayNext = true;
        mPlayState = PLAYSTATE_NEXT;
        mPlayJniProxy.next(playUrl);
    }
}
