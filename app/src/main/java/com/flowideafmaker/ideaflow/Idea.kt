package com.flowideafmaker.ideaflow

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Idea(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val status: String, // "Draft", "Developing", "Completed"
    val priority: String, // "Low", "Medium", "High"
    var progress: Int,
    val timeAgo: String,
) : Parcelable