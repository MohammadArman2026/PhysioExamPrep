package com.arman.dev.physioexamprep.core.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Everything the screen renders. Immutable; replaced (never mutated) by the reducer. */
interface UiState

/** Something the user did (or the screen asked for). The only way into a ViewModel. */
interface UiIntent

/** One-shot event for the UI: navigation, toast, share sheet. Consumed once. */
interface UiEffect

/**
 * MVI base for every feature ViewModel.
 *
 *  UI ──intent──▶ onIntent() ──▶ handleIntent() ──▶ setState { reduce } ──▶ state ──▶ UI
 *                                       └──────────▶ sendEffect()        ──▶ effects ─▶ UI (once)
 *
 * Rules:
 *  - The UI only calls [onIntent] and reads [state] / [effects].
 *  - State changes only through [setState]. Keep reducers pure (old state + input → new state).
 *  - Side effects (repository calls, timers) live in [handleIntent] / init, inside [viewModelScope].
 */
abstract class MviViewModel<S : UiState, I : UiIntent, E : UiEffect>(initialState: S) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effects = Channel<E>(Channel.BUFFERED)
    val effects: Flow<E> = _effects.receiveAsFlow()

    protected val currentState: S get() = _state.value

    fun onIntent(intent: I) = handleIntent(intent)

    protected abstract fun handleIntent(intent: I)

    protected fun setState(reducer: S.() -> S) = _state.update(reducer)

    protected fun sendEffect(effect: E) {
        viewModelScope.launch { _effects.send(effect) }
    }
}

/** Collects one-shot effects while the screen is in composition. */
@Composable
fun <E : UiEffect> CollectEffects(effects: Flow<E>, onEffect: (E) -> Unit) {
    val handler = rememberUpdatedState(onEffect)
    LaunchedEffect(effects) {
        effects.collect { handler.value(it) }
    }
}
