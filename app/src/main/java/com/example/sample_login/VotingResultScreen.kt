import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.ui.tooling.preview.Preview

data class CandidateResult(
    val name: String,
    val party: String,
    val votes: Int
)

@Composable
fun VotingResultsScreen(
    results: List<CandidateResult>,
    onSignOut: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Voting Results",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = { onSignOut() }
            ) {
                Icon(
                    imageVector = Icons.Filled.ExitToApp,
                    contentDescription = "Sign Out",
                    tint = Color.Red
                )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Overall Vote Count",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        val totalVotes = results.sumOf { it.votes }
        Text(
            text = "Total Votes: $totalVotes",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Candidate Cards
        results.forEach { candidate ->
            CandidateResultCard(
                name = candidate.name,
                votes = candidate.votes,
                delta = 0
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Vote Distribution",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Number of Votes",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Centralized Pie Chart
        PieChart(results)
    }
}

@Composable
fun CandidateResultCard(name: String, votes: Int, delta: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00C853)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = name, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = votes.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (delta > 0) "+$delta" else delta.toString(),
                color = if (delta > 0) Color.Green else Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun PieChart(results: List<CandidateResult>) {
    val totalVotes = results.sumOf { it.votes }.coerceAtLeast(1)
    val colors = listOf(Color(0xFF2196F3), Color(0xFFFF0000), Color(0xFF4CAF50))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Centered Pie Chart
        Canvas(
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp)
        ) {
            var startAngle = -90f
            results.forEachIndexed { index, candidate ->
                val sweepAngle = (candidate.votes.toFloat() / totalVotes.toFloat()) * 360f
                drawArc(
                    color = colors.getOrElse(index) { Color.Gray },
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true
                )
                startAngle += sweepAngle
            }
        }

        // Legend
        Column(modifier = Modifier.padding(top = 16.dp)) {
            results.forEachIndexed { index, candidate ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(colors.getOrElse(index) { Color.Gray })
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "${candidate.name} (${candidate.votes})")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VotingResultsScreenPreview() {
    val sampleResults = remember {
        mutableStateListOf(
            CandidateResult("Peter Obi", "Labour Party", 1200),
            CandidateResult("Bola Tinubu", "APC", 800),
            CandidateResult("Atiku Abubakar", "PDP", 400)
        )
    }

    MaterialTheme {
        VotingResultsScreen(results = sampleResults, onSignOut = {
            println("Signed Out!") // test callback
        })
    }
}
