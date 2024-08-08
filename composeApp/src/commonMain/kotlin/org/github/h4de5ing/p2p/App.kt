package org.github.h4de5ing.p2p

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.util.concurrent.CompletableFuture.runAsync

@Composable
@Preview
fun App() {
    MaterialTheme { P2P() }
}

@Composable
fun P2P() {
    var chatNode: ChatNode? by remember { mutableStateOf(null) }
    var log by remember { mutableStateOf("") }
    var send by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    fun chatMessage(msg: String) {
        log = "${log}${msg}\n"
    }
    LaunchedEffect(Unit) {
        runAsync {
            chatNode = ChatNode(::chatMessage)
        }
//        repeat(1000) {
//            chatMessage("写入了大量的数据:${it}\n")
//        }
    }
    Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            OutlinedTextField(value = send, onValueChange = { send = it })
            Button(onClick = { chatNode?.send(send) }) { Text(text = "发送") }
        }
        Text(text = log, modifier = Modifier.fillMaxWidth().verticalScroll(scrollState))
    }
}