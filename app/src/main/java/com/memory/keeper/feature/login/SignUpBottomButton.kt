package com.memory.keeper.feature.login

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.memory.keeper.core.Dimens
import com.memory.keeper.ui.theme.MemoryTheme

@Composable
fun SignUpBottomButton(
    enabled: Boolean,
    onClick: () -> Unit,
    title: String,
){
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.gapLarge),
        shape = RoundedCornerShape(Dimens.cornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = MemoryTheme.colors.primary,
            disabledContainerColor = MemoryTheme.colors.buttonUnfocused,
            contentColor = MemoryTheme.colors.buttonText,
            disabledContentColor = MemoryTheme.colors.buttonText
        ),
        enabled = enabled,
        onClick = onClick
    ){
        Text(
            text = title,
            style = MemoryTheme.typography.button
        )
    }
}