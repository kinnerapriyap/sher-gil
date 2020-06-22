package com.kinnerapriyap.sugar.mediagallery.media

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.FilterQueryProvider
import android.widget.Filterable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kinnerapriyap.sugar.R
import com.kinnerapriyap.sugar.choice.MimeType
import com.kinnerapriyap.sugar.databinding.ViewMediaCellBinding
import com.kinnerapriyap.sugar.mediagallery.MediaGalleryHandler.Companion.CAMERA_CAPTURE_ID
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellDisplayModel
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellListener
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellUpdateModel

class MediaGalleryAdapter(
    private var mediaCursor: Cursor?,
    private var selectedMediaCellDisplayModels: List<MediaCellDisplayModel>,
    private val mediaCellListener: MediaCellListener,
    private val mimeTypes: List<MimeType>,
    private val allowMultipleSelection: Boolean
) : RecyclerView.Adapter<MediaGalleryAdapter.MediaCellHolder>(),
    Filterable,
    MediaGalleryCursorFilterListener {

    private var isDataValid = mediaCursor != null

    var mediaCellUpdateModel: MediaCellUpdateModel =
        MediaCellUpdateModel(Pair(-1, -1), listOf())
        set(value) {
                field = value
                this.selectedMediaCellDisplayModels =
                    mediaCellUpdateModel.selectedMediaCellDisplayModels
                if (value.positions.first != -1) {
                    notifyItemChanged(value.positions.first)
                }
                if (value.positions.second != -1 && !allowMultipleSelection) {
                    notifyItemChanged(value.positions.second)
                }
            }

    var filterQueryProvider: FilterQueryProvider? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val cursorFilter = MediaGalleryCursorFilter(this)

    /**
     * getColumnIndexOrThrow is used since _ID column exists in [BaseColumns]
     */
    private var idColumnIndex =
        mediaCursor?.getColumnIndexOrThrow(MediaStore.MediaColumns._ID) ?: -1

    private val bucketDisplayNameColumnIndex =
        mediaCursor?.getColumnIndex(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME) ?: -1

    private val mimeTypeColumnIndex =
        mediaCursor?.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE) ?: -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MediaCellHolder {
        val binding = DataBindingUtil.inflate<ViewMediaCellBinding>(
            LayoutInflater.from(parent.context),
            R.layout.view_media_cell,
            parent,
            false
        )
        return MediaCellHolder(binding)
    }

    override fun getItemCount(): Int = mediaCursor?.count ?: 0

    /**
     * [mediaCursor] is moved to the correct position
     * so it is used to get the data
     */
    override fun onBindViewHolder(holder: MediaCellHolder, position: Int) {
        if (mediaCursor?.moveToPosition(position) == false || !isDataValid) {
            throw IllegalStateException("onBind $position")
        }
        val cursor = mediaCursor ?: throw IllegalStateException("invalid cursor")

        /**
         * Get a URI representing the media item and
         * append the id from the projection column to the base URI
         */
        val id = cursor.getLong(idColumnIndex)
        val contentUri: Uri = ContentUris.withAppendedId(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            id
        )
        val bucketDisplayName = cursor.getString(bucketDisplayNameColumnIndex)
        val mimeType = MimeType.fromValue(cursor.getString(mimeTypeColumnIndex))
        val displayModel = MediaCellDisplayModel(
            position = position,
            id = id,
            mediaUri = contentUri,
            isChecked = selectedMediaCellDisplayModels.any { it.id == id },
            bucketDisplayName = bucketDisplayName,
            mimeType = mimeType,
            isEnabled = mimeTypes.contains(mimeType) || isCameraCapture(id)
        )
        holder.bind(displayModel, mediaCellListener)
    }

    private fun isCameraCapture(id: Long) = id == CAMERA_CAPTURE_ID

    override fun getItemId(position: Int): Long =
        if (isDataValid && mediaCursor?.moveToPosition(position) == true) {
            mediaCursor?.getLong(idColumnIndex) ?: 0
        } else RecyclerView.NO_ID

    inner class MediaCellHolder(
        private val binding: ViewMediaCellBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(displayModel: MediaCellDisplayModel, listener: MediaCellListener) {
            binding.displayModel = displayModel
            binding.listener = listener
            binding.executePendingBindings()
        }
    }

    /**
     * Query with the specified constraint is
     * requested by the attached [MediaGalleryCursorFilter],
     * provided by the [FilterQueryProvider] and
     * is always performed asynchronously when [Filter.filter] is called
     * The current cursor is returned unfiltered if provider is not specified
     *
     * @param constraint to filter the query
     * @return [Cursor] for query results
     */
    override fun fetchMediaAsync(constraint: CharSequence?): Cursor? =
        filterQueryProvider?.runQuery(constraint) ?: getCursor()

    override fun getCursor(): Cursor? = mediaCursor

    /**
     * Update to new cursor with [swapCursor]
     *
     * @param cursor new
     */
    override fun changeCursor(cursor: Cursor?) {
        swapCursor(cursor)
    }

    /**
     * The returned old Cursor is *not* closed here,
     * but in [AppCompatActivity.onDestroy]
     *
     * @param newCursor to be used
     * @return previous [Cursor]
     * or null if it is equal to newCursor/does not exist
     */
    private fun swapCursor(newCursor: Cursor?) {
        if (newCursor === mediaCursor) return
        mediaCursor = newCursor
        isDataValid = newCursor != null
        idColumnIndex =
            newCursor?.getColumnIndex(MediaStore.MediaColumns._ID) ?: -1
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter = cursorFilter
}
