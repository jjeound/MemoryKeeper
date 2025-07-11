package com.memory.keeper.feature.home

import android.Manifest
import android.Manifest.permission_group.PHONE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.memory.keeper.MainActivity
import com.memory.keeper.R
import com.memory.keeper.core.Dimens
import com.memory.keeper.feature.util.ImageUtil.copyUriToFile
import com.memory.keeper.feature.util.ImageUtil.getRealPathFromURI
import com.memory.keeper.feature.util.PermissionDialog
import com.memory.keeper.ui.theme.MemoryTheme
import java.io.File

@Composable
fun DayDetailScreen() {
    var description by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Dimens.gapMedium)
    ) {
        PictureBox()
        PeriodOfPic()
        DescriptionOfPic(
            description = description,
            onChange = { it ->
                description = it
            }
        )
        EmotionsOfPic()
    }
}

@Composable
fun PictureBox() {
    var image by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    var imageFile by remember { mutableStateOf<File?>(null) }
    val showDialog = remember { mutableStateOf(false) }
    val albumLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    image = it.toString()
                    val filePath = context.getRealPathFromURI(it)
                    imageFile = filePath?.let { path -> File(path) } ?: context.copyUriToFile(it)
                    Log.d("TargetSDK", "imageUri - selected : $uri")
                }
            }
        }
    val imageAlbumIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "image/*"
        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        addCategory(Intent.CATEGORY_OPENABLE)
    }
    val galleryPermissions = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> arrayOf(
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
            Manifest.permission.READ_MEDIA_IMAGES,
        )
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES
        )
        else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { permissions ->
                galleryPermissions.forEach { permission ->
                    if (permissions[permission] == true){
                        Log.d("gallery", "gallery permission granted")
                    }
                }
            }
        )
    Card(
        modifier = Modifier.fillMaxWidth().widthIn(Dimens.maxPhoneWidth).wrapContentHeight().padding(Dimens.gapLarge),
        colors = CardDefaults.cardColors(
            containerColor = MemoryTheme.colors.box,
        ),
        shape = RoundedCornerShape(Dimens.cornerRadius)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.gapLarge),
            verticalArrangement = Arrangement.spacedBy(Dimens.gapLarge)
        ) {
            Text(
                text = "업로드할 사진",
                style = MemoryTheme.typography.header,
                color = MemoryTheme.colors.textOnPrimary
            )
            Box(
                modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                    .border(1.dp, color = MemoryTheme.colors.buttonBorderUnfocused, shape = RoundedCornerShape(
                        Dimens.cornerRadius))
                    .clickable{
                        when {
                            ContextCompat.checkSelfPermission(
                                context,
                                galleryPermissions[0]
                            ) == PackageManager.PERMISSION_GRANTED ->  {
                                albumLauncher.launch(imageAlbumIntent)
                            }
                            shouldShowRequestPermissionRationale(
                                context as MainActivity,
                                galleryPermissions[0]
                            ) -> {
                                showDialog.value = true
                            }
                            else -> {
                                requestPermissionLauncher.launch(galleryPermissions)
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ){
               if(image == null){
                   Column(
                       horizontalAlignment = Alignment.CenterHorizontally,
                       verticalArrangement = Arrangement.spacedBy(Dimens.gapSmall)
                   ) {
                       Icon(
                           imageVector = ImageVector.vectorResource(R.drawable.camera),
                           contentDescription = "camera",
                           tint = Color.Unspecified,
                       )
                       Text(
                           text = "사진을 업로드 해주세요.",
                           style = MemoryTheme.typography.body,
                           color = MemoryTheme.colors.textOnPrimary
                       )
                   }
               } else {
                     AsyncImage(
                          model = image,
                          contentDescription = "uploaded image",
                          alignment = Alignment.Center,
                          contentScale = ContentScale.Crop,
                          modifier = Modifier.fillMaxSize()
                     )
               }
            }
        }
    }
    PermissionDialog(
        showDialog = showDialog,
        message = "이미지를 업로드하려면 저장소 접근 권한이 필요합니다.",
        onDismiss = { showDialog.value = false },
        onConfirm = {
            showDialog.value = false
            context.startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
            )
        }
    )
}

