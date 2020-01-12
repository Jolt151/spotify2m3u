package com.test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

open class TrackMatcher(val localSongRepository: LocalSongRepository) {

    val library: Map<String, List<Song>> = localSongRepository.library

    //Pairs of playlist position to song
    val foundTracks = mutableListOf<FoundTrack>()
    val pendingTracks = mutableListOf<PendingTrack>()

    constructor(localSongRepository: LocalSongRepository, foundTracks: List<FoundTrack>, pendingTracks: List<PendingTrack>): this(localSongRepository) {
        this.foundTracks.addAll(foundTracks)
        this.pendingTracks.addAll(pendingTracks)
    }

    fun match(spotifyTracks: List<SpotifyTrack>) {
        spotifyTracks.forEachIndexed forEach@ { index, spotifyTrack ->

            val matchingTracks = library[spotifyTrack.title]
            matchingTracks?.let {matches ->
                if (matches.size == 1)
                    foundTracks.add(FoundTrack(matches.first(), index))
                else {
                    //Multiple tracks
                    val searchResults = searchForMatches(spotifyTrack)
                    pendingTracks.add(PendingTrack(spotifyTrack, index, matches, searchResults))
                }
            } ?: run {
                //no matching tracks
                val searchResults = searchForMatches(spotifyTrack)
                pendingTracks.add(PendingTrack(spotifyTrack, index, null, searchResults))
            }
        }
    }

    fun addMatch(index: Int, track: Song) {
        foundTracks.add(FoundTrack(track, index))
    }

    fun addMatch(index: Int, trackPath: String): Boolean {
        localSongRepository.getSingle(trackPath)?.let {
            foundTracks.add(FoundTrack(it, index))
            pendingTracks.removeIf { it.index == index }
            return true
        } ?: run {
            return false
        }
    }

    fun searchForMatches(spotifyTrack: SpotifyTrack): List<String> {
        return localSongRepository.searchLibrary(spotifyTrack.title, 10)
    }
}