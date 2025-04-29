package com.el_aouthmanie.flowinventory.screens.insertionScreen


import android.app.AlertDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.el_aouthmanie.flowinventory.module.InventoryCheck
import com.el_aouthmanie.flowinventory.module.SQLManager
import com.el_aouthmanie.flowinventory.module.SessionManager
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@Composable
fun InventoryCheckDetailsForm(
    check: InventoryCheck?,
    onSubmit: (InventoryCheckDetails) -> Unit,
) {
    var inventoryCheckId = check!!.inventory_check_id
    val ctx = LocalContext.current
    val scp = rememberCoroutineScope()

    var quantity by remember { mutableStateOf("0") }
    var expectedQuantity by remember { mutableStateOf("0") }
    var df by remember { mutableStateOf("0") }
    var codeBox by remember { mutableStateOf("") }
    var batch by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var performedBy by remember { mutableStateOf("") }


    val myId = SessionManager.getId(ctx) ?: exitProcess(0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            OutlinedTextField(
                value = codeBox,
                onValueChange = { codeBox = it },
                label = { Text("Code Box") },
                modifier = Modifier.weight(8f)
            )
            Button(
                onClick = {
                    scp.launch {
                        var qty: Double? = try {
                            SQLManager.getCurrentQuantityInBox(codeBox, 1)
                        } catch (e: Exception) {
                            null
                        }
                        if (qty == null) {
                            Toast.makeText(ctx, "invalid box", Toast.LENGTH_SHORT).show()
                            codeBox = ""
                        }
                        expectedQuantity = qty.toString()
                    }
                }
            ) {
                Text("Check")
            }
        }
        Text("Insert Inventory Check Detail", style = MaterialTheme.typography.headlineSmall)

        Column {
            Row {
                Text(
                    "Expected quantity :",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.weight(2f))
                Text(
                    expectedQuantity,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 5.dp))
            Row {

                Text(
                    "difference :",
                    style = MaterialTheme.typography.titleLarge

                )
                Spacer(Modifier.weight(2f))
                Text(
                    df,
                    style = MaterialTheme.typography.titleLarge
                )

            }
        }


        OutlinedTextField(
            value = quantity,
            onValueChange = {
                quantity = it
                df = ((expectedQuantity.toDoubleOrNull() ?: 0.0) - (quantity.toDoubleOrNull()
                    ?: 0.0)).toString()

            },
            label = { Text("Quantity") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )







        OutlinedTextField(
            value = batch,
            onValueChange = { batch = it },
            label = { Text("Batch") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = notes,
            minLines = 5,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.weight(2f))
        Icon(
            Icons.Outlined.Warning,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(50.dp)
                // todo
                .clickable(true) {

                },
            contentDescription = "warning",
            tint = Color.Red
        )
        Text(
            text = "you are responsible of all the informations provided on that form !",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )
        Button(
            onClick = {

                if (
                    quantity.toDoubleOrNull() == null ||
                    expectedQuantity.toDoubleOrNull() == null ||
                    codeBox.isEmpty() ||
                    batch.isEmpty()
                ) {
                    Toast.makeText(ctx, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                val details = InventoryCheckDetails(
                    inventory_check_id = inventoryCheckId.toInt(),
                    quantity = quantity.toDoubleOrNull() ?: 0.0,
                    expected_quantity = expectedQuantity.toDoubleOrNull() ?: 0.0,
                    code_box = codeBox,
                    batch = batch,
                    performed_by = myId.toIntOrNull()
                )

                if (quantity.toDoubleOrNull()!! != expectedQuantity.toDoubleOrNull()!!) {
                    AlertDialog.Builder(ctx)
                        .setTitle("different quantity !")
                        .setMessage("are your sure ?!")
                        .setPositiveButton("continue") { dialog, _ ->
                            onSubmit(details)
                            dialog.dismiss() }
                        .setNegativeButton ("cancel"){ dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                    return@Button
                }


                onSubmit(details)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("insert")
        }
        Button(
            onClick = {
                quantity = "0"
                expectedQuantity = "0"
                codeBox = ""
                batch = ""
                performedBy = ""
            }
        ) {
            Text(
                "Clear"
            )
        }
    }

}


data class InventoryCheckDetails(
    val inventory_check_id: Int,
    val quantity: Double,
    val expected_quantity: Double,
    val code_box: String,
    val batch: String,
    val performed_by: Int? = null
)

//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//private fun h2() {
//    InventoryCheckDetailsForm ({_->})
//
//
//}