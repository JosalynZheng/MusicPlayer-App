package com.example.proj2.music.ViewModel

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.AsyncTask
import android.provider.BaseColumns
import android.util.Log
import com.example.proj2.music.db.DbSettings
import com.example.proj2.music.db.MusicDatabaseHelper
import com.example.proj2.music.fragment.ResultList
import com.example.proj2.music.model.ArtistSimilar
import com.example.proj2.music.model.TopTrack
import com.example.proj2.music.util.QueryUtils

class MusicViewModel(application: Application): AndroidViewModel(application) {
    private  var musicDBHelper: MusicDatabaseHelper = MusicDatabaseHelper(application)
    private  var musicList: MutableLiveData<ArrayList<TopTrack>> = MutableLiveData()
    //private  var artistList: MutableLiveData<ArrayList<ArtistSimilar>> = MutableLiveData()
    var myAPIkey = "2ea3d612c214c7bdf3d2d12afc431cf5"
    // Return musicList to get the result from url
    fun getTopTrack(): MutableLiveData<ArrayList<TopTrack>> {

        var topTrack = "?method=chart.gettoptracks&api_key=" + myAPIkey + "&format=json"
        loadTopTrack(topTrack)
        return musicList
    }
// load toptrack by query
    fun getTopTrackByQueryText(query: String): MutableLiveData<ArrayList<TopTrack>> {
        var artistTopTrack = "?method=artist.gettoptracks&artist=$query&api_key=" + myAPIkey + "&format=json"
        loadArtistTopTrack(artistTopTrack)
        return  musicList
    }
// Execute query
    private fun loadTopTrack(query: String) {
        MusicAsyncTask().execute(query)
    }

    private fun loadArtistTopTrack(query: String) {
        ArtistMusicAsyncTask().execute(query)
    }

    @SuppressLint("StaticFieldLeak")
    inner class MusicAsyncTask: AsyncTask<String, Unit, ArrayList<TopTrack>>() {
        override fun doInBackground(vararg params: String?): ArrayList<TopTrack>? {
            return QueryUtils.fetchToptrackData(params[0]!!)
        }

        override fun onPostExecute(result: ArrayList<TopTrack>?) {
            if (result == null) {
                Log.e("RESULTS", "No Results Found")
            }
            else {
                musicList.value = result
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class ArtistMusicAsyncTask: AsyncTask<String, Unit, ArrayList<TopTrack>>() {
        override fun doInBackground(vararg params: String?): ArrayList<TopTrack>? {
            return QueryUtils.fetchArtistToptrackData(params[0]!!)
        }
// execute musiclist value
        override fun onPostExecute(result: ArrayList<TopTrack>?) {
            if (result == null) {
                Log.e("RESULTS", "No Results Found")
            }
            else {
                musicList.value = result
            }
        }
    }

    fun getPlaylist(): MutableLiveData<ArrayList<TopTrack>> {
        this.loadPlaylist()
        return this.musicList
    }
// load information into database
    private fun loadPlaylist(): ArrayList<TopTrack> {
        var playlist: ArrayList<TopTrack> = ArrayList()
        var database = this.musicDBHelper.readableDatabase
        val projection = arrayOf(BaseColumns._ID, DbSettings.DBPlaylistEntry.NAME, DbSettings.DBPlaylistEntry.ARTIST)
        var cursor = database.query(
            DbSettings.DBPlaylistEntry.TABLE,
            projection,
            null, null, null, null, null
        )
        with(cursor) {
            while (moveToNext()) {
                val name = getString(getColumnIndexOrThrow(DbSettings.DBPlaylistEntry.NAME))
                val artist = getString(getColumnIndexOrThrow(DbSettings.DBPlaylistEntry.ARTIST))
                val music = TopTrack(name, ArrayList(), artist, "", "", "")
                playlist.add(music)
            }
        }
        musicList.value = playlist

        cursor.close()
        database.close()

        return playlist

    }
// Create a function to add the music to playlist
    fun addPlayList(music: TopTrack) {
        val database: SQLiteDatabase = this.musicDBHelper.writableDatabase

        val listValues = ContentValues()

        listValues.put(DbSettings.DBPlaylistEntry.NAME, music.getName())
        listValues.put(DbSettings.DBPlaylistEntry.ARTIST, music.getArtist())

        val newId = database?.insert(DbSettings.DBPlaylistEntry.TABLE, null, listValues)
        database.close()
    }
// Create a function to delete the song from playlist
    fun deletePlayList(id: String, isFromResultList: Boolean = false){
        var db=musicDBHelper.writableDatabase
        db.delete(DbSettings.DBPlaylistEntry.TABLE, "${DbSettings.DBPlaylistEntry.NAME}=?", arrayOf(id))
        db.close()
        var list = musicList.value
        if (list != null){
            var i = 0
            list.forEachIndexed { index, music ->
                if(music.id==id){
                    i=index
                }
            }
            if(isFromResultList){
                list[i].isInPlaylist=false
            }
            else{
                list.remove(list[i])
            }
            this.musicList.value = list
        }
    }
}