package com.kamatiaakash.simpletodoapp.screens.todo_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamatiaakash.simpletodoapp.data.Todo
import com.kamatiaakash.simpletodoapp.data.TodoRepo
import com.kamatiaakash.simpletodoapp.util.Routes
import com.kamatiaakash.simpletodoapp.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoLIstViewModel @Inject constructor(private val repo:TodoRepo) :ViewModel() {

    val todos = repo.getTodos()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var deletedTodo: Todo? = null

    fun onEvent(event: TodoListEvent){
        when(event){
            is TodoListEvent.OnTodoClick ->{
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_TODO + "?todoId=${event.todo.id}"))
            }
            is TodoListEvent.OnAddTodoClick ->{
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_TODO))
            }
            is TodoListEvent.OnDeleteTodoClick ->{
                viewModelScope.launch {
                    deletedTodo = event.todo
                    repo.deleteTodo(event.todo)
                    sendUiEvent(UiEvent.ShowSnackBar(
                        message = "Todo deleted",
                        action = "Undo"
                    ))
                }
            }
            is TodoListEvent.OnUndoDeleteClick ->{
                deletedTodo?.let {todo ->
                    viewModelScope.launch {
                        repo.insertTodo(todo)
                    }
                }
            }
            is TodoListEvent.OnDoneChange ->{
                viewModelScope.launch {
                    repo.insertTodo(event.todo.copy(
                        isDone = event.isDone
                    ))
                }
            }
        }
    }

    private fun sendUiEvent(event: UiEvent){
        viewModelScope.launch {
            _uiEvent.send(event)

        }
    }
}