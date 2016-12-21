package com.pk.souzou1;


import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;

import java.util.Locale;

public class TriggerMic extends EventBase{
    AudioRecord recorder;
    public float f = 0;
    public float threshold = 0;
    public boolean invert;
    boolean rec = false;

    public void clone (EventBase e) {
        TriggerMic t = (TriggerMic)e;
        f = t.f;
        threshold = t.threshold;
        invert = t.invert;
    }

    public byte[] GetOutput () {
        return null;
    }

    public String Serialize () {
        return type + "&" + String.format(Locale.JAPAN, "%.2f", threshold) + "&" + (invert ? "1" : "0");
    }

    public TriggerMic () {
        //AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
    }

    @Override
    public void SetRun (boolean b) {
        RunState = b;
        if (b)
            StartRun();
        else
            StopRun();
    }

    public void StartRun () {
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                8000, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, 2048); //mono, 16bits (8x2), buffer size (x2)
        recorder.startRecording();
        rec = true;

        final Handler hh = new Handler();
        hh.postDelayed(new Runnable() {
            public void run() {
                if (rec) {
                    short sData[] = new short[1024];
                    int c = recorder.read(sData, 0, 1024);
                    short max = 0;
                    for (int q = 0; q < c; q++) {
                        if (sData[q] > max) {
                            max = sData[q];
                        }
                    }
                    f = max;
                    /*
                    Handler hhh = new Handler(main.getMainLooper());
                    hhh.post(new Runnable() {
                        @Override
                        public void run() {
                            main.d("mic " + f);
                        }
                    });
                    */
                    hh.postDelayed(this, 100);
                }
            }
        }, 100);
    }

    public void StopRun () {
        recorder.stop();
        recorder.release();
        recorder = null;
        rec = false;
        f = invert? 10000 : 0;
    }

    @Override
    public boolean Pass () {
        return invert? (threshold > f) : (threshold < f);
    }
}
