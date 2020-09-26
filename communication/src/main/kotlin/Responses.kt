public enum class Responses {
    OK,
    NOTFOUND,
    ALREADYSTARTED,
    ALREADYSTOPPED,
    ALREADYENABLED,
    ALREADYDISABLED,
    ERROR,
    NOTIMPLEMENTED,
    TRUE,
    FALSE;
    companion object {
        fun stringToEnum(string:String):Responses? {
            return try {
                Responses.valueOf(string)
            } catch(e: IllegalArgumentException) {
                null
            }
        }
    }

}





