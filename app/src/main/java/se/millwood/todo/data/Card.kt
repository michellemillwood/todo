package se.millwood.todo.data

import java.util.*

data class Card(
    val title: String = "",
    val imageUrl: String? = null,
    val cardId: UUID = UUID.randomUUID(),
    val timeStamp: Long = System.currentTimeMillis()
)