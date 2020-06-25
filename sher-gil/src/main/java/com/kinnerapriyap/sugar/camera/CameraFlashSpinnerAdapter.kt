package com.kinnerapriyap.sugar.camera

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.kinnerapriyap.sugar.R
import com.kinnerapriyap.sugar.databinding.CameraFlashSpinnerItemBinding

class CameraFlashSpinnerAdapter(
    context: Context,
    private val items: Array<CameraFlash> = CameraFlash.values()
) : ArrayAdapter<CameraFlash>(
    context,
    R.layout.camera_flash_spinner_item,
    R.id.cameraFlashSpinnerTextView,
    items
) {
    var isOpen: Boolean = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
        getCustomView(position, parent, true)

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View =
        getCustomView(position, parent)

    private fun getCustomView(position: Int, parent: ViewGroup, isView: Boolean = false): View {
        val binding =
            CameraFlashSpinnerItemBinding.inflate(LayoutInflater.from(parent.context))
        if (isView && isOpen) {
            binding.cameraFlashSpinnerImageView.setImageDrawable(null)
        } else {
            binding.cameraFlashSpinnerImageView.setImageResource(items[position].flashDrawable)
        }
        return binding.root
    }

    override fun getCount(): Int = items.size
}
