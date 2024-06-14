package com.example.travelpic.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.travelpic.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun Screen3fix() {
    var showNoteDialog by remember { mutableStateOf(false) }
    val notes = remember { mutableStateListOf<String>() }
    val database = Firebase.database.reference

    LaunchedEffect(Unit) {
        database.child("notes").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notes.clear()
                for (noteSnapshot in snapshot.children) {
                    val note = noteSnapshot.getValue(String::class.java)
                    if (note != null) {
                        notes.add(note)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    if (showNoteDialog) {
//        NoteDialog(onDismiss = { showNoteDialog = false }, onSave = { note ->
//            database.child("notes").push().setValue(note)
//            notes.add(note)
//        })
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = { showNoteDialog = true }) {
                Icon(
                    imageVector = Icons.Filled.ContentPaste,
                    contentDescription = "Note"
                )
            }
            IconButton(modifier = Modifier.padding(horizontal = 100.dp),
                onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Like"
                )
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Filled.Download,
                    contentDescription = "Download"
                )
            }
        }
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            notes.forEach { note ->
                NoteItem(note = note, onDelete = {
                    val noteKey = note // Assuming note content is unique for simplicity
                    database.child("notes").orderByValue().equalTo(noteKey).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (childSnapshot in snapshot.children) {
                                childSnapshot.ref.removeValue()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
                    notes.remove(note)
                })
            }
        }
    }
}

@Composable
fun NoteItem(note: String, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = note, modifier = Modifier.weight(1f))
        IconButton(onClick = onDelete) {
            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
        }
    }
}

@Composable
fun NoteDialog(memo:String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var noteText by remember { mutableStateOf("") }
    noteText = memo
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(Color(0xAA000000))
                    .padding(16.dp)
            ) {
                TextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("메모 내용을 적어주세요.") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (noteText.isNotEmpty()) {
                            onSave(noteText)
                            onDismiss()
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("저장")
                }
            }
        }
    }
}
