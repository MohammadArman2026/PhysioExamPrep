package com.arman.dev.physioexamprep.navigation

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.arman.dev.physioexamprep.core.domain.model.PracticeMode
import com.arman.dev.physioexamprep.core.domain.model.QuizRequest
import com.arman.dev.physioexamprep.core.domain.model.QuizSource

sealed class Routes(val route: String) {
    // Bottom-bar tabs
    data object Home : Routes("Home")
    data object Learn : Routes("Learn")
    data object PYQs : Routes("PYQs")
    data object Mock : Routes("Mocks")
    data object User : Routes("User") // Profile ("Me" tab)

    /** 03 · Topic picker for one subject. */
    data object Topics : Routes("Topics/{subjectId}") {
        const val ARG_SUBJECT_ID = "subjectId"
        val arguments: List<NamedNavArgument> = listOf(navArgument(ARG_SUBJECT_ID) { type = NavType.LongType })
        fun create(subjectId: Long) = "Topics/$subjectId"
    }

    /** 04/05 · Quiz. A [QuizRequest] flattened into query args. */
    data object Quiz : Routes("Quiz?source={source}&count={count}&mode={mode}&subjectId={subjectId}&topics={topics}&ref={ref}") {
        private const val SOURCE = "source"
        private const val COUNT = "count"
        private const val MODE = "mode"
        private const val SUBJECT = "subjectId"
        private const val TOPICS = "topics"
        private const val REF = "ref"

        val arguments: List<NamedNavArgument> = listOf(
            navArgument(SOURCE) { type = NavType.StringType; defaultValue = QuizSource.PRACTICE.name },
            navArgument(COUNT) { type = NavType.IntType; defaultValue = 10 },
            navArgument(MODE) { type = NavType.StringType; defaultValue = PracticeMode.PRACTICE.name },
            navArgument(SUBJECT) { type = NavType.LongType; defaultValue = -1L },
            navArgument(TOPICS) { type = NavType.StringType; defaultValue = "" },
            navArgument(REF) { type = NavType.StringType; nullable = true; defaultValue = null },
        )

        fun create(request: QuizRequest): String = buildString {
            append("Quiz?$SOURCE=${request.source.name}")
            append("&$COUNT=${request.questionCount}")
            append("&$MODE=${request.mode.name}")
            append("&$SUBJECT=${request.subjectId ?: -1L}")
            append("&$TOPICS=${request.topicIds.joinToString(",")}")
            request.refId?.let { append("&$REF=${Uri.encode(it)}") }
        }

        fun requestFrom(handle: SavedStateHandle): QuizRequest = QuizRequest(
            source = QuizSource.valueOf(handle.get<String>(SOURCE) ?: QuizSource.PRACTICE.name),
            questionCount = handle.get<Int>(COUNT) ?: 10,
            mode = PracticeMode.valueOf(handle.get<String>(MODE) ?: PracticeMode.PRACTICE.name),
            subjectId = handle.get<Long>(SUBJECT)?.takeIf { it >= 0 },
            topicIds = handle.get<String>(TOPICS).orEmpty().split(",").mapNotNull { it.toLongOrNull() },
            refId = handle.get<String>(REF),
        )
    }

    /** 08 · Mock result (also used at the end of any quiz). */
    data object MockResult : Routes("MockResult/{resultId}") {
        const val ARG_RESULT_ID = "resultId"
        val arguments: List<NamedNavArgument> = listOf(navArgument(ARG_RESULT_ID) { type = NavType.StringType })
        fun create(resultId: String) = "MockResult/${Uri.encode(resultId)}"
    }
}
