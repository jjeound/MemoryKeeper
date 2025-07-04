package com.memory.keeper.feature.more

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.memory.keeper.core.Dimens
import com.memory.keeper.data.model.News
import com.memory.keeper.feature.home.NewsCard
import com.memory.keeper.ui.theme.MemoryTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems

@Composable
fun MoreScreen(
    viewModel: MoreViewModel = hiltViewModel(),
    isHot: Boolean,
) {
    LaunchedEffect(true) {
        viewModel.fetchNewsList(
            isHot = isHot
        )
    }
    val newsList = viewModel.newsList.collectAsLazyPagingItems()

    Column(
        modifier = Modifier.fillMaxSize()
    ){
        MoreTopBar(
            isHot = isHot
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = Dimens.border,
            color = MemoryTheme.colors.divider
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(
                horizontal = Dimens.horizontalPadding,
                vertical = Dimens.verticalPadding
            ),
            verticalArrangement = Arrangement.spacedBy(Dimens.gapMedium)
        ){
            CategoryTab(
                onTabSelected = { category ->
                    viewModel.fetchNewsList(
                        isHot = isHot,
                        category = category
                    )
                }
            )
            NewsList(
                newsList = newsList,
            )
        }
    }
}

@Composable
fun CategoryTab(
    onTabSelected: (String) -> Unit
){
    var selectedIndex by remember { mutableIntStateOf(-1) }

    Row(
        modifier = Modifier.fillMaxWidth().padding(Dimens.verticalPadding),
        horizontalArrangement = Arrangement.spacedBy(Dimens.gapMedium)
    ) {
        Category.entries.forEachIndexed { index, category ->
            Card(
                modifier = Modifier
                    .wrapContentSize()
                    .clickable {
                        selectedIndex = index
                        onTabSelected(category.eng)
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MemoryTheme.colors.surface
                ),
                shape = RoundedCornerShape(Dimens.cornerRadius),
                border = BorderStroke(
                    width = Dimens.border,
                    color = if(selectedIndex == index) MemoryTheme.colors.optionBorderFocused else MemoryTheme.colors.optionBorderUnfocused
                )
            ) {
                Box(
                    modifier = Modifier.padding(
                        horizontal = Dimens.gapMedium,
                        vertical = Dimens.gapSmall
                    ),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = category.kor,
                        style = MemoryTheme.typography.option,
                        color = if(selectedIndex == index) MemoryTheme.colors.optionTextFocused else MemoryTheme.colors.optionTextUnfocused,
                    )
                }
            }
        }
    }
}

@Composable
private fun NewsList(
    newsList: LazyPagingItems<News>,
){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if(newsList.loadState.refresh is LoadState.Loading){
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = MemoryTheme.colors.center,
            )
        } else {
            LazyColumn(
                modifier = Modifier.padding(Dimens.verticalPadding),
                verticalArrangement = Arrangement.spacedBy(Dimens.gapMedium),
            ) {
                items(newsList.itemCount) {
                    val news = newsList[it]
                    if(news != null){
                        NewsCard(
                            news = news
                        )
                    }
                }
                item {
                    if(newsList.loadState.append is LoadState.Loading) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ){
                            CircularProgressIndicator(
                                modifier = Modifier.size(40.dp),
                                color = MemoryTheme.colors.center,
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class Category(
    val kor: String,
    val eng: String,
){
    POLITICS("정치", "politics"),
    ECONOMY("경제", "economy"),
//    SOCIETY("사회", "society"),
//    SPORTS("스포츠", "sports"),
//    ENTERTAINMENT("연예", "entertainment"),
//    LIFESTYLE("생활/문화", "lifestyle"),
//    IT_SCIENCE("IT과학", "it_science"),
//    WORLD("세계", "world"),
}