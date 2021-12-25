package com.hlder.bottom.navigation

import android.view.View

abstract class BottomNavigationAdapter<T : BottomNavigationAdapter.ViewHolder> {
    private val listViewHolders = mutableListOf<T>()
    internal var bottomNavigation: BottomNavigation? = null

    abstract fun onCreateViewHolder(viewType: Int): T
    abstract fun onItemSelect(item: T, position: Int)
    abstract fun onItemDisSelect(item: T, position: Int)
    abstract fun getItemCount(): Int

    fun notifyDataSetChanged() {
        bottomNavigation?.refreshChild()
    }

    internal fun showItem(position: Int) {
        onItemSelect(listViewHolders[position], position)
    }

    internal fun disShowItem(position: Int) {
        onItemDisSelect(listViewHolders[position], position)
    }

    internal fun getViewHolder(index: Int): T {
        if (index >= listViewHolders.size) { // 如果viewHolder还不存在则先创建
            listViewHolders.add(onCreateViewHolder(getItemViewType(index)))
        }
        return listViewHolders[index]
    }

    open fun getItemViewType(position: Int): Int {
        return 0
    }

    abstract class ViewHolder(val view: View) {
    }
}

