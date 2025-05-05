package com.example.myapplication.ui.theme

import com.example.myapplication.MainActivity



import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TaskDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    taskTitle: String,
    onTitleChange: (String) -> Unit,
    taskDescription: String,
    onDescriptionChange: (String) -> Unit,
    priority: MainActivity.Priority,
    onPriorityChange: (MainActivity.Priority) -> Unit,
    startTime: String,
    onStartTimeChange: (String) -> Unit,
    endTime: String,
    onEndTimeChange: (String) -> Unit,
    onSave: () -> Unit
) {
    if (!showDialog) return

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

    val startTimePicker = TimePickerDialog(
        context,
        { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            onStartTimeChange(timeFormatter.format(calendar.time))
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    )

    val endTimePicker = TimePickerDialog(
        context,
        { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            onEndTimeChange(timeFormatter.format(calendar.time))
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (taskTitle.isBlank()) "Add new to-do" else "Edit to-do") },
        text = {
            Column {
                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = onTitleChange,
                    label = { Text("Task title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = taskDescription,
                    onValueChange = onDescriptionChange,
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Priority", color = Color.White)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    MainActivity.Priority.values().forEach { option ->
                        val selected = priority == option
                        Button(
                            onClick = { onPriorityChange(option) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) Color(0xFFB39DDB) else Color.DarkGray
                            )
                        ) {
                            Text(option.name)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Start Time", color = Color.White)
                Button(onClick = { startTimePicker.show() }, modifier = Modifier.fillMaxWidth()) {
                    Text(startTime, color = Color.White)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("End Time", color = Color.White)
                Button(onClick = { endTimePicker.show() }, modifier = Modifier.fillMaxWidth()) {
                    Text(endTime, color = Color.White)
                }
            }
        },
        confirmButton = {
            Button(onClick = onSave) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}