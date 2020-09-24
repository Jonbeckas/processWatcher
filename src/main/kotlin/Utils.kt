import java.io.File
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class Utils {
    companion object {
        fun getJarPath(): String {
            return File(
                Main::class.java.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()
            ).path
        }

        fun getOsPath(): String {
            return if (isUnix()) {
                val file = File("/etc/processwatcher/")
                if (file.canWrite()) {
                    file.path;
                } else {
                    val jarPath = File(getCleanPath())
                    if (jarPath.canWrite()) {
                        println("Cannot acces /etc/processwatcher. Writing to instead to '${jarPath.path}'")
                        jarPath.path
                    } else {
                        val local = File("")
                        println("Cannot acces /etc/processwatcher. Writing to instead to '${local.path}'")
                        local.path
                    }
                }
            } else {
                getCleanPath();
            }
        }

        fun getCleanPath():String {
            val file = File(getJarPath());
            return if (file.isFile) {
                file.parent
            } else {
                file.path
            }
        }

        fun isUnix(): Boolean {
            val os = System.getProperty("os.name").toLowerCase()
            return os.contains("nix") || os.contains("nux") || os.contains("aix")
        }

        fun getDateAsString(date: LocalDateTime): String {
            val simpleDateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            return simpleDateFormat.format(date)
        }
    }
}
