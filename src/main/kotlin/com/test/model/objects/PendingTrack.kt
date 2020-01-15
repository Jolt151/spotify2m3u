package com.test.model.objects

data class PendingTrack(val spotifyTrack: SpotifyTrack, val index: Int, val matches: List<Song>?, val searchResults: List<String>)