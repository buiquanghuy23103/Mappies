package com.huy.mappies.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.huy.mappies.R
import com.huy.mappies.databinding.ActivityBookmarkDetailsBinding
import com.huy.mappies.model.BookmarkView
import com.huy.mappies.utils.ImageUtils
import com.huy.mappies.utils.getAppInjector
import com.huy.mappies.utils.getUri
import com.huy.mappies.viewmodel.BookmarkDetailsViewModel
import kotlinx.android.synthetic.main.activity_bookmark_details.*
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject

class BookmarkDetailsActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: BookmarkDetailsViewModel
    private lateinit var binding: ActivityBookmarkDetailsBinding
    private var bookmarkView: BookmarkView? = null
    private var photoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bookmark_details)

        getAppInjector().inject(this)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(BookmarkDetailsViewModel::class.java)

        setSupportActionBar(bookmark_details_toolbar)

        setupFields()
    }

    private fun setupFields() {
        val bookmarkId = intent.getLongExtra(getString(R.string.intent_extra_bookmark_id), 0)
        viewModel.getBookmarkView(bookmarkId).observe(this, Observer {
            it?.let {
                binding.bookmarkView = it
                bookmarkView = it
                inflatePlaceImage()
            }
        })
    }

    private fun inflatePlaceImage() {
        val imageFilename = bookmarkView?.id?.let { bookmarkViewId ->
            ImageUtils.getImageFilename(bookmarkViewId)
        }
        val placeImage = ImageUtils.loadBitmapFromFile(this, imageFilename)
        binding.bookmarkDetailsPlaceImageView.setImageBitmap(placeImage)
        binding.bookmarkDetailsPlaceImageView.setOnClickListener {
            showPhotoOptionDialog()
        }
    }

    private fun showPhotoOptionDialog() {

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.photo_dialog_title)
            .setOnCancelListener { dialog -> dialog.dismiss() }
            .setItems(R.array.photo_dialog_options) { dialog, itemId ->
                when(itemId) {
                    0 -> capturePictures()
                    1 -> startPickingPictures()
                }
            }
            .show()
    }

    private fun capturePictures() {
        photoFile = null

        try {
            photoFile = ImageUtils.getImageFilename(this)
        } catch (error: IOException) {
            Timber.e(error)
        }

        photoFile?.also {

            val photoUri = it.getUri(this)

            val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                .putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

            // Grant permission
            packageManager.queryIntentActivities(
                captureIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
                .map { info ->
                    info.activityInfo.packageName
                }
                .forEach { name ->
                    grantUriPermission(name, photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }

            startActivityForResult(captureIntent, REQUEST_CAPTURE_IMAGE)
        }

    }

    private fun startPickingPictures() {
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickIntent, REQUEST_GALLERY_IMAGE)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bookmark_details_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.bookmark_details_action_save -> saveChanges()
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun saveChanges(): Boolean {

        val bookmarkName = bookmark_details_name_edit_text.text.toString()
        if (bookmarkName.isEmpty()) return true

        val newBookmarkView = bookmarkView?.apply {
            name = bookmark_details_name_edit_text.text.toString()
            notes = bookmark_details_notes_edit_text.text.toString()
            address = bookmark_details_address_edit_text.text.toString()
            phone = bookmark_details_phone_edit_text.text.toString()
        }

        viewModel.updateBookmark(newBookmarkView)

        finish()

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {
                REQUEST_CAPTURE_IMAGE -> {
                    saveImage()
                }
                REQUEST_GALLERY_IMAGE -> if (data != null && data.data != null) {
                    val imageUri = data.data
                    imageUri?.let { saveImage(imageUri) }
                }
            }

        }
    }

    private fun saveImage() {

        photoFile?.let { file ->
            val uri = file.getUri(this)
            revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            val image = getImageWithPath(file.absolutePath)
            updateImage(image)
        }

    }

    private fun updateImage(image: Bitmap) {
        bookmarkView?.let { bookmark ->
            val imageFilename = bookmark.id?.let { ImageUtils.getImageFilename(it) }
            ImageUtils.saveBitmapToFile(this, image, imageFilename)
            binding.bookmarkDetailsPlaceImageView.setImageBitmap(image)
        }
    }


    private fun getImageWithPath(filePath: String): Bitmap {
        val defaultWidth = resources.getDimensionPixelSize(R.dimen.default_image_width)
        val defaultHeight = resources.getDimensionPixelSize(R.dimen.default_image_height)
        return ImageUtils.decodeFileToSize(filePath, defaultWidth, defaultHeight)
    }

    private fun saveImage(uri: Uri) {
        val image = getImageWithUri(uri)
        image?.let { updateImage(image) }
    }

    private fun getImageWithUri(uri: Uri): Bitmap? {
        val defaultWidth = resources.getDimensionPixelSize(R.dimen.default_image_width)
        val defaultHeight = resources.getDimensionPixelSize(R.dimen.default_image_height)
        return ImageUtils.decodeUriStreamToSize(this, uri, defaultWidth, defaultHeight)
    }

    companion object {
        private const val REQUEST_CAPTURE_IMAGE = 1
        private const val REQUEST_GALLERY_IMAGE = 2
    }

}
