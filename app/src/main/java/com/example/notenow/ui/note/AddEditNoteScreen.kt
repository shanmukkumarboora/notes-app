package com.example.notenow.ui.note

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notenow.NoteApplication
import com.example.notenow.data.Note

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    noteId: Int?,
    noteViewModel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory((LocalContext.current.applicationContext as NoteApplication).repository)
    ),
    onNavigateUp: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isEncrypted by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var showPasswordDialog by remember { mutableStateOf(noteId != null) }
    var note by remember { mutableStateOf<Note?>(null) }
    var passwordError by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    if (noteId != null) {
        val loadedNote by noteViewModel.getNoteById(noteId).collectAsState(initial = null)
        note = loadedNote
        if (note != null) {
            title = note!!.title
            content = note!!.content
            isEncrypted = note!!.isEncrypted
        }
    }

    if (showPasswordDialog && note?.isEncrypted == true) {
        PasswordDialog(
            onDismiss = { onNavigateUp() },
            onConfirm = { enteredPassword ->
                if (enteredPassword.hashCode() == note?.password) {
                    showPasswordDialog = false
                } else {
                    passwordError = true
                }
            },
            showError = passwordError
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (noteId == null) "Add Note" else "Edit Note") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateUp) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        val newNote = Note(
                            id = noteId ?: 0,
                            title = title,
                            content = content,
                            isEncrypted = isEncrypted,
                            password = if (isEncrypted) password.hashCode() else null
                        )
                        if (noteId == null) {
                            noteViewModel.insert(newNote)
                        } else {
                            noteViewModel.update(newNote)
                        }
                        onNavigateUp()
                    }
                ) {
                    Icon(Icons.Default.Done, contentDescription = "Save Note")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                BasicTextField(
                    value = title,
                    onValueChange = { title = it },
                    textStyle = TextStyle(color = Color.White, fontSize = MaterialTheme.typography.headlineSmall.fontSize),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        if (title.isEmpty()) {
                            Text(text = "Title", color = Color.Gray)
                        }
                        innerTextField()
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                BasicTextField(
                    value = content,
                    onValueChange = { content = it },
                    textStyle = TextStyle(color = Color.White, fontSize = MaterialTheme.typography.bodyLarge.fontSize),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    decorationBox = { innerTextField ->
                        if (content.isEmpty()) {
                            Text(text = "Content", color = Color.Gray)
                        }
                        innerTextField()
                    }
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Encrypt Note")
                    Switch(
                        checked = isEncrypted,
                        onCheckedChange = { isEncrypted = it }
                    )
                }
                if (isEncrypted) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BasicTextField(
                            value = password,
                            onValueChange = { password = it },
                            textStyle = TextStyle(color = Color.White),
                            modifier = Modifier.weight(1f),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            decorationBox = { innerTextField ->
                                if (password.isEmpty()) {
                                    Text(text = "Password", color = Color.Gray)
                                }
                                innerTextField()
                            }
                        )
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    showError: Boolean
) {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter Password") },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicTextField(
                        value = password,
                        onValueChange = { password = it },
                        textStyle = TextStyle(color = Color.White),
                        modifier = Modifier.weight(1f),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        decorationBox = { innerTextField ->
                            if (password.isEmpty()) {
                                Text(text = "Password", color = Color.Gray)
                            }
                            innerTextField()
                        }
                    )
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                }
                if (showError) {
                    Text("Incorrect password", color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(password) }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
