package com.arman.dev.physioexamprep.core.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.arman.dev.physioexamprep.core.database.entity.ChapterEntity
import com.arman.dev.physioexamprep.core.database.entity.SubjectEntity

data class SubjectWithChapters(
    @Embedded
    val subject: SubjectEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "subjectId"
    )
    val chapters: List<ChapterEntity>
) {
    /** Room does not order @Relation results, so use this for display. */
    val sortedChapters: List<ChapterEntity>
        get() = chapters.sortedWith(compareBy({ it.displayOrder }, { it.id }))
}
