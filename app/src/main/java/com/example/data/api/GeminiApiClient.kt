package com.example.data.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiApiClient {

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val apiKey: String
        get() = BuildConfig.GEMINI_API_KEY

    val isApiKeyConfigured: Boolean
        get() = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

    /**
     * Rapid Mission Polish using gemini-3.1-flash-lite-preview for instant response.
     */
    suspend fun polishMission(rawMission: String, industry: String): String = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured) {
            return@withContext "Elevate $industry through conscious design, structural integrity, and enduring community resonance."
        }
        val model = "gemini-3.1-flash-lite-preview"
        val prompt = """
            Polish and sharpen this company mission statement for a company in the "$industry" sector:
            "$rawMission"
            
            Return a punchy, powerful, 1-2 sentence company mission that sounds visionary, confident, and memorable.
            Return ONLY the polished mission text, without commentary or quotation marks.
        """.trimIndent()

        val requestJson = JSONObject().apply {
            val contents = JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    })
                })
            }
            put("contents", contents)
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
            })
        }

        try {
            val responseText = executePost(model, requestJson.toString())
            extractTextFromCandidates(responseText) ?: rawMission
        } catch (e: Exception) {
            Log.e("GeminiApiClient", "polishMission error", e)
            rawMission
        }
    }

    /**
     * Search Grounding using gemini-3.5-flash with googleSearch tool.
     */
    suspend fun searchIndustryTrends(industry: String): String = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured) {
            return@withContext "Real-time Grounding Note: Visual identity trends in $industry emphasize biophilic tones, warm earthy neutral palettes, brutalist-editorial typography pairings, and tactile minimalism."
        }
        val model = "gemini-3.5-flash"
        val prompt = """
            Perform Google Search Grounding for current branding, visual identity, and color design trends in the industry: "$industry".
            Provide a concise, 3-paragraph executive summary detailing:
            1. Current visual identity shifts and dominant aesthetics (e.g., typography styles, logo shapes).
            2. Prevailing color psychology and emerging color palette directions.
            3. Key pitfalls or overused design clichés in this sector to avoid.
        """.trimIndent()

        val requestJson = JSONObject().apply {
            val contents = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    })
                })
            }
            put("contents", contents)
            val tools = JSONArray().apply {
                put(JSONObject().apply {
                    put("googleSearch", JSONObject())
                })
            }
            put("tools", tools)
        }

        try {
            val responseText = executePost(model, requestJson.toString())
            extractTextFromCandidates(responseText) ?: "No trends retrieved."
        } catch (e: Exception) {
            Log.e("GeminiApiClient", "searchIndustryTrends error", e)
            "Could not fetch live search trends: ${e.message}"
        }
    }

    /**
     * Deep Strategic Brand Audit using gemini-3.1-pro-preview with thinkingLevel HIGH.
     * Note: Do NOT set maxOutputTokens per instructions.
     */
    suspend fun deepThinkingStrategyAudit(bible: BrandBible): String = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured) {
            return@withContext """
                ### Strategic Brand Audit
                - **Target Persona**: High-intent seekers who value craftsmanship over mass consumerism.
                - **Psychological Hook**: The blend of structural geometry and organic softness triggers trust, calm authority, and discerning exclusivity.
                - **Category Moat**: Moving away from generic corporate tech clichés creates instant visual differentiation.
                - **Tactical Execution**: Ensure consistent application of the 60-30-10 palette rule across all digital touchpoints.
            """.trimIndent()
        }
        val model = "gemini-3.1-pro-preview"
        val prompt = """
            Conduct a comprehensive, high-level Strategic Brand Identity Audit for this brand:
            Brand Name: ${bible.brandName}
            Industry: ${bible.industry}
            Mission: ${bible.mission}
            Tagline: ${bible.slogan}
            Archetype: ${bible.archetype}
            Primary Colors: ${bible.palette.joinToString { "${it.name} (${it.hex})" }}
            Typography: Header: ${bible.typography.headerFont}, Body: ${bible.typography.bodyFont}
            
            Synthesize your strategic reasoning into:
            1. Customer Psychological Resonance: Why this visual and tonal identity resonates deeply with target decision-makers.
            2. Competitive Whitespace & Moat: How to outmaneuver legacy incumbents visually and emotionally.
            3. Omnichannel Touchpoint Application: Strategic recommendations for physical (packaging/merch) and digital (app/web) executions.
            4. 3 Critical Strategic Warnings: Potential brand drift risks to guard against over the next 3 years.
        """.trimIndent()

        val requestJson = JSONObject().apply {
            val contents = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    })
                })
            }
            put("contents", contents)
            put("generationConfig", JSONObject().apply {
                put("thinkingConfig", JSONObject().apply {
                    put("thinkingLevel", "high")
                })
            })
        }

        try {
            val responseText = executePost(model, requestJson.toString())
            extractTextFromCandidates(responseText) ?: "Audit completed without notes."
        } catch (e: Exception) {
            Log.e("GeminiApiClient", "deepThinkingStrategyAudit error", e)
            "Strategic audit generated an error: ${e.message}"
        }
    }

    /**
     * Generate Comprehensive Brand Bible using gemini-3.1-pro-preview with JSON structured output.
     */
    suspend fun generateBrandBible(
        companyName: String,
        industry: String,
        mission: String,
        tone: String
    ): BrandBible = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured) {
            return@withContext BrandPresets.createFallbackBrandBible(companyName, industry, mission, tone)
        }

        val model = "gemini-3.1-pro-preview"
        val systemPrompt = "You are a world-class Executive Creative Director & Brand Identity Architect. You develop comprehensive, cohesive Brand Bibles for visionary companies."
        val userPrompt = """
            Generate a comprehensive Brand Bible for:
            Company Name: $companyName
            Industry: $industry
            Core Mission: $mission
            Desired Tone / Persona: $tone
            
            You MUST return a strictly valid JSON object matching this schema:
            {
              "brandName": "$companyName",
              "industry": "$industry",
              "mission": "...",
              "vision": "...",
              "slogan": "...",
              "archetype": "...",
              "primaryLogo": {
                "conceptTitle": "...",
                "conceptDescription": "...",
                "emblemShape": "...",
                "clearspaceRule": "0.5x minimum clearance on all sides.",
                "minimumSizePrint": "0.75 in",
                "minimumSizeDigital": "32 px",
                "visualPrompt": "A minimalist luxury logo for $companyName, featuring ..., vector style, isolated on clean background.",
                "forbiddenUses": ["...", "..."]
              },
              "secondaryMarks": [
                {
                  "type": "Favicon / Monogram",
                  "title": "...",
                  "description": "...",
                  "bestUseCase": "...",
                  "symbolStyle": "..."
                },
                {
                  "type": "Sub-mark / Wordmark",
                  "title": "...",
                  "description": "...",
                  "bestUseCase": "...",
                  "symbolStyle": "..."
                },
                {
                  "type": "Circular Crest Seal",
                  "title": "...",
                  "description": "...",
                  "bestUseCase": "...",
                  "symbolStyle": "..."
                },
                {
                  "type": "Social Avatar Mark",
                  "title": "...",
                  "description": "...",
                  "bestUseCase": "...",
                  "symbolStyle": "..."
                }
              ],
              "palette": [
                {
                  "hex": "#...",
                  "name": "...",
                  "role": "Primary Brand Dominant (60%)",
                  "usageNotes": "...",
                  "contrastRating": "...",
                  "rgb": "...",
                  "cmyk": "..."
                },
                {
                  "hex": "#...",
                  "name": "...",
                  "role": "Secondary Accent (30%)",
                  "usageNotes": "...",
                  "contrastRating": "...",
                  "rgb": "...",
                  "cmyk": "..."
                },
                {
                  "hex": "#...",
                  "name": "...",
                  "role": "Accent / CTA Highlight (10%)",
                  "usageNotes": "...",
                  "contrastRating": "...",
                  "rgb": "...",
                  "cmyk": "..."
                },
                {
                  "hex": "#...",
                  "name": "...",
                  "role": "Dark Surface & Text",
                  "usageNotes": "...",
                  "contrastRating": "...",
                  "rgb": "...",
                  "cmyk": "..."
                },
                {
                  "hex": "#...",
                  "name": "...",
                  "role": "Canvas & Neutral",
                  "usageNotes": "...",
                  "contrastRating": "...",
                  "rgb": "...",
                  "cmyk": "..."
                }
              ],
              "typography": {
                "headerFont": "...",
                "headerCategory": "...",
                "bodyFont": "...",
                "bodyCategory": "...",
                "pairingRationale": "...",
                "headerWeights": "...",
                "bodyWeights": "...",
                "headerLetterSpacing": "...",
                "bodyLineHeight": "...",
                "googleFontsUrl": "..."
              },
              "voiceGuidelines": {
                "tonePillars": [
                  {"title": "...", "description": "..."},
                  {"title": "...", "description": "..."}
                ],
                "vocabularyDo": ["...", "..."],
                "vocabularyDont": ["...", "..."],
                "positioningStatement": "..."
              }
            }
        """.trimIndent()

        val requestJson = JSONObject().apply {
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", systemPrompt) })
                })
            })
            val contents = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", userPrompt) })
                    })
                })
            }
            put("contents", contents)
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
                put("responseFormat", JSONObject().apply {
                    put("text", JSONObject().apply {
                        put("mimeType", "application/json")
                    })
                })
            })
        }

        try {
            val rawResponse = executePost(model, requestJson.toString())
            val candidateText = extractTextFromCandidates(rawResponse)
            if (candidateText != null) {
                parseBrandBibleJson(candidateText, companyName, industry, mission, tone)
            } else {
                BrandPresets.createFallbackBrandBible(companyName, industry, mission, tone)
            }
        } catch (e: Exception) {
            Log.e("GeminiApiClient", "generateBrandBible error", e)
            BrandPresets.createFallbackBrandBible(companyName, industry, mission, tone)
        }
    }

    /**
     * Image Generation using gemini-3-pro-image-preview with user selectable size (1K, 2K, 4K).
     */
    suspend fun generateLogoImage(
        prompt: String,
        imageSize: String = "1K"
    ): Pair<Bitmap, String>? = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured) {
            return@withContext null
        }
        val model = "gemini-3-pro-image-preview"
        val cleanSize = when (imageSize.uppercase()) {
            "4K" -> "4K"
            "2K" -> "2K"
            else -> "1K"
        }

        val requestJson = JSONObject().apply {
            val contents = JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "A high-end modern minimalist professional logo symbol mark: $prompt. Clean isolated vector logo on dark neutral background, golden ratio, premium design agency quality.")
                        })
                    })
                })
            }
            put("contents", contents)
            put("generationConfig", JSONObject().apply {
                put("imageConfig", JSONObject().apply {
                    put("aspectRatio", "1:1")
                    put("imageSize", cleanSize)
                })
                val modalities = JSONArray().apply {
                    put("TEXT")
                    put("IMAGE")
                }
                put("responseModalities", modalities)
            })
        }

        try {
            val rawResponse = executePost(model, requestJson.toString())
            val jsonRoot = JSONObject(rawResponse)
            val candidates = jsonRoot.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")

            if (parts != null) {
                for (i in 0 until parts.length()) {
                    val part = parts.getJSONObject(i)
                    val inlineData = part.optJSONObject("inlineData")
                    if (inlineData != null) {
                        val base64Data = inlineData.optString("data")
                        if (base64Data.isNotBlank()) {
                            val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                            if (bitmap != null) {
                                return@withContext Pair(bitmap, base64Data)
                            }
                        }
                    }
                }
            }
            null
        } catch (e: Exception) {
            Log.e("GeminiApiClient", "generateLogoImage error", e)
            null
        }
    }

    /**
     * Multi-turn Chat with Creative Director maintaining conversation history.
     */
    suspend fun chatWithCreativeDirector(
        history: List<ChatMessage>,
        userMessage: String,
        modelName: String = "gemini-3.5-flash",
        brandContext: BrandBible? = null
    ): String = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured) {
            return@withContext "As your Creative Director, I love the foundation of ${brandContext?.brandName ?: "your brand"}. With a 5-color palette anchored by ${brandContext?.palette?.firstOrNull()?.name ?: "rich tones"} and the ${brandContext?.typography?.headerFont ?: "distinctive"} header pairing, your visual voice projects authenticity. What specific marketing campaign or collateral should we design next?"
        }

        val resolvedModel = when (modelName) {
            "gemini-3.1-pro-preview" -> "gemini-3.1-pro-preview"
            "gemini-3.1-flash-lite-preview" -> "gemini-3.1-flash-lite-preview"
            else -> "gemini-3.5-flash"
        }

        val systemRoleInstruction = """
            You are the Chief Brand Director & Creative Strategist.
            Your role is to guide the user in evolving, critiquing, and implementing their Brand Bible into world-class digital, packaging, and marketing executions.
            ${if (brandContext != null) """
            Current Brand Context:
            - Brand Name: ${brandContext.brandName}
            - Industry: ${brandContext.industry}
            - Mission: ${brandContext.mission}
            - Tagline: ${brandContext.slogan}
            - Primary Logo: ${brandContext.primaryLogo.conceptTitle} (${brandContext.primaryLogo.emblemShape})
            - 5 Colors: ${brandContext.palette.joinToString { "${it.name} (${it.hex})" }}
            - Typography: Header: ${brandContext.typography.headerFont}, Body: ${brandContext.typography.bodyFont}
            - Brand Voice: ${brandContext.voiceGuidelines.tonePillars.joinToString { it.title }}
            """ else ""}
            Provide actionable, sophisticated, concise, and inspiring branding advice.
        """.trimIndent()

        val contents = JSONArray()
        // Append history
        for (msg in history.takeLast(10)) {
            val role = if (msg.role == "user") "user" else "model"
            contents.put(JSONObject().apply {
                put("role", role)
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", msg.text) })
                })
            })
        }
        // Append current user message
        contents.put(JSONObject().apply {
            put("role", "user")
            put("parts", JSONArray().apply {
                put(JSONObject().apply { put("text", userMessage) })
            })
        })

        val requestJson = JSONObject().apply {
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", systemRoleInstruction) })
                })
            })
            put("contents", contents)
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
            })
        }

        try {
            val rawResponse = executePost(resolvedModel, requestJson.toString())
            extractTextFromCandidates(rawResponse) ?: "I'm reviewing your brand notes. Could you elaborate on that aspect?"
        } catch (e: Exception) {
            Log.e("GeminiApiClient", "chatWithCreativeDirector error", e)
            "Creative Director note: ${e.message}"
        }
    }

    private fun executePost(model: String, bodyJson: String): String {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
        val requestBody = bodyJson.toRequestBody(jsonMediaType)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val response = okHttpClient.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""
        if (!response.isSuccessful) {
            throw Exception("HTTP ${response.code}: $responseBody")
        }
        return responseBody
    }

    private fun extractTextFromCandidates(jsonString: String): String? {
        return try {
            val root = JSONObject(jsonString)
            val candidates = root.optJSONArray("candidates")
            val first = candidates?.optJSONObject(0)
            val content = first?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val textBuilder = StringBuilder()
            if (parts != null) {
                for (i in 0 until parts.length()) {
                    val p = parts.getJSONObject(i)
                    if (p.has("text")) {
                        textBuilder.append(p.getString("text"))
                    }
                }
            }
            val res = textBuilder.toString().trim()
            if (res.isNotBlank()) res else null
        } catch (e: Exception) {
            null
        }
    }

    private fun parseBrandBibleJson(
        jsonString: String,
        fallbackName: String,
        fallbackIndustry: String,
        fallbackMission: String,
        fallbackTone: String
    ): BrandBible {
        // Strip any markdown fences ```json ... ```
        val clean = jsonString
            .replace("```json", "")
            .replace("```", "")
            .trim()

        val root = JSONObject(clean)
        val brandName = root.optString("brandName", fallbackName)
        val industry = root.optString("industry", fallbackIndustry)
        val mission = root.optString("mission", fallbackMission)
        val vision = root.optString("vision", "")
        val slogan = root.optString("slogan", "Inspiring Tomorrow, Today")
        val archetype = root.optString("archetype", "The Creator")

        // Primary Logo
        val logoObj = root.optJSONObject("primaryLogo") ?: JSONObject()
        val forbiddenList = mutableListOf<String>()
        val fArray = logoObj.optJSONArray("forbiddenUses")
        if (fArray != null) {
            for (i in 0 until fArray.length()) forbiddenList.add(fArray.getString(i))
        }
        val primaryLogo = LogoSpec(
            conceptTitle = logoObj.optString("conceptTitle", "The Central Mark"),
            conceptDescription = logoObj.optString("conceptDescription", "Geometric representation of brand purpose."),
            emblemShape = logoObj.optString("emblemShape", "Geometric Emblem"),
            clearspaceRule = logoObj.optString("clearspaceRule", "0.5x margin clearance."),
            minimumSizePrint = logoObj.optString("minimumSizePrint", "0.75 in"),
            minimumSizeDigital = logoObj.optString("minimumSizeDigital", "32 px"),
            visualPrompt = logoObj.optString("visualPrompt", "A minimalist logo for $brandName"),
            forbiddenUses = if (forbiddenList.isEmpty()) listOf("Do not stretch", "Do not alter approved colors") else forbiddenList
        )

        // Secondary Marks
        val secondaryMarks = mutableListOf<SecondaryMarkSpec>()
        val secArray = root.optJSONArray("secondaryMarks")
        if (secArray != null) {
            for (i in 0 until secArray.length()) {
                val item = secArray.getJSONObject(i)
                secondaryMarks.add(
                    SecondaryMarkSpec(
                        type = item.optString("type", "Secondary Mark"),
                        title = item.optString("title", "Mark"),
                        description = item.optString("description", ""),
                        bestUseCase = item.optString("bestUseCase", ""),
                        symbolStyle = item.optString("symbolStyle", "")
                    )
                )
            }
        }
        if (secondaryMarks.isEmpty()) {
            secondaryMarks.addAll(BrandPresets.createFallbackBrandBible().secondaryMarks)
        }

        // Palette (ensure exactly 5 colors)
        val palette = mutableListOf<ColorSwatchSpec>()
        val palArray = root.optJSONArray("palette")
        if (palArray != null) {
            for (i in 0 until palArray.length()) {
                val item = palArray.getJSONObject(i)
                palette.add(
                    ColorSwatchSpec(
                        hex = item.optString("hex", "#2563EB"),
                        name = item.optString("name", "Color ${i + 1}"),
                        role = item.optString("role", "Brand Palette"),
                        usageNotes = item.optString("usageNotes", ""),
                        contrastRating = item.optString("contrastRating", "High Contrast"),
                        rgb = item.optString("rgb", ""),
                        cmyk = item.optString("cmyk", "")
                    )
                )
            }
        }
        if (palette.size < 5) {
            val fallback = BrandPresets.createFallbackBrandBible().palette
            while (palette.size < 5 && palette.size < fallback.size) {
                palette.add(fallback[palette.size])
            }
        }

        // Typography
        val typoObj = root.optJSONObject("typography") ?: JSONObject()
        val typography = TypographySpec(
            headerFont = typoObj.optString("headerFont", "Playfair Display"),
            headerCategory = typoObj.optString("headerCategory", "Editorial Serif"),
            bodyFont = typoObj.optString("bodyFont", "Inter"),
            bodyCategory = typoObj.optString("bodyCategory", "Clean Neo-Grotesque"),
            pairingRationale = typoObj.optString("pairingRationale", "Harmonious pairing."),
            headerWeights = typoObj.optString("headerWeights", "Bold 700 / ExtraBold 800"),
            bodyWeights = typoObj.optString("bodyWeights", "Regular 400 / Medium 500"),
            headerLetterSpacing = typoObj.optString("headerLetterSpacing", "-0.015em"),
            bodyLineHeight = typoObj.optString("bodyLineHeight", "1.6x"),
            googleFontsUrl = typoObj.optString("googleFontsUrl", "")
        )

        // Voice
        val voiceObj = root.optJSONObject("voiceGuidelines") ?: JSONObject()
        val pillars = mutableListOf<TonePillar>()
        val pArray = voiceObj.optJSONArray("tonePillars")
        if (pArray != null) {
            for (i in 0 until pArray.length()) {
                val item = pArray.getJSONObject(i)
                pillars.add(TonePillar(item.optString("title"), item.optString("description")))
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
        val voiceSpec = VoiceSpec(
            tonePillars = if (pillars.isNotEmpty()) pillars else BrandPresets.createFallbackBrandBible().voiceGuidelines.tonePillars,
            vocabularyDo = if (doList.isNotEmpty()) doList else BrandPresets.createFallbackBrandBible().voiceGuidelines.vocabularyDo,
            vocabularyDont = if (dontList.isNotEmpty()) dontList else BrandPresets.createFallbackBrandBible().voiceGuidelines.vocabularyDont,
            positioningStatement = voiceObj.optString("positioningStatement", "Crafted for visionary excellence.")
        )

        return BrandBible(
            brandName = brandName,
            industry = industry,
            mission = mission,
            vision = vision,
            slogan = slogan,
            archetype = archetype,
            primaryLogo = primaryLogo,
            secondaryMarks = secondaryMarks,
            palette = palette.take(5),
            typography = typography,
            voiceGuidelines = voiceSpec
        )
    }
}
