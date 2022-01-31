package se.millwood.todo.todolist

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import se.millwood.todo.cardlist.CardListFragment
import se.millwood.todo.databinding.*
import java.util.*

class TodoEditDialogFragment : DialogFragment() {

    private val viewModel: TodoViewModel by activityViewModels()  {
        TodoViewModelFactory(requireContext().applicationContext)
    }
    private lateinit var binding: FragmentEditDialogBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditDialogBinding.inflate(inflater, container, false)

        if (arguments?.containsKey(TODO_ID_KEY) == true) {
            useEditTodoDialog()
        }
        else {
            useCreateTodoDialog()
        }
        return binding.root
    }

    private fun useEditTodoDialog() {
        val todoId = arguments?.getString(TODO_ID_KEY)
        lifecycleScope.launch {
            val todo = viewModel.fetchTodo(UUID.fromString(todoId))
            binding.editTodo.setText(todo.title)
            binding.saveButton.setOnClickListener {
                viewModel.updateTodo(
                    todo.copy(
                        title = binding.editTodo.text.toString(),
                    )
                )
                dismiss()
            }
        }
    }

    private fun useCreateTodoDialog() {
        val cardId = arguments?.getString(CardListFragment.CARD_ID_KEY)
        val cardUUID = UUID.fromString(cardId)
        binding.saveButton.setOnClickListener {
            viewModel.createTodo(
                binding.editTodo.text.toString(),
                cardUUID
            )
            dismiss()
        }
    }

    companion object {
        const val TODO_ID_KEY = "todo_id"
    }

}