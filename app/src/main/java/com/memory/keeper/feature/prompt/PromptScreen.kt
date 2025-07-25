package com.memory.keeper.feature.prompt

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.memory.keeper.MainActivity
import com.memory.keeper.R
import com.memory.keeper.core.Dimens
import com.memory.keeper.data.dto.request.UserInfoRequest
import com.memory.keeper.data.dto.response.UserInfoDetail
import com.memory.keeper.data.dto.response.UserInfoPhoto
import com.memory.keeper.feature.home.ContentBox
import com.memory.keeper.feature.main.TopBar
import com.memory.keeper.feature.util.ImageUtil
import com.memory.keeper.feature.util.ImageUtil.copyUriToFile
import com.memory.keeper.feature.util.ImageUtil.getRealPathFromURI
import com.memory.keeper.ui.theme.MemoryTheme
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun PromptScreen(
    viewModel: PromptViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val patientInfos by viewModel.patientInfos.collectAsStateWithLifecycle()
    val patientIds by viewModel.patientIds.collectAsStateWithLifecycle()
    val patientNames by viewModel.patientNames.collectAsStateWithLifecycle()
    val userInfoPhotos by viewModel.userInfoPhoto.collectAsStateWithLifecycle()
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val role by viewModel.role.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val myInfo by viewModel.myInfo.collectAsStateWithLifecycle()
    val userId by viewModel.userId.collectAsStateWithLifecycle()
    val selectedIdIndex by viewModel.selectedIndex
    val context = LocalContext.current
    LaunchedEffect(role) {
        if(role != null){
            if(role == "PATIENT"){
                viewModel.getMyDetailInfo()
            } else {
                viewModel.getMyPatients()
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopBar(
            isPatient = role == "PATIENT",
            userName = userName,
            selectedIndex = selectedIndex,
            onClick = { index ->
                selectedIndex = index
            },
        )
        if(uiState == PromptUIState.Loading){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator(
                    color = MemoryTheme.colors.primary
                )
            }
        }else{
            if(role != "PATIENT" && patientIds.isNotEmpty()){
                PromptContent(
                    patientId = patientIds[selectedIdIndex],
                    patientName = patientNames[selectedIdIndex],
                    patientInfo = patientInfos[selectedIdIndex],
                    userInfoPhotos = userInfoPhotos,
                    updateUserInfo = viewModel::updateUserInfo,
                    size = patientIds.size,
                    selectedIdIndex = selectedIdIndex,
                    onClick = { index ->
                        viewModel.setSelectedIndex(index)
                        viewModel.getUserDetailInfo(
                            patientIds[selectedIdIndex]
                        )
                        viewModel.getUserInfoPhotos(patientIds[selectedIdIndex])
                    },
                    uploadUserInfoPhoto = viewModel::uploadUserInfo,
                    deleteUserInfoPhoto = viewModel::deleteUserInfoPhoto,
                    modifyUserInfoPhoto = viewModel::modifyUserInfoPhoto
                )
            } else if( role == "PATIENT" && myInfo != null && userId != null) {
                PromptContent(
                    patientId = userId!!,
                    patientName = userName,
                    patientInfo = myInfo!!,
                    userInfoPhotos = userInfoPhotos,
                    updateUserInfo = viewModel::updateUserInfo,
                    size = 1,
                    selectedIdIndex = 0,
                    onClick = { },
                    uploadUserInfoPhoto = viewModel::uploadUserInfo,
                    deleteUserInfoPhoto = viewModel::deleteUserInfoPhoto,
                    modifyUserInfoPhoto = viewModel::modifyUserInfoPhoto
                )
            }
        }
    }
    LaunchedEffect(true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is PromptUIEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@Composable
private fun PromptContent(
    patientId: Long,
    patientName: String?,
    patientInfo: UserInfoDetail,
    userInfoPhotos: List<UserInfoPhoto>,
    updateUserInfo: (Long, Long, UserInfoRequest) -> Unit,
    size: Int,
    selectedIdIndex: Int,
    onClick: (Int) -> Unit,
    uploadUserInfoPhoto: (Long, File, String, String) -> Unit,
    deleteUserInfoPhoto: (Long) -> Unit,
    modifyUserInfoPhoto: (Long, String, String) -> Unit
){
    var age by remember { mutableStateOf(patientInfo.age.toString()) }
    var gender by remember {mutableStateOf(patientInfo.gender ?: "")}
    var cognitiveStatus by remember {mutableStateOf(patientInfo.cognitiveStatus ?: "")}
    var hometown by remember { mutableStateOf(patientInfo.hometown ?: "") }
    var familyInfo by remember { mutableStateOf(patientInfo.familyInfo ?: "") }
    var lifeHistory by remember { mutableStateOf(patientInfo.lifeHistory ?: "") }
    var education by remember { mutableStateOf(patientInfo.education ?: "") }
    var occupation by remember { mutableStateOf(patientInfo.occupation ?: "") }
    var forbiddenKeywords by remember { mutableStateOf(patientInfo.forbiddenKeywords ?: "") }
    var lifetimeLine by remember { mutableStateOf(patientInfo.lifetimeline ?: "") }
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val isAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        }
    }
    var isChanged by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(Dimens.gapHuge)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                },
            state = listState,
            verticalArrangement = Arrangement.spacedBy(Dimens.gapLarge)
        ) {
            stickyHeader {
                Row(
                    modifier = Modifier.fillMaxWidth().background(
                        color = MemoryTheme.colors.surface
                    ).padding(bottom = Dimens.gapMedium),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "${patientName}님의 정보 입력",
                            style = MemoryTheme.typography.headlineLarge,
                            color = MemoryTheme.colors.textPrimary
                        )
                        Text(
                            text = "자세하게 적을수록 도움이 됩니다.",
                            style = MemoryTheme.typography.headlineSmall,
                            color = MemoryTheme.colors.optionTextUnfocused
                        )
                    }
                    Button(
                        onClick = {
                            updateUserInfo(
                                patientId,
                                patientInfo.userInfoId,
                                UserInfoRequest(
                                    age = age.toInt(),
                                    gender = gender,
                                    cognitiveStatus = cognitiveStatus,
                                    education = education,
                                    occupation = occupation,
                                    hometown = hometown,
                                    familyInfo = familyInfo,
                                    forbiddenKeywords = forbiddenKeywords,
                                    lifetimeline = lifetimeLine,
                                    lifeHistory = lifeHistory,
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MemoryTheme.colors.primary,
                            contentColor = MemoryTheme.colors.textOnPrimary,
                            disabledContentColor = MemoryTheme.colors.buttonBorderUnfocused,
                            disabledContainerColor = MemoryTheme.colors.surface
                        ),
                        enabled = isChanged,
                        shape = RoundedCornerShape(Dimens.cornerRadius)
                    ) {
                        Text(
                            text = "전체 저장",
                            style = MemoryTheme.typography.button,
                        )
                    }
                }
            }
            item {
                AgeBox(
                    text = age,
                    onChange = {
                        age = it
                        if(age != patientInfo.age.toString()){
                            isChanged = true
                        }
                    }
                )
            }
            item {
                RadioBox(
                    title = "성별",
                    selectedOption = gender,
                    options = listOf("MALE", "FEMALE"),
                    optionsKor = listOf("남성", "여성"),
                    onClick = {
                        gender = it
                        if(gender != patientInfo.gender){
                            isChanged = true
                        }
                    }
                )
            }
            item {
                RadioBox(
                    title = "치매 인지 상태",
                    selectedOption = cognitiveStatus,
                    options = listOf("DEMENTIA", "MCI", "NORMAL"),
                    optionsKor = listOf("치매", "경도인지장애", "정상"),
                    onClick = {
                        cognitiveStatus = it
                        if(cognitiveStatus != patientInfo.cognitiveStatus){
                            isChanged = true
                        }
                    }
                )
            }
            item {
                ContentBox(
                    title = "고향",
                    text = hometown,
                    placeHolder = "ex) OO남도 OO시",
                    onChange = {
                        hometown = it
                        if(hometown != patientInfo.hometown){
                            isChanged = true
                        }
                    },
                    focusManager = focusManager
                )
            }
            item {
                ContentBox(
                    title = "가족 관계",
                    text = familyInfo,
                    placeHolder = "ex) O남 O녀",
                    onChange = {
                        familyInfo = it
                        if(familyInfo != patientInfo.familyInfo){
                            isChanged = true
                        }
                    },
                    focusManager = focusManager
                )
            }
            item {
                ContentBox(
                    title = "살았던 곳",
                    text = lifeHistory,
                    placeHolder = "ex) 19XX~19XX: OO 거주",
                    onChange = {
                        lifeHistory = it
                        if(lifeHistory != patientInfo.lifeHistory){
                            isChanged = true
                        }
                    },
                    focusManager = focusManager
                )
            }
            item {
                ContentBox(
                    title = "학력",
                    text = education,
                    placeHolder = "ex) OO고등학교 졸업",
                    onChange = {
                        education = it
                        if(education != patientInfo.education){
                            isChanged = true
                        }
                    },
                    focusManager = focusManager
                )
            }
            item {
                ContentBox(
                    title = "직업 경험",
                    text = occupation,
                    placeHolder = "ex) OOO(19XX~19XX)",
                    onChange = {
                        occupation = it
                        if(occupation != patientInfo.occupation){
                            isChanged = true
                        }
                    },
                    focusManager = focusManager
                )
            }
            item {
                ContentBox(
                    title = "금기어",
                    text = forbiddenKeywords,
                    placeHolder = "ex) 전쟁, 병원, 남편 사망",
                    onChange = {
                        forbiddenKeywords = it
                        if(forbiddenKeywords != patientInfo.forbiddenKeywords){
                            isChanged = true
                        }
                    },
                    focusManager = focusManager
                )
            }
            item {
                ContentBox(
                    title = "일대기",
                    text = lifetimeLine,
                    placeHolder = "ex) 1943년 출생 → 1962년 결혼 → 1975년 첫째 출산 → 1995년 서울 이사 → 2003년 남편 별세",
                    onChange = {
                        lifetimeLine = it
                        if(lifetimeLine != patientInfo.lifetimeline){
                            isChanged = true
                        }
                    },
                    focusManager = focusManager
                )
            }
            item {
                RelationShipPhotoBox(
                    userInfoPhotos = userInfoPhotos,
                    focusManager = focusManager,
                    uploadUserInfoPhoto = { file, description, relationToPatient ->
                        uploadUserInfoPhoto(patientId, file, description, relationToPatient)
                    },
                    deleteUserInfoPhoto = { id ->
                        deleteUserInfoPhoto(id)
                    },
                    modifyUserInfoPhoto = { id, description, relationToPatient ->
                        modifyUserInfoPhoto(id, description, relationToPatient)
                    }
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    for( i in 0..size - 1){
                        Text(
                            modifier = Modifier.clickable{
                                onClick(i)
                            },
                            text = (i+1).toString(),
                            style = MemoryTheme.typography.body,
                            color = if(i == selectedIdIndex) MemoryTheme.colors.textPrimary else MemoryTheme.colors.optionTextUnfocused,
                        )
                    }
                }
            }
        }
        if(!isAtTop){
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(Dimens.gapLarge),
                elevation = FloatingActionButtonDefaults.elevation(0.dp),
                containerColor = Color.Unspecified,
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.go_up),
                    contentDescription = "go up",
                    tint = Color.Unspecified
                )
            }
        }
    }
}

