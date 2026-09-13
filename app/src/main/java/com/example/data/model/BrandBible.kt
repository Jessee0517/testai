package com.example.data.model

import android.graphics.Bitmap

/**
 * Complete Brand Bible data model representing a company's visual and strategic identity.
 */
data class BrandBible(
    val id: Long = 0,
    val brandName: String,
    val industry: String,
    val mission: String,
    val vision: String = "",
    val slogan: String,
    val archetype: String = "The Creator",
    val primaryLogo: LogoSpec,
    val secondaryMarks: List<SecondaryMarkSpec>,
    val palette: List<ColorSwatchSpec>,
    val typography: TypographySpec,
    val voiceGuidelines: VoiceSpec,
    val deepStrategyAudit: String? = null,
    val industryTrends: String? = null,
    val generatedLogoBitmap: Bitmap? = null,
    val generatedLogoBase64: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

data class LogoSpec(
    val conceptTitle: String,
    val conceptDescription: String,
    val emblemShape: String, // e.g. "Geometric Hexagon", "Organic Leaf", "Abstract Monogram", "Shield"
    val clearspaceRule: String = "Minimum clearance is 0.5x the emblem height on all sides.",
    val minimumSizePrint: String = "0.75 in (19 mm)",
    val minimumSizeDigital: String = "32 px",
    val visualPrompt: String,
    val forbiddenUses: List<String> = listOf(
        "Do not alter proportions or stretch the mark.",
        "Do not rotate or tilt the logo on unapproved angles.",
        "Do not place the logo on low-contrast photographic backgrounds.",
        "Do not add unapproved drop shadows or bevels."
    )
)

data class SecondaryMarkSpec(
    val type: String, // e.g. "Favicon / Monogram", "Sub-mark Badge", "Circular Crest Seal", "Social Avatar"
    val title: String,
    val description: String,
    val bestUseCase: String,
    val symbolStyle: String
)

data class ColorSwatchSpec(
    val hex: String,
    val name: String,
    val role: String, // e.g. "Primary Dominant (60%)", "Secondary Accent (30%)", "Accent / CTA (10%)", "Dark Surface", "Canvas Neutral"
    val usageNotes: String,
    val contrastRating: String, // e.g. "Passes AAA against #FFFFFF"
    val rgb: String = "",
    val cmyk: String = ""
)

data class TypographySpec(
    val headerFont: String,
    val headerCategory: String, // e.g. "Neo-Grotesque Sans", "Geometric Display", "Editorial Serif"
    val bodyFont: String,
    val bodyCategory: String, // e.g. "Humanist Sans", "Modern Serif"
    val pairingRationale: String,
    val headerWeights: String = "Bold 700 / ExtraBold 800",
    val bodyWeights: String = "Regular 400 / Medium 500",
    val headerLetterSpacing: String = "-0.02em (tight for headers)",
    val bodyLineHeight: String = "1.6x (optimal body readability)",
    val googleFontsUrl: String = ""
)

data class VoiceSpec(
    val tonePillars: List<TonePillar>,
    val vocabularyDo: List<String>,
    val vocabularyDont: List<String>,
    val positioningStatement: String
)

data class TonePillar(
    val title: String,
    val description: String
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val role: String, // "user" or "model"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isThinking: Boolean = false
)
