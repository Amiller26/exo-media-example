package com.portfolioprojects.exomediaexample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView



class SongAdapter(private val songList: ArrayList<String>)
    : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener{                                      //Override in MainActivity
        fun onItemClick(position: Int, songName: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){          //Called in MainActivity
        mListener = listener
    }

    class SongViewHolder(songView: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(songView){
        val songTV : TextView = songView.findViewById(R.id.idTVSongName)

        init{//Needed for Clickable
            songView.setOnClickListener{
                listener.onItemClick(this.getBindingAdapterPosition(), songTV.text.toString())  //Calls the overriden interface

            }
        }
    }


//Overridden Functions


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder { //adding a view (. xml) to activity on runtime
        val songView = LayoutInflater.from(parent.context).inflate(R.layout.song_rv_item, parent,false)
        return SongViewHolder(songView, mListener)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int){               //When bound set the song name
        val song = songList[position]
        holder.songTV.text = song

    }

    override fun getItemCount(): Int{                                                   //Set song list size for Overridden function
        return songList.size
    }

}

