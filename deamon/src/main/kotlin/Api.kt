import api.Delegate
import api.Server
import co.gongzh.procbridge.IDelegate
import org.jetbrains.annotations.NotNull


class Api(val main: Main?,val PORT: Int = 57689) {
    init {
        val server = Server(main,PORT)
        server.start()
    }
}

