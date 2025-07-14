package com.memory.keeper.feature.home

import android.Manifest.permission_group.PHONE
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.memory.keeper.core.Dimens
import com.memory.keeper.ui.theme.MemoryTheme


@Composable
fun ContentBox(
    title: String,
    text: String,
    placeHolder: String,
    onChange: (String) -> Unit,
    focusManager: FocusManager
){
    Card(
        modifier = Modifier.fillMaxWidth().widthIn(Dimens.maxPhoneWidth).wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MemoryTheme.colors.box,
        ),
        shape = RoundedCornerShape(Dimens.boxCornerRadius)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.gapLarge)
                .padding(vertical = Dimens.gapMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.gapLarge)
        ) {
            Text(
                text = title,
                style = MemoryTheme.typography.boxText,
                color = MemoryTheme.colors.textSecondary
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                value = text,
                onValueChange = {
                    onChange(it)
                },
                textStyle = MemoryTheme.typography.boxText,
                placeholder = {
                    Text(
                        text = placeHolder,
                        style = MemoryTheme.typography.boxText,
                        color = MemoryTheme.colors.textSecondary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MemoryTheme.colors.textSecondary,
                    focusedBorderColor = MemoryTheme.colors.textSecondary,
                    focusedContainerColor = MemoryTheme.colors.box,
                    unfocusedContainerColor = MemoryTheme.colors.box,
                    focusedTextColor = MemoryTheme.colors.textOnPrimary,
                    unfocusedTextColor = MemoryTheme.colors.textOnPrimary
                ),
                shape = RoundedCornerShape(Dimens.cornerRadius),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions (
                    onDone = { focusManager.clearFocus() },
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = PHONE)
@Composable
fun DayDetailScreenPreview() {
    MemoryTheme {
        ContentBox(
            title = "제목",
            text = "내용",
            placeHolder = "내용을 입력하세요",
            onChange = {},
            focusManager = LocalFocusManager.current
        )
    }
}