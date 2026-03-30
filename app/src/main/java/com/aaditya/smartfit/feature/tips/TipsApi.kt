package com.aaditya.smartfit.feature.tips

import com.aaditya.smartfit.core.ui.components.TipCardModel
import kotlin.math.max
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val DUMMY_JSON_BASE_URL = "https://dummyjson.com/"
private const val WGER_BASE_URL = "https://wger.de/api/v2/"

interface DummyJsonTipsApiService {
    @GET("recipes")
    suspend fun getRecipes(
        @Query("limit") limit: Int = 12,
        @Query("skip") skip: Int = 0
    ): DummyJsonRecipesResponse
}

interface WgerTipsApiService {
    @GET("exerciseinfo/")
    suspend fun getExercises(
        @Query("language") language: Int = 2,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): WgerExerciseResponse
}

data class DummyJsonRecipesResponse(
    val recipes: List<DummyJsonRecipeDto> = emptyList()
)

data class DummyJsonRecipeDto(
    val id: Int,
    val name: String,
    val instructions: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val image: String,
    val prepTimeMinutes: Int? = null,
    val cookTimeMinutes: Int? = null
)

data class WgerExerciseResponse(
    val results: List<WgerExerciseDto> = emptyList()
)

data class WgerExerciseDto(
    val id: Int,
    val category: WgerCategoryDto? = null,
    val images: List<WgerExerciseImageDto> = emptyList(),
    val translations: List<WgerExerciseTranslationDto> = emptyList()
)

data class WgerCategoryDto(
    val name: String = ""
)

data class WgerExerciseImageDto(
    val image: String = ""
)

data class WgerExerciseTranslationDto(
    val name: String = "",
    val description: String = "",
    val language: Int? = null
)

object TipsRemoteSource {
    private val dummyJsonService: DummyJsonTipsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(DUMMY_JSON_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DummyJsonTipsApiService::class.java)
    }

    private val wgerService: WgerTipsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(WGER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WgerTipsApiService::class.java)
    }

    suspend fun fetchTips(limit: Int = 24): List<TipCardModel> {
        val nutritionTips = runCatching {
            dummyJsonService
                .getRecipes(limit = 12)
                .recipes
                .map { recipe -> recipe.toNutritionTipCard() }
        }.getOrDefault(emptyList())

        val workoutAndLifestyleTips = runCatching {
            wgerService
                .getExercises(limit = 24)
                .results
                .mapNotNull { exercise -> exercise.toWorkoutOrLifestyleTipCard() }
                .take(limit)
        }.getOrDefault(emptyList())

        val mergedTips = (workoutAndLifestyleTips + nutritionTips)
            .distinctBy { tip -> tip.id }

        if (mergedTips.isEmpty()) {
            throw IllegalStateException("Unable to fetch tips from remote sources")
        }

        return mergedTips
    }
}

private fun DummyJsonRecipeDto.toNutritionTipCard(): TipCardModel {
    val categoryLabel = "Nutrition"

    val instructionPreview = instructions
        .take(2)
        .joinToString(separator = " ")
        .ifBlank {
            tags
                .take(4)
                .joinToString(separator = ", ")
        }

    val fallbackDescription = "Smart routine guidance for healthier daily habits."
    val description = instructionPreview
        .ifBlank { fallbackDescription }
        .trim()

    val estimatedMinutes = max(
        2,
        ((prepTimeMinutes ?: 0) + (cookTimeMinutes ?: 0)).coerceAtLeast(2) / 8
    )

    return TipCardModel(
        id = "nutrition-$id",
        title = name,
        description = description.truncate(maxLength = 220),
        imageUrl = image,
        categoryLabel = categoryLabel,
        readDurationLabel = "$estimatedMinutes min read"
    )
}

private fun WgerExerciseDto.toWorkoutOrLifestyleTipCard(): TipCardModel? {
    val previewImage = images
        .firstOrNull { imageItem -> imageItem.image.isNotBlank() }
        ?.image
        ?: return null

    val preferredTranslation = translations
        .firstOrNull { translation -> translation.language == 2 && translation.name.isNotBlank() }
        ?: translations.firstOrNull { translation -> translation.name.isNotBlank() }
        ?: return null

    val title = preferredTranslation.name.trim()
    if (title.isBlank()) return null

    val cleanDescription = preferredTranslation.description
        .removeHtmlTags()
        .ifBlank { "Build consistency through structured movement, mobility, and recovery habits." }

    val categoryName = category?.name.orEmpty()
    val categoryLabel = if (isLifestyleTip(title, categoryName, cleanDescription)) {
        "Lifestyle"
    } else {
        "Workout"
    }

    val estimatedMinutes = max(
        2,
        cleanDescription
            .split(Regex("\\s+"))
            .filter { token -> token.isNotBlank() }
            .size / 45
    )

    return TipCardModel(
        id = "exercise-$id",
        title = title,
        description = cleanDescription.truncate(maxLength = 220),
        imageUrl = previewImage,
        categoryLabel = categoryLabel,
        readDurationLabel = "$estimatedMinutes min read"
    )
}

private fun isLifestyleTip(title: String, categoryName: String, description: String): Boolean {
    val normalized = "$title $categoryName $description".lowercase()
    val lifestyleKeywords = listOf(
        "stretch",
        "mobility",
        "warm up",
        "cool down",
        "yoga",
        "breath",
        "walk",
        "posture",
        "recovery"
    )

    return lifestyleKeywords.any { keyword -> normalized.contains(keyword) }
}

private fun String.removeHtmlTags(): String {
    return this
        .replace(Regex("<[^>]*>"), " ")
        .replace("&nbsp;", " ")
        .replace(Regex("\\s+"), " ")
        .trim()
}

private fun String.truncate(maxLength: Int): String {
    return if (length <= maxLength) {
        this
    } else {
        take(maxLength).trimEnd() + "…"
    }
}

