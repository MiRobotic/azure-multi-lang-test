package com.mirobotic.azuremultilanguage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener{


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnCantonese.setOnClickListener(this)
        btnEnglish.setOnClickListener(this)
        btnMandarin.setOnClickListener(this)
    }

    override fun onClick(v: View) {

        val code:String
        val language:String

        when {
            v.id==R.id.btnCantonese -> {
                code = "zh-HK"
                language = "Cantonese"
            }
            v.id==R.id.btnEnglish -> {
                code = "en-US"
                language = "English"
            }
            else -> {
                code = "zh-CN"
                language = "Mandarin"
            }
        }

        val intent = Intent(this@MainActivity,SpeechToTestActivity::class.java)
        intent.putExtra("code",code)
        intent.putExtra("language",language)
        startActivity(intent)
    }

}
