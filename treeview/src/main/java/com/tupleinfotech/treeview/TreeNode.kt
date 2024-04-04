package com.tupleinfotech.treeview

import java.util.*

class TreeNode(text: String,value: Any, layoutId: Int,imageUrl : String) {

    var value: Any
    var text: String
    var parent: TreeNode? = null
    var children: LinkedList<TreeNode> = LinkedList()
    var level: Int = 0
    var layoutId: Int
    var isExpanded: Boolean = false
    var isSelected: Boolean = false
    var imageurl: String

    init {
        this.value = value
        this.text = text
        this.children = LinkedList()
        this.level = 0
        this.layoutId = layoutId
        this.isExpanded = false
        this.isSelected = false
        this.imageurl = imageUrl
    }

    fun addChild(child: TreeNode) {
        child.parent = this

        child.level = level + 1
        children.add(child)
        updateNodeChildrenDepth1(child)
    }

    private fun updateNodeChildrenDepth(node: TreeNode) {
        if (node.children.isEmpty()) return
        for (child in node.children) {
            child.level = node.level + 1
        }
    }

    private fun updateNodeChildrenDepth1(node: TreeNode) {
        if (node.children.isEmpty()) return
        for (child in node.children) {
            child.level = node.level + 1
            updateNodeChildrenDepth1(child)
        }
    }
}