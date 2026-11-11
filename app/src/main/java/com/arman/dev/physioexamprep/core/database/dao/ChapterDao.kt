package com.arman.dev.physioexamprep.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.arman.dev.physioexamprep.core.database.entity.ChapterEntity
import com.arman.dev.physioexamprep.core.database.relation.ChapterWithQuestions
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {

    @Query("SELECT * FROM chapters WHERE subjectId = :subjectId ORDER BY displayOrder ASC, id ASC")
    fun getChaptersBySubject(subjectId: Long): Flow<List<ChapterEntity>>

    @Transaction
    @Query("SELECT * FROM chapters WHERE id = :chapterId")
    fun getChapterWithQuestions(chapterId: Long): Flow<ChapterWithQuestions?>

    /** Insert-or-update; never REPLACE (it would cascade-delete questions). */
    @Upsert
    suspend fun upsertChapters(chapters: List<ChapterEntity>)

    /** Also deletes the chapter's questions and their options (CASCADE). */
    @Delete
    suspend fun deleteChapter(chapter: ChapterEntity)
}
