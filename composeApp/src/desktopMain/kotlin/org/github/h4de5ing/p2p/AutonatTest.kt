package org.github.h4de5ing.p2p

import io.libp2p.core.Connection
import io.libp2p.core.Stream
import io.libp2p.core.crypto.PrivKey
import io.libp2p.core.dsl.HostBuilder
import io.libp2p.core.multiformats.Multiaddr
import io.libp2p.core.mux.StreamMuxer
import io.libp2p.core.mux.StreamMuxerProtocol
import io.libp2p.protocol.Ping
import io.libp2p.protocol.autonat.AutonatProtocol
import io.libp2p.security.noise.NoiseXXSecureChannel
import io.libp2p.transport.ConnectionUpgrader
import io.libp2p.transport.tcp.TcpTransport
import java.util.concurrent.TimeUnit
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Supplier


fun main() {
    val clientHost =
        HostBuilder()
            .transport(Function { upgrader: ConnectionUpgrader -> TcpTransport(upgrader) })
            .secureChannel(BiFunction { localKey: PrivKey, muxerProtocols: List<StreamMuxer> ->
                NoiseXXSecureChannel(localKey, muxerProtocols)
            })
            .muxer(Supplier { StreamMuxerProtocol.getYamux() })
            .protocol(Ping())
            .protocol(AutonatProtocol.Binding())
            .listen("/ip4/127.0.0.1/tcp/0")
            .build()

    val serverHost =
        HostBuilder()
            .transport(Function { upgrader: ConnectionUpgrader -> TcpTransport(upgrader) })
            .secureChannel(BiFunction { localKey: PrivKey, muxerProtocols: List<StreamMuxer> ->
                NoiseXXSecureChannel(localKey, muxerProtocols)
            })
            .muxer(Supplier { StreamMuxerProtocol.getYamux() })
            .protocol(Ping())
            .protocol(AutonatProtocol.Binding())
            .listen("/ip4/127.0.0.1/tcp/0")
            .build()

    val clientStarted = clientHost.start()
    val serverStarted = serverHost.start()
    clientStarted[5, TimeUnit.SECONDS]
    println("Client started")
    serverStarted[5, TimeUnit.SECONDS]
    println("Server started")

    val autonat = clientHost.network.connect(serverHost.peerId, serverHost.listenAddresses()[0])
        .thenApply { it: Connection ->
            it.muxerSession().createStream(AutonatProtocol.Binding())
        }[5, TimeUnit.SECONDS]

    val autonatStream: Stream = autonat.stream[5, TimeUnit.SECONDS]
    println("Autonat stream created")
    val autonatCtr = autonat.controller[5, TimeUnit.SECONDS]
    println("Autonat controller created")

    val resp = autonatCtr.requestDial(clientHost.peerId, clientHost.listenAddresses())[5, TimeUnit.SECONDS]
    println("resp= ${resp.status}")
    val received = Multiaddr.deserialize(resp.addr.toByteArray())
    println("received ${received},${clientHost.listenAddresses()[0]}")

    autonatStream.close().get(5, TimeUnit.SECONDS)
    println("Autonat stream closed")

    clientHost.stop()[5, TimeUnit.SECONDS]
    println("Client stopped")
    serverHost.stop()[5, TimeUnit.SECONDS]
    println("Server stopped")
}