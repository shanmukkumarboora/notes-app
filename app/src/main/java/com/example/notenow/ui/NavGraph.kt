package com.example.notenow.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.notenow.ui.note.AddEditNoteScreen
import com.example.notenow.ui.note.NoteScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "note_list") {
        composable("note_list") {
            NoteScreen(
                onNoteClick = { noteId ->
                    navController.navigate("add_edit_note?noteId=$noteId")
                },
                onAddNoteClick = {
                    navController.navigate("add_edit_note")
                }
            )
        }
        composable(
            route = "add_edit_note?noteId={noteId}",
            arguments = listOf(navArgument("noteId") {
                type = NavType.IntType
                defaultValue = -1
            })
        ) {
            AddEditNoteScreen(
                noteId = if (it.arguments?.getInt("noteId") == -1) null else it.arguments?.getInt("noteId"),
                onNavigateUp = { navController.popBackStack() }
            )
        }
    }
}