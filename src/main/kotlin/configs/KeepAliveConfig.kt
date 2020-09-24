package configs

data class KeepAliveConfig(
    val id: String,
    val process:String,
    val arguments: ArrayList<String> = ArrayList(),
    val test: KeepAliveTestConfig? =null
)  {

}

data class KeepAliveTestConfig(
    val test:String,
    val expect: String
) {

}
