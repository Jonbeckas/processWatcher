import co.gongzh.procbridge.Client

class ExampleClient(val command:String, val id:String) {
    init {
        val client = Client("127.0.0.1",57689)
        val ans = Responses.stringToEnum(client.request(command,id) as String)
        when(ans) {
            Responses.OK -> println("Completed succesfully")
            Responses.ALREADYSTARTED -> println("$id is already started")
            Responses.ALREADYSTOPPED -> println("$id is already stopped")
            Responses.TRUE -> println("true")
            Responses.FALSE -> println("false")
            Responses.ALREADYENABLED -> println("$id is already enabled")
            Responses.ALREADYDISABLED -> println("$id is already disabled")
            Responses.NOTFOUND -> println("$id is not found")
            Responses.NOTIMPLEMENTED -> println("ProcessWatcher does not suppert thr requested function!")
            Responses.ERROR -> println("Unknown Error")

        }
    }
}
