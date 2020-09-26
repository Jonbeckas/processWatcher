import co.gongzh.procbridge.Client
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals


internal class MainKtTest {
    private val client:Client = Client("127.0.0.1",57689);
    @Before
    fun initApi() {
        Thread {
            main(emptyArray())
        }.start();
        Thread.sleep(3000)
    }

    @Test
    fun testApiStopped() {
        val answer = Responses.stringToEnum(client.request("stop","lol") as String)
        assertEquals(Responses.NOTFOUND, answer)
        val answer1 = Responses.stringToEnum(client.request("stop","world") as String)
        assertEquals(Responses.OK, answer1)
        val answer2 = Responses.stringToEnum(client.request("sdad","loal") as String)
        assertEquals(Responses.NOTIMPLEMENTED, answer2)

    }

    @Test
    fun testAllApi() {
        val answer = Responses.stringToEnum(client.request("stop","world") as String)
        assertEquals(Responses.OK, answer)
        val answer1 = Responses.stringToEnum(client.request("start","world") as String)
        assertEquals(Responses.OK, answer1)
        val answer2 = Responses.stringToEnum(client.request("forcedisable","world") as String)
        assertEquals(Responses.OK, answer2)
        val answer3 = Responses.stringToEnum(client.request("enable","world") as String)
        assertEquals(Responses.OK, answer3)
        val answer4 = Responses.stringToEnum(client.request("disable","world") as String)
        assertEquals(Responses.OK, answer4)
        val answer5 = Responses.stringToEnum(client.request("disable","world") as String)
        assertEquals(Responses.ALREADYDISABLED, answer5)
        val answer6 = Responses.stringToEnum(client.request("restart","world") as String)
        assertEquals(Responses.OK, answer6)
        val answer7 = Responses.stringToEnum(client.request("running","world") as String)
        assertEquals(Responses.FALSE, answer7)
    }
}

