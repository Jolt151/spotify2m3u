package com.test

import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import java.lang.Exception

class LocalSongRepository {

    //Returns map of song names to all the songs with that title
    fun getSongsAsMap(path: String): Map<String, List<Song>> {
        val songs = getAllSongs(path)

        val map = HashMap<String, MutableList<Song>>()
        for (song in songs) {
            val list: MutableList<Song> = map.getOrDefault(song.title, mutableListOf())
            list.add(song)
            map[song.title] = list
        }

        return map
    }

    private fun getAllSongs(path: String): List<Song> {
        val files = getAllFiles(path)
        val songs: List<Song> = files.mapNotNull {file ->
            try {
                val title = AudioFileIO.read(file).tag.getFirst(FieldKey.TITLE)
                val artist = AudioFileIO.read(file).tag.getFirst(FieldKey.ARTIST)
                Song(file, title, artist)
            } catch (e: Exception) {
                null
            }
        }
        return songs
    }

    fun getSingle(path: String): Song? {
        try {
            val file = File(path)
            val title = AudioFileIO.read(file).tag.getFirst(FieldKey.TITLE)
            val artist = AudioFileIO.read(file).tag.getFirst(FieldKey.ARTIST)
            return Song(file, title, artist)
        } catch (e: Exception) {
            return null
        }
    }

    private fun getAllFiles(path: String): List<File> {
        val list = mutableListOf<File>()

        val file = File(path)
        if (file.isFile) {
            list.add(file)
/*            try {
                val trackName = file.toTrackTitle()
                list.add(trackName)
            } catch (e: Exception) {
                println("no title: ${file.absolutePath}" )
            }*/
        } else if (file.isDirectory) {

            val nestedDirectoryFiles = mutableListOf<File>()

            file.listFiles()?.forEach { child ->
                val files = getAllFiles(child.absolutePath)
                nestedDirectoryFiles.addAll(files)
            }
            list.addAll(nestedDirectoryFiles)
        }
        return list
    }
}