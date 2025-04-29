package com.el_aouthmanie.flowinventory.screens.loadingScreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.el_aouthmanie.flowinventory.module.InventoryCheck
import com.el_aouthmanie.flowinventory.module.SQLManager
import kotlinx.coroutines.launch
import kotlin.system.exitProcess



@Composable
fun LoadingScreen(content: @Composable (inventoryCheckResult: InventoryCheck?) -> Unit) {
    var isLoading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var inventoryCheckResult by remember { mutableStateOf<InventoryCheck?>(null) } // Holds the result of SQLManager
    val scp = rememberCoroutineScope()

    // Blurring background while loading
    Box(modifier = Modifier.fillMaxSize()) {
        // Apply blur effect and show CircularProgressIndicator while loading
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)) // semi-transparent overlay
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            LaunchedEffect(Unit) {
                scp.launch {
                    // Simulating a long operation
                    val c = SQLManager.getCurrentInventoryCheck()

                    // Store the result in inventoryCheckResult state
                    inventoryCheckResult = c
                    try {
                            val sts = SQLManager.getInventoryStats()
                            Log.e("sts",sts.toString())

                            c?.inventoryStats = sts
                        } catch (e : Exception){
                            e.printStackTrace()
                        }
                    // Check the result and handle loading and dialog visibility
                    if (c == null) {
                        isLoading = false
                        showDialog = true
                    } else {
                        isLoading = false
                    }
                }
            }
        }

        // Content passed as a parameter, passing the inventoryCheckResult to it
        if (
            !isLoading
        ){

            content(inventoryCheckResult)
        }
    }

    // Show AlertDialog after the operation
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { exitProcess(0) },
            title = {
                Text(text = "Exit?")
            },
            text = {
                Text("No inventory check is currently going!")
            },
            confirmButton = {
                Button(onClick = { exitProcess(0) }) {
                    Text("Exit")
                }
            }
        )
    }
}


