package com.hb.map.navigation.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hb.map.navigation.app.databinding.ActivityMainBinding
import com.hb.map.navigation.ui.test.NavigationLauncherActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSample.setOnClickListener {
            val intent = Intent(this, NavigationLauncherActivity::class.java)
            startActivity(intent)
        }
    }
}
