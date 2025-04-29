package com.el_aouthmanie.flowinventory.module

import android.util.Log
import com.el_aouthmanie.flowinventory.screens.insertionScreen.InventoryCheckDetails
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*

object SQLManager {
    private const val BASE_URL = "https://azzi-aoutmane.alwaysdata.net/index.php"
    private val client = OkHttpClient()
    private val gson = Gson()

    suspend fun isInventoryCheckActive(): Boolean = withContext(Dispatchers.IO) {
        val response = postForm(mapOf("action" to "isInventoryCheckActive")) ?: return@withContext false
        return@withContext gson.fromJson(response, IsActiveResponse::class.java).active
    }

    suspend fun insertInventoryCheckDetails(details: InventoryCheckDetails): Boolean = withContext(Dispatchers.IO) {
        val params = mutableMapOf(
            "action" to "insertInventoryCheckDetails",
            "inventory_check_id" to details.inventory_check_id.toString(),
            "quantity" to details.quantity.toString(),
            "expected_quantity" to details.expected_quantity.toString(),
            "code_box" to details.code_box,
            "batch" to details.batch,
        )
        details.performed_by?.let { params["performed_by"] = it.toString() }

        val response = postForm(params) ?: return@withContext false
        return@withContext response.contains("\"success\":true")
    }

    suspend fun login(log: String, pass: String): String? = withContext(Dispatchers.IO) {
        val response = postForm(mapOf("action" to "login", "username" to log, "password" to pass)) ?: return@withContext null
        val json = gson.fromJson(response, LoginResponse::class.java)
        return@withContext json.id.takeIf { it.isNotEmpty() }
    }

    suspend fun getCurrentInventoryCheck(): InventoryCheck? = withContext(Dispatchers.IO) {
        val response = postForm(mapOf("action" to "getCurrentInventoryCheck")) ?: return@withContext null
        val result = gson.fromJson(response, CurrentCheckResponse::class.java)
        return@withContext result.inventoryCheck?.toDomain()
    }

    suspend fun getCurrentQuantityInBox(codeBox: String, itemId: Int = 1): Double? = withContext(Dispatchers.IO) {
        val response = postForm(
            mapOf(
                "action" to "getCurrentQuantityInBox",
                "item_id" to itemId.toString(),
                "code_box" to codeBox
            )
        ) ?: return@withContext null
        val res = gson.fromJson(response, QuantityResponse::class.java)
        return@withContext res.currentQuantity
    }

    private fun postForm(params: Map<String, String>): String? {
        val formBody = FormBody.Builder().apply {
            params.forEach { (key, value) -> addEncoded(key, value) }
        }.build()

        val request = Request.Builder()
            .url(BASE_URL)
            .post(formBody)
            .build()

        client.newCall(request).execute().use { response ->
            return if (response.isSuccessful) response.body?.string() else null
        }
    }

    // Response classes for Gson parsing
    data class IsActiveResponse(@SerializedName("active") val active: Boolean)

    data class LoginResponse(@SerializedName("id") val id: String)

    data class CurrentCheckResponse(
        @SerializedName("inventory_check") val inventoryCheck: InventoryCheckDto?
    )

    data class InventoryCheckDto(
        @SerializedName("id") val id: Int,
        @SerializedName("status") val status: String,
        @SerializedName("start_date") val startDate: String,
        @SerializedName("end_date") val endDate: String?
    ) {
        fun toDomain(): InventoryCheck {
            return InventoryCheck(id, status, startDate, endDate ?: startDate)
        }
    }

    data class QuantityResponse(
        @SerializedName("current_quantity") val currentQuantity: Double
    )

    suspend fun getInventoryStats(): InventoryStats? = withContext(Dispatchers.IO) {
        val response = postForm(mapOf("action" to "getInventoryStats")) ?: return@withContext null
        return@withContext try {
            gson.fromJson(response, InventoryStats::class.java)
        } catch (e: Exception) {
            Log.e("SQLManager", "Error parsing InventoryStatsResponse", e)
            null
        }
    }
    data class InventoryStats(
        @SerializedName("success") val success: Boolean,
        @SerializedName("box_count") val boxCount: Int,
        @SerializedName("check_count") val checkCount: Int,
        @SerializedName("boxes_with_quantity") val boxesWithQuantity: List<BoxQuantity>
    )

    data class BoxQuantity(
        @SerializedName("code_box") val codeBox: String,
        @SerializedName("current_quantity") val currentQuantity: String
    )




}
data class InventoryCheck(
    val inventory_check_id: Int,
    val status: String,
    val start_date: String,
    val end_date: String?,

    var inventoryStats: SQLManager.InventoryStats?  = null
)