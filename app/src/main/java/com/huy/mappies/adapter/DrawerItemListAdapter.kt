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
import com.huy.mappies.utils.buildCategoryToIconMap
import timber.log.Timber

class DrawerItemListAdapter(private val onDrawerItemClick: OnDrawerItemClick)
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
        return DrawerItem.from(parent, onDrawerItemClick)
    }

    override fun onBindViewHolder(holder: DrawerItem, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class DrawerItem private constructor(
        private val binding: MapsDrawerItemBinding,
        private val onDrawerItemClick: OnDrawerItemClick
    ) : RecyclerView.ViewHolder(binding.root)
    {
        private val categoryToIconMap = buildCategoryToIconMap()



        companion object {
            fun from(viewGroup: ViewGroup, onDrawerItemClick: OnDrawerItemClick): DrawerItem {

                val inflater = LayoutInflater.from(viewGroup.context)

                val binding: MapsDrawerItemBinding = DataBindingUtil.inflate(
                    inflater,
                    R.layout.maps_drawer_item,
                    viewGroup,
                    false
                )

                return DrawerItem(binding, onDrawerItemClick)
            }
        }

        fun bind(bookmarkView: BookmarkView) {
            binding.bookmarkView = bookmarkView
            binding.root.setOnClickListener {
                onDrawerItemClick.handleDrawerItemClick(bookmarkView)
            }
            Timber.i(bookmarkView.category)
            val categoryIconResId = categoryToIcon(bookmarkView.category)
            binding.drawerItemIcon.setImageResource(categoryIconResId)
        }

        private fun categoryToIcon(category: String)
            = categoryToIconMap[category] ?: R.drawable.ic_other

    }

    interface OnDrawerItemClick {
        fun handleDrawerItemClick(bookmarkView: BookmarkView)
    }

}