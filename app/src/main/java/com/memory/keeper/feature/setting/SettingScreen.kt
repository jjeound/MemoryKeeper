package com.memory.keeper.feature.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.memory.keeper.R
import com.memory.keeper.core.Dimens
import com.memory.keeper.navigation.Screen
import com.memory.keeper.navigation.currentComposeNavigator
import com.memory.keeper.ui.theme.MemoryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(){
    val composeNavigator = currentComposeNavigator
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "설정",
                    color = MemoryTheme.colors.textPrimary,
                    style = MemoryTheme.typography.appBarTitle
                )
            },
            colors = TopAppBarDefaults.topAppBarColors().copy(
                containerColor = MemoryTheme.colors.surface,
            ),
            navigationIcon = {
                IconButton(
                    onClick = {composeNavigator.navigateUp()},
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.chevron_left),
                        contentDescription = "settings",
                        tint = MemoryTheme.colors.iconDefault
                    )
                }
            }
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(
                horizontal = Dimens.horizontalPadding,
                vertical = Dimens.verticalPadding
            ),
            verticalArrangement = Arrangement.spacedBy(Dimens.gapMedium)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(Dimens.gapMedium),
                verticalArrangement = Arrangement.spacedBy(Dimens.gapSmall)
            ) {
                Text(
                    text = "지원 및 도움",
                    style = MemoryTheme.typography.header,
                    color = MemoryTheme.colors.textPrimary
                )
                Row(
                    modifier = Modifier.fillMaxWidth().clickable{
                        composeNavigator.navigate(Screen.SettingDetail(0))
                    },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Feedback",
                        style = MemoryTheme.typography.menu,
                        color = MemoryTheme.colors.textPrimary
                    )
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.chevron_right),
                            contentDescription = "chevron right",
                            tint = MemoryTheme.colors.iconDefault
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth().padding(Dimens.gapMedium),
                verticalArrangement = Arrangement.spacedBy(Dimens.gapSmall)
            ) {
                Text(
                    text = "약관 및 정책",
                    style = MemoryTheme.typography.header,
                    color = MemoryTheme.colors.textPrimary
                )
                Row(
                    modifier = Modifier.fillMaxWidth().clickable{
                        composeNavigator.navigate(Screen.SettingDetail(1))
                    },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "About this app",
                        style = MemoryTheme.typography.menu,
                        color = MemoryTheme.colors.textPrimary
                    )
                    IconButton(
                        onClick = {}
                    ){
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.chevron_right),
                            contentDescription = "chevron right",
                            tint = MemoryTheme.colors.iconDefault
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().clickable{
                        composeNavigator.navigate(Screen.SettingDetail(2))
                    },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FAQ",
                        style = MemoryTheme.typography.menu,
                        color = MemoryTheme.colors.textPrimary
                    )
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.chevron_right),
                            contentDescription = "chevron right",
                            tint = MemoryTheme.colors.iconDefault
                        )
                    }
                }
            }
        }
    }
}