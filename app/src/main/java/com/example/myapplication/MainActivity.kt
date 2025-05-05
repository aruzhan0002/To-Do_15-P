package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.DataStoreHelper
import com.example.myapplication.ui.theme.TaskCard
import com.example.myapplication.ui.theme.TaskDialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


class MainActivity : ComponentActivity() {

    enum class Priority { LOW, MEDIUM, HIGH }

    data class Task(
        var title: String,
        var description: String = "",
        var isDone: Boolean = false,
        var priority: Priority = Priority.LOW,
        var startTime: String = "10:00 AM",
        var endTime: String = "06:00 PM"
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var showStartScreen by remember { mutableStateOf(true) }

            MaterialTheme(colorScheme = darkColorScheme()) {
                if (showStartScreen) {
                    StartScreen(onStartClicked = { showStartScreen = false })
                } else {
                    TaskPlannerApp()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun TaskPlannerApp() {
        val context = LocalContext.current
        val today = remember { LocalDate.now() }
        var selectedDate by remember { mutableStateOf(today) }
        var taskMap by remember { mutableStateOf(mutableMapOf<LocalDate, MutableList<Task>>()) }
        var showDialog by remember { mutableStateOf(false) }
        var newTaskTitle by remember { mutableStateOf("") }
        var newTaskDescription by remember { mutableStateOf("") }
        var newPriority by remember { mutableStateOf(Priority.LOW) }
        var newStartTime by remember { mutableStateOf("10:00 AM") }
        var newEndTime by remember { mutableStateOf("06:00 PM") }
        var editIndex by remember { mutableStateOf(-1) }

        LaunchedEffect(Unit) {
            taskMap = DataStoreHelper.loadTasks(context)
        }

        LaunchedEffect(taskMap) {
            DataStoreHelper.saveTasks(context, taskMap)
        }

        val tasks = taskMap[selectedDate] ?: mutableListOf()
        val (doneTasks, todoTasks) = tasks.partition { it.isDone }

        Scaffold(
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            newTaskTitle = ""
                            newTaskDescription = ""
                            newPriority = Priority.LOW
                            newStartTime = "10:00 AM"
                            newEndTime = "06:00 PM"
                            editIndex = -1
                            showDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("CREATE TASK", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            },
            containerColor = Color(0xFF121212)
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                HorizontalCalendar(selectedDate) { selectedDate = it }
                Spacer(modifier = Modifier.height(16.dp))
                Text("To-Do", style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White))
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(todoTasks) { index, task ->
                        TaskCard(task, selectedDate, tasks.indexOf(task),
                            onCheck = { isChecked ->
                                taskMap = taskMap.toMutableMap().apply {
                                    val updated = getOrPut(selectedDate) { mutableListOf() }.toMutableList()
                                    updated[tasks.indexOf(task)] = safeTaskCopy(task, isChecked)
                                    this[selectedDate] = updated
                                }
                            },
                            onEdit = {
                                newTaskTitle = task.title
                                newTaskDescription = task.description
                                newPriority = task.priority
                                newStartTime = task.startTime
                                newEndTime = task.endTime
                                editIndex = tasks.indexOf(task)
                                showDialog = true
                            },
                            onDelete = {
                                taskMap = taskMap.toMutableMap().apply {
                                    val updated = getOrPut(selectedDate) { mutableListOf() }.toMutableList()
                                    updated.removeAt(tasks.indexOf(task))
                                    this[selectedDate] = updated
                                }
                            }
                        )
                    }

                    if (doneTasks.isNotEmpty()) {
                        item {
                            Text("Done", color = Color.Green, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        itemsIndexed(doneTasks) { index, task ->
                            TaskCard(task, selectedDate, tasks.indexOf(task),
                                onCheck = { isChecked ->
                                    taskMap = taskMap.toMutableMap().apply {
                                        val updated = getOrPut(selectedDate) { mutableListOf() }.toMutableList()
                                        updated[tasks.indexOf(task)] = safeTaskCopy(task, isChecked)
                                        this[selectedDate] = updated
                                    }
                                },
                                onEdit = {
                                    newTaskTitle = task.title
                                    newTaskDescription = task.description
                                    newPriority = task.priority
                                    newStartTime = task.startTime
                                    newEndTime = task.endTime
                                    editIndex = tasks.indexOf(task)
                                    showDialog = true
                                },
                                onDelete = {
                                    taskMap = taskMap.toMutableMap().apply {
                                        val updated = getOrPut(selectedDate) { mutableListOf() }.toMutableList()
                                        updated.removeAt(tasks.indexOf(task))
                                        this[selectedDate] = updated
                                    }
                                }
                            )
                        }
                    }
                }

                TaskDialog(
                    showDialog = showDialog,
                    onDismiss = { showDialog = false },
                    taskTitle = newTaskTitle,
                    onTitleChange = { newTaskTitle = it },
                    taskDescription = newTaskDescription,
                    onDescriptionChange = { newTaskDescription = it },
                    priority = newPriority,
                    onPriorityChange = { newPriority = it },
                    startTime = newStartTime,
                    onStartTimeChange = { newStartTime = it },
                    endTime = newEndTime,
                    onEndTimeChange = { newEndTime = it },
                    onSave = {
                        if (newTaskTitle.isNotBlank()) {
                            taskMap = taskMap.toMutableMap().apply {
                                val updatedList = getOrPut(selectedDate) { mutableListOf() }.toMutableList()
                                if (editIndex == -1) {
                                    updatedList.add(Task(newTaskTitle, newTaskDescription, false, newPriority, newStartTime, newEndTime))
                                } else if (editIndex in updatedList.indices) {
                                    updatedList[editIndex] = Task(newTaskTitle, newTaskDescription, updatedList[editIndex].isDone, newPriority, newStartTime, newEndTime)
                                }
                                this[selectedDate] = updatedList
                            }
                            showDialog = false
                        } else {
                            Toast.makeText(context, "Please enter task title", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }

    fun safeTaskCopy(task: Task, isDone: Boolean): Task =
        Task(
            title = task.title ?: "",
            description = task.description ?: "",
            isDone = isDone,
            priority = task.priority ?: Priority.LOW,
            startTime = task.startTime?.takeIf { it.isNotBlank() } ?: "10:00 AM",
            endTime = task.endTime?.takeIf { it.isNotBlank() } ?: "06:00 PM"
        )

}





    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun HorizontalCalendar(selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
        val dates = (0..30).map { LocalDate.now().minusDays(15).plusDays(it.toLong()) }
        val dayFormatter = DateTimeFormatter.ofPattern("dd")
        val monthFormatter = DateTimeFormatter.ofPattern("MMMM, yyyy", Locale.ENGLISH)
        val dayNameFormatter = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH)

        val currentMonth = monthFormatter.format(selectedDate)

        Column {
            Text(
                text = currentMonth,
                style = TextStyle(color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(dates) { _, date ->
                    val isSelected = date == selectedDate
                    val bgColor = if (isSelected) Color.White else Color.DarkGray
                    val textColor = if (isSelected) Color.Black else Color.White

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(4.dp)
                            .width(60.dp)
                            .clickable { onDateSelected(date) }
                    ) {
                        Text(
                            text = dayNameFormatter.format(date),
                            style = TextStyle(color = Color.Gray, fontSize = 12.sp)
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .background(color = bgColor, shape = RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = dayFormatter.format(date),
                                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
                            )
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun StartScreen(onStartClicked: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.start_1),
                    contentDescription = "Start Illustration",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Task Management &\nTo-Do List",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "This productive tool is designed to help\nyou better manage your task\nproject-wise conveniently!",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onStartClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Let's Start", color = Color.White)


                }
            }
        }
    }