@Composable
fun PeriodOfPic(){
    Card(
        modifier = Modifier.fillMaxWidth().widthIn(Dimens.maxPhoneWidth).wrapContentHeight().padding(Dimens.gapLarge),
        colors = CardDefaults.cardColors(
            containerColor = MemoryTheme.colors.box,
        ),
        shape = RoundedCornerShape(Dimens.cornerRadius)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.gapLarge),
            verticalArrangement = Arrangement.spacedBy(Dimens.gapLarge)
        ) {
            Text(
                text = "사진 촬영 시기",
                style = MemoryTheme.typography.header,
                color = MemoryTheme.colors.textOnPrimary
            )
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "1975년 5월 --일",
                    style = MemoryTheme.typography.keywordLarge,
                    color = MemoryTheme.colors.textOnPrimary
                )
            }
        }
    }
}

@Composable
fun DescriptionOfPic(
    description: String,
    onChange: (String) -> Unit
){
    Card(
        modifier = Modifier.fillMaxWidth().widthIn(Dimens.maxPhoneWidth).wrapContentHeight().padding(Dimens.gapLarge),
        colors = CardDefaults.cardColors(
            containerColor = MemoryTheme.colors.box,
        ),
        shape = RoundedCornerShape(Dimens.cornerRadius)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.gapLarge),
            verticalArrangement = Arrangement.spacedBy(Dimens.gapLarge)
        ) {
            Text(
                text = "상세 설명",
                style = MemoryTheme.typography.header,
                color = MemoryTheme.colors.textOnPrimary
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                value = description,
                onValueChange = {
                    onChange(it)
                },
                placeholder = {
                    Text(
                        text = "상세설명을 입력해 주세요",
                        style = MemoryTheme.typography.body,
                        color = MemoryTheme.colors.textSecondary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MemoryTheme.colors.buttonBorderUnfocused,
                    focusedBorderColor = MemoryTheme.colors.buttonBorderUnfocused,
                    focusedContainerColor = MemoryTheme.colors.box,
                    unfocusedContainerColor = MemoryTheme.colors.box,
                    focusedTextColor = MemoryTheme.colors.textOnPrimary,
                    unfocusedTextColor = MemoryTheme.colors.textOnPrimary
                ),
                shape = RoundedCornerShape(Dimens.cornerRadius)
            )
        }
    }
}

@Composable
fun EmotionsOfPic(){
    val emojis = listOf(
        R.drawable.love,
        R.drawable.sad,
        R.drawable.interesting,
        R.drawable.tired,
        R.drawable.happy,
        R.drawable.angry
    )
    Card(
        modifier = Modifier.fillMaxWidth().widthIn(Dimens.maxPhoneWidth).wrapContentHeight().padding(Dimens.gapLarge),
        colors = CardDefaults.cardColors(
            containerColor = MemoryTheme.colors.box,
        ),
        shape = RoundedCornerShape(Dimens.cornerRadius)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.gapLarge),
            verticalArrangement = Arrangement.spacedBy(Dimens.gapLarge)
        ) {
            Text(
                text = "사진의 기분",
                style = MemoryTheme.typography.header,
                color = MemoryTheme.colors.textOnPrimary
            )
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                emojis.forEach {
                    AsyncImage(
                        modifier = Modifier
                            .weight(1f)
                            .padding(Dimens.gapSmall)
                            .clickable { /* Handle emoji click */ },
                        model = it,
                        contentDescription = "emoji",
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = PHONE)
@Composable
fun DayDetailScreenPreview() {
    MemoryTheme {
        DayDetailScreen()
    }
}