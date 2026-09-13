package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ColorSwatchSpec

fun parseHexColor(hexStr: String, fallback: Color = Color.DarkGray): Color {
    val clean = hexStr.trim().removePrefix("#")
    return try {
        when (clean.length) {
            6 -> Color(android.graphics.Color.parseColor("#$clean"))
            8 -> Color(android.graphics.Color.parseColor("#$clean"))
            3 -> {
                val r = clean[0]
                val g = clean[1]
                val b = clean[2]
                Color(android.graphics.Color.parseColor("#$r$r$g$g$b$b"))
            }
            else -> fallback
        }
    } catch (e: Exception) {
        fallback
    }
}

/**
 * Renders the complete 5-Color Hex Palette section with interactive copy,
 * detailed usage notes, and a live interface preview.
 */
@Composable
fun PaletteSection(
    palette: List<ColorSwatchSpec>,
    brandName: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showLivePreview by remember { mutableStateOf(true) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "5-Color Hex Palette & Usage Notes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Engineered with the 60-30-10 design system rule",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            FilledTonalButton(
                onClick = { showLivePreview = !showLivePreview },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("toggle_palette_preview_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "Toggle Preview",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (showLivePreview) "Hide Mock UI" else "Live UI Test", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Interface Sandbox
        AnimatedVisibility(visible = showLivePreview) {
            LivePaletteUiPreview(
                palette = palette,
                brandName = brandName,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        // Swatch cards list
        palette.forEachIndexed { index, swatch ->
            ColorSwatchCard(
                swatch = swatch,
                index = index + 1,
                onCopy = { hex ->
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Hex Color", hex)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Copied $hex to clipboard", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )
        }
    }
}

@Composable
fun ColorSwatchCard(
    swatch: ColorSwatchSpec,
    index: Int,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val parsedColor = parseHexColor(swatch.hex)
    var copied by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier
            .testTag("swatch_card_$index")
            .clickable {
                copied = true
                onCopy(swatch.hex)
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Swatch Block
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(parsedColor)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#$index",
                    color = if (isDarkColor(parsedColor)) Color.White.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = swatch.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(start = 6.dp)
                    ) {
                        Text(
                            text = swatch.hex.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Role Pill
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = swatch.role,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Usage notes
                Text(
                    text = swatch.usageNotes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )

                if (swatch.contrastRating.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "WCAG: ${swatch.contrastRating}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            IconButton(
                onClick = {
                    copied = true
                    onCopy(swatch.hex)
                },
                modifier = Modifier
                    .size(36.dp)
                    .testTag("copy_hex_button_$index")
            ) {
                Icon(
                    imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                    contentDescription = "Copy Hex Code",
                    tint = if (copied) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Interactive Live UI Preview that demonstrates how the 5-color palette
 * orchestrates a real user interface (hero card, CTA button, chips, dark/light contrast).
 */
@Composable
fun LivePaletteUiPreview(
    palette: List<ColorSwatchSpec>,
    brandName: String,
    modifier: Modifier = Modifier
) {
    val primaryColor = parseHexColor(palette.getOrNull(0)?.hex ?: "#0F2F24")
    val secondaryColor = parseHexColor(palette.getOrNull(1)?.hex ?: "#2D6A4F")
    val ctaColor = parseHexColor(palette.getOrNull(2)?.hex ?: "#E09F3E")
    val darkSurfaceColor = parseHexColor(palette.getOrNull(3)?.hex ?: "#121A16")
    val canvasNeutralColor = parseHexColor(palette.getOrNull(4)?.hex ?: "#F8F5F0")

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = canvasNeutralColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(primaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = brandName.take(1),
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = brandName,
                        fontWeight = FontWeight.Bold,
                        color = darkSurfaceColor,
                        fontSize = 15.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = secondaryColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "LIVE PALETTE TEST",
                        color = secondaryColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mock Hero Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(primaryColor)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Primary Dominant Surface (60%)",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Architecting the Next Era of Conscious Design",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Secondary Pill & CTA Button Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = secondaryColor
                        ) {
                            Text(
                                text = "Secondary (30%)",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Button(
                            onClick = { /* Demo */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ctaColor,
                                contentColor = if (isDarkColor(ctaColor)) Color.White else Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "CTA Highlight (10%)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Dark surface text on neutral canvas demonstrates optimal contrast and readability.",
                fontSize = 12.sp,
                color = darkSurfaceColor.copy(alpha = 0.85f),
                lineHeight = 16.sp
            )
        }
    }
}

fun isDarkColor(color: Color): Boolean {
    val luminance = (0.299 * color.red + 0.587 * color.green + 0.114 * color.blue)
    return luminance < 0.5
}
