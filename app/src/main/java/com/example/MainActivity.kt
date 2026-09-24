package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.ui.ChatScreen
import com.example.ui.ChatViewModel
import com.example.ui.FlashcardScreen
import com.example.ui.FlashcardViewModel
import com.example.ui.StudyPlannerScreen
import com.example.ui.ZannLauncherSplashScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel by lazy {
        ViewModelProvider(this)[ChatViewModel::class.java]
    }

    private val flashcardViewModel by lazy {
        ViewModelProvider(this)[FlashcardViewModel::class.java]
    }

    private val widgetActionState = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIncomingIntent(intent)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            val appThemeId by viewModel.appThemeId.collectAsState()
            val widgetAction by widgetActionState
            MyApplicationTheme(appThemeId = appThemeId, darkTheme = isDarkTheme) {
                var showSplashScreen by rememberSaveable { mutableStateOf(true) }
                var appMode by rememberSaveable { mutableStateOf("chat") }

                if (showSplashScreen) {
                    ZannLauncherSplashScreen(
                        onFinished = { showSplashScreen = false }
                    )
                } else {
                    if (appMode == "flashcard") {
                        BackHandler {
                            appMode = "chat"
                        }
                        FlashcardScreen(
                            viewModel = flashcardViewModel,
                            onNavigateToChat = { appMode = "chat" },
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = { viewModel.toggleTheme() }
                        )
                    } else if (appMode == "planner") {
                        BackHandler {
                            appMode = "chat"
                        }
                        StudyPlannerScreen(
                            chatViewModel = viewModel,
                            onNavigateToChat = { appMode = "chat" },
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = { viewModel.toggleTheme() }
                        )
                    } else {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            ChatScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding),
                                onNavigateToFlashcards = { appMode = "flashcard" },
                                onNavigateToPlanner = { appMode = "planner" },
                                initialWidgetAction = widgetAction,
                                onHandledWidgetAction = { widgetActionState.value = null }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        val action = intent?.getStringExtra("WIDGET_ACTION")
        if (action != null) {
            widgetActionState.value = action
        }
    }
}

