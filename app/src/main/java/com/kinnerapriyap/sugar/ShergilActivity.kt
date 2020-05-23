package com.kinnerapriyap.sugar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class ShergilActivity : AppCompatActivity() {

    private val container: View
        get() = findViewById(R.id.container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shergil)
    }
}
