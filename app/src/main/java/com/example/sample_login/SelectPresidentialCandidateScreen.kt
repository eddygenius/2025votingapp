package com.example.sample_login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.layout.ContentScale

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SelectPresidentialCandidateScreen(
    onConfirm: (String, String, Int) -> Unit // name, party, imageRes
) {
    var selectedCandidate by remember { mutableStateOf<Candidate?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.Top

    ) {
        HeroSection(
            images = listOf(
                R.drawable.hero1,
                R.drawable.hero2,
                R.drawable.hero4,
                R.drawable.hero3
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = "Presidential Candidates",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        CandidateItem(
            name = "Peter Obi",
            party = "Labour Party",
            imageRes = R.drawable.peter_obi,
            selected = selectedCandidate?.name == "Peter Obi",
            onClick = { selectedCandidate = Candidate("Peter Obi", "Labour Party", R.drawable.peter_obi) }
        )

        CandidateItem(
            name = "Bola Ahmed Tinubu",
            party = "All Progressives Congress",
            imageRes = R.drawable.tinubu,
            selected = selectedCandidate?.name == "Bola Ahmed Tinubu",
            onClick = { selectedCandidate = Candidate("Bola Ahmed Tinubu", "All Progressives Congress", R.drawable.tinubu) }
        )

        CandidateItem(
            name = "Atiku Abubakar",
            party = "People's Democratic Party",
            imageRes = R.drawable.atiku,
            selected = selectedCandidate?.name == "Atiku Abubakar",
            onClick = { selectedCandidate = Candidate("Atiku Abubakar", "People's Democratic Party", R.drawable.atiku) }
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                selectedCandidate?.let {
                    onConfirm(it.name, it.party, it.imageRes)
                }
            },
            enabled = selectedCandidate != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00C853),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "CONFIRM", fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HeroSection(images: List<Int>) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-slide effect
    LaunchedEffect(pagerState.currentPage) {
        delay(3000) // Change image every 3 seconds
        val nextPage = (pagerState.currentPage + 1) % images.size
        coroutineScope.launch {
            pagerState.animateScrollToPage(nextPage)
        }
    }

    HorizontalPager(
        count = images.size,
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp) // slightly taller for better visuals
            .clip(RoundedCornerShape(12.dp))
    ) { page ->
        Image(
            painter = painterResource(id = images[page]),
            contentDescription = "Hero Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // 🔥 this makes it fill properly
        )
    }
}


@Composable
fun CandidateItem(
    name: String,
    party: String,
    imageRes: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = name,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
        )

        Column {
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (selected) Color(0xFF00C853) else Color.Black
            )
            Text(
                text = party,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

data class Candidate(val name: String, val party: String, val imageRes: Int)
