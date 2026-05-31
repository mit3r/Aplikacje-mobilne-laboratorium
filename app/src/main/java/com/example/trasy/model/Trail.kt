package com.example.trasy.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

enum class TrailType {
    RUNNING,
    CYCLING,
}

/**
 * Adapter GSON, który dokleja bazowy adres URL do relatywnej ścieżki zdjęcia.
 */
class ImageUrlDeserializer : JsonDeserializer<String> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): String {
        val path = json.asString
        return if (path.startsWith("http")) {
            path
        } else {
            "https://2g4f.web.svpj.pl/$path"
        }
    }
}

data class Trail(
    val id: Int,
    val title: String,
    val distance: Float,
    val type: TrailType,
    val description: String,
    @SerializedName("photo")
    @JsonAdapter(ImageUrlDeserializer::class)
    val imageUrl: String
)
