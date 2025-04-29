package com.el_aouthmanie.flowinventory.screens.insertionScreen

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.el_aouthmanie.flowinventory.module.SQLManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InventoryCheckViewModel : ViewModel() {
    private val _quantity = MutableStateFlow("0")
    val quantity: StateFlow<String> = _quantity

    private val _expectedQuantity = MutableStateFlow("0")
    val expectedQuantity: StateFlow<String> = _expectedQuantity

    private val _df = MutableStateFlow("0")
    val df: StateFlow<String> = _df

    private val _codeBox = MutableStateFlow("")
    val codeBox: StateFlow<String> = _codeBox

    private val _batch = MutableStateFlow("")
    val batch: StateFlow<String> = _batch

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes

    fun updateQuantity(value: String) {
        _quantity.value = value
        _df.value = ((expectedQuantity.value.toDoubleOrNull() ?: 0.0) - (value.toDoubleOrNull() ?: 0.0)).toString()
    }

    fun updateCodeBox(value: String) {
        _codeBox.value = value
    }

    fun updateBatch(value: String) {
        _batch.value = value
    }

    fun updateNotes(value: String) {
        _notes.value = value
    }

    fun checkCodeBox(ctx: Context) {
        viewModelScope.launch {
            val qty: Double? = try {
                SQLManager.getCurrentQuantityInBox(_codeBox.value, 1)
            } catch (e: Exception) {
                null
            }
            if (qty == null) {
                Toast.makeText(ctx, "invalid box", Toast.LENGTH_SHORT).show()
                _codeBox.value = ""
            } else {
                _expectedQuantity.value = qty.toString()
            }
        }
    }

    fun clearFields() {
        _quantity.value = "0"
        _expectedQuantity.value = "0"
        _codeBox.value = ""
        _batch.value = ""
        _notes.value = ""
    }
}