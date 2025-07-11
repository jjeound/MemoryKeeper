package com.memory.keeper.feature.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.memory.keeper.core.Dimens
import com.memory.keeper.ui.theme.MemoryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionDialog(
    showDialog: MutableState<Boolean>,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(
                        Dimens.gapLarge
                    ),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                )  {
                    Text(
                        text = message,
                        style = MemoryTheme.typography.headlineLarge,
                        color = MemoryTheme.colors.textPrimary,
                    )
                }
            },
            containerColor = MemoryTheme.colors.surface,
            dismissButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MemoryTheme.colors.buttonUnfocused,
                        contentColor = MemoryTheme.colors.textPrimary,
                    ),
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text(
                        text = "닫기",
                        style = MemoryTheme.typography.button,
                    )
                }
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MemoryTheme.colors.primary,
                        contentColor = MemoryTheme.colors.buttonUnfocused,
                    ),
                    onClick = {
                        onConfirm()
                    }
                ) {
                    Text(
                        text = "설정",
                        style = MemoryTheme.typography.button,
                    )
                }
            }
        )
    }
}