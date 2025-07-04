package com.memory.keeper.feature.detail

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.memory.keeper.R
import com.memory.keeper.ui.theme.MemoryTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.memory.keeper.core.Dimens
import com.memory.keeper.data.dto.BiasRatio
import com.memory.keeper.data.dto.Keyword
import com.memory.keeper.data.dto.NewsInfo
import com.memory.keeper.data.dto.OriginalSource
import com.memory.keeper.navigation.currentComposeNavigator
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val newsInfo by viewModel.newsDetail.collectAsStateWithLifecycle()
    val composeNavigator = currentComposeNavigator
    Column(
        modifier = Modifier
        .fillMaxSize()
    ) {
        TopAppBar(
            title = {},
            colors = TopAppBarDefaults.topAppBarColors().copy(
                containerColor = MemoryTheme.colors.surface,
            ),
            navigationIcon = {
                IconButton(
                    onClick = {
                        composeNavigator.navigateUp()
                    }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.chevron_left),
                        contentDescription = "back",
                        tint = MemoryTheme.colors.iconDefault,
                    )
                }
            },
        )
        if (uiState == DetailsUiState.Idle && newsInfo != null) {
            DetailContent(newsInfo = newsInfo!!)
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.wrapContentSize(),
                    color = MemoryTheme.colors.iconDefault
                )
            }
        }
    }
}

@Composable
private fun DetailContent(
    newsInfo: NewsInfo,
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    Column(
        modifier = Modifier.fillMaxSize().padding(
            horizontal = Dimens.horizontalPadding,
            vertical = Dimens.verticalPadding
        ).verticalScroll(
            rememberScrollState()
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.gapMedium)
    ) {
        Text(
            text = newsInfo.title,
            style = MemoryTheme.typography.headlineLarge,
            color = MemoryTheme.colors.textPrimary
        )
        AsyncImage(
            model = newsInfo.representativeImage,
            contentDescription = newsInfo.title,
            alignment = Alignment.Center,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth().height(300.dp)
        )
        val articleCounts = (newsInfo.left?.originalSource?.size ?: 0) +
        (newsInfo.center?.originalSource?.size ?: 0) + (newsInfo.right?.originalSource?.size ?: 0)
        when(selectedIndex){
            0 -> {
                if (newsInfo.left != null){
                    Summary(
                        summary = newsInfo.left.summary,
                        counts = articleCounts,
                        biasRatio = newsInfo.biasRatio,
                        selectedIndex = selectedIndex,
                        onClick = {
                            selectedIndex = it
                        }
                    )
                    KeywordAnalysis(
                        keyword = newsInfo.left.keywords
                    )
                    MediaList(
                        media = newsInfo.left.originalSource
                    )
                } else {
                    Summary(
                        counts = articleCounts,
                        biasRatio = newsInfo.biasRatio,
                        selectedIndex = selectedIndex,
                        onClick = {
                            selectedIndex = it
                        }
                    )
                }
            }
            1 -> {
                if(newsInfo.center != null){
                    Summary(
                        summary = newsInfo.center.summary,
                        counts = articleCounts,
                        biasRatio = newsInfo.biasRatio,
                        selectedIndex = selectedIndex,
                        onClick = {
                            selectedIndex = it
                        }
                    )
                    KeywordAnalysis(
                        keyword = newsInfo.center.keywords
                    )
                    MediaList(
                        media = newsInfo.center.originalSource
                    )
                }else {
                    Summary(
                        counts = articleCounts,
                        biasRatio = newsInfo.biasRatio,
                        selectedIndex = selectedIndex,
                        onClick = {
                            selectedIndex = it
                        }
                    )
                }
            }
            2 -> {
                if(newsInfo.right != null){
                    Summary(
                        summary = newsInfo.right.summary,
                        counts = articleCounts,
                        biasRatio = newsInfo.biasRatio,
                        selectedIndex = selectedIndex,
                        onClick = {
                            selectedIndex = it
                        }
                    )
                    KeywordAnalysis(
                        keyword = newsInfo.right.keywords
                    )
                    MediaList(
                        media = newsInfo.right.originalSource
                    )
                }else {
                    Summary(
                        counts = articleCounts,
                        biasRatio = newsInfo.biasRatio,
                        selectedIndex = selectedIndex,
                        onClick = {
                            selectedIndex = it
                        }
                    )
                }
            }
        }
        MediaOpList(
            mediaOp = emptyList<String>(),
            updatedAt = newsInfo.updatedAt
        )
        OriginalSource(
            sourceAll = (newsInfo.left?.originalSource ?:emptyList()) + (newsInfo.center?.originalSource ?: emptyList()) + (newsInfo.right?.originalSource ?: emptyList()),
            sourceLeft = newsInfo.left?.originalSource,
            sourceCenter = newsInfo.center?.originalSource,
            sourceRight = newsInfo.right?.originalSource
        )
    }
}

