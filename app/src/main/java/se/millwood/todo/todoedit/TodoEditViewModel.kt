package se.millwood.todo.todoedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import se.millwood.todo.alarmmanager.TodoAlarmManager
import se.millwood.todo.data.Repository
import java.time.Instant
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TodoEditViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle,
    private val alarmManager: TodoAlarmManager,
) : ViewModel() {

    private val editArgs = savedStateHandle.get<TodoEditDialogFragment.TodoEditArguments>(
        TodoEditDialogFragment.TODO_EDIT_ARGS
    )
    private val cardId = editArgs?.cardId
    val todoId = editArgs?.todoId
    val alarm: Flow<Instant?> = repository.getTodoAlarm(UUID.fromString(todoId))

    fun updateTodoTitle(title: String) {
        viewModelScope.launch {
            repository.updateTodoTitle(
                cardId = UUID.fromString(cardId),
                todoId = UUID.fromString(todoId),
                title = title
            )
        }
    }

    fun deleteTodo(todoId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            alarmManager.updateTodoAlarm(
                cardId = UUID.fromString(cardId),
                todoId = UUID.fromString(todoId),
                alarmTime = null
            )
            repository.deleteTodo(
                cardId = UUID.fromString(cardId),
                todoId = UUID.fromString(todoId)
            )
        }
    }

    fun updateTodoAlarm(alarm: Instant?) {
        viewModelScope.launch {
            alarmManager.updateTodoAlarm(
                cardId = UUID.fromString(cardId),
                todoId = UUID.fromString(todoId),
                alarmTime = alarm
            )
        }
    }

    suspend fun getTodoTitle() = repository.getTodoTitle(UUID.fromString(todoId))
}
