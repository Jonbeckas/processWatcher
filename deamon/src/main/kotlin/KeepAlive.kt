import configs.KeepAliveConfig
import configs.KeepAliveTestConfig
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit


class KeepAlive(
    var attempts: Int,
    var process: String,
    var arguments: ArrayList<String>,
    val id: String,
    val directory:String,
    var test: KeepAliveTestConfig? = null,
    var enabled: Boolean,
    private var threadActive:Boolean=true,
    private var passedAttempts: Int = 1,
) {

    public fun start() {
        Thread {
            writeToLog("[PROCESS_WATCHER ${Utils.getDateAsString(LocalDateTime.now())}] Start Process \n")
            startAsync();
        }.start()
    }

    private fun startAsync() {
        this.threadActive = true
        val parts: ArrayList<String> = this.arguments.clone() as ArrayList<String>
        parts.add(0, this.process)
        val process = ProcessBuilder(*parts.toTypedArray())
            .directory(File(this.directory))
            .redirectErrorStream(true)
            .start();
        val log = BufferedReader(InputStreamReader(process.inputStream))
        var line: String? = log.readLine();
        while (line != null&& threadActive) {
            writeStdOut(line)
            line = log.readLine()
        }
        if (threadActive && this.enabled) {
            if (this.passedAttempts < this.attempts) {
                writeToLog("[PROCESS_WATCHER ${Utils.getDateAsString(LocalDateTime.now())}] Process Stopped, Restart! \n")
                passedAttempts++
                startAsync();
            } else if (this.passedAttempts == this.attempts) {
                writeToLog("[PROCESS_WATCHER ${Utils.getDateAsString(LocalDateTime.now())}] Reached max retries! \n")
            }
        } else {
            process.destroyForcibly()
            return
        }
    }

     fun restart(config: KeepAliveConfig, attempts: Int) {
        this.process = config.process
        this.arguments = config.arguments
        this.test = config.test
        this.attempts = attempts;
         restart()
    }

    fun restart() {
        if (!this.enabled) return
        val oldAttemps = this.passedAttempts;
        this.passedAttempts = 0
        println("Run evt restarts for ${this.id}")
        if (this.test!=null) {
            if (getTestResult(this.test!!.test) == this.test!!.expect) {
                if (oldAttemps > this.attempts && this.threadActive) {
                    writeToLog("[PROCESS_WATCHER ${Utils.getDateAsString(LocalDateTime.now())}] Reset attempts\n")
                    startAsync()
                }
            } else {
                stop()
                writeToLog("[PROCESS_WATCHER ${Utils.getDateAsString(LocalDateTime.now())}] Restart after failed test\n")
                start()
            }
        } else {
            if (oldAttemps >= this.attempts && this.threadActive) {
                writeToLog("[PROCESS_WATCHER ${Utils.getDateAsString(LocalDateTime.now())}] Reset attempts\n")
                startAsync()
            }
        }
    }

    fun isRunning():Boolean {
        return this.threadActive
    }

    fun stop() {
        this.threadActive = false;
        writeToLog("[PROCESS_WATCHER ${Utils.getDateAsString(LocalDateTime.now())}] Manually Stopped keepalive\n")
    }

    fun setEnablet(boolean: Boolean) {
        this.enabled = boolean;
        val state = if(boolean){ "enabled"} else {"disabled"}
        writeToLog("[PROCESS_WATCHER ${Utils.getDateAsString(LocalDateTime.now())}] Manually $state keepalive\n")
    }
    fun isEnabled(): Boolean {
        return this.enabled
    }

    private fun getTestResult(test:String): String {
        val parts = test.split(" ")
        val proc = ProcessBuilder(*parts.toTypedArray())
            .directory(File(this.directory))
            .redirectErrorStream(true)
            .start()
        proc.waitFor(60,TimeUnit.MINUTES)
        return proc.inputStream.bufferedReader().readText().replace("\n","")
    }

    private fun writeStdOut(string: String) {
        writeToLog("[INFO ${Utils.getDateAsString(LocalDateTime.now())}] $string \n")
    }


    private fun writeToLog(string: String) {
        File(this.directory + "/logs/").mkdirs()
        val file = File(this.directory + "/logs/${this.id}.log")
        file.appendText(string)
    }
}
