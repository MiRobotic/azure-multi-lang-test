package com.mirobotic.azuremultilanguage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {


    private var code = ""
    private var language = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        spnLang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {


            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if (position == 0 ){
                    code = "zh-HK"
                    language = "Cantonese"
                }else if (position == 1) {
                    code = "zh-CN"
                    language = "Mandarin"
                }else {
                    code = "en-US"
                    language = "English"
                }
            }

        }

        btnStart.setOnClickListener {
            val key = etKey.text.toString()

            if (key.isEmpty()) {
                Toast.makeText(this, "Please enter subscription key", Toast.LENGTH_SHORT).show()
                etKey.error = "Enter Subscription Key"

                return@setOnClickListener

            }


            val intent = Intent(this, SpeechToTestActivity::class.java)
            intent.putExtra("key",key)
            intent.putExtra("code",code)
            intent.putExtra("language",language)

            startActivity(intent)

        }



    }
}