@Composable
private fun Summary(
    summary: String? = null,
    counts: Int,
    biasRatio: BiasRatio,
    selectedIndex : Int,
    onClick: (Int) -> Unit,
){
    val options = listOf("좌", "중도", "우")
    val ratio = listOf(biasRatio.left, biasRatio.center, biasRatio.right)
    Column(
        modifier = Modifier.fillMaxWidth().padding(Dimens.gapMedium),
        verticalArrangement = Arrangement.spacedBy(Dimens.gapLarge)
    ) {
        Text(
            text = "요약",
            style = MemoryTheme.typography.header,
            color = MemoryTheme.colors.textPrimary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                options.forEachIndexed { index, option ->
                    if(selectedIndex == index){
                        val bgColor = when(index){
                            0 -> MemoryTheme.colors.blueBias
                            1 -> MemoryTheme.colors.center
                            2 -> MemoryTheme.colors.redBias
                            else -> MemoryTheme.colors.buttonUnfocused
                        }
                        Box(
                            modifier = Modifier
                                .width(50.dp).height(90.dp)
                                .clickable { onClick(index) }
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(bgColor, MemoryTheme.colors.surface)
                                    )
                                )
                            ,
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.fillMaxHeight().padding(
                                    vertical = Dimens.gapSmall
                                ),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = option,
                                    style = MemoryTheme.typography.button,
                                    color = MemoryTheme.colors.badgeText
                                )
                                Text(
                                    text = "${ratio[index]*100}%",
                                    style = MemoryTheme.typography.button,
                                    color = MemoryTheme.colors.textPrimary
                                )
                            }
                        }
                    }else{
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .wrapContentHeight()
                                .clickable { onClick(index) }
                                .background(MemoryTheme.colors.buttonUnfocused),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Text(
                                modifier = Modifier.padding(
                                    vertical = Dimens.gapSmall
                                ),
                                text = option,
                                style = MemoryTheme.typography.button,
                                color = MemoryTheme.colors.buttonTextUnfocused
                            )
                        }
                    }
                }
            }
            Text(
                text = "총 ${counts}개 기사",
                style = MemoryTheme.typography.time,
                color = MemoryTheme.colors.textThird
            )
        }
        if(summary != null){
            val text = summary.split(".")
            text.forEach { it ->
                Text(
                    text = it,
                    style = MemoryTheme.typography.body,
                    color = MemoryTheme.colors.textPrimary
                )
            }
        }else{
            Box(
                modifier = Modifier.fillMaxWidth().padding(
                    vertical = Dimens.gapHuge
                ),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "데이터가 없습니다.",
                    style = MemoryTheme.typography.body,
                    color = MemoryTheme.colors.textPrimary
                )
            }
        }
    }
}

@Composable
private fun KeywordAnalysis(
    keyword: List<Keyword>?,
){
    Column(
        modifier = Modifier.fillMaxWidth().padding(Dimens.gapMedium),
        verticalArrangement = Arrangement.spacedBy(Dimens.gapLarge)
    ) {
        Text(
            text = "키워드 분석",
            style = MemoryTheme.typography.header,
            color = MemoryTheme.colors.textPrimary
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(Dimens.gapMedium).horizontalScroll(
                rememberScrollState()
            ),
            horizontalArrangement = Arrangement.spacedBy(Dimens.gapMedium),
            verticalAlignment = Alignment.CenterVertically
        ){
            keyword?.let {
                it.forEach { keyword ->
                    Card(
                        modifier = Modifier
                            .size((keyword.score*100).toInt().dp),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = MemoryTheme.colors.surface
                        ),
                        border = BorderStroke(
                            width = Dimens.border,
                            color = MemoryTheme.colors.optionBorderFocused
                        ),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ){
                            Text(
                                text = keyword.word,
                                style = MemoryTheme.typography.keywordMedium,
                                color = MemoryTheme.colors.textPrimary,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaList(
    media: List<OriginalSource>,
){
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.gapLarge)
    ) {
        Text(
            text = "언론사",
            style = MemoryTheme.typography.header,
            color = MemoryTheme.colors.textPrimary
        )
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                .clip(shape = RoundedCornerShape(Dimens.cornerRadius)).background(
                    color = MemoryTheme.colors.blueBackground
                ).padding(Dimens.gapSmall),
            horizontalArrangement = Arrangement.spacedBy(Dimens.gapMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            media.forEach {
                AsyncImage(
                    model = it.logo,
                    contentDescription = "media logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(Dimens.circle))
                )
            }
        }
    }
}

@Composable
private fun MediaOpList(
    mediaOp: List<String>,
    updatedAt: String
){
    val instant = Instant.parse(updatedAt)
    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")
        .withZone(ZoneId.systemDefault())
    val time = formatter.format(instant)
    Column(
        modifier = Modifier.fillMaxWidth().padding(Dimens.gapMedium),
        verticalArrangement = Arrangement.spacedBy(Dimens.gapLarge)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "이 사건을 다루지 않은 언론사",
                style = MemoryTheme.typography.header,
                color = MemoryTheme.colors.textPrimary
            )
            Text(
                text = "${time}분 기준",
                style = MemoryTheme.typography.time,
                color = MemoryTheme.colors.textThird
            )
        }
        if(mediaOp.isEmpty()){
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "데이터가 없습니다.",
                    style = MemoryTheme.typography.body,
                    color = MemoryTheme.colors.textPrimary
                )
            }
        }else{
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(Dimens.gapMedium)
                    .clip(shape = RoundedCornerShape(Dimens.cornerRadius)).background(
                        color = MemoryTheme.colors.surface
                    ).padding(Dimens.gapSmall),
                horizontalArrangement = Arrangement.spacedBy(Dimens.gapMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                mediaOp.forEach {
                    AsyncImage(
                        model = it,
                        contentDescription = "media logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(Dimens.circle))
                    )
                }
            }
        }
    }
}

