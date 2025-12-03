package com.example.labx.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labx.ui.viewmodel.RegistroViewModel
import com.example.labx.ui.viewmodel.RegistroViewModelFactory

/**
 * RegistroScreen: Formulario de registro de usuario
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onVolverClick: () -> Unit,
    onRegistroExitoso: () -> Unit
) {
    val viewModel: RegistroViewModel = viewModel(
        factory = RegistroViewModelFactory()
    )
    
    val uiState by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Usuario") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Completa tus datos",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            
            // Campo: Nombre Completo
            OutlinedTextField(
                value = uiState.formulario.nombreCompleto,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.errores.nombreCompletoError != null,
                supportingText = {
                    uiState.errores.nombreCompletoError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                }
            )
            
            // Campo: Email
            OutlinedTextField(
                value = uiState.formulario.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = uiState.errores.emailError != null,
                supportingText = {
                    uiState.errores.emailError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                }
            )
            
            // Campo: Contraseña
            OutlinedTextField(
                value = uiState.formulario.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(
                            text = if (passwordVisible) "Ocultar" else "Mostrar",
                            fontSize = 12.sp
                        )
                    }
                },
                isError = uiState.errores.passwordError != null,
                supportingText = {
                    uiState.errores.passwordError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Botón Registrarse
            Button(
                onClick = {
                    viewModel.registrar(onExito = onRegistroExitoso)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = viewModel.esFormularioValido() && !uiState.estaGuardando
            ) {
                if (uiState.estaGuardando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Registrarse")
                }
            }
            
            // Botón Cancelar
            OutlinedButton(
                onClick = onVolverClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}
