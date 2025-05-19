package com.vincent.jetmp3

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.vincent.jetmp3.ui.layout.SplashScreen

@ExperimentalMaterial3ExpressiveApi
@SuppressLint("CustomSplashScreen")
class SplashActivity: ComponentActivity() {
    @ExperimentalPermissionsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen(onTimeout = {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            })
        }
    }
}