package ch.lkmc.wortkatze.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.lkmc.wortkatze.data.VocabRepository
import ch.lkmc.wortkatze.learn.Vocabulary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * One immutable [HomeUiState] per screen, exposed as a [StateFlow] — the house
 * MVVM shape (AGENTS.md). Composables read state and send events; they never
 * reach for a repository themselves.
 */
data class HomeUiState(
    val loading: Boolean = true,
    val vocabulary: Vocabulary? = null,
    val failed: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val vocab: VocabRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = runCatching { vocab.vocabulary() }.fold(
                onSuccess = { HomeUiState(loading = false, vocabulary = it) },
                // A vocabulary that won't parse is a build problem, not a
                // runtime one, and the JVM test suite is what catches it. The
                // screen still has to say something rather than stay blank.
                onFailure = { HomeUiState(loading = false, failed = true) },
            )
        }
    }
}
