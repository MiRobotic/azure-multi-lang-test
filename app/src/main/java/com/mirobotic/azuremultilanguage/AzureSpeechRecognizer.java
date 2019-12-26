package com.mirobotic.azuremultilanguage;

import android.app.Activity;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.RECORD_AUDIO;

public class AzureSpeechRecognizer {


    private static String KEY = "";

    private static final String REGION = "eastus";

    private static String LANG_CODE;


    private static boolean continuousListeningStarted = false;
    private static final String logTag = AzureSpeechRecognizer.class.getSimpleName();
    private SpeechRecognizer recognizer = null;
    private OnSpeechResultListener speechResultListener;
    private SpeechConfig speechConfig;

    private MicrophoneStream microphoneStream;

    private MicrophoneStream createMicrophoneStream() {
        if (microphoneStream != null) {
            microphoneStream.close();
            microphoneStream = null;
        }

        microphoneStream = new MicrophoneStream();
        return microphoneStream;
    }

    private static AzureSpeechRecognizer speechRecognizer = null;

    public static AzureSpeechRecognizer getInstance(Activity activity,String key, String langCode){
        LANG_CODE = langCode;
        KEY = key;
        if (speechRecognizer == null){
            speechRecognizer = new AzureSpeechRecognizer(activity);
        }

        return speechRecognizer;
    }

    private AzureSpeechRecognizer(Activity activity){

        try {
            int permissionRequestId = 5;
            ActivityCompat.requestPermissions(activity, new String[]{RECORD_AUDIO, INTERNET}, permissionRequestId);

            speechConfig = SpeechConfig.fromSubscription(KEY, REGION);

            speechConfig.setSpeechRecognitionLanguage(LANG_CODE);

            // audioInput = AudioConfig.fromDefaultMicrophoneInput();
            AudioConfig audioInput = AudioConfig.fromStreamInput(createMicrophoneStream());

            recognizer = new SpeechRecognizer(speechConfig, audioInput);

        }
        catch(Exception ex) {
            String msg = "could not init sdk, " + ex.toString();
            Log.e("SpeechSDK", msg);
        }


    }

    public void setSpeechResultListener(OnSpeechResultListener speechResultListener) {
        this.speechResultListener = speechResultListener;
        stopRecognition();
    }

    public void startContinuousRecognition() {

        if (continuousListeningStarted){
            return;
        }

        try {

            recognizer.recognizing.addEventListener((o, speechRecognitionResultEventArgs) -> {
                final String s = speechRecognitionResultEventArgs.getResult().getText();
                Log.i(logTag, "Intermediate result received: " + s);
                speechResultListener.onIntermediateResult(s);
            });

            recognizer.recognized.addEventListener((o, speechRecognitionResultEventArgs) -> {
                final String s = speechRecognitionResultEventArgs.getResult().getText();
                Log.i(logTag, "Final result received: " + s);
                speechResultListener.onFinalResult(s);
            });

            final Future<Void> task = recognizer.startContinuousRecognitionAsync();
            setOnTaskCompletedListener(task, result ->{
                        continuousListeningStarted = true;
                        speechResultListener.onSpeechRecognitionStarted();
                    }
            );

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            speechResultListener.onSpeechRecognitionStopped();
        }
    }

    void stopRecognition(){
        if (continuousListeningStarted){
            final Future<Void> task = recognizer.stopContinuousRecognitionAsync();
            setOnTaskCompletedListener(task, result -> {
                Log.i(logTag, "Continuous recognition stopped.");
                speechResultListener.onSpeechRecognitionStopped();
                continuousListeningStarted = false;
                speechRecognizer=null;
            });
        }
    }



    private <T> void setOnTaskCompletedListener(Future<T> task, OnTaskCompletedListener<T> listener) {
        s_executorService.submit(() -> {
            T result = task.get();
            listener.onCompleted(result);
            return null;
        });
    }

    private interface OnTaskCompletedListener<T> {
        void onCompleted(T taskResult);
    }

    private static ExecutorService s_executorService;
    static {
        s_executorService = Executors.newCachedThreadPool();
    }

    public interface OnSpeechResultListener{
        void onFinalResult(String result);
        void onIntermediateResult(String result);
        void onFailure(String message);
        void onSpeechRecognitionStarted();
        void onSpeechRecognitionStopped();
    }

}
