package net.tetraowl.processwatcher.cli

import org.jetbrains.annotations.TestOnly
import org.junit.Test

internal class MainKtTest {
    @Test
    fun testLog() {
        main(arrayOf("logs","world"))
    }

}
