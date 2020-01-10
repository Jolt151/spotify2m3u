package com.test

fun <T : Any> tryOrNull(body: () -> T?): T? {
    //the body returns T? so that you can give up the work by returning null
    return try {
        body()
    } catch (e: Exception) {
        //e.printStackTrace()
        null
    }
}