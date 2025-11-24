package com.memory.keeper.feature.notification

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.memory.keeper.R
import com.memory.keeper.core.Dimens
import com.memory.keeper.data.dto.response.Notification
import com.memory.keeper.navigation.currentComposeNavigator
import com.memory.keeper.ui.theme.MemoryTheme

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize().widthIn(max = 600.dp)
            .windowInsetsPadding(WindowInsets.systemBars).background(
                color = MemoryTheme.colors.surface
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        NotificationTopBar()
        if(uiState == NotificationUIState.Loading){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator(
                    color = MemoryTheme.colors.primary
                )
            }
        }else{
            NotificationContent(
                modifier = Modifier.weight(1f),
                notifications = notifications,
                onClick = viewModel::onResponseRelationship
            )
        }
    }
    LaunchedEffect(true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is NotificationUIEvent.ShowToast -> {
                    Toast.makeText(
                        context,
                        event.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationTopBar() {
    val composeNavigator = currentComposeNavigator
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "알림",
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
                    composeNavigator.navigateUp()
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

@Composable
private fun NotificationContent(
    modifier: Modifier,
    notifications: List<Notification>,
    onClick: (Long, String) -> Unit
) {
    LazyColumn(
        modifier = modifier.widthIn(Dimens.maxPhoneWidth).padding(Dimens.gapLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.gapLarge)
    ){
        items(notifications.size) { index ->
            val notification = notifications[index]
            Box(
                modifier = Modifier.fillMaxWidth().border(
                    width = 1.dp,
                    color = if(notification.status == Status.PENDING)MemoryTheme.colors.box else MemoryTheme.colors.buttonBorderUnfocused,
                    shape = RoundedCornerShape(Dimens.cornerRadius)
                ),
            ){
                Column(
                    modifier = Modifier.fillMaxWidth().padding(
                        horizontal = Dimens.gapLarge
                    ).padding(
                        top = Dimens.gapLarge,
                        bottom = Dimens.gapMedium
                    ),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(Dimens.gapMedium)
                ) {
                    Text(
                        text = "${notification.senderName}님이 관계 설정을 요청하였습니다.",
                        style = MemoryTheme.typography.button,
                        color =  if(notification.status == Status.PENDING) MemoryTheme.colors.textPrimary else MemoryTheme.colors.buttonBorderUnfocused,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        if(notification.status == Status.PENDING){
                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MemoryTheme.colors.surface,
                                    contentColor = MemoryTheme.colors.textPrimary,
                                ),
                                onClick = {onClick(notification.requestId, Status.REJECTED)}
                            ) {
                                Text(
                                    text = "거절",
                                    style = MemoryTheme.typography.button,
                                )
                            }
                            Spacer(
                                modifier = Modifier.width(Dimens.gapMedium)
                            )
                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MemoryTheme.colors.primary,
                                    contentColor = MemoryTheme.colors.textOnPrimary,
                                ),
                                onClick = {onClick(notification.requestId, Status.ACCEPTED)}
                            ) {
                                Text(
                                    text = "수락",
                                    style = MemoryTheme.typography.button,
                                )
                            }
                        }else{
                            Text(
                                modifier = Modifier.padding( Dimens.gapMedium),
                                text = "거절됨",
                                style = MemoryTheme.typography.button,
                                color = MemoryTheme.colors.buttonBorderUnfocused
                            )
                        }
                    }
                }
            }
        }
    }
}

object Status{
    const val PENDING = "PENDING"
    const val ACCEPTED = "ACCEPTED"
    const val REJECTED = "REJECTED"
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PHONE)
@Composable
private fun NotificationContentPreview() {
    MemoryTheme {
        NotificationContent(
            modifier = Modifier.fillMaxSize(),
            notifications = listOf(
                Notification(1,1, "보호자", "PENDING"),
                Notification(2,1, "사용자", "ACCEPTED",),
                Notification(3, 1,"보호자", "REJECTED")
            ),
            onClick = { _, _ -> }
        )
    }
}