@Composable
private fun AgeBox(
    text: String,
    onChange: (String) -> Unit
){
    val focusManager = LocalFocusManager.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(Dimens.maxPhoneWidth)
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MemoryTheme.colors.box,
        ),
        shape = RoundedCornerShape(Dimens.boxCornerRadius)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.gapLarge),
            verticalArrangement = Arrangement.spacedBy(Dimens.gapLarge)
        ) {
            Text(
                text = "나이",
                style = MemoryTheme.typography.boxText,
                color = MemoryTheme.colors.textSecondary
            )
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = MemoryTheme.colors.divider,
                thickness = 1.dp
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    modifier = Modifier
                        .width(80.dp)
                        .wrapContentHeight(),
                    value = text,
                    onValueChange = {
                        onChange(it)
                    },
                    textStyle = MemoryTheme.typography.boxText,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MemoryTheme.colors.box,
                        unfocusedContainerColor = MemoryTheme.colors.box,
                        focusedTextColor = MemoryTheme.colors.textOnPrimary,
                        unfocusedTextColor = MemoryTheme.colors.textOnPrimary,
                        focusedIndicatorColor = MemoryTheme.colors.textOnPrimary,
                        unfocusedIndicatorColor = MemoryTheme.colors.textOnPrimary,
                    ),
                    shape = RoundedCornerShape(Dimens.cornerRadius),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {focusManager.clearFocus()}
                    )
                )
                Text(
                    text = "세",
                    style = MemoryTheme.typography.boxText,
                    color = MemoryTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun RadioBox(
    title: String,
    selectedOption: String,
    options: List<String>,
    optionsKor: List<String>,
    onClick: (String) -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(Dimens.maxPhoneWidth)
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MemoryTheme.colors.box,
        ),
        shape = RoundedCornerShape(Dimens.boxCornerRadius)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.gapLarge),
            verticalArrangement = Arrangement.spacedBy(Dimens.gapLarge)
        ) {
            Text(
                text = title,
                style = MemoryTheme.typography.boxText,
                color = MemoryTheme.colors.textSecondary
            )
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = MemoryTheme.colors.divider,
                thickness = 1.dp
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.gapSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                options.forEachIndexed { index, option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = selectedOption == option,
                            onClick = { onClick(option) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MemoryTheme.colors.primary,
                                unselectedColor = MemoryTheme.colors.textSecondary
                            )
                        )
                        Text(
                            text = optionsKor[index],
                            style = MemoryTheme.typography.boxText,
                            color = MemoryTheme.colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RelationShipPhotoBox(
    userInfoPhotos: List<UserInfoPhoto> = emptyList(),
    focusManager: FocusManager,
    uploadUserInfoPhoto: (File, String, String) -> Unit,
    deleteUserInfoPhoto: (Long) -> Unit,
    modifyUserInfoPhoto: (Long, String, String) -> Unit
){
    var photoCount by remember { mutableIntStateOf(if(userInfoPhotos.isEmpty()) 1 else userInfoPhotos.size) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(Dimens.maxPhoneWidth)
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MemoryTheme.colors.box,
        ),
        shape = RoundedCornerShape(Dimens.boxCornerRadius)
    ) {
        Column(
            modifier = Modifier
                .padding(Dimens.gapLarge)
                .padding(vertical = Dimens.gapMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.gapLarge)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "인물 사진 등록",
                    style = MemoryTheme.typography.boxText,
                    color = MemoryTheme.colors.textSecondary
                )
                IconButton(
                    onClick = {
                        photoCount += 1
                    }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.upload),
                        contentDescription = "upload",
                        tint = Color.Unspecified,
                    )
                }
            }
            Column (
                verticalArrangement = Arrangement.spacedBy(Dimens.gapLarge)
            ) {
                repeat(photoCount) { index ->
                    PhotoUploadForm(
                        userInfoPhoto = if(index < userInfoPhotos.size) userInfoPhotos[index] else null,
                        focusManager = focusManager,
                        uploadUserInfoPhoto = uploadUserInfoPhoto,
                        deleteUserInfoPhoto = deleteUserInfoPhoto,
                        modifyUserInfoPhoto = modifyUserInfoPhoto
                    )
                }

            }
        }
    }
}

