package se.millwood.todo.card

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import se.millwood.todo.R
import se.millwood.todo.databinding.FragmentCardBinding

@AndroidEntryPoint
class CardFragment : Fragment() {

    private val viewModel: CardViewModel by viewModels()

    private lateinit var binding: FragmentCardBinding

    private val adapter: TodoAdapter by lazy {
        TodoAdapter(
            onItemCheck = viewModel::setIsCompleted,
            onItemDelete = { todoId, title ->
                val bundle = bundleOf(
                    TODO_DELETE_ARGUMENTS to TodoDeleteArguments(
                        cardId = viewModel.cardId.toString(),
                        todoId = todoId.toString(),
                        title = title
                    )
                )
                findNavController().navigate(R.id.todoDeleteDialogFragment, bundle)
            },
            onItemEdit = { todoId ->
                val bundle = bundleOf(
                    TODO_EDIT_ARGUMENTS to TodoEditArguments(
                        cardId = viewModel.cardId.toString(),
                        todoId = todoId.toString()
                    )
                )
                findNavController().navigate(R.id.todoEditDialogFragment, bundle)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCardBinding.inflate(inflater)
        binding.recyclerView.adapter = adapter
        getCardAndTodos()
        setupCreateTodoFab(viewModel.cardId)
        setupImagePickerButton(viewModel.cardId)
        setupUpButton()
        return binding.root
    }

    private fun setupImagePickerButton(cardId: String?) {
        if (cardId == null) return
        binding.cardImage.setOnClickListener {
            val bundle = bundleOf(
                IMAGE_PICKER_ARGUMENTS to ImagePickerArguments(
                    cardId = cardId
                )
            )
            findNavController().navigate(R.id.imagePickerFragment, bundle)
        }
    }

    private fun getCardAndTodos() {
        lifecycleScope.launch {
            binding.cardTitle.setText(viewModel.getCardTitle())
            binding.cardTitle.doOnTextChanged { title, _, _, _ ->
                viewModel.updateCardTitle(title.toString())
            }
            viewModel.getTodos().collect { todos ->
                adapter.submitList(todos)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    /*
    private fun loadCardImage() {
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.image
                .collectLatest { imageUri ->
                    Coil.execute(
                        ImageRequest.Builder(requireContext())
                            .data(imageUri)
                            .target(binding.image)
                            .build()
                    )
                }
        }
    }*/

    private fun setupCreateTodoFab(cardId: String?) {
        if (cardId == null) return
        binding.fab.setOnClickListener {
            val bundle = bundleOf(
                TODO_EDIT_ARGUMENTS to TodoEditArguments(
                    cardId = cardId,
                    todoId = null
                )
            )
            findNavController().navigate(R.id.todoEditDialogFragment, bundle)
        }
    }

    private fun setupUpButton() {
        binding.listFragmentToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    companion object {
        const val TODO_DELETE_ARGUMENTS = "todo_delete_args"
        const val TODO_EDIT_ARGUMENTS = "todo_edit_args"
        const val IMAGE_PICKER_ARGUMENTS = "image_picker_args"
    }

    @Parcelize
    data class TodoDeleteArguments(
        val cardId: String,
        val todoId: String,
        val title: String
    ) : Parcelable

    @Parcelize
    data class TodoEditArguments(
        val cardId: String,
        val todoId: String?
    ) : Parcelable

    @Parcelize
    data class ImagePickerArguments(
        val cardId: String,
    ) : Parcelable
}
