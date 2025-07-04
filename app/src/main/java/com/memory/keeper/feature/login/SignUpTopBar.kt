package com.memory.keeper.feature.login

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.memory.keeper.R
import com.memory.keeper.navigation.currentComposeNavigator
import com.memory.keeper.ui.theme.MemoryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpTopBar() {
    //val composeNavigator = currentComposeNavigator
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "회원가입",
                color = MemoryTheme.colors.textPrimary,
                style = MemoryTheme.typography.appBarTitle
            )
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = MemoryTheme.colors.surface,
        ),
        navigationIcon = {
            IconButton(
                onClick = {
                    //composeNavigator.navigateUp()
                },
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.chevron_left),
                    contentDescription = "back",
                    tint = MemoryTheme.colors.iconDefault
                )
            }
        }
    )
}