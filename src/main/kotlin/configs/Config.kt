package configs

import com.beust.klaxon.Json
import kotlin.collections.ArrayList

class Config(
    val attempts: Int,
    @Json(name = "refreshRate")
    val refresh: Int,
    val work: ArrayList<KeepAliveConfig>? =null,
    val link: ArrayList<String>? = null
) {
}
