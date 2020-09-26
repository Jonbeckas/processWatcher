package api

import InfoJson
import KeepAlive
import Main
import Responses
import co.gongzh.procbridge.Delegate
import co.gongzh.procbridge.Handler
import co.gongzh.procbridge.Server
import com.beust.klaxon.Klaxon
import org.jetbrains.annotations.Nullable

class Server (main:Main?,port: Int) : Server(port, api.Delegate(main))

class Delegate(val main: Main?) : Delegate() {
    @Handler
    fun stop(payload: String): Responses? {
        val keepAlive: KeepAlive? = this.main?.keepAlives?.find { it.id == payload }
        return if (keepAlive != null) {
            if (!keepAlive.isRunning()) {
                Responses.ALREADYSTOPPED
            } else {
                keepAlive.stop()
                Responses.OK
            }
        } else {
            Responses.NOTFOUND
        }
    }

    @Handler
    fun start(payload: String): Responses? {
        val keepAlive: KeepAlive? = this.main?.keepAlives?.find { it.id == payload }
        return if (keepAlive != null) {
            if (keepAlive.isRunning()) {
                Responses.ALREADYSTARTED
            } else {
                keepAlive.start()
                Responses.OK
            }
        } else {
            Responses.NOTFOUND
        }
    }

    @Handler
    fun disable(payload: String): Responses? {
        val keepAlive: KeepAlive? = this.main?.keepAlives?.find { it.id == payload }
        return if (keepAlive != null) {
            if (!keepAlive.isEnabled()) {
                Responses.ALREADYDISABLED
            } else {
                keepAlive.setEnablet(false)
                Responses.OK
            }
        } else {
            Responses.NOTFOUND
        }
    }

    @Handler
    fun forcedisable(payload: String): Responses? {
        val keepAlive: KeepAlive? = this.main?.keepAlives?.find { it.id == payload }
        return if (keepAlive != null) {
            if (!keepAlive.isEnabled()) {
                Responses.ALREADYDISABLED
            } else {
                keepAlive.setEnablet(false)
                keepAlive.stop()
                Responses.OK
            }
        } else {
            Responses.NOTFOUND
        }
    }
    @Handler
    fun enable(payload: String): Responses? {
        val keepAlive: KeepAlive? = this.main?.keepAlives?.find { it.id == payload }
        return if (keepAlive != null) {
            if (keepAlive.isEnabled()) {
                Responses.ALREADYENABLED
            } else {
                keepAlive.setEnablet(true)
                Responses.OK
            }
        } else {
            Responses.NOTFOUND
        }
    }

    @Handler
    fun running(payload: String): Responses? {
        val keepAlive: KeepAlive? = this.main?.keepAlives?.find { it.id == payload }
        return if (keepAlive != null) {
            return if(keepAlive.isRunning()) Responses.TRUE else Responses.FALSE
        } else {
            Responses.NOTFOUND
        }
    }

    @Handler
    fun restart(payload: String): Responses? {
        val keepAlive: KeepAlive? = this.main?.keepAlives?.find { it.id == payload }
        return if (keepAlive != null) {
            keepAlive.restart()
            return Responses.OK
        } else {
            Responses.NOTFOUND
        }
    }

    @Handler
    fun info(): String {
        return Klaxon().toJsonString(InfoJson("2.0.0",Utils.getOsPath()))
    }


    @Nullable
    override fun handleUnknownRequest(@Nullable method: String?, @Nullable payload: Any?): Any? {
        return Responses.NOTIMPLEMENTED
    }
}
