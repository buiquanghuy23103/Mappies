package com.huy.mappies.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.huy.mappies.R
import com.huy.mappies.databinding.MapsDrawerItemBinding
import com.huy.mappies.model.BookmarkView

class DrawerItemListAdapter
    : ListAdapter<BookmarkView, DrawerItemListAdapter.DrawerItem>(diffCallback) {

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<BookmarkView>() {
            override fun areItemsTheSame(oldItem: BookmarkView, newItem: BookmarkView): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: BookmarkView, newItem: BookmarkView): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawerItem {
        return DrawerItem.from(parent)
    }

    override fun onBindViewHolder(holder: DrawerItem, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class DrawerItem private constructor(private val binding: MapsDrawerItemBinding)
        : RecyclerView.ViewHolder(binding.root)
    {

        companion object {
            fun from(viewGroup: ViewGroup): DrawerItem {

                val inflater = LayoutInflater.from(viewGroup.context)

                val binding: MapsDrawerItemBinding = DataBindingUtil.inflate(
                    inflater,
                    R.layout.maps_drawer_item,
                    viewGroup,
                    false
                )

                return DrawerItem(binding)
            }
        }

        fun bind(bookmarkView: BookmarkView) {
            binding.bookmarkView = bookmarkView
        }

    }
}