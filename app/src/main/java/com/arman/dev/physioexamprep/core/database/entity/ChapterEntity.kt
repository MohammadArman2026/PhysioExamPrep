package com.arman.dev.physioexamprep.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A chapter inside a [SubjectEntity]. Questions belong directly to chapters.
 * Deleting a subject deletes its chapters (and, through them, their questions).
 */
@Entity(
    tableName = "chapters",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["subjectId"])]
)
data class ChapterEntity(
    @PrimaryKey
    val id: Long,
    val subjectId: Long,
    val name: String,
    val description: String?,
    /** Position of the chapter inside its subject (ascending). */
    val displayOrder: Int
)
