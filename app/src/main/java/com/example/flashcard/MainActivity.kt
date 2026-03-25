package com.example.flashcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
// Compose UI imports
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.flashcard.ui.theme.FlashCardTheme

// Main entry point of the app
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashCardTheme {
                val navController = rememberNavController()

                // Navigation setup
                NavHost(navController = navController, startDestination = "subjects") {

                    // Subjects screen
                    composable("subjects") {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            FlashCardApp(
                                navController,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }

                    // Cards screen for selected subject
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
    val storage = remember { SubjectStorage(context) } // handles saving/loading subjects

    // Load subjects into state
    val subjects = remember {
        mutableStateListOf<String>().apply {
            addAll(storage.loadSubjects())
        }
    }

    // Save subjects if list is not empty
    if (subjects.isNotEmpty()) {
        storage.saveSubjects(subjects)
    }

    var showAddDialog by remember { mutableStateOf(false) }

    // Show add subject dialog
    if (showAddDialog) {
        AddSubjectDialog(
            onAdd = {
                subjects.add(it)
                storage.saveSubjects(subjects)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    // Main layout
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0, 0, 82))
    ) {
        Header()

        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                // Subject list
                BuildSubjectBody(
                    subjects = subjects,
                    onDelete = {
                        subjects.remove(it)
                        storage.saveSubjects(subjects)
                    },
                    navController = navController
                )
            }

            // Add subject button
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Text("+", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FlashCardPreview() {
    FlashCardTheme {
        val navController = rememberNavController()

        // Preview navigation
        NavHost(navController = navController, startDestination = "cards/{subject}") {

            composable("subjects") {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FlashCardApp(navController, Modifier.padding(innerPadding))
                }
            }

            composable("cards/{subject}") {
                FlashCardScreen(navController, "Math") // sample preview
            }
        }
    }
}

// App header
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
            fontWeight = FontWeight.Bold
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
    // Title
    Text(
        text = "Subjects",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(20.dp)
    )

    // Empty state
    if (subjects.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Please a subject you would like to study.",
                fontSize = 25.sp,
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        }
    } else {
        // Subject list
        LazyColumn(
            modifier = Modifier.padding(30.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(subjects) { subject ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Open subject
                    Button(
                        onClick = { navController.navigate("cards/$subject") },
                        modifier = modifier.width(250.dp)
                    ) {
                        Text(subject, fontSize = 20.sp, color = Color.White)
                    }

                    // Delete subject
                    FloatingActionButton(
                        onClick = { onDelete(subject) },
                        containerColor = Color.Transparent,
                        modifier = modifier.height(50.dp)
                    ) {
                        Text("🗑️", fontSize = 25.sp)
                    }
                }
            }
        }
    }
}

// Dialog to add subject
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
            Button(onClick = {
                if (text.isNotBlank()) onAdd(text)
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// Screen showing flashcards for a subject
@Composable
fun FlashCardScreen(
    navController: NavController,
    subject: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val storage = remember { CardStorage(context) } // handles card storage

    // Load cards
    val cards = remember {
        mutableStateListOf<Pair<String, String>>().apply {
            addAll(storage.loadCards(subject))
        }
    }

    var showDialog by remember { mutableStateOf(false) }

    // Add card dialog
    if (showDialog) {
        AddCardDialog(
            onAdd = { q, a ->
                cards.add(q to a)
                storage.saveCards(subject, cards)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }

    Scaffold {
            innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0, 0, 82))
                .padding(innerPadding)
        ) {
            Header()

            Box(modifier = Modifier.fillMaxSize()) {

                Column {
                    // Top row (back + title)
                    Row(
                        modifier = Modifier.padding(10.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        FloatingActionButton(
                            onClick = { navController.navigate("subjects") },
                            modifier = Modifier.size(width = 85.dp, height = 50.dp)
                        ) {
                            Text("Back", fontSize = 20.sp, color = Color.White)
                        }

                        Text(subject, fontSize = 30.sp, color = Color.White)
                    }

                    // Empty state
                    if (cards.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = "No cards yet. Add one!",
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    } else {
                        // Card list
                        LazyColumn(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(cards) { (q, a) ->
                                FlashCardItem(
                                    question = q,
                                    answer = a,
                                    onDelete = {
                                        cards.remove(q to a)
                                        storage.saveCards(subject, cards)
                                    }
                                )
                            }
                        }
                    }
                }

                // Add card button
                FloatingActionButton(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .padding(20.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Text("+", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Single flashcard item
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
    ) {
        // Toggle question/answer
        Button(
            onClick = { showAnswer = !showAnswer },
            modifier = Modifier.width(250.dp)
        ) {
            Text(
                text = if (showAnswer) answer else question,
                fontSize = 15.sp,
                color = Color.White
            )
        }

        // Delete card
        FloatingActionButton(
            onClick = onDelete,
            containerColor = Color.Transparent,
            modifier = Modifier.height(50.dp)
        ) {
            Text("🗑️", fontSize = 25.sp)
        }
    }
}

// Dialog to add card
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
                TextField(question, { question = it }, placeholder = { Text("Question") })
                Spacer(modifier = Modifier.height(10.dp))
                TextField(answer, { answer = it }, placeholder = { Text("Answer") })
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
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    )
}