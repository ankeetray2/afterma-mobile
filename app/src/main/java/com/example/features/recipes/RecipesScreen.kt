package com.example.features.recipes

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
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
import com.example.data.database.entities.RecipeEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun RecipesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val recipesList by viewModel.recipes.collectAsState()
    val favoritesList by viewModel.favoriteRecipes.collectAsState()

    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Postpartum Recovery", "Lactation Support", "Anti-inflammatory", "Sleep & Calm")

    var selectedRecipe by remember { mutableStateOf<RecipeEntity?>(null) }

    val filteredRecipes = remember(recipesList, selectedCategory) {
        if (selectedCategory == "All") recipesList
        else recipesList.filter { it.category == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
    ) {
        SoftTopBar(
            title = "Restorative Kitchen",
            actions = {
                // Number of favorites badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SoftPink)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Favorite, contentDescription = null, tint = PrimaryPink, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${favoritesList.size}", fontSize = 11.sp, color = DeepPink, fontWeight = FontWeight.Bold)
                }
            }
        )

        if (selectedRecipe != null) {
            // Recipe Details View overlay (beautiful full screen detail drawer)
            val recipe = selectedRecipe!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Background)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
                    .padding(bottom = 100.dp) // margin for nav
            ) {
                // Header details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { selectedRecipe = null },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close Detail", tint = PrimaryText)
                    }

                    // Fav Toggle symbol
                    val isFav = favoritesList.any { it.id == recipe.id }
                    IconButton(
                        onClick = { viewModel.toggleRecipeFavorite(recipe.id, !isFav) },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .testTag("recipe_detail_fav_${recipe.id}")
                    ) {
                        Icon(
                            imageVector = if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Toggle Fav",
                            tint = PrimaryPink
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Beautifully designed pastel food banner illustration representation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(AftermaGradients.Healing),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.LocalPharmacy,
                            contentDescription = null,
                            tint = DeepPink,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = recipe.category.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = DeepPink,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = PrimaryText,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "Prep: ${recipe.prepTime}", fontSize = 13.sp, color = SecondaryText, fontWeight = FontWeight.Bold)
                    Text(text = "Calories: ${recipe.calories}", fontSize = 13.sp, color = SecondaryText, fontWeight = FontWeight.Bold)
                }

                // Core biological maternal benefit card
                AftermaCard(borderColor = Success, backgroundColor = Color(0xFFF0FDF4)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.Eco, contentDescription = null, tint = Success)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Maternal Recovery Benefit", fontSize = 12.sp, color = Success, fontWeight = FontWeight.Bold)
                            Text(text = recipe.benefits, fontSize = 13.sp, color = PrimaryText, lineHeight = 18.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Ingredients block
                Text(text = "Soothing Ingredients Needed", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Border)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        recipe.ingredients.split(",").forEach { ingredient ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryPink)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(text = ingredient.trim(), fontSize = 14.sp, color = PrimaryText)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Culinary Guidelines instructions
                Text(text = "Preparation Instructions", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Border)
                ) {
                    Text(
                        text = recipe.instructions,
                        fontSize = 14.sp,
                        color = PrimaryText,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                AftermaButton(
                    text = "Culinary Guidelines Completed",
                    onClick = { selectedRecipe = null },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            // Master Lists of Recipes
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Header category listing
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                        Text(
                            text = "Ayurvedic Restorative Kitchen",
                            style = MaterialTheme.typography.headlineMedium,
                            color = PrimaryText,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "A healing nutrient ecosystem designed to calibrate maternal organs, reduce inflammation, and enhance lactation flows.",
                            fontSize = 12.sp,
                            color = SecondaryText,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // 1. Horizontally scrolled Chips categories
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("recipe_categories_scroll")
                        ) {
                            items(categories) { cat ->
                                SafeChip(
                                    text = cat,
                                    isSelected = cat == selectedCategory,
                                    onClick = { selectedCategory = cat }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Grid list of Recipe items
                items(filteredRecipes) { recipe ->
                    val isFav = favoritesList.any { it.id == recipe.id }
                    RecipeCardItem(
                        recipe = recipe,
                        isFav = isFav,
                        onFavClick = { viewModel.toggleRecipeFavorite(recipe.id, !isFav) },
                        onCardClick = { selectedRecipe = recipe }
                    )
                }
            }
        }
    }
}

// Custom cell representing Recipe items
@Composable
fun RecipeCardItem(
    recipe: RecipeEntity,
    isFav: Boolean,
    onFavClick: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(28.dp))
            .border(1.dp, Border, RoundedCornerShape(28.dp))
            .clickable(onClick = onCardClick)
            .testTag("recipe_item_${recipe.id}"),
        shape = RoundedCornerShape(28.dp),
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
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SoftPink),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Fastfood,
                    contentDescription = null,
                    tint = PrimaryPink,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recipe.category.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepPink,
                    letterSpacing = 1.sp
                )
                Text(
                    text = recipe.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = recipe.prepTime, fontSize = 11.sp, color = SecondaryText)
                    Text(text = recipe.calories, fontSize = 11.sp, color = SecondaryText)
                }
            }

            IconButton(
                onClick = onFavClick,
                modifier = Modifier.testTag("recipe_fav_button_${recipe.id}")
            ) {
                Icon(
                    imageVector = if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favoritise",
                    tint = PrimaryPink
                )
            }
        }
    }
}
