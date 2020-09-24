import com.beust.klaxon.Klaxon
import configs.Config
import configs.KeepAliveConfig
import java.io.File
import java.io.FileNotFoundException
import kotlin.collections.ArrayList

fun main(args: Array<String>) {
    if (args.isNotEmpty()) {
        Main(args[0])
    } else {
        Main()
    }
}

class Main(val path:String =Utils.getOsPath(),val keepAlives: ArrayList<KeepAlive> = ArrayList()) {

    init {

        while (true) {
            val refresh = runConfig("$path/config.json",)
            Thread.sleep(refresh?.toLong()?.times(1000) ?:60000)
        }
    }

    private fun runConfig(path:String):Int? {
        val config:Config? =parseConfig(path)
        if (config == null) {
            createNewConfig(path)
            println("No config found at $path. Created a new one")
        }

        if (config != null) {
            config.link?.forEach { p:String ->
                runConfig(p)
            }

            config.work?.forEach { c:KeepAliveConfig ->
                runKeepAlive(c,config.attempts)
            }
        }

        return config?.refresh
    }

    private fun runKeepAlive(config:KeepAliveConfig, attempts: Int) {
        val ka: KeepAlive? = this.keepAlives.find{ it.id == config.id };
        if (ka == null) {
            println("Starte KeepAlive ${config.id}")
            val keepAlive = KeepAlive(attempts, config.process, config.arguments,config.id,this.path, config.test)
            keepAlive.start()
            this.keepAlives.add(keepAlive)
        } else {
            ka.restart(config,attempts);
        }
    }

    private fun parseConfig(path:String): Config? {
        return try {
            val file: File = File(path)
            Klaxon().parse<Config>(file.readText())
        } catch (e:FileNotFoundException) {
            null;
        }

    }

    private fun createNewConfig(path: String) {
        val config = Config(5,60, ArrayList(), ArrayList())
        File(path).writeText(Klaxon().toJsonString(config))
    }
}
