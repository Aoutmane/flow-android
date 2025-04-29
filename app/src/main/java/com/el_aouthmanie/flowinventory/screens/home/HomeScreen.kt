package com.el_aouthmanie.flowinventory.screens.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.el_aouthmanie.flowinventory.module.InventoryCheck
import com.el_aouthmanie.flowinventory.module.SQLManager

@Composable
fun HomeScreen(
    check : InventoryCheck?,
    onStartClick: () -> Unit
) {

    val bxs = check?.inventoryStats?.boxCount ?: 10
    val chks = check?.inventoryStats?.checkCount ?: 10


    Log.e("chkx",chks.toString())
    Log.e("chkxd",bxs.toString())

    val progress = remember(bxs,chks) {
        if (chks == 0) 0f else chks.toFloat() / bxs
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("General Details", style = MaterialTheme.typography.headlineMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            DateCard("Start Date", check!!.start_date)
            DateCard("End Date", check.end_date ?:  check.start_date)
        }

        ProgressCard("Total Checked", progress)
        ProgressCard("Remaining", 1f - progress)
        Spacer(
            Modifier.weight(2f)
        )
        Button(
            onClick = onStartClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text("Start Inserting", fontSize = 18.sp)
        }
    }
}

@Composable
fun DateCard(title: String, date: String) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(title, style = MaterialTheme.typography.labelMedium)
            Text(date, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun ProgressCard(label: String, progress: Float) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            color = Color(0xFF4CAF50),
            )
        }
    }
}

//@Preview(showBackground = true, showSystemUi = true, )
//@Composable
//private fun f() {
//    HomeScreen(){}
//}