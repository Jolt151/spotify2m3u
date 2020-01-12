package com.test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class RestController {

    @Autowired lateinit var localSongRepository: LocalSongRepository
    @Autowired lateinit var spotifyApi: SpotifyApiWrapper
    //@Autowired lateinit var trackMatcher: TrackMatcher

    @RequestMapping("/api/playlist")
    fun playlist(@RequestParam playlistId: String): List<SpotifyTrack> {
        return spotifyApi.getPlaylist(playlistId)
    }

    @RequestMapping("/api/startMatching")
    fun startMatching(@RequestParam playlistId: String): MatchJob {
        val trackMatcher = TrackMatcher(localSongRepository)
        val spotifyTracks = spotifyApi.getPlaylist(playlistId)
        trackMatcher.match(spotifyTracks)

        val matchJob = MatchJob(spotifyTracks, trackMatcher.foundTracks, trackMatcher.pendingTracks)
        return matchJob

    }

}

data class MatchJob(val spotifyTracks: List<SpotifyTrack>, val foundTracks: List<FoundTrack>,
                    val pendingTracks: List<PendingTrack>)

