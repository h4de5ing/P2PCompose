package org.github.h4de5ing.p2p


fun main() {
    println("Hello World!")
    val node = ChatNode(::println)
    println()
    println("Libp2p Chatter!")
    println("===============")
    println()
    println("This node is ${node.peerId}, listening on ${node.address}")
    println()
    println("Enter 'bye' to quit, enter 'alias <name>' to set chat name")
    println()

    var message: String?
    do {
        print(">> ")
        message = readlnOrNull()?.trim()
        if (message.isNullOrEmpty()) continue
        node.send(message)
    } while ("bye" != message)

    node.stop()
}