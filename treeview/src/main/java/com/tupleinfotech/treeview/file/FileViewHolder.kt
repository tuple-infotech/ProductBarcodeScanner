package com.tupleinfotech.treeview.file

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.tupleinfotech.treeview.R
import com.tupleinfotech.treeview.TreeNode
import com.tupleinfotech.treeview.TreeViewHolder

class FileViewHolder(itemView: View) : TreeViewHolder(itemView) {
    private var fileName: TextView
    private var fileStateIcon: ImageView
    private var fileTypeIcon: ImageView

    init {
        fileName = itemView.findViewById(R.id.file_name)
        fileStateIcon = itemView.findViewById(R.id.file_state_icon)
        fileTypeIcon = itemView.findViewById(R.id.file_type_icon)
    }

    override fun bindTreeNode(node: TreeNode) {
        super.bindTreeNode(node)
        val fileNameStr: String = node.text
        //val fileNameStr: String = node.value.toString()
        fileName.text = fileNameStr
        val dotIndex = fileNameStr.indexOf('.')

        if (dotIndex == -1) {
            //node.imageurl

            Glide.with(fileTypeIcon)
                .load(node.imageurl)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL) //using to load into cache then second time it will load fast.
                .transform(RoundedCorners(10))
                .into(fileTypeIcon)
            //fileTypeIcon.setImageResource(R.drawable.ic_folder)
        } else {
            val extension = fileNameStr.substring(dotIndex)
            val extensionIcon = ExtensionTable.getExtensionIcon(extension)
            fileTypeIcon.setImageResource(extensionIcon)
        }

        if (node.children.isEmpty()) {
            fileStateIcon.visibility = View.INVISIBLE
        } else {
            fileStateIcon.visibility = View.VISIBLE
            val stateIcon =
                if (node.isExpanded) R.drawable.ic_arrow_down else R.drawable.ic_arrow_right
            fileStateIcon.setImageResource(stateIcon)
        }
    }
}