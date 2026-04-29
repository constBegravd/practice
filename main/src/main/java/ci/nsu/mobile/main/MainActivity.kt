package ci.nsu.mobile.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ci.nsu.mobile.main.ui.theme.PracticeTheme
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PracticeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TemperatureScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

data class TemperatureUiState(
    val celsius: String = "",
    val fahrenheit: String = ""
) {
    // Вычисляемые свойства для валидации
    val isCelsiusValid: Boolean get() = celsius.toDoubleOrNull() != null
    val isFahrenheitValid: Boolean get() = fahrenheit.toDoubleOrNull() != null
}

class TemperatureViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TemperatureUiState())
    val uiState: StateFlow<TemperatureUiState> = _uiState.asStateFlow()

    fun onCelsiusChanged(newValue: String) {
        _uiState.update { currentState ->
            val celsius = newValue
            val fahrenheit = if (celsius.isNotBlank()) {
                val c = celsius.toDoubleOrNull()
                if (c != null) String.format("%.2f", c * 9/5 + 32)
                else ""
            } else ""

            currentState.copy(
                celsius = celsius,
                fahrenheit = fahrenheit
            )
        }
    }

    fun onFahrenheitChanged(newValue: String) {
        _uiState.update { currentState ->
            val fahrenheit = newValue
            val celsius = if (fahrenheit.isNotBlank()) {
                val f = fahrenheit.toDoubleOrNull()
                if (f != null) String.format("%.2f", (f - 32) * 5/9)
                else ""
            } else ""

            currentState.copy(
                celsius = celsius,
                fahrenheit = fahrenheit
            )
            }
        }
    }


@Composable
fun TemperatureScreen(viewModel: TemperatureViewModel = viewModel(),
                      modifier: Modifier) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        OutlinedTextField(
            value = uiState.celsius,
            onValueChange = { viewModel.onCelsiusChanged(it) },
            label = { Text("cels")},
            isError = uiState.celsius.isNotBlank() && !uiState.isCelsiusValid,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = uiState.fahrenheit,
            onValueChange = { viewModel.onFahrenheitChanged(it) },
            label = { Text("fahr")},
            isError = uiState.fahrenheit.isNotBlank() && !uiState.isFahrenheitValid,
            modifier = Modifier.fillMaxWidth()
        )
    }

}