package com.test

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.model_objects.specification.Playlist

class SpotifyApiWrapper {

    companion object {
        val api by lazy {
            SpotifyApiWrapper()
        }
    }

    private val api: SpotifyApi = SpotifyApi.Builder()
        .setClientId("deecbb22fbb641fb853c69721338e3a2")
        .setClientSecret("09b0d29264be4ba6905b3edec3d3615a")
        .build()

    init {
        val clientCredentials = api.clientCredentials().build().execute()
        api.accessToken = clientCredentials.accessToken
    }

    fun getPlaylist(id: String): List<SpotifyTrack> {
        return api.getPlaylist(id).build().execute().tracks.items.map { SpotifyTrack(it.track.name, it.track.artists.map { it.name }.toString()) }
    }
}