package com.arman.dev.physioexamprep.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A top-level subject shown on the Learn screen (e.g. Anatomy, Physiology).
 *
 * [id] is a stable, manually assigned ID (never auto-generated) so content
 * updates and user progress can always refer to the same subject.
 */
@Entity(
    tableName = "subjects",
    indices = [Index(value = ["isActive", "displayOrder"])]
)
data class SubjectEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val description: String?,
    /** Position of the subject on the Learn screen (ascending). */
    val displayOrder: Int,
    /** Only active subjects are shown to the user. */
    val isActive: Boolean = true
)
