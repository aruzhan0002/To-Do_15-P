package com.example.myapplication.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.MainActivity
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskCard(
    task: MainActivity.Task,
    selectedDate: LocalDate,
    index: Int,
    onCheck: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(
                if (task.isDone) Color(0xFF1B5E20) else Color(0xFF2C2C2E),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = when (task.priority) {
                        MainActivity.Priority.HIGH -> Color.Red
                        MainActivity.Priority.MEDIUM -> Color(0xFFFFA726)
                        MainActivity.Priority.LOW -> Color(0xFF81C784)
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        task.priority.name,
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Checkbox(
                    checked = task.isDone,
                    onCheckedChange = onCheck
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(task.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(task.description, color = Color.LightGray, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("${task.startTime} - ${task.endTime}", color = Color.Gray, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "Due Date: ${selectedDate.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${selectedDate.dayOfMonth}",
                color = Color.White,
                fontSize = 13.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onEdit) { Text("Edit") }
                TextButton(onClick = onDelete) { Text("Delete") }
            }
        }
    }
}
