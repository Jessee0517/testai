package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "brand_bibles")
data class BrandBibleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val brandName: String,
    val industry: String,
    val mission: String,
    val vision: String,
    val slogan: String,
    val archetype: String,
    val primaryLogoJson: String,
    val secondaryMarksJson: String,
    val paletteJson: String,
    val typographyJson: String,
    val voiceJson: String,
    val deepStrategyAudit: String?,
    val industryTrends: String?,
    val generatedLogoBase64: String?,
    val createdAt: Long,
    val isFavorite: Boolean
)
