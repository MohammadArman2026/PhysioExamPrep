package com.arman.dev.physioexamprep.core.data.fake

import com.arman.dev.physioexamprep.core.domain.model.ScoredSession
import com.arman.dev.physioexamprep.core.domain.model.SessionResultStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/** Process-lifetime store. Replace with AssessmentRepository.completeSession() when wiring Room. */
@Singleton
class InMemorySessionResultStore @Inject constructor() : SessionResultStore {
    private val sessions = MutableStateFlow<Map<String, ScoredSession>>(emptyMap())
    override suspend fun save(session: ScoredSession) = sessions.update { it + (session.id to session) }
    override fun observe(id: String): Flow<ScoredSession?> = sessions.map { it[id] }
}
