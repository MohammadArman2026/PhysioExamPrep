package com.arman.dev.physioexamprep.core.domain.model

data class UserProfile(
    val userId: String,
    val displayName: String,
    val email: String?,
    val targetExam: String?,
    val dailyQuestionGoal: Int,
    val createdAt: Long,
    val updatedAt: Long
)
