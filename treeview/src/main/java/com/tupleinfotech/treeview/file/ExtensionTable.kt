package com.tupleinfotech.treeview.file

import com.tupleinfotech.treeview.R


class ExtensionTable {
    companion object{
        fun getExtensionIcon(extension: String?): Int {
            return when (extension) {
                /*".c" -> R.drawable.ic_c
                ".cpp" -> R.drawable.ic_cpp
                ".cs" -> R.drawable.ic_cs
                ".git" -> R.drawable.ic_git
                ".go" -> R.drawable.ic_go
                ".gradle" -> R.drawable.ic_gradle
                ".java" -> R.drawable.ic_java*/
                else -> R.drawable.ic_file
            }
        }
    }
}