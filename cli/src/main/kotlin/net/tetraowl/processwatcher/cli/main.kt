package net.tetraowl.processwatcher.cli

import ExampleClient
import InfoJson
import co.gongzh.procbridge.Client
import com.beust.klaxon.Klaxon
import java.io.File

fun main(args:Array<String>) {
    if (args.size==2) {
        val action = args[0]
        val id = args[1]
        if (action=="start"||action=="stop"||action=="disable"|| action == "enable"||action== "forcedisable"|| action=="restart"||action=="running") {
            ExampleClient(action.toLowerCase(),id)
        }else if (action == "logs") {
            getlogs(id)
        } else {
            sendHelp();
        }
    } else sendHelp();
}

fun sendHelp() {
    println("""
        Process Watecher CLI
        
        log <id> -> Show logs for keepalive
        start <id> -> start keepalive
        stop <id> -> stop keepalive
        disable <id> -> disables keepalive
        enable <id> -> enables keepalive
        forcedisable <id> -> disable and kill keepalive
    """.trimIndent())
}

fun getInfo(): InfoJson? {
    val client = Client("127.0.0.1",57689)
    return Klaxon().parse<InfoJson>(client.request("info",null) as String)
}

fun getlogs(id:String) {
    val json:InfoJson? = getInfo()
    val file = File("${json?.path}/logs/${id}.log")
    println(file.readText())
}
