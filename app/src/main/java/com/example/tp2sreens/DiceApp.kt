package com.example.tp2sreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlin.random.Random

@Composable
fun DiceApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {

        // Ecrã principal
        composable("main") {
            MainDiceScreen(
                onGoToResult = { value -> navController.navigate("result/$value") }
            )
        }

        // Ecrãs de resultado: "value" = ecrã/dado; "prev" = dado atual (só vem do jogo do ecrã 6)
        composable(
            route = "result/{value}?prev={prev}",
            arguments = listOf(
                navArgument("value") { type = NavType.IntType },
                navArgument("prev") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val value = backStackEntry.arguments?.getInt("value") ?: 1
            val prev = backStackEntry.arguments?.getInt("prev") ?: 0

            val goTo: (Int) -> Unit = { navController.navigate("result/$it") }
            val goToFromGame: (Int, Int) -> Unit = { newDie, currentDie ->
                navController.navigate("result/$newDie?prev=$currentDie")
            }
            val back: () -> Unit = { navController.popBackStack() }

            Column(modifier = Modifier.fillMaxSize()) {
                // Se vimos do jogo (ecrã 6), mostra os dois dados: o atual e o novo
                if (prev != 0) {
                    DicePair(current = prev, new = value)
                }
                Box(modifier = Modifier.weight(1f)) {
                    when (value) {
                        1 -> DiceResult1(onBack = back)
                        2 -> DiceResult2(onBack = back)
                        3 -> DiceResult3(onBack = back)
                        4 -> DiceResult4(onGoTo = goTo, onBack = back)
                        5 -> DiceResult5(onBack = back)
                        else -> DiceResult6(onGoToResult = goToFromGame, onBack = back)
                    }
                }
            }
        }
    }
}

// Mostra o dado atual e o novo lado a lado (resultado do jogo)
@Composable
fun DicePair(current: Int, new: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Current Die")
            DiceImage(current, size = 70.dp)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("New Die")
            DiceImage(new, size = 70.dp)
        }
    }
}

@Composable
fun MainDiceScreen(onGoToResult: (Int) -> Unit) {
    // rememberSaveable: o valor mantém-se quando voltas do "Back"
    var result by rememberSaveable { mutableIntStateOf(1) }

    ScreenColumn {
        Text("Main Dice Screen", style = MaterialTheme.typography.titleLarge)
        DiceImage(result)
        Text("Current Roll: $result")
        // Lança as vezes que quiseres, sem sair do ecrã
        Button(onClick = { result = Random.nextInt(1, 7) }) { Text("Roll Die") }
        // Só aqui há transição
        Button(onClick = { onGoToResult(result) }) { Text("Go to Result") }
    }
}