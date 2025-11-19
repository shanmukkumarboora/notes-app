package com.example.notenow.ui.note

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notenow.NoteApplication
import com.example.notenow.data.Note

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    noteViewModel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory((LocalContext.current.applicationContext as NoteApplication).repository)
    ),
    onNoteClick: (Int) -> Unit,
    onAddNoteClick: () -> Unit
) {
    val notes by noteViewModel.allNotes.collectAsState()
    var selectedNotes by remember { mutableStateOf(emptyList<Int>()) }
    val inSelectionMode = selectedNotes.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (inSelectionMode) "${selectedNotes.size} selected" else "NoteNow") },
                actions = {
                    if (inSelectionMode) {
                        IconButton(onClick = {
                            noteViewModel.deleteNotes(selectedNotes)
                            selectedNotes = emptyList()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Selected Notes")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (!inSelectionMode) {
                FloatingActionButton(onClick = onAddNoteClick) {
                    Icon(Icons.Default.Add, contentDescription = "Add Note")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(notes) { note ->
                NoteItem(
                    note = note,
                    inSelectionMode = inSelectionMode,
                    isSelected = selectedNotes.contains(note.id),
                    onClick = {
                        if (inSelectionMode) {
                            selectedNotes = if (selectedNotes.contains(note.id)) {
                                selectedNotes.filter { it != note.id }
                            } else {
                                selectedNotes + note.id
                            }
                        } else {
                            onNoteClick(note.id)
                        }
                    },
                    onLongClick = {
                        if (!inSelectionMode) {
                            selectedNotes = listOf(note.id)
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItem(
    note: Note,
    inSelectionMode: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (inSelectionMode) {
                Checkbox(checked = isSelected, onCheckedChange = { onClick() })
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (note.isEncrypted) {
                    Text(
                        text = "This note is encrypted",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else {
                    Text(
                        text = note.content,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}