package com.test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
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

    @RequestMapping("/api/startMatching", method = [RequestMethod.POST])
    fun startMatching(@RequestParam playlistId: String): MatchJob {
        val trackMatcher = TrackMatcher(localSongRepository)
        val spotifyTracks = spotifyApi.getPlaylist(playlistId)
        trackMatcher.match(spotifyTracks)

        val matchJob = MatchJob(spotifyTracks, trackMatcher.foundTracks, trackMatcher.pendingTracks)
        return matchJob

    }

    @RequestMapping("/api/addMatches", method = [RequestMethod.POST])
    fun addMatches(@RequestBody matchJob: ReceivingMatchJob): MatchJob {
        val trackMatcher = TrackMatcher(localSongRepository, matchJob.foundTracks, matchJob.pendingTracks)

        matchJob.fixedPendingTracks?.forEach {fixedTrack ->
            trackMatcher.addMatch(fixedTrack.index, fixedTrack.trackPath)
        }

        return MatchJob(matchJob.spotifyTracks, trackMatcher.foundTracks, trackMatcher.pendingTracks)
    }

    @RequestMapping("/api/getMoreSearchResults")
    fun getMoreSearchResults(@RequestParam title: String, @RequestParam size: Int): List<String> {
        return localSongRepository.searchLibrary(title, size)
    }

    @RequestMapping("/api/finishMatching")
    fun finishMatching(@RequestBody matchJob: MatchJob) {
        TODO("Create the m3u from the matchJob.foundTracks")
    }

}

data class MatchJob(val spotifyTracks: List<SpotifyTrack>, val foundTracks: List<FoundTrack>,
                    val pendingTracks: List<PendingTrack>)

data class ReceivingMatchJob(val spotifyTracks: List<SpotifyTrack>, val foundTracks: List<FoundTrack>,
                             val pendingTracks: List<PendingTrack>, val fixedPendingTracks: List<FixedPendingTrack>?)

data class FixedPendingTrack(val index: Int, val trackPath: String)

