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
    val tracks = playlist.tracks.items
    val trackNames = tracks.map { it.track.name }


    val localSongRepository = LocalSongRepository()
    val library = localSongRepository.getSongsAsMap(properties["library_folder"] as String)
    println(library)
    println(library.size)


    val out =File("output.m3u").printWriter()
    var matches = 0
    trackNames.forEach forEach@ {trackName ->
        if (library.containsKey(trackName)) {
            out.println(library[trackName]!!.file)
            matches++
        } else {
            println("missing $trackName")
            println("Add path to track, or press enter to skip")
            println("Path: ")
            val path = readLine()
            if (path != null && path.isNotBlank()) {
                var song = localSongRepository.getSingle(path)
                while (song == null) {
                    println("not found, enter path to try again or press enter to skip")
                    println("Path: ")
                    val path = readLine()
                    if (path == null || path.isBlank()) return@forEach
                    song = localSongRepository.getSingle(path)
                }
                out.println(song.file)
                matches++
            }
        }
    }
    out.flush()
    println(matches)

}

