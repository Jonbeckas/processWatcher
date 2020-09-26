package configs

import com.beust.klaxon.Json
import kotlin.collections.ArrayList

class Config(
    val attempts: Int = 5,
    @Json(name = "refreshRate")
    val refresh: Int=60,
    val work: ArrayList<KeepAliveConfig>? =null,
    val link: ArrayList<String>? = null
) {
}
