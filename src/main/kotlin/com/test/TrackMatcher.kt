package com.test

class TrackMatcher(val library: Map<String, List<Song>>, val localSongRepository: LocalSongRepository) {

    //Pairs of playlist position to song
    private val foundTracks = mutableListOf<Pair<Int, Song>>()
    private val pendingTracks = mutableListOf<Pair<Int, SpotifyTrack>>()

    fun match(spotifyTracks: List<SpotifyTrack>) {
        spotifyTracks.forEachIndexed forEach@ { index, spotifyTrack ->

            val matchingTracks = library[spotifyTrack.title]
            matchingTracks?.let {
                if (it.size == 1)
                    foundTracks.add(Pair(index, it.first()))
                else {
                    //Multiple tracks
                    pendingTracks.add(Pair(index, spotifyTrack))
                }
            } ?: run {
                //no matching tracks
                pendingTracks.add(Pair(index, spotifyTrack))
            }
        }
    }

    @JvmName("getPending")
    fun getPendingTracks(): List<Pair<Int, SpotifyTrack>> {
        return pendingTracks
    }


    fun getFoundTracks(): List<Pair<Int, Song>> {
        return foundTracks
    }

    fun addMatch(index: Int, track: Song) {
        foundTracks.add(Pair(index, track))
    }

    fun searchForMatches(spotifyTrack: SpotifyTrack): List<String> {
        return localSongRepository.searchLibrary(spotifyTrack.title, 10)
    }
}