//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE.md file in the project root for full license information.
//
package com.mirobotic.azuremultilanguage;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;

import com.microsoft.cognitiveservices.speech.audio.AudioStreamFormat;
import com.microsoft.cognitiveservices.speech.audio.PullAudioInputStreamCallback;


/**
 * MicrophoneStream exposes the Android Microphone as an PullAudioInputStreamCallback
 * to be consumed by the Speech SDK.
 * It configures the microphone with 16 kHz sample rate, 16 bit samples, mono (single-channel).
 */
public class MicrophoneStream extends PullAudioInputStreamCallback {
    private final static int SAMPLE_RATE = 16000;
    private final AudioStreamFormat format;
    private AudioRecord recorder;




    private static final int SAMPLING_RATE_IN_HZ = 16000;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE_FACTOR = 2;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLING_RATE_IN_HZ,
            CHANNEL_CONFIG, AUDIO_FORMAT) * BUFFER_SIZE_FACTOR;


    MicrophoneStream() {
        this.format = AudioStreamFormat.getWaveFormatPCM(SAMPLE_RATE, (short)16, (short)1);
        this.initMic();
    }

    public AudioStreamFormat getFormat() {
        return this.format;
    }

    @Override
    public int read(byte[] bytes) {
        long ret = this.recorder.read(bytes, 0, bytes.length);
        return (int)ret;
    }

    @Override
    public void close() {
        this.recorder.release();
        this.recorder = null;
    }

    private void initMic() {
        // Note: currently, the Speech SDK support 16 kHz sample rate, 16 bit samples, mono (single-channel) only.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioFormat af = new AudioFormat.Builder()
                    .setSampleRate(SAMPLE_RATE)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                    .build();

            this.recorder = new AudioRecord.Builder()
                    .setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION)
                    .setAudioFormat(af)
                    .build();
        }else {

            recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, SAMPLE_RATE,
                    CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);
        }

        this.recorder.startRecording();
    }
}
