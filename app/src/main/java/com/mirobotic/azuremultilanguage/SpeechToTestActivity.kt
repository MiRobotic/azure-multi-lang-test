package com.mirobotic.azuremultilanguage

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_speech_to_test.*
import android.view.MenuItem



class SpeechToTestActivity : AppCompatActivity() {


    private lateinit var azureSpeechRecognizer:AzureSpeechRecognizer

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speech_to_test)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        val code = intent.getStringExtra("code")
        val lang = intent.getStringExtra("language")
        val key = intent.getStringExtra("key")

        tvLanguage.text = lang

        val speechListener = object : AzureSpeechRecognizer.OnSpeechResultListener{

            override fun onFinalResult(result: String) {
                tvText.text = result
            }

            override fun onIntermediateResult(result: String) {

            }

            override fun onFailure(message: String) {
                tvStatus.text = "Error: $message"
            }

            override fun onSpeechRecognitionStarted() {
                tvStatus.text = "Speech Recognition Started in $lang"
            }

            override fun onSpeechRecognitionStopped() {
                tvStatus.text = "Speech Recognition Stopped"
            }
        }

        azureSpeechRecognizer = AzureSpeechRecognizer.getInstance(this@SpeechToTestActivity,key, code)
        azureSpeechRecognizer.setSpeechResultListener(speechListener)
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed(Runnable {
            azureSpeechRecognizer.startContinuousRecognition()
        },200)
    }

    override fun onPause() {
        super.onPause()
        azureSpeechRecognizer.stopRecognition()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

}
