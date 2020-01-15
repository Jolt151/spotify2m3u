package com.test.controller

import com.test.model.LocalSongRepository
import com.test.model.SpotifyApiWrapper
import com.test.TrackMatcher
import com.test.model.objects.FoundTrack
import com.test.model.objects.PendingTrack
import com.test.model.objects.SpotifyTrack
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.lang.StringBuilder

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

        val matchJob =
            MatchJob(spotifyTracks, trackMatcher.foundTracks, trackMatcher.pendingTracks)
        return matchJob

    }

    @RequestMapping("/api/addMatches", method = [RequestMethod.POST])
    fun addMatches(@RequestBody matchJob: ReceivingMatchJob): MatchJob {
        val trackMatcher =
            TrackMatcher(localSongRepository, matchJob.foundTracks, matchJob.pendingTracks)

        matchJob.fixedPendingTracks?.forEach {fixedTrack ->
            trackMatcher.addMatch(fixedTrack.index, fixedTrack.trackPath)
        }

        return MatchJob(
            matchJob.spotifyTracks,
            trackMatcher.foundTracks,
            trackMatcher.pendingTracks
        )
    }

    @RequestMapping("/api/getMoreSearchResults")
    fun getMoreSearchResults(@RequestParam title: String, @RequestParam size: Int): List<String> {
        return localSongRepository.searchLibrary(title, size)
    }

    @RequestMapping("/api/finishMatching", produces = ["text/plain"])
    fun finishMatching(@RequestBody matchJob: MatchJob): ResponseEntity<String> {
        //val file = File("output.m3u")
        val file = File.createTempFile("output", "m3u")

        val out = file.printWriter()
        val filepaths = localSongRepository.orderSongsAsFilepaths(matchJob.foundTracks)
        val sb = StringBuilder()

        filepaths.forEach {
            out.println(filepaths)
            sb.appendln(it)
        }
        out.flush()

        println(sb.toString())
        return ResponseEntity(sb.toString(), HttpStatus.OK)

/*        val inputStreamResource = InputStreamResource(FileInputStream(file))
         val fileSystemResource = FileSystemResource(file)*/
/*        return ResponseEntity.ok()
            .contentLength(file.length())
            .contentType(MediaType.parseMediaType("controller/octet-stream"))
            .body(fileSystemResource)*/


    }

}

data class MatchJob(val spotifyTracks: List<SpotifyTrack>, val foundTracks: List<FoundTrack>,
                    val pendingTracks: List<PendingTrack>)

data class ReceivingMatchJob(val spotifyTracks: List<SpotifyTrack>, val foundTracks: List<FoundTrack>,
                             val pendingTracks: List<PendingTrack>, val fixedPendingTracks: List<FixedPendingTrack>?)

data class FixedPendingTrack(val index: Int, val trackPath: String)

