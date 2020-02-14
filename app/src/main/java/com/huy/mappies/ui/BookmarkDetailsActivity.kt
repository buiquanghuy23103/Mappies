package com.huy.mappies.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.huy.mappies.R
import kotlinx.android.synthetic.main.activity_bookmark_details.*

class BookmarkDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark_details)
        setSupportActionBar(bookmark_details_toolbar)
    }
}
