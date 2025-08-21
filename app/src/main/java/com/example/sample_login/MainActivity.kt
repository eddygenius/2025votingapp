package com.example.sample_login

import CandidateResult
import VotingResultsScreen
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.sample_login.ui.theme.Sample_loginTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Sample_loginTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") { SplashScreen(navController) }

        composable("login") {
            LoginScreen(
                onCreateAccountClick = { navController.navigate("signup") },
                onLoginSuccess = { navController.navigate("selectElection") }
            )
        }

        composable("signup") {
            SignUpScreen(
                onBack = { navController.popBackStack() },
                onSubmit = { navController.popBackStack() }
            )
        }

        composable("selectCandidate") {
            SelectPresidentialCandidateScreen(
                onConfirm = { name, party, imageRes ->
                    navController.navigate("selectedCandidate/$name/$party/$imageRes")
                }
            )
        }

        composable("selectElection") {
            SelectElectionScreen(
                onConfirm = { category ->
                    if (category == "Presidential Election") {
                        navController.navigate("presidentialCandidates")
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("presidentialCandidates") {
            SelectPresidentialCandidateScreen { name, party, imageRes ->
                navController.navigate("selectedCandidate/$name/$party/$imageRes")
            }
        }

        composable(
            route = "selectedCandidate/{name}/{party}/{imageRes}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("party") { type = NavType.StringType },
                navArgument("imageRes") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val party = backStackEntry.arguments?.getString("party") ?: ""
            val imageRes = backStackEntry.arguments?.getInt("imageRes") ?: 0

            SelectedCandidateScreen(
                name = name,
                party = party,
                imageResId = imageRes,
                onVote = { },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun SplashScreen(navController: NavHostController) {
    var startAnim by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0.5f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = OvershootInterpolator(2f).toEasing()
        ),
        label = "scaleAnim"
    )

    val alpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(durationMillis = 1500),
        label = "alphaAnim"
    )

    LaunchedEffect(Unit) {
        startAnim = true
        delay(2500)
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val image: Painter = painterResource(id = R.drawable.ballot_box)
            Image(
                painter = image,
                contentDescription = "Ballot Box",
                modifier = Modifier
                    .size(200.dp)
                    .scale(scale)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "2025 ELECTION",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.alpha(alpha)
            )
        }
    }
}

fun OvershootInterpolator.toEasing(): Easing = Easing { x -> getInterpolation(x) }

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onCreateAccountClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var isLoginSuccessful by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ballot_box),
            contentDescription = "Ballot Box",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Log in", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.Black)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Email") },
            placeholder = { Text("Enter email") },
            shape = RoundedCornerShape(6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00BA54),
                unfocusedBorderColor = Color(0xFF00BA54),
                cursorColor = Color(0xFF00BA54)
            ),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            placeholder = { Text("Enter password") },
            shape = RoundedCornerShape(6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00BA54),
                unfocusedBorderColor = Color(0xFF00BA54),
                cursorColor = Color(0xFF00BA54)
            ),
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                isLoginSuccessful = (username == "admin" && password == "1234")
                showDialog = true
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BA54)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Login", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = buildAnnotatedString {
                append("Don't have an account? ")
                withStyle(
                    style = SpanStyle(color = Color.Red, fontWeight = FontWeight.Bold)
                ) { append("Create one") }
            },
            fontSize = 18.sp,
            modifier = Modifier.clickable { onCreateAccountClick() }
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    if (isLoginSuccessful) {
                        onLoginSuccess()
                    }
                }) {
                    Text("OK")
                }
            },
            title = {
                Text(if (isLoginSuccessful) "Login Successful" else "Invalid Credentials")
            },
            text = {
                Text(
                    if (isLoginSuccessful) "Welcome back!"
                    else "The username or password you entered is incorrect."
                )
            }
        )
    }
}

@Preview(showBackground = true, name = "Login Preview")
@Composable
fun LoginPreview() {
    Sample_loginTheme {
        LoginScreen(onCreateAccountClick = {}, onLoginSuccess = {})
    }
}

@Preview(showBackground = true, name = "Sign Up Preview")
@Composable
fun SignUpPreview() {
    Sample_loginTheme {
        SignUpScreen(onBack = {}, onSubmit = {})
    }
}

@Preview(showBackground = true, name = "Select Election Preview")
@Composable
fun SelectElectionPreview() {
    Sample_loginTheme {
        SelectElectionScreen(onConfirm = { }, onBack = { })
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewElectionScreen() {
    MaterialTheme {
        SelectPresidentialCandidateScreen(
            onConfirm = { _, _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSelectedCandidateScreen() {
    SelectedCandidateScreen(
        name = "",
        party = "",
        imageResId = android.R.drawable.sym_def_app_icon,
        onVote = {},
        onBack = {}
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VotingResultsPreview() {
    val results = listOf(
        CandidateResult("Peter Obi", "Labour Party", 1200),
        CandidateResult("Bola Tinubu", "APC", 950),
        CandidateResult("Atiku Abubakar", "PDP", 700)
    )

    Sample_loginTheme {
        VotingResultsScreen(results = results)
    }
}
