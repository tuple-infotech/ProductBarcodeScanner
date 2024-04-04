package com.tupleinfotech.treeview

import android.view.View

interface TreeViewHolderFactory {
    /**
     * Provide a TreeViewHolder class depend on the current view
     * @param view The list item view
     * @param layout The layout xml file id for current view
     * @return A TreeViewHolder instance
     */
    fun getTreeViewHolder(view: View, layout: Int): TreeViewHolder
}