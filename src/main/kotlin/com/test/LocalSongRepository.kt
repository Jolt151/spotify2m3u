package com.test

import me.xdrop.fuzzywuzzy.FuzzySearch
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import java.lang.Exception

class LocalSongRepository(libraryPath: String) {

    private val filterWords = listOf("feat",
        "remix",
        "mix",
        "edit")


    val library: Map<String, List<Song>> by lazy {
        getSongsAsMap(libraryPath)
    }
    val libraryList: List<Song> by lazy {
        library.flatMap { it.value }
    }
    val libraryFileNames: List<String> by lazy {
        libraryList.map { it.file.toString() }
    }


    //Returns map of song names to all the songs with that title
    private fun getSongsAsMap(path: String): Map<String, List<Song>> {
        val songs = getAllSongs(path)

        val map = HashMap<String, MutableList<Song>>()
        for (song in songs) {
            val list: MutableList<Song> = map.getOrDefault(song.title, mutableListOf())
            list.add(song)
            map[song.title] = list
        }

        return map
    }

    fun getSingle(path: String): Song? {
        return tryOrNull {
            val file = File(path)
            val title = AudioFileIO.read(file).tag.getFirst(FieldKey.TITLE)
            val artist = AudioFileIO.read(file).tag.getFirst(FieldKey.ARTIST)
            Song(file, title, artist)
        }
    }

    fun searchList(query: String, size: Int, list: List<String>): List<String> {
        //filter out common words
        val newQuery = formatQuery(query)

        //Use take because this returns the entire list for some reason even though we pass the size
        return FuzzySearch.extractSorted(newQuery, list, size).take(size).map { it.string }
    }

    /**
     * Removes unwanted elements from the search query
     */
    private fun formatQuery(query: String): String {

        var mutableQuery = query
        filterWords.forEach { word ->
            mutableQuery = mutableQuery.replace(word, " ", ignoreCase = true)
        }
        mutableQuery = mutableQuery.filter { it.isLetterOrDigit() }
        return mutableQuery
    }

    private fun getAllSongs(path: String): List<Song> {
        val files = getAllFiles(path)
        val songs: List<Song> = files.mapNotNull {file ->
            tryOrNull {
                val title = AudioFileIO.read(file).tag.getFirst(FieldKey.TITLE)
                val artist = AudioFileIO.read(file).tag.getFirst(FieldKey.ARTIST)
                Song(file, title, artist)
            }
        }
        return songs
    }

    private fun getAllFiles(path: String): List<File> {
        val list = mutableListOf<File>()

        val file = File(path)
        if (file.isFile) {
            list.add(file)
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