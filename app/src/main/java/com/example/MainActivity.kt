package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.repository.AnimeRepository
import com.example.ui.AnimeMainApp
import com.example.ui.AuthUiState
import com.example.ui.AuthViewModel
import com.example.ui.MainViewModel
import com.example.ui.screens.AuthScreen
import com.example.ui.theme.AnimeBlackBg
import com.example.ui.theme.AnimeCrimson
import com.example.ui.theme.MyApplicationTheme
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AnimeBlackBg
                ) {
                    BlackAnimeApp()
                }
            }
        }
    }
}

@Composable
fun BlackAnimeApp(
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val currentUser by authViewModel.currentUserFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        authViewModel.trySilentSignIn(context)
    }

    val user = currentUser
    if (user == null) {
        AuthScreen(
            uiState = authState,
            onSignInClick = { ctx ->
                authViewModel.signInWithGoogle(ctx)
            }
        )
    } else {
        // Authenticated Session: Inject repository with custom database ID from R.string.firestore_database_id
        val mainViewModel: MainViewModel = viewModel(
            key = user.uid,
            factory = viewModelFactory {
                initializer {
                    val app = checkNotNull(this[APPLICATION_KEY])
                    val databaseId = app.getString(R.string.firestore_database_id)
                    val db = FirebaseFirestore.getInstance(databaseId)
                    val repository = AnimeRepository(db)
                    MainViewModel(repository, user.uid, user)
                }
            }
        )

        AnimeMainApp(
            viewModel = mainViewModel,
            onSignOutClick = { authViewModel.signOut() }
        )
    }
}
