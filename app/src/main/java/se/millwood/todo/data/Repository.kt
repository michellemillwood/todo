package se.millwood.todo.data

import android.content.Context
import kotlinx.coroutines.flow.map
import se.millwood.todo.settings.SettingsFragment
import java.util.*

class Repository(context: Context) {

    private val cardDao = TodoDatabase.getDatabase(context).cardDao()
    private val todoDao = TodoDatabase.getDatabase(context).todoDao()

    fun getCardsWithTodos(sortOrder: SettingsFragment.Companion.SortOrder) =
        cardDao.getCardsWithTodos(sortOrder.name).map { cards ->
            cards.map { CardWithTodos.from(it) }
        }

    suspend fun addCard(
        card: Card
    ) = cardDao.addCard(CardEntity.from(card))

    suspend fun updateCardTitle(
        title: String,
        cardId: UUID
    ) = cardDao.updateCard(CardEntity(title, cardId))

    suspend fun getCardTitle(
        cardId: UUID
    ) = cardDao.getCardTitle(cardId)

    suspend fun deleteCardWithTodos(
        cardId: UUID
    ) {
        todoDao.deleteCardTodos(cardId)
        cardDao.deleteCard(cardId)
    }

    fun getTodos(
        cardId: UUID
    ) = todoDao.getTodos(cardId).map { todoEntity ->
        todoEntity.map { it.toTodo() }
    }

    suspend fun updateTodo(
        cardId: UUID,
        todoId: UUID,
        title: String
    ) {
        cardDao.updateCardTimeStamp(cardId, System.currentTimeMillis())
        todoDao.updateTodoTitle(todoId, title)
    }

    suspend fun addTodo(
        cardId: UUID,
        todo: Todo
    ) {
        cardDao.updateCardTimeStamp(cardId, System.currentTimeMillis())
        todoDao.addTodo(TodoEntity.from(todo))
    }

    suspend fun setIsCompleted(
        cardId: UUID,
        todoId: UUID,
        isCompleted: Boolean
    ) {
        cardDao.updateCardTimeStamp(cardId, System.currentTimeMillis())
        todoDao.updateTodoIsCompleted(todoId, isCompleted)
    }

    suspend fun getTodoTitle(
        todoId: UUID
    ): String = todoDao.getTodoTitle(todoId)

    suspend fun deleteTodo(
        cardId: UUID,
        todoId: UUID
    ) {
        cardDao.updateCardTimeStamp(cardId, System.currentTimeMillis())
        todoDao.deleteTodo(todoId)
    }
}