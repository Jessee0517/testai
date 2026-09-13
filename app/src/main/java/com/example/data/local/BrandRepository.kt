package com.example.data.local

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class BrandRepository(private val dao: BrandBibleDao) {

    val allBrandBibles: Flow<List<BrandBible>> = dao.getAllBrandBibles().map { entities ->
        entities.map { entityToDomain(it) }
    }

    suspend fun saveBrandBible(bible: BrandBible): Long {
        val entity = domainToEntity(bible)
        return dao.insertBrandBible(entity)
    }

    suspend fun updateBrandBible(bible: BrandBible) {
        dao.updateBrandBible(domainToEntity(bible))
    }

    suspend fun deleteBrandBible(id: Long) {
        dao.deleteBrandBibleById(id)
    }

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) {
        dao.updateFavorite(id, isFavorite)
    }

    suspend fun updateGeneratedLogo(id: Long, base64: String) {
        dao.updateLogoAsset(id, base64)
    }

    private fun domainToEntity(domain: BrandBible): BrandBibleEntity {
        // Logo JSON
        val logoJson = JSONObject().apply {
            put("conceptTitle", domain.primaryLogo.conceptTitle)
            put("conceptDescription", domain.primaryLogo.conceptDescription)
            put("emblemShape", domain.primaryLogo.emblemShape)
            put("clearspaceRule", domain.primaryLogo.clearspaceRule)
            put("minimumSizePrint", domain.primaryLogo.minimumSizePrint)
            put("minimumSizeDigital", domain.primaryLogo.minimumSizeDigital)
            put("visualPrompt", domain.primaryLogo.visualPrompt)
            val forbiddenArray = JSONArray()
            domain.primaryLogo.forbiddenUses.forEach { forbiddenArray.put(it) }
            put("forbiddenUses", forbiddenArray)
        }.toString()

        // Secondary Marks JSON
        val secondaryArray = JSONArray()
        domain.secondaryMarks.forEach { mark ->
            val obj = JSONObject().apply {
                put("type", mark.type)
                put("title", mark.title)
                put("description", mark.description)
                put("bestUseCase", mark.bestUseCase)
                put("symbolStyle", mark.symbolStyle)
            }
            secondaryArray.put(obj)
        }
        val secondaryMarksJson = secondaryArray.toString()

        // Palette JSON
        val paletteArray = JSONArray()
        domain.palette.forEach { swatch ->
            val obj = JSONObject().apply {
                put("hex", swatch.hex)
                put("name", swatch.name)
                put("role", swatch.role)
                put("usageNotes", swatch.usageNotes)
                put("contrastRating", swatch.contrastRating)
                put("rgb", swatch.rgb)
                put("cmyk", swatch.cmyk)
            }
            paletteArray.put(obj)
        }
        val paletteJson = paletteArray.toString()

        // Typography JSON
        val typoJson = JSONObject().apply {
            put("headerFont", domain.typography.headerFont)
            put("headerCategory", domain.typography.headerCategory)
            put("bodyFont", domain.typography.bodyFont)
            put("bodyCategory", domain.typography.bodyCategory)
            put("pairingRationale", domain.typography.pairingRationale)
            put("headerWeights", domain.typography.headerWeights)
            put("bodyWeights", domain.typography.bodyWeights)
            put("headerLetterSpacing", domain.typography.headerLetterSpacing)
            put("bodyLineHeight", domain.typography.bodyLineHeight)
            put("googleFontsUrl", domain.typography.googleFontsUrl)
        }.toString()

        // Voice JSON
        val voiceJson = JSONObject().apply {
            val pillarsArray = JSONArray()
            domain.voiceGuidelines.tonePillars.forEach { pillar ->
                pillarsArray.put(JSONObject().apply {
                    put("title", pillar.title)
                    put("description", pillar.description)
                })
            }
            put("tonePillars", pillarsArray)

            val doArray = JSONArray()
            domain.voiceGuidelines.vocabularyDo.forEach { doArray.put(it) }
            put("vocabularyDo", doArray)

            val dontArray = JSONArray()
            domain.voiceGuidelines.vocabularyDont.forEach { dontArray.put(it) }
            put("vocabularyDont", dontArray)

            put("positioningStatement", domain.voiceGuidelines.positioningStatement)
        }.toString()

        return BrandBibleEntity(
            id = domain.id,
            brandName = domain.brandName,
            industry = domain.industry,
            mission = domain.mission,
            vision = domain.vision,
            slogan = domain.slogan,
            archetype = domain.archetype,
            primaryLogoJson = logoJson,
            secondaryMarksJson = secondaryMarksJson,
            paletteJson = paletteJson,
            typographyJson = typoJson,
            voiceJson = voiceJson,
            deepStrategyAudit = domain.deepStrategyAudit,
            industryTrends = domain.industryTrends,
            generatedLogoBase64 = domain.generatedLogoBase64,
            createdAt = domain.createdAt,
            isFavorite = domain.isFavorite
        )
    }

    private fun entityToDomain(entity: BrandBibleEntity): BrandBible {
        val logoObj = try { JSONObject(entity.primaryLogoJson) } catch (e: Exception) { JSONObject() }
        val forbiddenList = mutableListOf<String>()
        val forbiddenArray = logoObj.optJSONArray("forbiddenUses")
        if (forbiddenArray != null) {
            for (i in 0 until forbiddenArray.length()) {
                forbiddenList.add(forbiddenArray.getString(i))
            }
        }

        val primaryLogo = LogoSpec(
            conceptTitle = logoObj.optString("conceptTitle", "The Core Emblem"),
            conceptDescription = logoObj.optString("conceptDescription", "Primary visual representation of the brand."),
            emblemShape = logoObj.optString("emblemShape", "Geometric Mark"),
            clearspaceRule = logoObj.optString("clearspaceRule", "Minimum 0.5x clear space on all sides."),
            minimumSizePrint = logoObj.optString("minimumSizePrint", "0.75 in"),
            minimumSizeDigital = logoObj.optString("minimumSizeDigital", "32 px"),
            visualPrompt = logoObj.optString("visualPrompt", ""),
            forbiddenUses = if (forbiddenList.isEmpty()) listOf("Do not stretch", "Do not alter colors") else forbiddenList
        )

        val secondaryMarks = mutableListOf<SecondaryMarkSpec>()
        try {
            val sArray = JSONArray(entity.secondaryMarksJson)
            for (i in 0 until sArray.length()) {
                val item = sArray.getJSONObject(i)
                secondaryMarks.add(
                    SecondaryMarkSpec(
                        type = item.optString("type", "Mark"),
                        title = item.optString("title", "Secondary Mark"),
                        description = item.optString("description", ""),
                        bestUseCase = item.optString("bestUseCase", ""),
                        symbolStyle = item.optString("symbolStyle", "")
                    )
                )
            }
        } catch (e: Exception) {
            // fallback
        }

        val palette = mutableListOf<ColorSwatchSpec>()
        try {
            val pArray = JSONArray(entity.paletteJson)
            for (i in 0 until pArray.length()) {
                val item = pArray.getJSONObject(i)
                palette.add(
                    ColorSwatchSpec(
                        hex = item.optString("hex", "#000000"),
                        name = item.optString("name", "Color"),
                        role = item.optString("role", "Brand Accent"),
                        usageNotes = item.optString("usageNotes", ""),
                        contrastRating = item.optString("contrastRating", "High Contrast"),
                        rgb = item.optString("rgb", ""),
                        cmyk = item.optString("cmyk", "")
                    )
                )
            }
        } catch (e: Exception) {
            // fallback
        }

        val typoObj = try { JSONObject(entity.typographyJson) } catch (e: Exception) { JSONObject() }
        val typography = TypographySpec(
            headerFont = typoObj.optString("headerFont", "Playfair Display"),
            headerCategory = typoObj.optString("headerCategory", "Serif"),
            bodyFont = typoObj.optString("bodyFont", "Inter"),
            bodyCategory = typoObj.optString("bodyCategory", "Sans-Serif"),
            pairingRationale = typoObj.optString("pairingRationale", "Harmonious balance of elegance and clarity."),
            headerWeights = typoObj.optString("headerWeights", "Bold 700"),
            bodyWeights = typoObj.optString("bodyWeights", "Regular 400"),
            headerLetterSpacing = typoObj.optString("headerLetterSpacing", "-0.01em"),
            bodyLineHeight = typoObj.optString("bodyLineHeight", "1.6x"),
            googleFontsUrl = typoObj.optString("googleFontsUrl", "")
        )

        val voiceObj = try { JSONObject(entity.voiceJson) } catch (e: Exception) { JSONObject() }
        val pillarsList = mutableListOf<TonePillar>()
        val pillarsArray = voiceObj.optJSONArray("tonePillars")
        if (pillarsArray != null) {
            for (i in 0 until pillarsArray.length()) {
                val item = pillarsArray.getJSONObject(i)
                pillarsList.add(TonePillar(item.optString("title"), item.optString("description")))
            }
        }

        val doList = mutableListOf<String>()
        voiceObj.optJSONArray("vocabularyDo")?.let { arr ->
            for (i in 0 until arr.length()) doList.add(arr.getString(i))
        }

        val dontList = mutableListOf<String>()
        voiceObj.optJSONArray("vocabularyDont")?.let { arr ->
            for (i in 0 until arr.length()) dontList.add(arr.getString(i))
        }

        val voiceGuidelines = VoiceSpec(
            tonePillars = pillarsList,
            vocabularyDo = doList,
            vocabularyDont = dontList,
            positioningStatement = voiceObj.optString("positioningStatement", "")
        )

        var bitmap: Bitmap? = null
        if (!entity.generatedLogoBase64.isNullOrBlank()) {
            try {
                val decodedBytes = Base64.decode(entity.generatedLogoBase64, Base64.DEFAULT)
                bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            } catch (e: Exception) {
                // ignore
            }
        }

        return BrandBible(
            id = entity.id,
            brandName = entity.brandName,
            industry = entity.industry,
            mission = entity.mission,
            vision = entity.vision,
            slogan = entity.slogan,
            archetype = entity.archetype,
            primaryLogo = primaryLogo,
            secondaryMarks = secondaryMarks,
            palette = palette,
            typography = typography,
            voiceGuidelines = voiceGuidelines,
            deepStrategyAudit = entity.deepStrategyAudit,
            industryTrends = entity.industryTrends,
            generatedLogoBitmap = bitmap,
            generatedLogoBase64 = entity.generatedLogoBase64,
            createdAt = entity.createdAt,
            isFavorite = entity.isFavorite
        )
    }
}
