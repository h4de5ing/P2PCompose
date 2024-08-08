package org.github.h4de5ing.p2p

import io.libp2p.core.dsl.host
import io.libp2p.core.multiformats.Multiaddr
import io.libp2p.protocol.Ping


fun main() {
    // Create a libp2p node and configure it
    // to accept TCP connections on a random port

    val node = host {
        protocols {
            +Ping()
        }
        network {
            listen("/ip4/127.0.0.1/tcp/0")
        }
    }
//    val node = HostBuilder().protocol(Ping()).listen("/ip4/127.0.0.1/tcp/0").build()
    // start listening
    node.start().get()
    print("Node started and listening on ")
    println(node.listenAddresses())
    val address = Multiaddr.fromString("/ip4/127.0.0.1/tcp/0")
    val pinger = Ping().dial(node, address).controller.get()
    println("Sending 5 ping messages to $address")
    for (i in 1..5) {
        val latency = pinger.ping().get()
        println("Ping " + i + ", latency " + latency + "ms")
    }
    node.stop().get()
}