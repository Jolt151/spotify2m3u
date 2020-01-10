package com.test

import com.wrapper.spotify.SpotifyApi
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

    val api: SpotifyApi = SpotifyApi.Builder()
        .setClientId("deecbb22fbb641fb853c69721338e3a2")
        .setClientSecret("09b0d29264be4ba6905b3edec3d3615a")
        .build()

    val clientCredentials = api.clientCredentials().build().execute()
    println(clientCredentials.expiresIn)
    api.accessToken = clientCredentials.accessToken

    val playlistId: String = properties["spotify_playlist"] as String
    print(playlistId)

    val playlist = api.getPlaylist(playlistId).build()
        .execute()
    //val tracks = playlist.tracks.items
    //val trackNames = tracks.map { it.track.name }
    val spotifyTracks = playlist.tracks.items.map { SpotifyTrack(it.track.name, it.track.artists.map { it.name }.toString()) }


    val localSongRepository = LocalSongRepository()
    val library = localSongRepository.getSongsAsMap(properties["library_folder"] as String)
    val libraryList = localSongRepository.convertMapToList(library)
    val libraryFileNames = libraryList.map { it.file.toString() }
    println(library)
    println(library.size)


    val out = File("output.m3u").printWriter()
    var matches = 0
    spotifyTracks.forEach forEach@ { spotifyTrack ->
        val songs = library[spotifyTrack.title]

        songs?.let {songs ->
            //We have one or more songs that match

            if (songs.size == 1) {
                //We only have one song that matches. Write it.
                out.println(songs.first().file)
            } else {
                //We have multiple
                //Ask user for which one
                println("Multiple tracks found for ${spotifyTrack.artist} - ${spotifyTrack.title}")
                songs.forEachIndexed { index, foundTrack ->
                    println("($index) ${foundTrack.file}")
                }

                println("Search results for ${spotifyTrack.artist} - ${spotifyTrack.title}: ")
                val searchList = localSongRepository.searchList(spotifyTrack.title, 10, libraryFileNames)
                searchList.forEachIndexed { index, item ->
                    println("(${index + songs.size}) $item")
                }

                var libraryTrack: Song? = null
                while (libraryTrack == null) {
                    println("Select the correct one, enter a path to a different one, or press enter to skip ")
                    val entry = readLine()
                    if (entry.isNullOrBlank()) return@forEach

                        //Int entry
                        entry.toIntOrNull()?.let { int ->
                            libraryTrack = songs.getOrNull(int)
                                ?: searchList.getOrNull(int + songs.size)?.let { //Int entry that corresponds to search results
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
        } ?: run {

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

