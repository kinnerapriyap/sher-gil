package com.kinnerapriyap.sample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.kinnerapriyap.sugar.ShergilActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.hello).setOnClickListener {
            startActivity(
                Intent(this, ShergilActivity::class.java)
            )
        }
    }
}
