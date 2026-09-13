package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiApiClient
import com.example.data.local.AppDatabase
import com.example.data.local.BrandRepository
import com.example.data.model.BrandBible
import com.example.data.model.BrandPresets
import com.example.data.model.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BrandViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BrandRepository
    private val geminiApiClient = GeminiApiClient()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = BrandRepository(db.brandBibleDao())
    }

    val savedBibles: StateFlow<List<BrandBible>> = repository.allBrandBibles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Brand Bible
    private val _activeBible = MutableStateFlow<BrandBible>(BrandPresets.createFallbackBrandBible())
    val activeBible: StateFlow<BrandBible> = _activeBible.asStateFlow()

    // Form inputs
    val companyName = MutableStateFlow("AuraTerra")
    val industry = MutableStateFlow("Sustainable Cleantech")
    val mission = MutableStateFlow("We build zero-carbon modular homes made from regenerative bio-composites, making beautiful, self-sustaining living accessible to every modern community.")
    val tone = MutableStateFlow("Warm, Organic & Visionary")

    // Loading states
    val isGeneratingBible = MutableStateFlow(false)
    val isPolishingMission = MutableStateFlow(false)
    val isGeneratingImage = MutableStateFlow(false)
    val isAuditing = MutableStateFlow(false)
    val isSearchingTrends = MutableStateFlow(false)
    val isChatSending = MutableStateFlow(false)

    val errorMessage = MutableStateFlow<String?>(null)
    val successMessage = MutableStateFlow<String?>(null)

    // Creative Director Chat
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    fun setCompanyName(value: String) { companyName.value = value }
    fun setIndustry(value: String) { industry.value = value }
    fun setMission(value: String) { mission.value = value }
    fun setTone(value: String) { tone.value = value }

    fun clearFeedback() {
        errorMessage.value = null
        successMessage.value = null
    }

    /**
     * Fast Polish of company mission using gemini-3.1-flash-lite-preview.
     */
    fun polishMission() {
        val currentMission = mission.value
        val currentIndustry = industry.value
        if (currentMission.isBlank()) return

        viewModelScope.launch {
            isPolishingMission.value = true
            try {
                val polished = geminiApiClient.polishMission(currentMission, currentIndustry)
                mission.value = polished
                successMessage.value = "Mission statement polished with Gemini Flash-Lite!"
            } catch (e: Exception) {
                errorMessage.value = "Polish error: ${e.message}"
            } finally {
                isPolishingMission.value = false
            }
        }
    }

    /**
     * Full Brand Bible Generation using gemini-3.1-pro-preview with JSON schema.
     */
    fun generateBrandBible(onComplete: () -> Unit = {}) {
        val name = companyName.value.trim()
        val ind = industry.value.trim()
        val mis = mission.value.trim()
        val t = tone.value.trim()

        if (name.isBlank() || mis.isBlank()) {
            errorMessage.value = "Please enter your company name and mission."
            return
        }

        viewModelScope.launch {
            isGeneratingBible.value = true
            clearFeedback()
            try {
                val bible = geminiApiClient.generateBrandBible(name, ind, mis, t)
                _activeBible.value = bible
                // Auto-save to Room library
                val savedId = repository.saveBrandBible(bible)
                _activeBible.value = bible.copy(id = savedId)
                successMessage.value = "Brand Bible for $name successfully generated & saved!"
                onComplete()
            } catch (e: Exception) {
                errorMessage.value = "Generation error: ${e.message}"
            } finally {
                isGeneratingBible.value = false
            }
        }
    }

    /**
     * High-Quality Logo Image Generation using gemini-3-pro-image-preview with 1K, 2K, 4K size affordance.
     */
    fun generateLogoImage(prompt: String, size: String = "1K") {
        viewModelScope.launch {
            isGeneratingImage.value = true
            clearFeedback()
            try {
                val result = geminiApiClient.generateLogoImage(prompt, size)
                if (result != null) {
                    val (bitmap, base64) = result
                    val updated = _activeBible.value.copy(
                        generatedLogoBitmap = bitmap,
                        generatedLogoBase64 = base64
                    )
                    _activeBible.value = updated
                    if (updated.id != 0L) {
                        repository.updateGeneratedLogo(updated.id, base64)
                    }
                    successMessage.value = "High-resolution $size Logo asset generated with gemini-3-pro-image-preview!"
                } else {
                    errorMessage.value = "Image rendering returned empty. Check API key configuration or prompt."
                }
            } catch (e: Exception) {
                errorMessage.value = "Logo image generation error: ${e.message}"
            } finally {
                isGeneratingImage.value = false
            }
        }
    }

    /**
     * Deep Strategic Brand Audit using gemini-3.1-pro-preview with thinkingLevel HIGH.
     */
    fun runDeepThinkingAudit() {
        val current = _activeBible.value
        viewModelScope.launch {
            isAuditing.value = true
            clearFeedback()
            try {
                val audit = geminiApiClient.deepThinkingStrategyAudit(current)
                val updated = current.copy(deepStrategyAudit = audit)
                _activeBible.value = updated
                if (updated.id != 0L) {
                    repository.updateBrandBible(updated)
                }
                successMessage.value = "Deep thinking strategic audit completed!"
            } catch (e: Exception) {
                errorMessage.value = "Audit error: ${e.message}"
            } finally {
                isAuditing.value = false
            }
        }
    }

    /**
     * Google Search Grounded Market Trends using gemini-3.5-flash with googleSearch tool.
     */
    fun refreshSearchTrends() {
        val current = _activeBible.value
        viewModelScope.launch {
            isSearchingTrends.value = true
            clearFeedback()
            try {
                val trends = geminiApiClient.searchIndustryTrends(current.industry)
                val updated = current.copy(industryTrends = trends)
                _activeBible.value = updated
                if (updated.id != 0L) {
                    repository.updateBrandBible(updated)
                }
                successMessage.value = "Industry search trends grounded with Google Search!"
            } catch (e: Exception) {
                errorMessage.value = "Search error: ${e.message}"
            } finally {
                isSearchingTrends.value = false
            }
        }
    }

    /**
     * Multi-turn Creative Director Chat with message history and role instructions.
     */
    fun sendChatMessage(text: String, modelName: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(role = "user", text = text)
        val currentList = _chatMessages.value + userMsg
        _chatMessages.value = currentList

        viewModelScope.launch {
            isChatSending.value = true
            try {
                val reply = geminiApiClient.chatWithCreativeDirector(
                    history = currentList,
                    userMessage = text,
                    modelName = modelName,
                    brandContext = _activeBible.value
                )
                val modelMsg = ChatMessage(role = "model", text = reply)
                _chatMessages.value = _chatMessages.value + modelMsg
            } catch (e: Exception) {
                val errorMsg = ChatMessage(role = "model", text = "Sorry, I ran into an issue: ${e.message}")
                _chatMessages.value = _chatMessages.value + errorMsg
            } finally {
                isChatSending.value = false
            }
        }
    }

    fun clearChat() {
        _chatMessages.value = emptyList()
    }

    fun selectBible(bible: BrandBible) {
        _activeBible.value = bible
        companyName.value = bible.brandName
        industry.value = bible.industry
        mission.value = bible.mission
        successMessage.value = "Switched to ${bible.brandName} Brand Bible"
    }

    fun saveCurrentBible() {
        viewModelScope.launch {
            val current = _activeBible.value
            val id = repository.saveBrandBible(current)
            _activeBible.value = current.copy(id = id)
            successMessage.value = "Saved ${current.brandName} to Library!"
        }
    }

    fun toggleFavorite(bible: BrandBible) {
        viewModelScope.launch {
            val nextState = !bible.isFavorite
            repository.toggleFavorite(bible.id, nextState)
            if (_activeBible.value.id == bible.id) {
                _activeBible.value = _activeBible.value.copy(isFavorite = nextState)
            }
        }
    }

    fun deleteBible(bible: BrandBible) {
        viewModelScope.launch {
            repository.deleteBrandBible(bible.id)
            successMessage.value = "Deleted ${bible.brandName}"
        }
    }
}
