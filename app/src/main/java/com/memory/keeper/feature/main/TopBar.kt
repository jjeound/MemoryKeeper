package com.memory.keeper.feature.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memory.keeper.R
import com.memory.keeper.core.Dimens
import com.memory.keeper.feature.util.PreviewTheme
import com.memory.keeper.navigation.Screen
import com.memory.keeper.navigation.currentComposeNavigator
import com.memory.keeper.ui.theme.MemoryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    isPatient: Boolean,
    isHome: Boolean = false,
    userName: String? = "보호자",
    patients: List<String>? = null,
    onClick: (Int) -> Unit,
    isExpanded: Boolean = false,
    onDismiss: () -> Unit = {},
) {
    val composeNavigator = currentComposeNavigator
    Column {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(60.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.ic_launcher_foreground),
                        contentDescription = "profile",
                        tint = Color.Unspecified
                    )
                    Text(
                        text = stringResource(id = R.string.app_name), //유저명
                        color = MemoryTheme.colors.textOnPrimary,
                        style = MemoryTheme.typography.appBarTitle
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors().copy(
                containerColor = MemoryTheme.colors.primary,
            ),
            actions = {
                IconButton(
                    onClick = {
                        composeNavigator.navigate(
                            Screen.Notification
                        )
                    },
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.notification),
                        contentDescription = "settings",
                        tint = Color.Unspecified
                    )
                }
            }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MemoryTheme.colors.primary),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp).background(
                        color = MemoryTheme.colors.onPrimary,
                        shape = RoundedCornerShape(
                            topEnd = Dimens.cornerRadius
                        )
                    ),
                contentAlignment = Alignment.Center,
            ){
                Text(
                    text = "${userName ?: "사용자"}님",
                    style = MemoryTheme.typography.button,
                    color = MemoryTheme.colors.textOnPrimary,
                )
            }
            Box(
                modifier = Modifier.weight(1f).height(50.dp),
                contentAlignment = Alignment.Center,
            ) {
                if(isHome && patients != null) {
                    Row(
                        modifier = Modifier.padding(start = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = patients.getOrNull(0) ?: "",
                            style = MemoryTheme.typography.button,
                            color = MemoryTheme.colors.textOnPrimary,
                        )
                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )
//                        Icon(
//                            imageVector = ImageVector.vectorResource(R.drawable.dropdown),
//                            contentDescription = "select patient",
//                        )
                    }
                }
                patients?.let{
                    DropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = onDismiss,
                        containerColor = MemoryTheme.colors.primary,
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                        shape = RoundedCornerShape(Dimens.cornerRadius),
                    ){
                        it.forEachIndexed { index, patient ->
                            DropdownMenuItem(
                                modifier = Modifier.height(50.dp),
                                text = {
                                    Text(
                                        text = patient,
                                        style = MemoryTheme.typography.button,
                                        color = MemoryTheme.colors.textOnPrimary,
                                    )
                                },
                                onClick = {
                                    onClick(index)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    PreviewTheme {
        TopBar(
            isPatient = false,
            isHome = true,
            userName = "박성근",
            patients = listOf("박정수"),
            onClick = {},
        )
    }
}