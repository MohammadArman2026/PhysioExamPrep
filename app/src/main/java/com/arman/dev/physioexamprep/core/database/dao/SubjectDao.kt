package com.arman.dev.physioexamprep.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.arman.dev.physioexamprep.core.database.entity.SubjectEntity
import com.arman.dev.physioexamprep.core.database.relation.SubjectWithChapters
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Query("SELECT * FROM subjects WHERE isActive = 1 ORDER BY displayOrder ASC, id ASC")
    fun getAllActiveSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :id")
    fun getSubjectById(id: Long): Flow<SubjectEntity?>

    @Transaction
    @Query("SELECT * FROM subjects WHERE id = :subjectId")
    fun getSubjectWithChapters(subjectId: Long): Flow<SubjectWithChapters?>

    /**
     * Insert-or-update. Do NOT replace this with @Insert(onConflict = REPLACE):
     * REPLACE deletes the existing row first, which would cascade-delete all of
     * the subject's chapters, questions and options.
     */
    @Upsert
    suspend fun upsertSubjects(subjects: List<SubjectEntity>)

    /** Also deletes the subject's chapters, questions and options (CASCADE). */
    @Delete
    suspend fun deleteSubject(subject: SubjectEntity)
}
