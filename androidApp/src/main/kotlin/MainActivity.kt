package com.bitsycore.bubblewrap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import bubblewrap.App

// Android entry point — the SAME App(), on Google's androidx.compose
// artifacts (the org.jetbrains.compose coordinates redirect to them on
// Android; the bridge plugin never touches this target).
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}
