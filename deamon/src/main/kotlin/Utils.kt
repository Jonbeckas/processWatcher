import java.io.File
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

        fun getOsPathWithOut(): String {
            return if (isUnix()) {
                val file = File("/usr/share/processwatcher/")
                if (file.canWrite()) {
                    file.path;
                } else {
                    val jarPath = File(getCleanPath())
                    if (jarPath.canWrite()) {
                        println("Cannot acces /usr/share/processwatcher/. Writing instead to '${jarPath.path}'")
                        jarPath.path
                    } else {
                        val local = System.getProperty("user.dir")
                        println("Cannot acces ${jarPath}. Writing instead to '${local}'")
                        local
                    }
                }
            } else {
                getCleanPath();
            }
        }

        fun getOsPath(): String {
            return if (isUnix()) {
                val file = File("/usr/share/processwatcher/")
                if (file.canWrite()) {
                    file.path;
                } else {
                    val jarPath = File(getCleanPath())
                    if (jarPath.canWrite()) {
                        jarPath.path
                    } else {
                        val local = System.getProperty("user.dir")
                        local
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
