package com.example.features.learning

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.entities.LearningArticle
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun LearningScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val articlesList by viewModel.articles.collectAsState()

    var selectedCategorization by remember { mutableStateOf("All") }
    val categories = listOf("All", "Physical Recovery", "Mental Wellness", "Pediatric Guide")

    var selectedArticle by remember { mutableStateOf<LearningArticle?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredArticles = remember(articlesList, selectedCategorization, searchQuery) {
        articlesList.filter { article ->
            val matchesCategory = selectedCategorization == "All" || article.category == selectedCategorization
            val matchesSearch = article.title.contains(searchQuery, ignoreCase = true) ||
                    article.snippet.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
    ) {
        SoftTopBar(
            title = "Maternal Academy",
            actions = {
                Icon(
                    imageVector = Icons.Filled.MenuBook,
                    contentDescription = null,
                    tint = PrimaryPink
                )
            }
        )

        if (selectedArticle != null) {
            val article = selectedArticle!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
                    .padding(bottom = 100.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { selectedArticle = null },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = PrimaryText)
                    }

                    val isSaved = article.isSaved
                    IconButton(
                        onClick = { viewModel.toggleArticleSaved(article.id, !isSaved) },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .testTag("save_article_${article.id}")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = "Save article",
                            tint = PrimaryPink
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(AftermaGradients.PrimaryHero),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalLibrary,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(56.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = article.category.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryPink,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = article.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = PrimaryText,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 36.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Author: ${article.author}", fontSize = 12.sp, color = SecondaryText, fontWeight = FontWeight.Bold)
                    Text(text = article.readTime, fontSize = 12.sp, color = MutedText)
                }

                Divider(color = DividerColor)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = article.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PrimaryText,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                AftermaButton(
                    text = "Acknowledge Academy Article",
                    onClick = { selectedArticle = null },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                        Text(
                            text = "Maternal Clinical Repository",
                            style = MaterialTheme.typography.headlineMedium,
                            color = PrimaryText,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Clinically grounded guides mapping pelvic anatomy transitions, dream science cycles, and neonatal psychology insights.",
                            fontSize = 12.sp,
                            color = SecondaryText,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Clean search input
                        AftermaInput(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = "Search Articles (e.g. sleep, pelvic)...",
                            leadingIcon = Icons.Filled.Search,
                            textTag = "search_academy_articles",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // 1. Chips categories
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(categories) { cat ->
                                SafeChip(
                                    text = cat,
                                    isSelected = cat == selectedCategorization,
                                    onClick = { selectedCategorization = cat }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (filteredArticles.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No matched healing articles found.",
                                color = SecondaryText,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    items(filteredArticles) { article ->
                        ArticleListItem(
                            article = article,
                            onClick = { selectedArticle = article }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleListItem(
    article: LearningArticle,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(24.dp))
            .border(1.dp, Border, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .testTag("article_item_${article.id}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CalmBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Class,
                    contentDescription = null,
                    tint = DeepPink,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = article.category.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepPink,
                    letterSpacing = 1.sp
                )
                Text(
                    text = article.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = article.snippet,
                    fontSize = 11.sp,
                    color = SecondaryText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 15.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(text = article.author, fontSize = 10.sp, color = MutedText)
                    Text(text = article.readTime, fontSize = 10.sp, color = MutedText)
                }
            }

            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = PrimaryPink
            )
        }
    }
}
