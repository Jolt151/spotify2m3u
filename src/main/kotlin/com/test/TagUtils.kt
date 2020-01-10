package com.test

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File

class TagUtils {

    companion object {
        fun getTrackTitle(songFile: File): String {
            val audioFile = AudioFileIO.read(songFile)
            val title = audioFile.tag.getFirst(FieldKey.TITLE)
            return title
        }
    }
}

fun File.toTrackTitle(): String {
    return TagUtils.getTrackTitle(this)
}