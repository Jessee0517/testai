package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BrandPresets

@Composable
fun GeneratorFormView(
    companyName: String,
    industry: String,
    mission: String,
    tone: String,
    isPolishingMission: Boolean,
    isGeneratingBible: Boolean,
    onCompanyNameChange: (String) -> Unit,
    onIndustryChange: (String) -> Unit,
    onMissionChange: (String) -> Unit,
    onToneChange: (String) -> Unit,
    onPolishMission: () -> Unit,
    onGenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val industries = listOf(
        "Sustainable Cleantech",
        "AI & Software",
        "Luxury Hospitality",
        "Artisanal Food & Beverage",
        "Fintech & Wealth",
        "Health & Bio-Tech",
        "Modern Architecture",
        "Creative Design Studio"
    )

    val tones = listOf(
        "Warm, Organic & Visionary",
        "Minimalist, Sharp & Modern",
        "Quiet Luxury & Editorial",
        "Bold, Dynamic & Disruptive",
        "Playful, Vibrant & Friendly",
        "Trustworthy, Noble & Heritage"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("generator_form_container")
    ) {
        // Welcome Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Brand Identity Architect",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Transform your company mission into a cohesive Brand Bible",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Preset Inspirations Row
        Text(
            text = "QUICK MISSION INSPIRATIONS:",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(BrandPresets.samplePresets) { preset ->
                SuggestionChip(
                    onClick = {
                        onCompanyNameChange(preset.companyName)
                        onIndustryChange(preset.industry)
                        onToneChange(preset.tone)
                        onMissionChange(preset.mission)
                    },
                    label = { Text(preset.companyName, fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    modifier = Modifier.testTag("preset_chip_${preset.companyName}")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Company Name Input
        OutlinedTextField(
            value = companyName,
            onValueChange = onCompanyNameChange,
            label = { Text("Company or Brand Name") },
            placeholder = { Text("e.g. AuraTerra, Synapse Labs") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("company_name_input"),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Industry Selection
        Text(
            text = "INDUSTRY SECTOR:",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(industries) { ind ->
                FilterChip(
                    selected = industry == ind,
                    onClick = { onIndustryChange(ind) },
                    label = { Text(ind, fontSize = 12.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Desired Tone
        Text(
            text = "BRAND PERSONALITY & TONE:",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(tones) { t ->
                FilterChip(
                    selected = tone == t,
                    onClick = { onToneChange(t) },
                    label = { Text(t, fontSize = 12.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Mission Statement Input with Smart Polish
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "COMPANY MISSION & PURPOSE:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Smart Polish button (gemini-3.1-flash-lite-preview)
            FilledTonalButton(
                onClick = onPolishMission,
                enabled = !isPolishingMission && mission.isNotBlank(),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.testTag("smart_polish_button")
            ) {
                if (isPolishingMission) {
                    CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 1.5.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Polishing...", fontSize = 11.sp)
                } else {
                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Fast Polish (AI Lite)", fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = mission,
            onValueChange = onMissionChange,
            placeholder = {
                Text(
                    "Describe what your company does, who you serve, and the change you create in the world...",
                    fontSize = 14.sp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("company_mission_input"),
            shape = RoundedCornerShape(14.dp),
            minLines = 4,
            maxLines = 6
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Primary Action: Generate Brand Bible
        Button(
            onClick = onGenerate,
            enabled = !isGeneratingBible && companyName.isNotBlank() && mission.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("generate_brand_bible_button"),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            if (isGeneratingBible) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.5.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Architecting Brand Bible with Gemini...",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Generate Brand Bible Dashboard",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Explanatory specs summary
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "YOUR BRAND BIBLE WILL INCLUDE:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• Primary Logo symbol concept & 0.5x clearspace standards\n" +
                            "• Secondary marks: Monogram, Favicon, Circular Seal & Social Avatar\n" +
                            "• 5-Color Hex Palette with 60-30-10 usage notes & WCAG contrast\n" +
                            "• Suggested Google Font pairings for headers & body typography\n" +
                            "• Strategic voice pillars, vocabulary bounds & positioning\n" +
                            "• AI Logo Visual Asset Generator (1K, 2K, 4K resolution options)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