@Composable
fun PhotoUploadForm(
    userInfoPhoto: UserInfoPhoto? = null,
    focusManager: FocusManager,
    uploadUserInfoPhoto: (File, String, String) -> Unit,
    deleteUserInfoPhoto: (Long) -> Unit,
    modifyUserInfoPhoto: (Long, String, String) -> Unit
){
    val context = LocalContext.current
    var image by remember { mutableStateOf(userInfoPhoto?.url) }
    var description by remember { mutableStateOf(userInfoPhoto?.description ?: "") }
    var relationToPatient by remember { mutableStateOf(userInfoPhoto?.relationToPatient ?: "") }
    var imageFile by remember { mutableStateOf<File?>(null) }
    val showDialog = remember { mutableStateOf(false) }
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
    val enabled = description.isNotBlank() && relationToPatient.isNotBlank() && image != null
    Column (
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier.then(Modifier.size(24.dp)),
                onClick = {
                    if (userInfoPhoto != null){
                        deleteUserInfoPhoto(userInfoPhoto.id)
                    }
                    image = null
                    imageFile = null
                    description = ""
                    relationToPatient = ""
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.close),
                    contentDescription = "delete",
                    tint = Color.Unspecified
                )
            }
            OutlinedButton(
                onClick = {
                    if(userInfoPhoto != null && userInfoPhoto.url == image){
                        modifyUserInfoPhoto(userInfoPhoto.id, description, relationToPatient)
                    } else {
                        val compressedImageFile = ImageUtil.compressImage(context, imageFile!!)
                        uploadUserInfoPhoto(compressedImageFile, description, relationToPatient)
                    }
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MemoryTheme.colors.box,
                    contentColor = MemoryTheme.colors.textOnPrimary,
                    disabledContentColor = MemoryTheme.colors.textSecondary,
                    disabledContainerColor = MemoryTheme.colors.box
                ),
                enabled = enabled,
                border = BorderStroke(width = 1.dp, color = if(enabled)MemoryTheme.colors.textOnPrimary else MemoryTheme.colors.textSecondary),
                shape = RoundedCornerShape(Dimens.cornerRadius)
            ) {
                Text(
                    text = "저장",
                    style = MemoryTheme.typography.button,
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.gapMedium)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.gapMedium)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .border(
                            1.dp,
                            color = MemoryTheme.colors.textSecondary,
                            shape = RoundedCornerShape(
                                Dimens.cornerRadius
                            )
                        )
                        .clickable {
                            when {
                                ContextCompat.checkSelfPermission(
                                    context,
                                    galleryPermissions[0]
                                ) == PackageManager.PERMISSION_GRANTED -> {
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
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.camera),
                                contentDescription = "camera",
                                tint = Color.Unspecified,
                            )
                        }
                    } else {
                        AsyncImage(
                            model = image,
                            contentDescription = "uploaded image",
                            alignment = Alignment.Center,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                                .clip(
                                    RoundedCornerShape(Dimens.cornerRadius)
                                )
                        )
                    }
                }
                TextField(
                    modifier = Modifier
                        .width(80.dp),
                    value = relationToPatient,
                    onValueChange = {
                        relationToPatient = it
                    },
                    textStyle = MemoryTheme.typography.boxText,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MemoryTheme.colors.box,
                        unfocusedContainerColor = MemoryTheme.colors.box,
                        focusedTextColor = MemoryTheme.colors.textOnPrimary,
                        unfocusedTextColor = MemoryTheme.colors.textOnPrimary,
                        focusedIndicatorColor = MemoryTheme.colors.textOnPrimary,
                        unfocusedIndicatorColor = MemoryTheme.colors.textOnPrimary,
                    ),
                    placeholder = {
                        Text(
                            text = "관계",
                            style = MemoryTheme.typography.boxText,
                            color = MemoryTheme.colors.textSecondary
                        )
                    },
                    shape = RoundedCornerShape(Dimens.cornerRadius),
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
            OutlinedTextField(
                modifier = Modifier.height(145.dp),
                value = description,
                onValueChange = {
                    description = it
                },
                textStyle = MemoryTheme.typography.boxText,
                placeholder = {
                    Text(
                        text = "사진에 대한 간단한 설명",
                        style = MemoryTheme.typography.boxText,
                        color = MemoryTheme.colors.textSecondary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MemoryTheme.colors.textSecondary,
                    focusedBorderColor = MemoryTheme.colors.textSecondary,
                    focusedContainerColor = MemoryTheme.colors.box,
                    unfocusedContainerColor = MemoryTheme.colors.box,
                    focusedTextColor = MemoryTheme.colors.textOnPrimary,
                    unfocusedTextColor = MemoryTheme.colors.textOnPrimary
                ),
                shape = RoundedCornerShape(Dimens.cornerRadius),
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
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PHONE)
@Composable
fun PromptContentPreview() {
    MemoryTheme {
        PhotoUploadForm(
            focusManager = LocalFocusManager.current,
            uploadUserInfoPhoto = {_,_, _ -> },
            deleteUserInfoPhoto = {_ -> },
            modifyUserInfoPhoto = { _, _, _ -> }
        )
    }
}