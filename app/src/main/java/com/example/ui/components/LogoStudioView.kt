package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.ZoomIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.BrandBible
import com.example.data.model.SecondaryMarkSpec

@Composable
fun LogoStudioSection(
    bible: BrandBible,
    isGeneratingImage: Boolean,
    onGenerateImage: (prompt: String, size: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var previewThemeDark by remember { mutableStateOf(true) }
    var selectedImageSize by remember { mutableStateOf("1K") }
    var customImagePrompt by remember(bible.primaryLogo.visualPrompt) {
        mutableStateOf(bible.primaryLogo.visualPrompt)
    }
    var showFullImageDialog by remember { mutableStateOf(false) }

    val primaryColor = parseHexColor(bible.palette.getOrNull(0)?.hex ?: "#6366F1")
    val accentColor = parseHexColor(bible.palette.getOrNull(2)?.hex ?: "#F43F5E")
    val darkBgColor = parseHexColor(bible.palette.getOrNull(3)?.hex ?: "#0F172A")
    val lightBgColor = parseHexColor(bible.palette.getOrNull(4)?.hex ?: "#F8FAFC")

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Primary Logo & Mark Architecture",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Core mark, clearspace standards, and high-res asset generation",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Primary Logo Presentation Stage
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("primary_logo_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Header with Dark / Light Switcher
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = bible.primaryLogo.conceptTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Shape: ${bible.primaryLogo.emblemShape}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Dark / Light switcher pill
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.testTag("logo_preview_mode_toggle")
                    ) {
                        Row(modifier = Modifier.padding(3.dp)) {
                            FilledTonalIconToggleButton(
                                checked = previewThemeDark,
                                onCheckedChange = { previewThemeDark = true },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.DarkMode, contentDescription = "Dark Canvas", modifier = Modifier.size(16.dp))
                            }
                            FilledTonalIconToggleButton(
                                checked = !previewThemeDark,
                                onCheckedChange = { previewThemeDark = false },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.LightMode, contentDescription = "Light Canvas", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Emblem Display Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (previewThemeDark) darkBgColor else lightBgColor)
                        .border(
                            width = 1.dp,
                            color = if (previewThemeDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(18.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Dashed clearspace boundary guides
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .border(
                                width = 1.dp,
                                color = if (previewThemeDark) Color.White.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (bible.generatedLogoBitmap != null) {
                            Image(
                                bitmap = bible.generatedLogoBitmap.asImageBitmap(),
                                contentDescription = "Generated Primary Logo",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { showFullImageDialog = true },
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            BrandEmblemCanvas(
                                style = bible.primaryLogo.emblemShape,
                                letter = bible.brandName.take(1),
                                primaryColor = primaryColor,
                                accentColor = accentColor,
                                size = 88.dp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = bible.brandName.uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (previewThemeDark) Color.White else darkBgColor,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = bible.slogan,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (previewThemeDark) Color.White.copy(alpha = 0.7f) else darkBgColor.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        )
                    }

                    // Clearspace tag
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "0.5X CLEARSPACE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = bible.primaryLogo.conceptDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Dimensions & Clearspace specifications
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LogoSpecMini("Min Digital Size", bible.primaryLogo.minimumSizeDigital)
                    LogoSpecMini("Min Print Size", bible.primaryLogo.minimumSizePrint)
                    LogoSpecMini("Clearance Boundary", "0.5x Height")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // AI High-Resolution Logo Studio (Gemini 3 Pro Image Preview with 1K, 2K, 4K affordance)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ai_image_generation_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "AI Visual Logo Studio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Powered by gemini-3-pro-image-preview",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Image Size Selection Affordance (1K, 2K, 4K)
                Text(
                    text = "SELECT EXPORT RESOLUTION:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("1K", "2K", "4K").forEach { sizeOpt ->
                        FilterChip(
                            selected = selectedImageSize == sizeOpt,
                            onClick = { selectedImageSize = sizeOpt },
                            label = {
                                Text(
                                    text = "$sizeOpt Resolution",
                                    fontWeight = if (selectedImageSize == sizeOpt) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = if (selectedImageSize == sizeOpt) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            modifier = Modifier.testTag("size_chip_$sizeOpt")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Prompt description textfield
                OutlinedTextField(
                    value = customImagePrompt,
                    onValueChange = { customImagePrompt = it },
                    label = { Text("Visual Prompt & Style Guidance") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("image_prompt_input"),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Generate Button
                Button(
                    onClick = {
                        onGenerateImage(customImagePrompt, selectedImageSize)
                    },
                    enabled = !isGeneratingImage && customImagePrompt.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("generate_logo_image_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isGeneratingImage) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Rendering $selectedImageSize Asset...")
                    } else {
                        Icon(Icons.Outlined.Image, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate $selectedImageSize Logo Visual Asset")
                    }
                }

                // If image already generated, show badge and view button
                if (bible.generatedLogoBitmap != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    bitmap = bible.generatedLogoBitmap.asImageBitmap(),
                                    contentDescription = "Current Generated Logo",
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "High-Res Asset Rendered",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Ready for Brand Bible export",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            TextButton(
                                onClick = { showFullImageDialog = true },
                                modifier = Modifier.testTag("view_full_image_button")
                            ) {
                                Icon(Icons.Outlined.ZoomIn, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Zoom")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Secondary Marks Grid
        Text(
            text = "Secondary Marks & Sub-Identities",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Adaptive variations for favicons, stamps, badges, and social media",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        bible.secondaryMarks.forEachIndexed { index, mark ->
            SecondaryMarkCard(
                mark = mark,
                brandLetter = bible.brandName.take(1),
                primaryColor = primaryColor,
                accentColor = accentColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Brand Logo Forbidden Practices
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Block,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Logo Integrity: Forbidden Practices",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                bible.primaryLogo.forbiddenUses.forEach { rule ->
                    Row(
                        modifier = Modifier.padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "•",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            text = rule,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }

    // Full screen zoom dialog for generated image
    if (showFullImageDialog && bible.generatedLogoBitmap != null) {
        Dialog(onDismissRequest = { showFullImageDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${bible.brandName} Logo Asset",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showFullImageDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Image(
                        bitmap = bible.generatedLogoBitmap.asImageBitmap(),
                        contentDescription = "Full zoom logo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Model: gemini-3-pro-image-preview",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SecondaryMarkCard(
    mark: SecondaryMarkSpec,
    brandLetter: String,
    primaryColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Procedural preview emblem
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                BrandEmblemCanvas(
                    style = mark.type,
                    letter = brandLetter,
                    primaryColor = primaryColor,
                    accentColor = accentColor,
                    size = 46.dp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = mark.type.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = mark.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = mark.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Best for: ${mark.bestUseCase}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun LogoSpecMini(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
    }
}