@Composable
private fun OriginalSource(
    sourceAll: List<OriginalSource>?,
    sourceLeft: List<OriginalSource>?,
    sourceCenter: List<OriginalSource>?,
    sourceRight: List<OriginalSource>?,
){
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("전체", "좌", "중도", "우")
    val counts = listOf(
        sourceAll?.size ?: 0,
        sourceLeft?.size ?: 0,
        sourceCenter?.size ?: 0,
        sourceRight?.size ?: 0
    )
    Column(
        modifier = Modifier.fillMaxWidth().padding(Dimens.gapMedium),
        verticalArrangement = Arrangement.spacedBy(Dimens.gapLarge)
    ) {
        Text(
            text = "원본 뉴스",
            style = MemoryTheme.typography.header,
            color = MemoryTheme.colors.textPrimary
        )
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                var rowWidth by remember { mutableIntStateOf(0) }
                options.forEachIndexed { index, option ->
                    Column(
                        modifier = Modifier.wrapContentWidth().clickable{
                            if(counts[index] > 0){
                                selectedIndex = index
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.wrapContentWidth().padding(
                                Dimens.gapMedium
                            ).onGloballyPositioned { coordinates ->
                                if (selectedIndex == index) {
                                    rowWidth = coordinates.size.width
                                }
                            },
                            horizontalArrangement = Arrangement.spacedBy(Dimens.gapSmall),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option,
                                style = MemoryTheme.typography.body,
                                color = MemoryTheme.colors.textPrimary
                            )
                            Badge(
                                modifier = Modifier.wrapContentSize(),
                                containerColor = MemoryTheme.colors.badge,
                                content = {
                                    Text(
                                        text = counts[index].toString(),
                                        style = MemoryTheme.typography.badge,
                                        color = MemoryTheme.colors.badgeText
                                    )
                                }
                            )
                        }
                        if(selectedIndex == index){
                            HorizontalDivider(
                                modifier = Modifier
                                    .width(with(LocalDensity.current) { rowWidth.toDp() + Dimens.gapMedium }),
                                color = MemoryTheme.colors.optionBorderFocused
                            )
                        }
                    }
                }
            }
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = MemoryTheme.colors.optionBorderUnfocused
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.gapLarge),
        ) {
            when(selectedIndex){
                0 -> {
                    sourceAll?.let {
                        it.forEach { source ->
                            SourceCard(
                                source = source
                            )
                        }
                    }
                }
                1 -> {
                    sourceLeft?.let {
                        it.forEach { source ->
                            SourceCard(
                                source = source
                            )
                        }
                    }
                }
                2 -> {
                    sourceCenter?.let {
                        it.forEach { source ->
                            SourceCard(
                                source = source
                            )
                        }
                    }
                }
                3 -> {
                    sourceRight?.let {
                        it.forEach { source ->
                            SourceCard(
                                source = source
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun SourceCard(
    source: OriginalSource
){
    var showOriginalNews by remember { mutableStateOf(false) }

    if (showOriginalNews) {
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            val intent = Intent(Intent.ACTION_VIEW, source.url.toUri())
            context.startActivity(intent)
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MemoryTheme.colors.surface
        ),
        shape = RoundedCornerShape(Dimens.cornerRadius),
        border = BorderStroke(
            width = Dimens.border,
            color = MemoryTheme.colors.buttonUnfocused
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Dimens.gapMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.gapSmall)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable {
                    showOriginalNews = true
                },
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = source.name,
                    style = MemoryTheme.typography.media,
                    color = MemoryTheme.colors.textPrimary
                )
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.chevron_right),
                    contentDescription = "chevron right",
                    tint = MemoryTheme.colors.iconDefault,
                )
            }
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = MemoryTheme.colors.divider
            )
            Text(
                text = source.title,
                style = MemoryTheme.typography.description,
                color = MemoryTheme.colors.textPrimary
            )
        }
    }
}
