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
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import androidx.recyclerview.widget.RecyclerView
import com.kinnerapriyap.sugar.choice.MimeType
import com.kinnerapriyap.sugar.databinding.ViewMediaCellBinding
import com.kinnerapriyap.sugar.mediagallery.MediaGalleryHandler
import com.kinnerapriyap.sugar.mediagallery.MediaGalleryHandler.Companion.CAMERA_CAPTURE_ID
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellDisplayModel
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellUpdateModel
import com.kinnerapriyap.sugar.mediagallery.cell.bindMediaUri

const val DEFAULT_ID = -1L

class MediaGalleryAdapter(
    private var mediaCursor: Cursor,
    private var selectedMediaCellDisplayModels: List<MediaCellDisplayModel>,
    private val onMediaCellClicked: ((MediaCellDisplayModel) -> Unit),
    private val mimeTypes: List<MimeType>,
    private val allowMultipleSelection: Boolean
) : RecyclerView.Adapter<MediaGalleryAdapter.MediaCellHolder>(),
    Filterable,
    MediaGalleryCursorFilterListener {

    private var isDataValid = true

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
        mediaCursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)

    private val bucketDisplayNameColumnIndex =
        mediaCursor.getColumnIndex(MediaGalleryHandler.BUCKET_DISPLAY_NAME)

    private val mimeTypeColumnIndex =
        mediaCursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MediaCellHolder {
        val binding = ViewMediaCellBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MediaCellHolder(binding)
    }

    override fun getItemCount(): Int = mediaCursor.count

    /**
     * [mediaCursor] is moved to the correct position
     * so it is used to get the data
     */
    override fun onBindViewHolder(holder: MediaCellHolder, position: Int) {
        if (!mediaCursor.moveToPosition(position) || !isDataValid) {
            throw IllegalStateException("onBind position:$position isDataValid:$isDataValid")
        } else if (idColumnIndex == -1 ||
            bucketDisplayNameColumnIndex == -1 ||
            mimeTypeColumnIndex == -1
        ) {
            throw IllegalStateException(
                "onBind invalid column index $idColumnIndex " +
                        "$bucketDisplayNameColumnIndex $mimeTypeColumnIndex"
            )
        }

        /**
         * Get a URI representing the media item and
         * append the id from the projection column to the base URI
         */
        val id = mediaCursor.getLongOrNull(idColumnIndex) ?: DEFAULT_ID
        val contentUri: Uri = ContentUris.withAppendedId(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            id
        )
        val bucketDisplayName = mediaCursor.getStringOrNull(bucketDisplayNameColumnIndex)
        val mimeType = MimeType.fromValue(mediaCursor.getStringOrNull(mimeTypeColumnIndex))
        val displayModel = MediaCellDisplayModel(
            position = position,
            id = id,
            mediaUri = contentUri,
            isChecked = selectedMediaCellDisplayModels.any { it.id == id },
            bucketDisplayName = bucketDisplayName,
            mimeType = mimeType,
            isEnabled = mimeTypes.contains(mimeType) || isCameraCapture(id)
        )
        holder.bind(displayModel, onMediaCellClicked)
    }

    private fun isCameraCapture(id: Long) = id == CAMERA_CAPTURE_ID

    inner class MediaCellHolder(
        private val binding: ViewMediaCellBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            displayModel: MediaCellDisplayModel,
            onMediaCellClicked: ((MediaCellDisplayModel) -> Unit)
        ) {
            binding.imageView.bindMediaUri(displayModel.mediaUri)
            binding.cardView.isChecked = displayModel.isChecked
            binding.cardView.isEnabled = displayModel.isEnabled
            binding.cardView.setOnClickListener { onMediaCellClicked.invoke(displayModel) }
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
    override fun fetchMediaAsync(constraint: CharSequence?): Cursor =
        filterQueryProvider?.runQuery(constraint) ?: getCursor()

    override fun getCursor(): Cursor = mediaCursor

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
        if (newCursor != null) {
            mediaCursor = newCursor
        }
        isDataValid = newCursor != null
        idColumnIndex =
            mediaCursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter = cursorFilter
}
