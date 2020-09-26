enum class Methods {
    START,
    STOP,
    ENABLE,
    DISABLE,
    FORCEDISABLE,
    RUNNING,
    RESTART;
    companion object {
        fun stringToEnum(string:String):Methods? {
            return try {
                Methods.valueOf(string)
            } catch(e: IllegalArgumentException) {
                null
            }
        }
    }
}
