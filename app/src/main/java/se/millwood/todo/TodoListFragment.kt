package se.millwood.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import se.millwood.todo.databinding.FragmentTodoListBinding

class TodoListFragment : Fragment() {

    private val viewModel: TodoViewModel by activityViewModels()  {
        TodoViewModelFactory(requireContext().applicationContext)
    }

    private val adapter: TodoAdapter by lazy {
        TodoAdapter(
            onItemChecked = viewModel::toggleCheckbox,
            onItemRemoved = { todoId, title ->
                viewModel.removeTodo(todoId)
                Snackbar.make(
                    binding.root,
                    "todo $title removed",
                    Snackbar.LENGTH_SHORT
                ).show()
            },
            onItemDetails = { title, description ->
                val bundle = bundleOf(
                    TodoDetailsFragment.TITLE_KEY to title,
                    TodoDetailsFragment.DESCRIPTION_KEY to description
                )
                findNavController().navigate(R.id.todoDetailsFragment, bundle)
            })
    }

    private lateinit var binding: FragmentTodoListBinding


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            binding = FragmentTodoListBinding.inflate(inflater)
            binding.recyclerView.adapter = adapter
            setupCreateTodoFab()
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            lifecycleScope.launch {
                viewModel.todos.collect { todos ->
                    adapter.submitList(todos)
                }
            }
        }


        private fun setupCreateTodoFab() {
            binding.fab.setOnClickListener {
                findNavController().navigate(R.id.addTodoFragment)
            }
        }
    }