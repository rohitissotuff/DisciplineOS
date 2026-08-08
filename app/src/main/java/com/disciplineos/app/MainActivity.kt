package com.disciplineos.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.disciplineos.app.ui.navigation.DisciplineOsApp
import com.disciplineos.app.ui.theme.Black
import com.disciplineos.app.ui.theme.DisciplineOsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DisciplineOsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Black,
                ) {
                    DisciplineOsApp()
                }
            }
        }
    }
}
