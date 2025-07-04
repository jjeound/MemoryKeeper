package com.memory.keeper.feature.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.memory.keeper.R
import com.memory.keeper.core.Dimens
import com.memory.keeper.navigation.currentComposeNavigator
import com.memory.keeper.ui.theme.MemoryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingDetailScreen(
    order: Int
){
    val composeNavigator = currentComposeNavigator
    val instructions = when(order){
        0 -> listOf(
            "Contact to developer: wjdwlsdl32167@gmail.com"
        )
        1 -> listOf(
            "SWEN News는 데이터 사이언스와 AI 기술을 활용하여 한국 언론 생태계의 편향성을 정량적으로 분석하는 차세대 미디어 분석 플랫폼입니다.",
            "자연어 처리(NLP) 엔진이 실시간으로 한국 주요 언론사의 뉴스 기사를 수집하고 분석하여, 각 매체의 정치적 성향과 편향도를 객관적 수치로 환산합니다.",
            "딥러닝 기반 감정 분석 시스템이 기사의 감정적 톤과 어조를 실시간으로 평가하여 좌편향, 중도, 우편향 성향을 95% 이상의 정확도로 분류하며, 시계열 분석을 통해 언론사별 편향 트렌드의 변화 패턴을 추적합니다."
        )
        2 -> listOf(
            "Q1. 정치 성향을 판단하는 기준이 무엇인가요?",
            "A1. SWEN NEWS는 자연어 처리(NLP) 기술을 활용하여 뉴스 기사의 내용을 분석하고, 딥러닝 기반 감정 분석 시스템을 통해 기사의 어조와 톤을 평가합니다. 이를 통해 좌편향, 중도, 우편향 성향을 95% 이상의 정확도로 분류합니다.",
            "Q2. SWEN NEWS의 데이터 출처는 무엇인가요?",
            "A2. SWEN NEWS는 한국 주요 언론사의 뉴스 기사를 실시간으로 수집하여 분석합니다. 데이터는 공신력 있는 뉴스 매체에서 제공되며, 지속적으로 업데이트됩니다.",
            "Q3. SWEN NEWS는 수익 창출을 하나요?",
            "A3. SWEN NEWS는 현재 수익 창출을 목적으로 운영되지 않습니다. 플랫폼은 사용자에게 무료로 제공되며, 광고나 유료 구독 모델은 도입하지 않았습니다.",
        )
        else -> listOf()
    }
    val title = when(order){
        0 -> "Feedback"
        1 -> "About this app"
        2 -> "FAQ"
        else -> ""
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
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
            instructions.forEach {
                Text(
                    text = it,
                    style = MemoryTheme.typography.body,
                    color = MemoryTheme.colors.textPrimary,
                )
            }
        }
    }
}