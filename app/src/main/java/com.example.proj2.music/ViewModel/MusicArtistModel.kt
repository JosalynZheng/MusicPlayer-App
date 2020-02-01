package com.example.proj2.music.ViewModel

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.AsyncTask
import android.util.Log
import com.example.proj2.music.db.DbSettings
import com.example.proj2.music.model.ArtistSimilar
import com.example.proj2.music.util.QueryUtils

class MusicArtistModel(application: Application): AndroidViewModel(application) {
    private  var artistList: MutableLiveData<ArrayList<ArtistSimilar>> = MutableLiveData()
    var myAPIkey = "2ea3d612c214c7bdf3d2d12afc431cf5"
    fun getArtistSimilar(query: String): MutableLiveData<ArrayList<ArtistSimilar>>{
        var simiarArtist="?method=artist.getsimilar&artist=$query&api_key=" + myAPIkey + "&format=json"
        loadArtistSimilar(simiarArtist)
        return artistList
    }

    private fun loadArtistSimilar(query: String){
        SimilarArtistAsyncTask().execute(query)
    }

    @SuppressLint("StaticFieldLeak")
    inner class SimilarArtistAsyncTask: AsyncTask<String, Unit, ArrayList<ArtistSimilar>>(){
        override fun doInBackground(vararg params: String?): ArrayList<ArtistSimilar>?{
            return QueryUtils.fetchSimilarArtistData(params[0]!!)
        }

        override fun onPostExecute(result: ArrayList<ArtistSimilar>?){
            if (result == null) {
                Log.e("RESULTS","NO Results Found")
            }
            else{
                artistList.value = result
            }
        }
    }

}