package com.huy.mappies.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.huy.mappies.R
import com.huy.mappies.databinding.ActivityBookmarkDetailsBinding
import com.huy.mappies.utils.getAppInjector
import com.huy.mappies.viewmodel.BookmarkDetailsViewModel
import kotlinx.android.synthetic.main.activity_bookmark_details.*
import javax.inject.Inject

class BookmarkDetailsActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: BookmarkDetailsViewModel
    private lateinit var binding: ActivityBookmarkDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bookmark_details)

        getAppInjector().inject(this)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(BookmarkDetailsViewModel::class.java)

        setSupportActionBar(bookmark_details_toolbar)
    }
}
