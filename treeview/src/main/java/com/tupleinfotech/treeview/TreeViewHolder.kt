package com.tupleinfotech.treeview

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class TreeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var nodePadding =   50

    /**
     * Bind method that provide padding and bind TreeNode to the view list item
     * @param node the current TreeNode
     */
    open fun bindTreeNode(node: TreeNode) {

        val padding: Int = node.level * nodePadding
        itemView.setPadding(
            padding,
            itemView.paddingTop,
            itemView.paddingRight,
            itemView.paddingBottom
        )

    }

    /**
     * Modify the current node padding value
     * @param padding the new padding value
     */
    fun setNodePadding(padding: Int) {
        nodePadding = padding
    }

    /**
     * Return the current TreeNode padding value
     * @return The current padding value
     */
    fun getNodePadding(): Int {
        return nodePadding
    }

}