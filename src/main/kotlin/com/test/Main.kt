package com.test

import com.github.kittinunf.fuel.core.interceptors.redirectResponseInterceptor
import com.wrapper.spotify.SpotifyApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.io.FileInputStream
import okhttp3.CertificatePinner.pin
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.properties.Delegates


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

                var libraryTrack: Song? = null
                while (libraryTrack == null) {
                    println("Select the correct one, enter a path to a different one, or press enter to skip ")
                    val entry = readLine()
                    if (entry.isNullOrBlank()) return@forEach

                        entry.toIntOrNull()?.let {
                            libraryTrack = songs.getOrNull(it)
                        } ?: localSongRepository.getSingle(entry)?.let {
                            libraryTrack = it
                        } ?: run {
                            println("Not found, try again")
                        }
                }
                out.println(libraryTrack!!.file)
                matches++
            }
        } ?: run {

            var song: Song? = null
            while (song == null) {
                println("missing ${spotifyTrack.artist} - ${spotifyTrack.title}")
                println("Add path to track, or press enter to skip")
                println("Path: ")
                val path = readLine()
                if (path.isNullOrBlank()) return@forEach

                song = localSongRepository.getSingle(path)
            }

            out.println(song.file)
            matches++

        }
    }
    out.flush()
    println(matches)

}

