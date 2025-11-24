package com.memory.keeper.feature.prompt

import android.Manifest.permission_group.PHONE
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memory.keeper.core.Dimens
import com.memory.keeper.feature.util.PreviewTheme
import com.memory.keeper.ui.theme.MemoryTheme


@Composable
fun ContentBox(
    header: String,
    icon: Int? = null,
    onClickIcon: (() -> Unit)? = null,
    content: @Composable () -> Unit
){
    Card(
        modifier = Modifier.fillMaxWidth().widthIn(Dimens.maxPhoneWidth).wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MemoryTheme.colors.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = header,
                    style = MemoryTheme.typography.boxText,
                    color = MemoryTheme.colors.textPrimary
                )
                icon?.let {
                    Icon(
                        modifier = Modifier.clickable {
                            onClickIcon?.invoke()
                        },
                        imageVector = ImageVector.vectorResource(it),
                        contentDescription = null,
                    )
                }
            }
            content()
        }
    }
}

@Composable
fun MemoryTextField(
    text: String,
    placeHolder: Pair<String, String>,
    onChange: (String) -> Unit,
    focusManager: FocusManager
){
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        value = text,
        onValueChange = {
            onChange(it)
        },
        textStyle = MemoryTheme.typography.textField,
        prefix = {
            Text(
                modifier = Modifier.padding(end = 4.dp),
                text = placeHolder.first,
                style = MemoryTheme.typography.textField,
                color = MemoryTheme.colors.prefix
            )
        },
        placeholder = {
            Text(
                text = placeHolder.second,
                style = MemoryTheme.typography.textField,
                color = MemoryTheme.colors.textSecondary
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MemoryTheme.colors.background,
            focusedBorderColor = MemoryTheme.colors.background,
            focusedContainerColor = MemoryTheme.colors.background,
            unfocusedContainerColor = MemoryTheme.colors.background,
            focusedTextColor = MemoryTheme.colors.textPrimary,
            unfocusedTextColor = MemoryTheme.colors.textPrimary,
        ),
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

@Composable
fun RadioBox(
    title: String,
    selectedOption: String,
    options: List<String>,
    optionsKor: List<String>,
    onClick: (String) -> Unit
){
    Card(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MemoryTheme.colors.background,
        ),
        shape = RectangleShape
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MemoryTheme.typography.textField,
                color = MemoryTheme.colors.prefix
            )
            options.forEachIndexed { index, option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier.wrapContentSize().background(
                            color = if (selectedOption == option) MemoryTheme.colors.primary else MemoryTheme.colors.box,
                        ).clickable{ onClick(option) },
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp),
                            text = optionsKor[index],
                            style = MemoryTheme.typography.option,
                            color = if (selectedOption == option) MemoryTheme.colors.surface else MemoryTheme.colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true, device = PHONE)
@Composable
fun DayDetailScreenPreview() {
    PreviewTheme {
        ContentBox(
            header = "개인 정보",
            content = {
                MemoryTextField(
                    text = "",
                    placeHolder = Pair("이름", "홍길동"),
                    onChange = {},
                    focusManager = LocalFocusManager.current
                )
                RadioBox(
                    title = "성별",
                    options = listOf("mail", "femail"),
                    optionsKor = listOf("남", "여"),
                    selectedOption = "남",
                    onClick = {}
                )
            }
        )
    }
}