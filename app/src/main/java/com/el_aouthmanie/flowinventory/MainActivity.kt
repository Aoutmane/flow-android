package com.el_aouthmanie.flowinventory

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.el_aouthmanie.flowinventory.module.SQLManager
import com.el_aouthmanie.flowinventory.module.SessionManager
import com.el_aouthmanie.flowinventory.screens.home.HomeScreen
import com.el_aouthmanie.flowinventory.screens.insertionScreen.InventoryCheckDetailsForm
import com.el_aouthmanie.flowinventory.screens.loadingScreen.LoadingScreen
import com.el_aouthmanie.flowinventory.screens.login.LoginScreen
import com.el_aouthmanie.flowinventory.ui.theme.FlowInventoryTheme
import kotlinx.coroutines.launch
import kotlin.system.exitProcess


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            FlowInventoryTheme {
                LoadingScreen() {
                    MainScreen()
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val ctx = LocalContext.current
    val scp = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier,
                title = {
                    Row {
                        Text(
                            "flow : inventory check"
                        )
                        Spacer(
                            modifier = Modifier.weight(2f)
                        )
                        IconButton(onClick = {
                            SessionManager.removeLogSession(ctx)
                            exitProcess(0)
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "exit"
                            )
                        }
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        val navController = rememberNavController()
        val ctx = LocalContext.current

        val startDestination = "login"


        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = startDestination
        ) {
            composable("login") {
                val scp = rememberCoroutineScope()
                LoginScreen { log, pass ->
                    scp.launch {
                        val id = SQLManager.login(log, pass)
                        Log.e("hah", SQLManager.getCurrentQuantityInBox("v dssvd",1).toString())
                        if (id != null) {
                            SessionManager.saveLogin(ctx, log, pass)
                            SessionManager.saveId(ctx,id)

                            navController.navigate("home")
                        } else {
                            Toast.makeText(
                                ctx,
                                "Wrong USERNAME or PASSWORD !",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }
            }
            composable("home") {
                LoadingScreen {
                    Log.e("date dara", it.toString())
                    HomeScreen(
                        it
                    ) {
                        navController.navigate("insertionScreen")
                    }
                }
            }
            composable("insertionScreen") {
                LoadingScreen {

                    InventoryCheckDetailsForm(it)
                    { item ->
                        scp.launch {
                            val r = SQLManager.insertInventoryCheckDetails(item)

                            if (r){
                                Toast.makeText(ctx,"inserted successfully",Toast.LENGTH_SHORT).show()

                            } else {
                                Toast.makeText(ctx,"insertion fieled",Toast.LENGTH_SHORT).show()

                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun GreetingPreview() {
    FlowInventoryTheme {
        MainScreen()
    }
}

