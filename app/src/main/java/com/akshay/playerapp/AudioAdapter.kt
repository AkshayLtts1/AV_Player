package com.akshay.playerapp

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.akshay.playerapp.databinding.AudioViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class AudioAdapter(private val context: Context, private val audioList: ArrayList<Audio>, private val isFolder: Boolean=false): RecyclerView.Adapter<AudioAdapter.MyHolder>() {
    class MyHolder(binding: AudioViewBinding): RecyclerView.ViewHolder(binding.root) {
        val title = binding.audioName
        val folder = binding.folderName
        val duration = binding.duration
        val image = binding.audioImg
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(AudioViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.title.text = audioList[position].title
        holder.folder.text = audioList[position].folderName
        holder.duration.text = DateUtils.formatElapsedTime(audioList[position].duration/1000)

        Glide.with(context)
            .asBitmap()
            .load(audioList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.audio_icon).centerCrop())
            .into(holder.image)
        holder.root.setOnClickListener {
//            val intent = Intent(context, PlayerActivity::class.java)
//            ContextCompat.startActivity(context, intent, null)
            when{
                isFolder->{
                    sendIntent(position, "FolderActivity")
                }
                else->{
                    sendIntent(position, "AllAudios")
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return audioList.size
    }

    private fun sendIntent(pos: Int, ref: String){
        PlayerActivity.position = pos
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("class", ref)
        ContextCompat.startActivity(context, intent, null)
    }
}