package com.aaditya.smartfit

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.aaditya.smartfit.navigation.SmartFitNavGraph
import com.aaditya.smartfit.ui.theme.SmartFitTheme

@Composable
fun SmartFitApp() {
    var darkTheme by remember { mutableStateOf(false) }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    SmartFitTheme(darkTheme = darkTheme) {
        SmartFitNavGraph(
            onToggleTheme = { enabled -> darkTheme = enabled },
            profileImageUri = profileImageUri,
            onProfileImageChanged = { uri -> profileImageUri = uri }
        )
    }
}
