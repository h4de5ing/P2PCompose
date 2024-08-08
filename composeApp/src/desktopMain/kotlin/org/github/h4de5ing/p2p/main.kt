package org.github.h4de5ing.p2p

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "P2PCompose",
    ) {
        App()
    }
}