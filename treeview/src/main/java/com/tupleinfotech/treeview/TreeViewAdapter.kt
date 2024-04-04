package com.tupleinfotech.treeview

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TreeViewAdapter(factory: TreeViewHolderFactory) : RecyclerView.Adapter<TreeViewHolder>() {

    /**
     * Interface definition for a callback to be invoked when a TreeNode has been clicked and held.
     */
    interface OnTreeNodeClickListener {
        /**
         * Called when a TreeNode has been clicked.
         * @param treeNode The current clicked node
         * @param view The view that was clicked and held.
         */
        fun onTreeNodeClick(treeNode: TreeNode, view: View)
    }

    /**
     * Interface definition for a callback to be invoked when a TreeNode has been clicked and held.
     */
    interface OnTreeNodeLongClickListener {
        /**
         * Called when a TreeNode has been clicked and held.
         * @param treeNode The current clicked node
         * @param view The view that was clicked and held.
         * @return true if the callback consumed the long click, false otherwise.
         */
        fun onTreeNodeLongClick(treeNode: TreeNode, view: View): Boolean
    }

    /**
     * Manager class for TreeNodes to easily apply operations on them
     * and to make it easy for testing and extending
     */
    private var treeNodeManager: TreeNodeManager

    /**
     * A ViewHolder Factory to get TreeViewHolder object that mapped with layout
     */
    private var treeViewHolderFactory: TreeViewHolderFactory

    /**
     * The current selected Tree Node
     */
    private var currentSelectedNode: TreeNode? = null

    /**
     * Custom OnClickListener to be invoked when a TreeNode has been clicked.
     */
    private var treeNodeClickListener: OnTreeNodeClickListener? = null

    /**
     * Custom OnLongClickListener to be invoked when a TreeNode has been clicked and hold.
     */
    private lateinit var treeNodeLongClickListener: OnTreeNodeLongClickListener

    init{
        treeViewHolderFactory = factory
        treeNodeManager = TreeNodeManager()
    }

    override fun onCreateViewHolder(parent: ViewGroup, layoutId: Int): TreeViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)

        val holder = treeViewHolderFactory.getTreeViewHolder(view, layoutId)
        return holder
    }

    override fun onBindViewHolder(holder: TreeViewHolder, position: Int) {
        val currentNode = treeNodeManager[position]
        holder.bindTreeNode(currentNode)

        holder.itemView.setOnClickListener { v ->
            // Handle node selection
            currentNode.isSelected = true
            if (currentSelectedNode != null) currentSelectedNode!!.isSelected = false
            currentSelectedNode = currentNode

            // Handle node expand and collapse event
            if (!currentNode.children.isEmpty()) {
                val isNodeExpanded: Boolean = currentNode.isExpanded
                if (isNodeExpanded) collapseNode(currentNode) else expandNode(currentNode)
                currentNode.isExpanded = !isNodeExpanded
            }
            notifyDataSetChanged()

            // Handle TreeNode click listener event
            if (treeNodeClickListener != null) treeNodeClickListener!!.onTreeNodeClick(currentNode, v)
        }

//        if(currentNode.isSelected)
//            (holder.itemView.findViewById(R.id.file_name) as TextView).setTextColor(ContextCompat.getColor(holder.itemView.context,R.color.red))
//        else
//            (holder.itemView.findViewById(R.id.file_name) as TextView).setTextColor(Color.BLACK)

        (holder.itemView.findViewById<TextView>(R.id.file_name)!!).setTextColor(Color.BLACK)
        // Handle TreeNode long click listener event

        // Handle TreeNode long click listener event
        /*holder.itemView.setOnLongClickListener { v ->
            return@setOnLongClickListener treeNodeLongClickListener.onTreeNodeLongClick(
                currentNode,
                v
            )
            true
        }*/
    }

    override fun getItemViewType(position: Int): Int {
        return treeNodeManager[position].layoutId
    }
    override fun getItemCount(): Int {
        return treeNodeManager.size()
    }

    /**
     * Collapsing node and all of his children
     * @param node The node to collapse it
     */
    fun collapseNode(node: TreeNode) {
        val position = treeNodeManager.collapseNode(node)
        if (position != -1) {
            notifyDataSetChanged()
        }
    }

    /**
     * Expanding node and all of his children
     * @param node The node to expand it
     */
    fun expandNode(node: TreeNode) {
        val position = treeNodeManager.expandNode(node)
        if (position != -1) {
            notifyDataSetChanged()
        }
    }

    fun expandNodeManually(node: TreeNode) {
        val position = treeNodeManager.expandNode(node)
        if (position != -1) {
            notifyDataSetChanged()
        }
    }

    /**
     * Collapsing full node branches
     * @param node The node to collapse it
     */
    fun collapseNodeBranch(node: TreeNode) {
        treeNodeManager.collapseNodeBranch(node)
        notifyDataSetChanged()
    }

    /**
     * Expanding node full branches
     * @param node The node to expand it
     */
    fun expandNodeBranch(node: TreeNode) {
        treeNodeManager.expandNodeBranch(node)
        notifyDataSetChanged()
    }

    /**
     * Expanding one node branch to until specific level
     * @param node to expand branch of it until level
     * @param level to expand node branches to it
     */
    fun expandNodeToLevel(node: TreeNode, level: Int) {
        treeNodeManager.expandNodeToLevel(node, level)
        notifyDataSetChanged()
    }

    /**
     * Expanding all tree nodes branches to until specific level
     * @param level to expand all nodes branches to it
     */
    fun expandNodesAtLevel(level: Int) {
        treeNodeManager.expandNodesAtLevel(level)
        notifyDataSetChanged()
    }

    /**
     * Collapsing all nodes in the tree with their children
     */
    fun collapseAll() {
        treeNodeManager.collapseAll()
        notifyDataSetChanged()
    }

    /**
     * Expanding all nodes in the tree with their children
     */
    fun expandAll() {
        treeNodeManager.expandAll()
        notifyDataSetChanged()
    }

    /**
     * Update the list of tree nodes
     * @param treeNodes The new tree nodes
     */
    fun updateTreeNodes(treeNodes: List<TreeNode>) {
        treeNodeManager.updateNodes(treeNodes)
        notifyDataSetChanged()
    }

    /**
     * Delete all tree nodes
     */
    fun clearTreeNodes() {
        val size = treeNodeManager.size()
        treeNodeManager.clearNodes()
        notifyItemRangeRemoved(0, size)
    }

    /**
     * Register a callback to be invoked when this TreeNode is clicked
     * @param listener The callback that will run
     */
    fun setTreeNodeClickListener(listener: OnTreeNodeClickListener) {
        treeNodeClickListener = listener
    }

    /**
     * Register a callback to be invoked when this TreeNode is clicked and held
     * @param listener The callback that will run
     */
    fun setTreeNodeLongClickListener(listener: OnTreeNodeLongClickListener) {
        treeNodeLongClickListener = listener
    }

    /**
     * Set the current visible tree nodes and notify adapter data
     * @param treeNodes New tree nodes
     */
    fun setTreeNodes(treeNodes: List<TreeNode>) {
        treeNodeManager.setTreeNodes(treeNodes)
        notifyDataSetChanged()
    }

    /**
     * Get the Current visible Tree nodes
     * @return The visible Tree nodes main
     */
    fun getTreeNodes(): List<TreeNode> {
        return treeNodeManager.getTreeNodes()
    }

    /**
     * @return The current selected TreeNode
     */
    fun getSelectedNode(): TreeNode? {
        return currentSelectedNode
    }

    fun setNodeSelectionById(categoryId :  String) {
        val node = treeNodeManager.selectNode(treeNodeManager[0],"Bass")
        expandNodeToLevel(node!!, node.level-1)
        /*for(i in 0 until treeNodeManager.size()){
            val cat = treeNodeManager[i].value as CategoryTree
            if(cat.CATEGORYID == categoryId){
                expandNode(treeNodeManager[i])

            }
        }*/
    }

}