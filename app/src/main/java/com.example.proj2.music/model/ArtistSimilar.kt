package com.example.proj2.music.model

import java.io.Serializable
import java.time.Duration
import java.util.*

class ArtistSimilar(): Serializable {
    private var name: String = ""
    private var image: ArrayList<String> = ArrayList()
    //private var artist: String = ""
    //private var duration: String = ""
    private var url: String = ""
    //private var playcount: String = ""

    var isInPlaylist: Boolean = false

    constructor(
        name: String,
        image: ArrayList<String>,
        //artist: String,
        //duration: String,
        url: String
        //playcount: String
    ): this() {
        this.name = name
        this.image = image
        //this.artist = artist
        //this.duration = duration
        this.url = url
        //this.playcount = playcount

    }

    fun getName(): String {
        return this.name
    }

    fun getImage(): ArrayList<String> {
        return this.image
    }

    fun getURL(): String {
        return this.url
    }


}