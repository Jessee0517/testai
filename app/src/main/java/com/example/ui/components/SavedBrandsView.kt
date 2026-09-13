package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BrandBible
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SavedBrandsView(
    savedBibles: List<BrandBible>,
    activeBibleId: Long,
    onSelectBible: (BrandBible) -> Unit,
    onToggleFavorite: (BrandBible) -> Unit,
    onDeleteBible: (BrandBible) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("saved_brands_container")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Brand Library",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${savedBibles.size} Brand Bibles preserved in local Room storage",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (savedBibles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.BookmarkBorder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Saved Brand Bibles Yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Generate a brand identity from your company mission and tap 'Save Brand' to archive it here.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(savedBibles, key = { it.id }) { bible ->
                    val isActive = bible.id == activeBibleId
                    val primaryColor = parseHexColor(bible.palette.firstOrNull()?.hex ?: "#6366F1")
                    val dateFormatted = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(bible.createdAt))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("saved_brand_item_${bible.id}")
                            .clickable { onSelectBible(bible) },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = if (isActive) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(primaryColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = bible.brandName.take(1),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = bible.brandName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${bible.industry} • $dateFormatted",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row {
                                    IconButton(
                                        onClick = { onToggleFavorite(bible) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (bible.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Favorite",
                                            tint = if (bible.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            val markdown = exportBibleToMarkdown(bible)
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("Brand Bible", markdown))
                                            Toast.makeText(context, "Copied ${bible.brandName} Brand Bible Markdown!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Share,
                                            contentDescription = "Copy Markdown",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    IconButton(
                                        onClick = { onDeleteBible(bible) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Delete,
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "\"${bible.slogan}\"",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // 5 Color palette dots
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                bible.palette.forEach { swatch ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(18.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(parseHexColor(swatch.hex))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun exportBibleToMarkdown(bible: BrandBible): String {
    return """
        # Brand Bible: ${bible.brandName}
        **Industry:** ${bible.industry}  
        **Slogan:** ${bible.slogan}  
        **Archetype:** ${bible.archetype}  

        ## Core Mission
        ${bible.mission}

        ## Primary Logo
        - **Concept:** ${bible.primaryLogo.conceptTitle}
        - **Description:** ${bible.primaryLogo.conceptDescription}
        - **Emblem Shape:** ${bible.primaryLogo.emblemShape}
        - **Clearspace Rule:** ${bible.primaryLogo.clearspaceRule}
        - **Forbidden Uses:**
        ${bible.primaryLogo.forbiddenUses.joinToString("\n") { "  - $it" }}

        ## 5-Color Hex Palette
        ${bible.palette.mapIndexed { idx, s -> "${idx + 1}. **${s.name}** (`${s.hex}`)\n   - Role: ${s.role}\n   - Usage: ${s.usageNotes}\n   - WCAG: ${s.contrastRating}" }.joinToString("\n\n")}

        ## Typography System
        - **Header Font:** ${bible.typography.headerFont} (${bible.typography.headerCategory})
        - **Body Font:** ${bible.typography.bodyFont} (${bible.typography.bodyCategory})
        - **Pairing Rationale:** ${bible.typography.pairingRationale}
        - **Header Weights:** ${bible.typography.headerWeights}
        - **Body Weights:** ${bible.typography.bodyWeights}

        ## Brand Voice
        ${bible.voiceGuidelines.tonePillars.joinToString("\n") { "- **${it.title}:** ${it.description}" }}
        - **Vocabulary Do:** ${bible.voiceGuidelines.vocabularyDo.joinToString(", ")}
        - **Vocabulary Don't:** ${bible.voiceGuidelines.vocabularyDont.joinToString(", ")}
        - **Positioning:** ${bible.voiceGuidelines.positioningStatement}
    """.trimIndent()
}
