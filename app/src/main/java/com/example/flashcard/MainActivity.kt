package com.example.flashcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.flashcard.ui.theme.FlashCardTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashCardTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "subjects") {

                    composable("subjects") {
                        Scaffold(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            innerPadding -> FlashCardApp(
                            navController,
                            modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }

                    composable("cards/{subject}") { backStackEntry ->
                        val subject = backStackEntry.arguments?.getString("subject") ?: ""
                        FlashCardScreen(navController, subject)
                    }
                }
            }
        }
    }
}

@Composable
fun FlashCardApp(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val storage = remember { SubjectStorage(context) }

    val subjects = remember {
        mutableStateListOf<String>().apply {
            addAll(storage.loadSubjects())
        }
    }

    if (subjects.isEmpty()) {
        println("")
    } else {
        storage.saveSubjects(subjects)
    }

    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddSubjectDialog(
            onAdd = { newSubject ->
                subjects.add(newSubject)
                storage.saveSubjects(subjects)
                showAddDialog = false
            },
            onDismiss = {
                showAddDialog = false
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0, 0, 82))
    ) {
        Header()
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                BuildSubjectBody(
                    subjects = subjects,
                    onDelete = { subject ->
                        subjects.remove(subject)
                        storage.saveSubjects(subjects)
                    },
                    navController = navController
                )
            }


            FloatingActionButton(
                onClick = {
                    showAddDialog = true },
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Text(
                    text = "+",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(255, 255, 255, 255),
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun FlashCardPreview() {
    FlashCardTheme {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "cards/{subject}") {

            composable("subjects") {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) {
                        innerPadding -> FlashCardApp(
                    navController,
                    modifier = Modifier.padding(innerPadding)
                )
                }
            }

            composable("cards/{subject}") { backStackEntry ->
                val subject = backStackEntry.arguments?.getString("subject") ?: ""
                FlashCardScreen(navController, "Math")
            }
        }
    }
}

@Composable
fun Header() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Blue)
            .padding(16.dp)
    ) {
        Text(
            text = "Flash Card",
            color = Color.White,
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.TopStart)
        )
    }
}

@Composable
fun BuildSubjectBody(
    modifier: Modifier = Modifier,
    subjects: List<String>,
    onDelete: (String) -> Unit,
    navController: NavController
) {
    Text(
        text = "Subjects",
        textAlign = TextAlign.Start,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 10.dp)
    )
    if (subjects.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = "Please a subject you would like to study.",
                fontSize = 25.sp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(30.dp),
                textAlign = TextAlign.Center,
                color = Color(182, 182, 182, 198)
            )
        }


    }
    else {
        LazyColumn (
            modifier = Modifier.padding(30.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ){
            items(subjects) { subject->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Button(
                        onClick = { navController.navigate("cards/$subject") },
                        modifier = modifier
                            .width(250.dp)
                    ) {
                        Text(
                            text = subject,
                            fontSize = 20.sp,
                            color = Color(255, 255, 255, 255)
                        )
                    }
                    FloatingActionButton(
                        onClick = { onDelete(subject) },
                        containerColor = Color(255, 255, 255, 0),
                        modifier = modifier
                            .height(50.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        Text(
                            text = "🗑️",
                            fontSize = 25.sp
                        )
                    }
                }


            }
        }
    }

}

@Composable
fun AddSubjectDialog(
    onAdd: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Subject") },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Enter subject name") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        onAdd(text)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun FlashCardScreen(
    navController: NavController,
    subject: String,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val storage = remember { CardStorage(context) }

    val cards = remember {
        mutableStateListOf<Pair<String, String>>().apply {
            addAll(storage.loadCards(subject))
        }
    }

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AddCardDialog(
            onAdd = { question, answer ->
                cards.add(question to answer)
                storage.saveCards(subject, cards)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        innerPadding -> Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0, 0, 82))
            .padding(innerPadding)
        ) {
            Header()

            Box(modifier = Modifier.fillMaxSize()) {

                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        FloatingActionButton(
                            onClick = { navController.navigate("subjects") },
                            modifier = Modifier
                                .height(50.dp)
                                .width(85.dp)
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(
                                text = "Back",
                                fontSize = 30.sp,
                                color = Color(255, 255, 255, 255),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                            )
                        }

                        Text(
                            text = subject,
                            fontSize = 30.sp,
                            color = Color.White,
                            modifier = Modifier
                                .padding(20.dp)

                        )

                    }

                    if (cards.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = "No cards yet. Add one!",
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(cards) { (question, answer) ->
                                FlashCardItem(
                                    question = question,
                                    answer = answer,
                                    onDelete = {
                                        cards.remove(question to answer)
                                        storage.saveCards(subject, cards)
                                    }
                                )
                            }
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .padding(20.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Text(
                        text = "+",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun FlashCardItem(
    question: String,
    answer: String,
    onDelete: () -> Unit
) {

    var showAnswer by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Button(
            onClick = { showAnswer = !showAnswer  },
            modifier = Modifier
                .width(250.dp)
        ) {
            Text(
                text = if (showAnswer) answer else question,
                fontSize = 15.sp,
                color = Color(255, 255, 255, 255)
            )
        }
        FloatingActionButton(
            onClick = { onDelete() },
            containerColor = Color(255, 255, 255, 0),
            modifier = Modifier
                .height(50.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = "🗑️",
                fontSize = 25.sp
            )
        }
    }
}

@Composable
fun AddCardDialog(
    onAdd: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var question by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Card") },
        text = {
            Column {
                TextField(
                    value = question,
                    onValueChange = { question = it },
                    placeholder = { Text("Question") }
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    value = answer,
                    onValueChange = { answer = it },
                    placeholder = { Text("Answer") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (question.isNotBlank() && answer.isNotBlank()) {
                    onAdd(question, answer)
                }
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
