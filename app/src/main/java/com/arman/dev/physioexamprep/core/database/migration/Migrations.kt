package com.arman.dev.physioexamprep.core.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Hand-written migrations. SQL must match what Room expects for the entities
 * exactly (table/column names, types, nullability, PKs, FKs, index names).
 * Room validates this when the migration runs; see MigrationTest.
 */

/** v1 → v2: adds the user-data tables. Content tables are not touched. */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // user_profiles
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `user_profiles` (`userId` TEXT NOT NULL, `displayName` TEXT NOT NULL, " +
                "`email` TEXT, `targetExam` TEXT, `dailyQuestionGoal` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, " +
                "`updatedAt` INTEGER NOT NULL, PRIMARY KEY(`userId`))"
        )

        // mock_definitions
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `mock_definitions` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, " +
                "`title` TEXT NOT NULL, `description` TEXT, `mockType` TEXT NOT NULL, `sourcePaperId` INTEGER, " +
                "`durationMinutes` INTEGER NOT NULL, `totalQuestions` INTEGER NOT NULL, `filterConfig` TEXT, " +
                "`createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `deletedAt` INTEGER, PRIMARY KEY(`id`), " +
                "FOREIGN KEY(`userId`) REFERENCES `user_profiles`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_mock_definitions_userId` ON `mock_definitions` (`userId`)")

        // mock_definition_questions
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `mock_definition_questions` (`id` TEXT NOT NULL, " +
                "`mockDefinitionId` TEXT NOT NULL, `questionId` INTEGER NOT NULL, `position` INTEGER NOT NULL, " +
                "`marks` REAL NOT NULL, `negativeMarks` REAL NOT NULL, PRIMARY KEY(`id`), " +
                "FOREIGN KEY(`mockDefinitionId`) REFERENCES `mock_definitions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
                "FOREIGN KEY(`questionId`) REFERENCES `questions`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )"
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_mock_definition_questions_mockDefinitionId_position` " +
                "ON `mock_definition_questions` (`mockDefinitionId`, `position`)"
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_mock_definition_questions_mockDefinitionId_questionId` " +
                "ON `mock_definition_questions` (`mockDefinitionId`, `questionId`)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_mock_definition_questions_questionId` " +
                "ON `mock_definition_questions` (`questionId`)"
        )

        // assessment_sessions
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `assessment_sessions` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, " +
                "`sessionType` TEXT NOT NULL, `title` TEXT NOT NULL, `mockDefinitionId` TEXT, `sourcePaperId` INTEGER, " +
                "`durationSeconds` INTEGER, `startedAt` INTEGER NOT NULL, `completedAt` INTEGER, " +
                "`currentQuestionIndex` INTEGER NOT NULL, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, " +
                "`updatedAt` INTEGER NOT NULL, `deletedAt` INTEGER, PRIMARY KEY(`id`), " +
                "FOREIGN KEY(`userId`) REFERENCES `user_profiles`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
                "FOREIGN KEY(`mockDefinitionId`) REFERENCES `mock_definitions`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )"
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_assessment_sessions_userId` ON `assessment_sessions` (`userId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_assessment_sessions_status` ON `assessment_sessions` (`status`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_assessment_sessions_startedAt` ON `assessment_sessions` (`startedAt`)")
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_assessment_sessions_mockDefinitionId` " +
                "ON `assessment_sessions` (`mockDefinitionId`)"
        )

        // session_questions
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `session_questions` (`id` TEXT NOT NULL, `sessionId` TEXT NOT NULL, " +
                "`questionId` INTEGER NOT NULL, `position` INTEGER NOT NULL, `marks` REAL NOT NULL, " +
                "`negativeMarks` REAL NOT NULL, PRIMARY KEY(`id`), " +
                "FOREIGN KEY(`sessionId`) REFERENCES `assessment_sessions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
                "FOREIGN KEY(`questionId`) REFERENCES `questions`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )"
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_session_questions_sessionId_position` " +
                "ON `session_questions` (`sessionId`, `position`)"
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_session_questions_sessionId_questionId` " +
                "ON `session_questions` (`sessionId`, `questionId`)"
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_session_questions_questionId` ON `session_questions` (`questionId`)")

        // session_answers
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `session_answers` (`id` TEXT NOT NULL, `sessionQuestionId` TEXT NOT NULL, " +
                "`answerStatus` TEXT NOT NULL, `isMarkedForReview` INTEGER NOT NULL, `isVisited` INTEGER NOT NULL, " +
                "`timeSpentSeconds` INTEGER NOT NULL, `answeredAt` INTEGER, `updatedAt` INTEGER NOT NULL, " +
                "PRIMARY KEY(`id`), FOREIGN KEY(`sessionQuestionId`) REFERENCES `session_questions`(`id`) " +
                "ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_session_answers_sessionQuestionId` " +
                "ON `session_answers` (`sessionQuestionId`)"
        )

        // session_answer_options
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `session_answer_options` (`id` TEXT NOT NULL, " +
                "`sessionAnswerId` TEXT NOT NULL, `selectedOptionId` INTEGER NOT NULL, PRIMARY KEY(`id`), " +
                "FOREIGN KEY(`sessionAnswerId`) REFERENCES `session_answers`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
                "FOREIGN KEY(`selectedOptionId`) REFERENCES `question_options`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )"
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_session_answer_options_sessionAnswerId_selectedOptionId` " +
                "ON `session_answer_options` (`sessionAnswerId`, `selectedOptionId`)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_session_answer_options_selectedOptionId` " +
                "ON `session_answer_options` (`selectedOptionId`)"
        )

        // session_results
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `session_results` (`sessionId` TEXT NOT NULL, " +
                "`totalQuestions` INTEGER NOT NULL, `attemptedQuestions` INTEGER NOT NULL, " +
                "`correctAnswers` INTEGER NOT NULL, `incorrectAnswers` INTEGER NOT NULL, " +
                "`unattemptedQuestions` INTEGER NOT NULL, `totalMarks` REAL NOT NULL, `obtainedMarks` REAL NOT NULL, " +
                "`accuracy` REAL NOT NULL, `timeTakenSeconds` INTEGER NOT NULL, `completedAt` INTEGER NOT NULL, " +
                "PRIMARY KEY(`sessionId`), FOREIGN KEY(`sessionId`) REFERENCES `assessment_sessions`(`id`) " +
                "ON UPDATE NO ACTION ON DELETE CASCADE )"
        )

        // question_bookmarks
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `question_bookmarks` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, " +
                "`questionId` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, " +
                "`deletedAt` INTEGER, PRIMARY KEY(`id`), " +
                "FOREIGN KEY(`userId`) REFERENCES `user_profiles`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
                "FOREIGN KEY(`questionId`) REFERENCES `questions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_question_bookmarks_userId_questionId` " +
                "ON `question_bookmarks` (`userId`, `questionId`)"
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_question_bookmarks_questionId` ON `question_bookmarks` (`questionId`)")

        // question_progress
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `question_progress` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, " +
                "`questionId` INTEGER NOT NULL, `attemptCount` INTEGER NOT NULL, `correctCount` INTEGER NOT NULL, " +
                "`incorrectCount` INTEGER NOT NULL, `lastAttemptAt` INTEGER, `updatedAt` INTEGER NOT NULL, " +
                "PRIMARY KEY(`id`), " +
                "FOREIGN KEY(`userId`) REFERENCES `user_profiles`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
                "FOREIGN KEY(`questionId`) REFERENCES `questions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_question_progress_userId_questionId` " +
                "ON `question_progress` (`userId`, `questionId`)"
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_question_progress_questionId` ON `question_progress` (`questionId`)")

        // chapter_progress
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `chapter_progress` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, " +
                "`chapterId` INTEGER NOT NULL, `attemptedQuestions` INTEGER NOT NULL, " +
                "`correctQuestions` INTEGER NOT NULL, `lastPracticedAt` INTEGER, `updatedAt` INTEGER NOT NULL, " +
                "PRIMARY KEY(`id`), " +
                "FOREIGN KEY(`userId`) REFERENCES `user_profiles`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
                "FOREIGN KEY(`chapterId`) REFERENCES `chapters`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_chapter_progress_userId_chapterId` " +
                "ON `chapter_progress` (`userId`, `chapterId`)"
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_chapter_progress_chapterId` ON `chapter_progress` (`chapterId`)")

        // daily_activity
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `daily_activity` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, " +
                "`activityDate` TEXT NOT NULL, `questionsAttempted` INTEGER NOT NULL, " +
                "`timeSpentSeconds` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`), " +
                "FOREIGN KEY(`userId`) REFERENCES `user_profiles`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_daily_activity_userId_activityDate` " +
                "ON `daily_activity` (`userId`, `activityDate`)"
        )

        // sync_outbox
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `sync_outbox` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, " +
                "`entityType` TEXT NOT NULL, `entityId` TEXT NOT NULL, `operation` TEXT NOT NULL, " +
                "`createdAt` INTEGER NOT NULL, `retryCount` INTEGER NOT NULL, PRIMARY KEY(`id`), " +
                "FOREIGN KEY(`userId`) REFERENCES `user_profiles`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_sync_outbox_userId_createdAt` ON `sync_outbox` (`userId`, `createdAt`)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_sync_outbox_entityType_entityId` ON `sync_outbox` (`entityType`, `entityId`)"
        )
    }
}

/** Register every migration here; DatabaseModule and tests use this list. */
val ALL_MIGRATIONS = arrayOf(MIGRATION_1_2)
