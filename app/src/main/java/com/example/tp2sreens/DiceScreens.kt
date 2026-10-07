package com.example.tp2sreens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

// ---------- Componentes auxiliares ----------

// Desenha o dado com pintinhas
@Composable
fun DiceImage(value: Int, modifier: Modifier = Modifier, size: Dp = 120.dp) {
    Box(
        modifier = modifier
            .size(size)
            .background(Color(0xFFE6E0F5), RoundedCornerShape(16.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val r = w * 0.08f
            val l = w * 0.25f
            val cx = w * 0.5f
            val rt = w * 0.75f
            val t = h * 0.25f
            val cy = h * 0.5f
            val b = h * 0.75f

            val dots = when (value) {
                1 -> listOf(Offset(cx, cy))
                2 -> listOf(Offset(l, t), Offset(rt, b))
                3 -> listOf(Offset(l, t), Offset(cx, cy), Offset(rt, b))
                4 -> listOf(Offset(l, t), Offset(rt, t), Offset(l, b), Offset(rt, b))
                5 -> listOf(Offset(l, t), Offset(rt, t), Offset(cx, cy), Offset(l, b), Offset(rt, b))
                else -> listOf(
                    Offset(l, t), Offset(rt, t),
                    Offset(l, cy), Offset(rt, cy),
                    Offset(l, b), Offset(rt, b)
                )
            }
            dots.forEach { drawCircle(color = Color.Black, radius = r, center = it) }
        }
    }
}

// Coluna centrada que todos os ecrãs reutilizam
@Composable
fun ScreenColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

@Composable
fun BackButton(onBack: () -> Unit) {
    OutlinedButton(onClick = onBack) { Text("Back") }
}

// ---------- Die Result 1: Travel Badge ----------
@Composable
fun DiceResult1(onBack: () -> Unit) {
    var temperature by remember { mutableIntStateOf(21) }

    ScreenColumn {
        Text("Die Result: 1", style = MaterialTheme.typography.titleLarge)
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("✈️ Your Favorite Travel Badge 🌴", fontWeight = FontWeight.Bold)
                Text("Santorini, Greece", fontSize = 20.sp)
                Text("Mock Temperature: $temperature°C")
                Button(onClick = { temperature = Random.nextInt(10, 36) }) {
                    Text("Refresh Temperature")
                }
            }
        }
        BackButton(onBack)
    }
}

// ---------- Die Result 2: incrementar de 2 em 2 ----------
@Composable
fun DiceResult2(onBack: () -> Unit) {
    var value by remember { mutableIntStateOf(2) }

    ScreenColumn {
        Text("Die Result: 2", style = MaterialTheme.typography.titleLarge)
        DiceImage(value)
        Text("Current Value: $value")
        // Soma 2 e volta ao início quando passa de 6 (5->1, 6->2)
        Button(onClick = { value = (value + 1) % 6 + 1 }) { Text("Increment by +2") }
        BackButton(onBack)
    }
}

// ---------- Die Result 3: TextField que atualiza o dado ----------
@Composable
fun DiceResult3(onBack: () -> Unit) {
    var value by remember { mutableIntStateOf(3) }
    var text by remember { mutableStateOf("") }

    ScreenColumn {
        Text("Die Result: 3", style = MaterialTheme.typography.titleLarge)
        DiceImage(value)
        Text("Current Value: $value")
        OutlinedTextField(
            value = text,
            onValueChange = { newText ->
                text = newText
                val n = newText.toIntOrNull()
                if (n != null && n in 1..6) value = n   // só aceita 1 a 6
            },
            label = { Text("Enter 1-6") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        BackButton(onBack)
    }
}

// ---------- Die Result 4: seis botões ----------
@Composable
fun DiceResult4(onGoTo: (Int) -> Unit, onBack: () -> Unit) {
    ScreenColumn {
        Text("Die Result: 4", style = MaterialTheme.typography.titleLarge)
        Text("Select a screen to navigate to")
        for (n in 1..6) {
            Button(
                onClick = { onGoTo(n) },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) { Text("Go to Screen $n") }
        }
        BackButton(onBack)
    }
}

// ---------- Die Result 5: simples ----------
@Composable
fun DiceResult5(onBack: () -> Unit) {
    ScreenColumn {
        Text("Die Result: 5", style = MaterialTheme.typography.titleLarge)
        DiceImage(5)
        Text("Result: 5")
        BackButton(onBack)
    }
}

// ---------- Die Result 6: jogo de dados ----------
// onGoToResult recebe (dado novo, dado atual)
@Composable
fun DiceResult6(onGoToResult: (Int, Int) -> Unit, onBack: () -> Unit) {
    // Dado atual aleatório (rememberSaveable: mantém-se ao voltar com Back)
    val currentDie by rememberSaveable { mutableIntStateOf(Random.nextInt(1, 7)) }
    var secondDie by rememberSaveable { mutableIntStateOf(0) }  // 0 = ainda não lançado

    ScreenColumn {
        Text("Die Result: 6 (Dice Game)", style = MaterialTheme.typography.titleLarge)

        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Current Die")
                DiceImage(currentDie, size = 80.dp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Second Die")
                if (secondDie == 0) {
                    Box(Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                        Text("?", fontSize = 40.sp)
                    }
                } else {
                    DiceImage(secondDie, size = 80.dp)
                }
            }
        }

        Button(onClick = { secondDie = Random.nextInt(1, 7) }) { Text("Roll Second Die") }

        if (secondDie != 0) {
            val won = secondDie >= currentDie
            Text(
                text = if (won) "You Won! ($secondDie ≥ $currentDie)"
                else "You Lost! ($secondDie < $currentDie)",
                fontWeight = FontWeight.Bold
            )
            if (won) {
                Button(onClick = { onGoToResult(secondDie, currentDie) }) {
                    Text("Go to Result")
                }
            }
        }

        BackButton(onBack)
    }
}