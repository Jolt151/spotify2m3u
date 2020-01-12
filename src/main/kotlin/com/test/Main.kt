package com.test

import com.google.gson.Gson
import java.io.File
import java.io.FileInputStream
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger


fun main(args: Array<String>) { /*
    //Disable loggers
    val pin = arrayOf<Logger>(Logger.getLogger("org.jaudiotagger"))
    for (l in pin)
        l.level = Level.OFF


    val properties = Properties()
    properties.load(FileInputStream("application.properties"))

    val playlistId: String = properties["spotify_playlist"] as String
    print(playlistId)

    val spotifyApi = SpotifyApiWrapper()

    val spotifyTracks = spotifyApi.getPlaylist(playlistId)

    val localSongRepository = LocalSongRepository(properties["library_folder"] as String)
    val library = localSongRepository.library
    val libraryList = localSongRepository.libraryList
    val libraryFileNames = localSongRepository.libraryFileNames
    println(library)
    println(library.size)


    val out = File("output.m3u").printWriter()

    val trackMatcher = TrackMatcher()
    trackMatcher.match(spotifyTracks)


    trackMatcher.getPendingTracks().forEach {
        //prompt user for location

        val playlistIndex = it.first
        val spotifyTrack = it.second

        if (library.containsKey(spotifyTrack.title)) {
            //We already have multiple
            //Ask user for which one
            println("Multiple tracks found for ${spotifyTrack.artist} - ${spotifyTrack.title}")
            val matchingTracks = library[spotifyTrack.title]!! //already used containsKey
            matchingTracks.forEachIndexed { index, foundTrack ->
                println("($index) ${foundTrack.file}")
            }

            println("Search results for ${spotifyTrack.artist} - ${spotifyTrack.title}: ")
            val searchList = trackMatcher.searchForMatches(spotifyTrack)
            searchList.forEachIndexed { index, item ->
                println("(${index + matchingTracks.size}) $item")
            }

            var libraryTrack: Song? = null
            while (libraryTrack == null) {
                println("Select the correct one, enter a path to a different one, or press enter to skip ")
                val entry = readLine()
                if (entry.isNullOrBlank()) return@forEach

                entry.toIntOrNull()?.let { int ->
                    //Int entry
                    libraryTrack = matchingTracks.getOrNull(int)
                        ?: searchList.getOrNull(int + matchingTracks.size)?.let {
                            //Int entry that corresponds to search results
                            localSongRepository.getSingle(it)
                        }
                } ?: localSongRepository.getSingle(entry)?.let {
                    //Valid String entry
                    libraryTrack = it
                } ?: run {
                    //Anything else
                    println("Not found, try again")
                }
            }


            trackMatcher.addMatch(playlistIndex, libraryTrack!!)
        } else { //just search

            var song: Song? = null

            println("Missing ${spotifyTrack.artist} - ${spotifyTrack.title}")
            val searchList = localSongRepository.searchLibrary(spotifyTrack.title, 10)
            searchList.forEachIndexed { index, item ->
                println("($index) $item")
            }

            while (song == null) {
                println("Select the correct one, enter a path to a different one, or press enter to skip ")
                println("Selection: ")

                val entry = readLine()
                if (entry.isNullOrBlank()) return@forEach
                entry.toIntOrNull()?.let {
                    song = localSongRepository.getSingle(searchList[it])
                } ?: localSongRepository.getSingle(entry)?.let {
                    song = it
                } ?: run {
                    println("Not found, try again")
                }
            }

            trackMatcher.addMatch(playlistIndex, song!!)
        }

    }


    val filepaths = localSongRepository.orderSongsAsFilepaths(trackMatcher.getFoundTracks())
    filepaths.forEach {
        out.println(filepaths)
    }
    out.flush()
    println(filepaths.size)
 */


    val gson = Gson()
    val result: MatchJob = gson.fromJson("fdsa", MatchJob::class.java)
}

