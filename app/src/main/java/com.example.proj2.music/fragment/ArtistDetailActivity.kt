package com.example.proj2.music.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.proj2.music.R
import com.example.proj2.music.Toptrack
import com.example.proj2.music.ViewModel.MusicArtistModel
import com.example.proj2.music.ViewModel.MusicViewModel
//import com.example.proj2.music.fragment.SimilarArtist
import com.example.proj2.music.model.ArtistSimilar
import com.example.proj2.music.model.TopTrack
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_artist.*
import kotlinx.android.synthetic.main.activity_artist_detail.*
import kotlinx.android.synthetic.main.activity_music_detail.*
import kotlinx.android.synthetic.main.fragment_similarartist.*
import kotlinx.android.synthetic.main.fragment_toptrack.*
import kotlinx.android.synthetic.main.playlist_item.*
import kotlinx.android.synthetic.main.similar_artist_list_item.*
import kotlinx.android.synthetic.main.similar_artist_list_item.view.*

class ArtistDetailActivity: AppCompatActivity() {
    private lateinit var music: TopTrack
    private var adapter = ArtistAdapter()
    private lateinit var viewModel: MusicArtistModel
    private var ArtistList: ArrayList<ArtistSimilar> = ArrayList()
    // private lateinit var Artist: ArtistSimilar
    // private lateinit var viewModel: MusicArtistModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_detail)
        music = intent.extras!!.getSerializable("music") as TopTrack

        viewModel = ViewModelProviders.of(this).get(MusicArtistModel::class.java)

        val observer = Observer<ArrayList<ArtistSimilar>> {
            artist_list.adapter = adapter
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(p0: Int, p1: Int): Boolean {
                    return ArtistList[p0].getName() == ArtistList[p1].getName()
                }
                // Get the size of ArtistList
                override fun getOldListSize(): Int {
                    return ArtistList.size
                }

                override fun getNewListSize(): Int {
                    if (it == null) {
                        return 0
                    }
                    return it.size
                }
                // function to see if two artists are same
                override fun areContentsTheSame(p0: Int, p1: Int): Boolean {
                    return ArtistList[p0] == ArtistList[p1]
                }
            })
            result.dispatchUpdatesTo(adapter)
            ArtistList = it ?: ArrayList()
        }


        viewModel.getArtistSimilar(music.getArtist()).observe(this, observer)
        //  Artist = intent.extras!!.getSerializable("artist") as ArtistSimilar
        //Log.e("FAVORITE", product.isFavorite.toString())

        // viewModel = ViewModelProviders.of(this).get(MusicArtistModel::class.java)

        // this.loadUI(Artist)
    }
    inner class ArtistAdapter: RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ArtistViewHolder {
            val itemView = LayoutInflater.from(p0.context).inflate(R.layout.similar_artist_list_item, p0, false)
            return ArtistViewHolder(itemView)
        }
        // Load artist image
        override fun onBindViewHolder(p0: ArtistViewHolder, p1: Int) {
            val artist = ArtistList[p1]
            val artistImages = artist.getImage()
            if (artistImages.size == 0) {

            } else {
                Picasso.with(this@ArtistDetailActivity).load(artistImages[3]).into(p0.artistImg)            }
            p0.artistName.text = artist.getName()



        }
        // Get ArtistList's size
        override fun getItemCount(): Int {
            return ArtistList.size
        }

        inner class ArtistViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

            var artistImg: ImageView = itemView.artist_img
            var artistName: TextView = itemView.artist_name
        }
    }

    override fun onBackPressed() {
        this.finish()
    }

    // Create fun loadUI to get artist name and url
    private fun loadUI(Artist: ArtistSimilar) {

        artist_name.text = "Name: " + Artist.getName()
        artist_url.text = "url: " + Artist.getURL()


        val images = Artist.getImage()
        if (images.size > 0) {
            Picasso.with(this).load(Artist.getImage()[3]).into(detail_music_img)
        } else {
            // eventually show image not available pic
        }
    }
}