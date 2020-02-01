package com.example.proj2.music.util

import android.text.TextUtils
import android.util.Log
import com.example.proj2.music.model.ArtistSimilar
import com.example.proj2.music.model.TopTrack

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList

class QueryUtils {
    companion object {
        private val  LogTag = this::class.java.simpleName
        private const val BaseURL = " https://ws.audioscrobbler.com/2.0/"
        //"

        fun fetchToptrackData(jsonQueryString: String): ArrayList<TopTrack>? {
            val url: URL? = createUrl("${this.BaseURL }$jsonQueryString")

            var jsonResponse: String? = null
            try {
                jsonResponse = makeHttpRequest(url)
            }
            catch (e: IOException) {
                Log.e(this.LogTag, "Problem making the HTTP request.", e)
            }

            return extractDataFromJson(jsonResponse)
        }

        fun fetchArtistToptrackData(jsonQueryString: String): ArrayList<TopTrack>? {
            val url: URL? = createUrl("${this.BaseURL }$jsonQueryString")

            var jsonResponse: String? = null
            try {
                jsonResponse = makeHttpRequest(url)
            }
            catch (e: IOException) {
                Log.e(this.LogTag, "Problem making the HTTP request.", e)
            }

            return extractDataFromJson2(jsonResponse)
        }

        fun fetchSimilarArtistData(jsonQueryString: String): ArrayList<ArtistSimilar>?{
            val url: URL? = createUrl("${this.BaseURL }$jsonQueryString")
            var jsonResponse: String? = null
            try {
                jsonResponse = makeHttpRequest(url)
            }
            catch (e: IOException) {
                Log.e(this.LogTag, "Problem making the HTTP request.", e)
            }

            return extractDataFromJson3(jsonResponse)
        }

        private fun createUrl(stringUrl: String): URL? {
            var url: URL? = null
            try {
                url = URL(stringUrl)
            } catch (e: MalformedURLException) {
                Log.e(this.LogTag, "Problem building the URL.", e)
            }
            return url
        }

        private fun makeHttpRequest(url: URL?): String {
            var jsonResponse = ""

            if (url == null) {
                return jsonResponse
            }

            var urlConnection: HttpURLConnection? = null
            var inputStream: InputStream? = null
            try {
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.readTimeout = 10000 // 10 seconds
                urlConnection.connectTimeout = 15000 // 15 seconds
                urlConnection.requestMethod = "GET"
                urlConnection.connect()

                if (urlConnection.responseCode == 200) {
                    inputStream = urlConnection.inputStream
                    jsonResponse = readFromStream(inputStream)
                }
                else {
                    Log.e(this.LogTag, "Error response code: ${urlConnection.responseCode}")
                }
            }
            catch (e: IOException) {
                Log.e(this.LogTag, "Problem retrieving the product data results: $url", e)
            }
            finally {
                urlConnection?.disconnect()
                inputStream?.close()
            }

            return jsonResponse
        }

        private fun readFromStream(inputStream: InputStream?): String {
            val output = StringBuilder()
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
                val reader = BufferedReader(inputStreamReader)
                var line = reader.readLine()
                while (line != null) {
                    output.append(line)
                    line = reader.readLine()
                }
            }

            return output.toString()
        }

        private fun extractDataFromJson(musicJson: String?): ArrayList<TopTrack>? {
            if (TextUtils.isEmpty(musicJson)) {
                return null
            }
            val musicList = ArrayList<TopTrack>()
            try {
                val baseJasonResponse = JSONObject(musicJson)
                val tracks = baseJasonResponse.getJSONObject("tracks")
                val trackArray = tracks.getJSONArray("track")
                for (i in 0 until trackArray.length()) {
                    val musicObject = trackArray.getJSONObject(i)

                    // Images
                    val images = returnValueOrDefault<JSONArray>(musicObject, "image") as JSONArray?
                    val imageArrayList = ArrayList<String>()
                    if (images != null) {
                        for (j in 0 until images.length()) {
                            imageArrayList.add((images[j] as JSONObject).getString("#text"))
                        }
                    }

                    // Artist
                    var artistName = ""
                    val artist = returnValueOrDefault<JSONObject>(musicObject, "artist") as JSONObject?
                    if (artist != null) {
                        artistName = artist.getString("name")
                    }
                    musicList.add(
                        TopTrack(
                            //name
                            returnValueOrDefault<String>(musicObject, "name") as String,
                            imageArrayList,
                            artistName,
                            returnValueOrDefault<String>(musicObject, "duration") as String,
                            returnValueOrDefault<String>(musicObject, "url") as String,
                            returnValueOrDefault<String>(musicObject, "playcount") as String

                        ))
                }

            } catch (e: JSONException) {
                Log.e(this.LogTag, "Problem parsing the product JSON results", e)

            }
            return musicList
        }

