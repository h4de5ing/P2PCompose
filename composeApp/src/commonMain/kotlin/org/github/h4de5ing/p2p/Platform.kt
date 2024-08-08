package org.github.h4de5ing.p2p

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform