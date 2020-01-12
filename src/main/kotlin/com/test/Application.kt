package com.test

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import java.util.*

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java)
}

@SpringBootApplication
public open class Application {

    @Value("\${library_folder}")
    lateinit var libraryPath: String

    @Bean
    open fun localSongRepository(): LocalSongRepository {
        return LocalSongRepository(libraryPath)
    }

    @Bean
    open fun spotifyApi(): SpotifyApiWrapper {
        return SpotifyApiWrapper()
    }


}