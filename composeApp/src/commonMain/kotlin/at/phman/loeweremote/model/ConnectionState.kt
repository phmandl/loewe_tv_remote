package at.phman.loeweremote.model

/**
 * State representing TV connection status and session readiness.
 */
sealed interface ConnectionState {
    data object Disconnected : ConnectionState
    data object Connecting : ConnectionState
    data class Connected(val clientId: String) : ConnectionState
    data class Error(val message: String) : ConnectionState
}

data class RemoteUiState(
    val connectionState: ConnectionState = ConnectionState.Disconnected,
    val isSendingCommand: Boolean = false,
    val lastSentKey: LoeweKey? = null,
    val statusMessage: String? = null,
    val isWolSending: Boolean = false,
    val showSettings: Boolean = false,
    val isNumpadExpanded: Boolean = false,
    val logs: List<String> = listOf("Loewe Remote ready. Tap ⚙ Settings to configure TV.")
)
