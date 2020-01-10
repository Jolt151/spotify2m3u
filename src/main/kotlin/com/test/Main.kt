package com.test

import com.test.SpotifyApiWrapper.Companion.api
import java.io.File
import java.io.FileInputStream
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger


fun main(args: Array<String>) {
    //Disable loggers
    val pin = arrayOf<Logger>(Logger.getLogger("org.jaudiotagger"))
    for (l in pin)
        l.level = Level.OFF


    val properties = Properties()
    properties.load(FileInputStream("application.properties"))

    val playlistId: String = properties["spotify_playlist"] as String
    print(playlistId)

    val spotifyTracks = api.getPlaylist(playlistId)

    val localSongRepository = LocalSongRepository(properties["library_folder"] as String)
    val library = localSongRepository.library
    val libraryList = localSongRepository.libraryList
    val libraryFileNames = localSongRepository.libraryFileNames
    println(library)
    println(library.size)


    val out = File("output.m3u").printWriter()
    var matches = 0
    spotifyTracks.forEach forEach@ { spotifyTrack ->
        val matchingTracks = library[spotifyTrack.title]

        matchingTracks?.let { matchingTracks ->
            //We have one or more songs that match

            if (matchingTracks.size == 1) {
                //We only have one song that matches. Write it.
                out.println(matchingTracks.first().file)
            } else {
                //We have multiple
                //Ask user for which one
                println("Multiple tracks found for ${spotifyTrack.artist} - ${spotifyTrack.title}")
                matchingTracks.forEachIndexed { index, foundTrack ->
                    println("($index) ${foundTrack.file}")
                }

                println("Search results for ${spotifyTrack.artist} - ${spotifyTrack.title}: ")
                val searchList = localSongRepository.searchList(spotifyTrack.title, 10, libraryFileNames)
                searchList.forEachIndexed { index, item ->
                    println("(${index + matchingTracks.size}) $item")
                }

                var libraryTrack: Song? = null
                while (libraryTrack == null) {
                    println("Select the correct one, enter a path to a different one, or press enter to skip ")
                    val entry = readLine()
                    if (entry.isNullOrBlank()) return@forEach

                        entry.toIntOrNull()?.let { int -> //Int entry
                            libraryTrack = matchingTracks.getOrNull(int)
                                ?: searchList.getOrNull(int + matchingTracks.size)?.let { //Int entry that corresponds to search results
                                    localSongRepository.getSingle(it)
                                }
                        } ?: localSongRepository.getSingle(entry)?.let { //Valid String entry
                            libraryTrack = it
                        } ?: run { //Anything else
                            println("Not found, try again")
                        }
                }
                out.println(libraryTrack!!.file)
                matches++
            }
        } ?: run { //No matching tracks found, just do a search

            var song: Song? = null

            println("Missing ${spotifyTrack.artist} - ${spotifyTrack.title}")
            val searchList = localSongRepository.searchList(spotifyTrack.title, 15, libraryFileNames)
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

            out.println(song!!.file)
            matches++

        }
    }
    out.flush()
    println(matches)

}

