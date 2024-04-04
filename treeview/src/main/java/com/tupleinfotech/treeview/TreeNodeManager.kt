package com.tupleinfotech.treeview

import java.util.*

class TreeNodeManager {
    /**
     * Collection to save the current tree nodes
     */
    private var rootsNodes: LinkedList<TreeNode> = LinkedList()

    /**
     * Set the current visible tree nodes
     * @param treeNodes New tree nodes
     */
    fun setTreeNodes(treeNodes: List<TreeNode>) {
        rootsNodes.clear()
        rootsNodes.addAll(treeNodes)
    }

    /**
     * Get the Current visible Tree nodes
     * @return The visible Tree nodes main
     */
    fun getTreeNodes(): List<TreeNode> {
        return rootsNodes
    }

    /**
     * Get TreeNode from the current nodes by index
     * @param index of node to get it
     * @return TreeNode from by index from current tree nodes if exists
     */
    operator fun get(index: Int): TreeNode {
        return rootsNodes[index]
    }

    /**
     * Add new node to the current tree nodes
     * @param node to add it to the current tree nodes
     * @return true of this node is added
     */
    fun addNode(node: TreeNode): Boolean {
        return rootsNodes.add(node)
    }

    /**
     * Clear the current nodes and insert new nodes
     * @param newNodes to update the current nodes with them
     */
    fun updateNodes(newNodes: List<TreeNode>) {
        rootsNodes.clear()
        rootsNodes.addAll(newNodes)
    }

    /**
     * Delete one node from the visible nodes
     * @param node to delete it from the current nodes
     * @return true of this node is deleted
     */
    fun removeNode(node: TreeNode): Boolean {
        return rootsNodes.remove(node)
    }

    /**
     * Clear the current nodes
     */
    fun clearNodes() {
        rootsNodes.clear()
    }

    /**
     * Get the current number of visible nodes
     * @return the size of visible nodes
     */
    fun size(): Int {
        return rootsNodes.size
    }

    /**
     * Collapsing node and all of his children
     * @param node The node to collapse it
     * @return the index of this node if it exists in the list
     */
    fun collapseNode(node: TreeNode): Int {
        val position = rootsNodes.indexOf(node)
        if (position != -1 && node.isExpanded) {
            node.isExpanded = false
            val deletedParents: LinkedList<TreeNode> = LinkedList(node.children)
            rootsNodes.removeAll(node.children)
            for (i in position + 1 until rootsNodes.size) {
                val iNode = rootsNodes[i]
                if (deletedParents.contains(iNode.parent)) {
                    deletedParents.add(iNode)
                    deletedParents.addAll(iNode.children)
                }
            }
            rootsNodes.removeAll(deletedParents)
        }
        return position
    }

    /**
     * Expanding node and all of his children
     * @param node The node to expand it
     * @return the index of this node if it exists in the list
     */
    fun expandNode(node: TreeNode): Int {
        val position = rootsNodes.indexOf(node)
        if (position != -1 && !node.isExpanded) {
            node.isExpanded = true
            rootsNodes.addAll(position + 1, node.children)
            for (child in node.children) {
                if (child.isExpanded) updateExpandedNodeChildren(child)
            }
        }
        return position
    }

    /**
     * Update the list for expanded node
     * to expand any child of his children that is already expanded before
     * @param node that just expanded now
     */
    private fun updateExpandedNodeChildren(node: TreeNode) {
        val position = rootsNodes.indexOf(node)
        if (position != -1 && node.isExpanded) {
            rootsNodes.addAll(position + 1, node.children)
            for (child in node.children) {
                if (child.isExpanded) updateExpandedNodeChildren(child)
            }
        }
    }

    /**
     *
     * @param  node The node to collapse the branch of it
     * @return the index of this node if it exists in the list
     */
    fun collapseNodeBranch(node: TreeNode): Int {
        val position = rootsNodes.indexOf(node)
        if (position != -1 && node.isExpanded) {
            node.isExpanded = false
            for (child in node.children) {
                if (!child.children.isEmpty()) collapseNodeBranch(child)
                rootsNodes.remove(child)
            }
        }
        return position
    }

    /**
     * Expanding node full branches
     * @param  node The node to expand the branch of it
     * @return the index of this node if it exists in the list
     */
    fun expandNodeBranch(node: TreeNode): Int {
        val position = rootsNodes.indexOf(node)
        if (position != -1 && !node.isExpanded) {
            node.isExpanded = true
            var index = position + 1
            for (child in node.children) {
                val before = rootsNodes.size
                rootsNodes.add(index, child)
                expandNodeBranch(child)
                val after = rootsNodes.size
                val diff = after - before
                index += diff
            }
        }
        return position
    }

    /**
     * Expanding one node branch to until specific level
     * @param node to expand branch of it until level
     * @param level to expand node branches to it
     */
    fun expandNodeToLevel(node: TreeNode, level: Int) {
        if (node.level <= level) expandNode(node)
        for (child in node.children) {
            expandNodeToLevel(child, level)
        }
    }

    /**
     * Expanding all tree nodes branches to until specific level
     * @param level to expand all nodes branches to it
     */
    fun expandNodesAtLevel(level: Int) {
        for (i in rootsNodes.indices) {
            val node = rootsNodes[i]
            expandNodeToLevel(node, level)
        }
    }

    /**
     * Collapsing all nodes in the tree with their children
     */
    fun collapseAll() {
        val treeNodes: MutableList<TreeNode> = LinkedList()
        for (i in rootsNodes.indices) {
            val root = rootsNodes[i]
            if (root.level == 0) {
                collapseNodeBranch(root)
                treeNodes.add(root)
            } else {
                root.isExpanded = false
            }
        }
        updateNodes(treeNodes)
    }

    /**
     * Expanding all nodes in the tree with their children
     */
    fun expandAll() {
        for (i in rootsNodes.indices) {
            val root = rootsNodes[i]
            expandNodeBranch(root)
        }
    }

    fun selectNode(root: TreeNode, targetValue: String): TreeNode? {
        if (root.text == targetValue) {
            // Found the target node, mark it as selected
            root.isSelected = true
            return root
        }

        for (child in root.children) {
            val selectedNode = selectNode(child, targetValue)
            if (selectedNode != null) {
                // The target node was found in a child node
                // You can handle additional logic here if needed
                return selectedNode
            }
        }

        return null
    }
}