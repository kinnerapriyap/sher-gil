package com.kinnerapriyap.sugar.mediagallery.album

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.kinnerapriyap.sugar.R

class MediaGalleryAlbumSpinnerAdapter(
    private val activity: Activity,
    private val albums: List<MediaGalleryAlbum>
) : ArrayAdapter<MediaGalleryAlbum>(activity, R.layout.album_spinner_item, albums) {

    override fun getItem(position: Int): MediaGalleryAlbum? = albums[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
        populateView(
            position,
            convertView ?: activity.layoutInflater.inflate(
                R.layout.album_spinner_item,
                null
            )
        )

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View =
        populateView(
            position,
            convertView ?: activity.layoutInflater.inflate(
                R.layout.album_spinner_dropdown_item,
                null
            )
        )

    private fun populateView(position: Int, v: View): View {
        v.findViewById<TextView>(R.id.albumName)?.text = getItem(position)?.albumName
        v.findViewById<TextView>(R.id.mediaCount)?.text =
            getItem(position)?.mediaCount.toString()
        return v
    }
}
