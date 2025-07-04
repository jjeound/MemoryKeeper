package com.memory.keeper.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.memory.keeper.core.Dimens
import com.memory.keeper.data.model.News
import com.memory.keeper.navigation.Screen
import com.memory.keeper.navigation.currentComposeNavigator
import com.memory.keeper.ui.theme.MemoryTheme

@Composable
fun NewsCard(
    news: News
){
    val composeNavigator = currentComposeNavigator
    Row(
        modifier = Modifier.fillMaxWidth().padding(
            vertical = Dimens.verticalPadding
        ).clickable{
            composeNavigator.navControllerFlow.value?.currentBackStackEntry?.savedStateHandle?.set("id", news.id)
            composeNavigator.navigate(Screen.Detail(news.id))
        },
        horizontalArrangement = Arrangement.spacedBy(Dimens.gapMedium),
    ) {
        if(news.thumbnail != null){
            AsyncImage(
                model = news.thumbnail,
                contentDescription = news.title,
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(92.dp)
            )
        }else{
            Card(
                modifier = Modifier.size(92.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MemoryTheme.colors.center
                ),
                shape = RoundedCornerShape(Dimens.gapSmall),
            ) {
                Spacer(
                    modifier = Modifier.size(92.dp)
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.gapSmall)
        ) {
            Text(
                text = news.title,
                style = MemoryTheme.typography.headlineSmall,
                color = MemoryTheme.colors.textPrimary
            )
            Row(
                modifier = Modifier.padding(
                    vertical = Dimens.gapSmall
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HorizontalDivider(
                    modifier = Modifier.width(news.left.dp),
                    thickness = Dimens.gapSmall,
                    color = MemoryTheme.colors.blueBias
                )
                HorizontalDivider(
                    modifier = Modifier.width(news.center.dp),
                    thickness = Dimens.gapSmall,
                    color = MemoryTheme.colors.center
                )
                HorizontalDivider(
                    modifier = Modifier.width(news.right.dp),
                    thickness = Dimens.gapSmall,
                    color = MemoryTheme.colors.redBias
                )
                Spacer(
                    modifier = Modifier.width(Dimens.gapSmall)
                )
                Text(
                    text = "좌 ${news.left}% 중도 ${news.center}% 우 ${news.right}%",
                    style = MemoryTheme.typography.percent,
                    color = MemoryTheme.colors.textSecondary
                )
            }
        }
    }
}