        private fun extractDataFromJson2(musicJson: String?): ArrayList<TopTrack>? {
            if (TextUtils.isEmpty(musicJson)) {
                return null
            }
            val musicList = ArrayList<TopTrack>()
            try {
                val baseJasonResponse = JSONObject(musicJson)
                val tracks = baseJasonResponse.getJSONObject("toptracks")
                val trackArray = tracks.getJSONArray("track")
                for (i in 0 until trackArray.length()) {
                    val musicObject = trackArray.getJSONObject(i)

                    // Images
                    val images = returnValueOrDefault<JSONArray>(musicObject, "image") as JSONArray?
                    val imageArrayList = ArrayList<String>()
                    if (images != null) {
                        for (j in 0 until images.length()) {
                            imageArrayList.add((images[j] as JSONObject).getString("#text"))
                        }
                    }

                    // Artist
                    var artistName = ""
                    val artist = returnValueOrDefault<JSONObject>(musicObject, "artist") as JSONObject?
                    if (artist != null) {
                        artistName = artist.getString("name")
                    }
                    musicList.add(
                        TopTrack(
                            //name
                            returnValueOrDefault<String>(musicObject, "name") as String,
                            imageArrayList,
                            artistName,
                            returnValueOrDefault<String>(musicObject, "duration") as String,
                            returnValueOrDefault<String>(musicObject, "url") as String,
                            returnValueOrDefault<String>(musicObject, "playcount") as String

                        ))
                }

            } catch (e: JSONException) {
                Log.e(this.LogTag, "Problem parsing the product JSON results", e)

            }
            return musicList
        }

        private fun extractDataFromJson3(musicJson: String?): ArrayList<ArtistSimilar>?{
            if (TextUtils.isEmpty(musicJson)) {
                return null
            }
            val artistList = ArrayList<ArtistSimilar>()
            try {
                val baseJasonResponse = JSONObject(musicJson)
                val Artists = baseJasonResponse.getJSONObject("similarartists")
                val ArtistsArray = Artists.getJSONArray("artist")
                for (i in 0 until ArtistsArray.length()) {
                    val artistObject = ArtistsArray.getJSONObject(i)

                    // Images
                    val images = returnValueOrDefault<JSONArray>(artistObject, "image") as JSONArray?
                    val imageArrayList = ArrayList<String>()
                    if (images != null) {
                        for (j in 0 until images.length()) {
                            imageArrayList.add((images[j] as JSONObject).getString("#text"))
                        }
                    }

                    /* Artist
                    var artistName = ""
                    val artist = returnValueOrDefault<JSONObject>(musicObject, "artist") as JSONObject?
                    if (artist != null) {
                        artistName = artist.getString("name")
                    }*/
                    artistList.add(
                        ArtistSimilar(
                            //name
                            returnValueOrDefault<String>(artistObject, "name") as String,
                            imageArrayList,
                            //artistName,
                            //returnValueOrDefault<String>(musicObject, "duration") as String,
                            returnValueOrDefault<String>(artistObject, "url") as String
                            //returnValueOrDefault<String>(musicObject, "playcount") as String

                        ))
                }

            } catch (e: JSONException) {
                Log.e(this.LogTag, "Problem parsing the product JSON results", e)

            }
            return artistList
        }

        private inline fun <reified T> returnValueOrDefault(json: JSONObject, key: String): Any? {
            when (T::class) {
                String::class -> {
                    return if (json.has(key)) {
                        json.getString(key)
                    } else {
                        ""
                    }
                }
                Int::class -> {
                    return if (json.has(key)) {
                        json.getInt(key)
                    }
                    else {
                        return -1
                    }
                }
                Double::class -> {
                    return if (json.has(key)) {
                        json.getDouble(key)
                    }
                    else {
                        return -1.0
                    }
                }
                Long::class -> {
                    return if (json.has(key)) {
                        json.getLong(key)
                    }
                    else {
                        return (-1).toLong()
                    }
                }
                JSONObject::class -> {
                    return if (json.has(key)) {
                        json.getJSONObject(key)
                    }
                    else {
                        return null
                    }
                }
                JSONArray::class -> {
                    return if (json.has(key)) {
                        json.getJSONArray(key)
                    }
                    else {
                        return null
                    }
                }
                else -> {
                    return null
                }
            }
        }


    }
}