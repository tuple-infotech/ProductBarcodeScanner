package com.tupleinfotech.treeview.file

import android.view.View
import android.widget.ImageView
import android.widget.TextView
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
            when(node.text.toString()){
                "Dashboard"             -> {
                    fileTypeIcon.setImageResource(R.drawable.icon_dashboard)
                }
                "Barcode"               -> {
                    fileTypeIcon.setImageResource(R.drawable.icon_barcode)
                }
                "Print Barcode"         -> {
                    fileTypeIcon.setImageResource(R.drawable.icon_barcode)
                }
                "Print Barcode Report"  -> {
                    fileTypeIcon.setImageResource(R.drawable.icon_barcode)
                }
                "Production"            -> {
                    fileTypeIcon.setImageResource(R.drawable.icon_side_menu_orderhistory)
                }
                "Product Manufacture"   -> {
                    fileTypeIcon.setImageResource(R.drawable.icon_side_menu_orderhistory)
                }
                "Warehouse Entry"       -> {
                    fileTypeIcon.setImageResource(R.drawable.icon_side_menu_orderhistory)
                }
                "Product Details"       -> {
                    fileTypeIcon.setImageResource(R.drawable.icon_side_menu_orderhistory)
                }
                "Production Report"     -> {
                    fileTypeIcon.setImageResource(R.drawable.icon_side_menu_orderhistory)
                }
                "User Utility"          -> {
                    fileTypeIcon.setImageResource(R.drawable.icon_user)
                }
                "Manage User"           -> {
                    fileTypeIcon.setImageResource(R.drawable.icon_user)
                }
                "Master"                -> {
                    fileTypeIcon.setImageResource(R.drawable.icon_master)
                }
                "State Master"          -> {
                    fileTypeIcon.setImageResource(R.drawable.icon_master)
                }
                "City Master"           -> {
                    fileTypeIcon.setImageResource(R.drawable.icon_master)
                }
                "Logout"                -> {
                    fileTypeIcon.setImageResource(R.drawable.icon_logout)
                }
                else                    -> {
                    //fileTypeIcon.setImageResource(R.drawable.ic_folder)
                }
            }
//            fileTypeIcon.setImageResource(R.drawable.ic_folder)
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