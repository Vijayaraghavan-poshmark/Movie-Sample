package com.example.movie.ui.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.example.movie.model.Movie
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object DetailNavType {
    val detailType = object : NavType<Movie>(
        isNullableAllowed = false
    ) {
        override fun get(bundle: Bundle, key: String): Movie? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): Movie {
            return Json.decodeFromString(Uri.decode(value))
        }

        override fun put(bundle: Bundle, key: String, value: Movie) {
            bundle.putString(key, Json.encodeToString(value))
        }

        override fun serializeAsValue(value: Movie): String {
            return Uri.encode(Json.encodeToString(value))
        }
    }
}