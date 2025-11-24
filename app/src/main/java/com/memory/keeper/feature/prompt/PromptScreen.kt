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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
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
import com.memory.keeper.data.dto.request.UserInfoRequest
import com.memory.keeper.data.dto.response.UserInfoDetail
import com.memory.keeper.data.dto.response.UserInfoPhoto
import com.memory.keeper.feature.main.TopBar
import com.memory.keeper.feature.util.ImageUtil
import com.memory.keeper.feature.util.ImageUtil.copyUriToFile
import com.memory.keeper.feature.util.ImageUtil.getRealPathFromURI
import com.memory.keeper.feature.util.PreviewTheme
import com.memory.keeper.ui.theme.MemoryTheme
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
    val role by viewModel.role.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val myInfo by viewModel.myInfo.collectAsStateWithLifecycle()
    val userId by viewModel.userId.collectAsStateWithLifecycle()
    val selectedIdIndex by viewModel.selectedIndex
    var isExpanded by remember { mutableStateOf(false) }
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
                patients = patientNames,
                onClick = { index ->
                    isExpanded = false
                    viewModel.setSelectedIndex(index)
                    viewModel.getUserDetailInfo(
                        patientIds[selectedIdIndex]
                    )
                    viewModel.getUserInfoPhotos(patientIds[selectedIdIndex])
                },
                isExpanded = isExpanded,
                onDismiss = { isExpanded = false }
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
                    patientInfo = patientInfos[selectedIdIndex],
                    userInfoPhotos = userInfoPhotos,
                    updateUserInfo = viewModel::updateUserInfo,
                    uploadUserInfoPhoto = viewModel::uploadUserInfo,
                    deleteUserInfoPhoto = viewModel::deleteUserInfoPhoto,
                    modifyUserInfoPhoto = viewModel::modifyUserInfoPhoto
                )
            } else if( role == "PATIENT" && myInfo != null && userId != null) {
                PromptContent(
                    patientId = userId!!,
                    patientInfo = myInfo!!,
                    userInfoPhotos = userInfoPhotos,
                    updateUserInfo = viewModel::updateUserInfo,
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
    patientInfo: UserInfoDetail,
    userInfoPhotos: List<UserInfoPhoto>,
    updateUserInfo: (Long, Long, UserInfoRequest) -> Unit,
    uploadUserInfoPhoto: (Long, File, String, String) -> Unit,
    deleteUserInfoPhoto: (Long) -> Unit,
    modifyUserInfoPhoto: (Long, String, String) -> Unit
){
    var age by remember { mutableStateOf(patientInfo.age?.toString() ?: "0") }
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
    var newImageFormCount by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .padding(
                horizontal = 20.dp
            )
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
        state = listState,
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "사용자 정보 등록",
                    color = MemoryTheme.colors.textPrimary,
                    style = MemoryTheme.typography.header
                )
                Spacer(
                    modifier = Modifier.height(10.dp)
                )
                Text(
                    text = "치매 환자를 위한 개인화된 서비스를 위해 정보를 입력해주세요",
                    color = MemoryTheme.colors.textSecondary,
                    style = MemoryTheme.typography.viewAll
                )
            }
        }
        item {
            ContentBox(
                header = "기본 정보",
                content = {
                    MemoryTextField(
                        text = age,
                        placeHolder = Pair("나이", " 세"),
                        onChange = {
                            age = it
                        },
                        focusManager = focusManager
                    )
                    RadioBox(
                        title = "성별",
                        selectedOption = gender,
                        options = listOf("MALE", "FEMALE"),
                        optionsKor = listOf("남성", "여성"),
                        onClick = {
                            gender = it
                        }
                    )
                    RadioBox(
                        title = "치매 인지 상태",
                        selectedOption = cognitiveStatus,
                        options = listOf("DEMENTIA", "MCI", "NORMAL"),
                        optionsKor = listOf("치매", "경도인지장애", "정상"),
                        onClick = {
                            cognitiveStatus = it
                        }
                    )
                }
            )
        }
        item {
            ContentBox(
                header = "개인 정보",
                content = {
                    MemoryTextField(
                        text = hometown,
                        placeHolder = Pair("고향", "예: 서울시 강남구"),
                        onChange = {
                            hometown = it
                        },
                        focusManager = focusManager
                    )
                    MemoryTextField(
                        text = familyInfo,
                        placeHolder = Pair("가족관계", "예: O남 O녀"),
                        onChange = { text ->
                            familyInfo = text
                        },
                        focusManager = focusManager
                    )
                    MemoryTextField(
                        text = lifeHistory,
                        placeHolder = Pair("살았던 곳", "예: 19XX~19XX: OO 거주"),
                        onChange = { text ->
                            lifeHistory = text
                        },
                        focusManager = focusManager
                    )
                },
            )
        }
        item {
            ContentBox(
                header = "배경 정보",
                content = {
                    MemoryTextField(
                        text = education,
                        placeHolder = Pair("학력", "예: 고등학교 졸업"),
                        onChange = { text ->
                            education = text
                        },
                        focusManager = focusManager
                    )
                    MemoryTextField(
                        text = occupation,
                        placeHolder = Pair("직업 경험", "예: 교사 30년, 은퇴"),
                        onChange = { text ->
                            occupation = text
                        },
                        focusManager = focusManager
                    )
                },
            )
        }
        item {
            ContentBox(
                header = "특별 정보",
                content = {
                    MemoryTextField(
                        text = forbiddenKeywords,
                        placeHolder = Pair("금기어", "예: 죽음, 병원, 약"),
                        onChange = { text ->
                            forbiddenKeywords = text
                        },
                        focusManager = focusManager
                    )
                    MemoryTextField(
                        text = lifetimeLine,
                        placeHolder = Pair("일대기", "예: 주요 인생 사건들"),
                        onChange = { text ->
                            lifetimeLine = text
                        },
                        focusManager = focusManager
                    )
                },
            )
        }
        item {
            ContentBox(
                header = "인물 사진 등록",
                icon = R.drawable.plus,
                onClickIcon = {
                    newImageFormCount += 1
                }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ){
                    if(userInfoPhotos.isNotEmpty()){
                        userInfoPhotos.forEach { photo ->
                            var description by remember { mutableStateOf(photo.description) }
                            var relationToPatient by remember { mutableStateOf(photo.relationToPatient) }
                            ImageForm(
                                userInfoPhoto = photo,
                                onUploadImage = { file ->
                                    val compressedImageFile = ImageUtil.compressImage(context, file)
                                    uploadUserInfoPhoto(patientId, compressedImageFile, description, relationToPatient)
                                },
                                focusManager = focusManager,
                                onDeleteImage = deleteUserInfoPhoto,
                                description = description,
                                relationToPatient = relationToPatient,
                                onChangeDescription = {
                                    description = it
                                },
                                onChangeRelationToPatient = {
                                    relationToPatient = it
                                }
                            )
                        }
                    } else {
                        var description by remember { mutableStateOf("") }
                        var relationToPatient by remember { mutableStateOf("") }
                        ImageForm(
                            userInfoPhoto = null,
                            onUploadImage = { file ->
                                val compressedImageFile = ImageUtil.compressImage(context, file)
                                uploadUserInfoPhoto(patientId, compressedImageFile, description, relationToPatient)
                            },
                            focusManager = focusManager,
                            onDeleteImage = deleteUserInfoPhoto,
                            description = description,
                            relationToPatient = relationToPatient,
                            onChangeDescription = {
                                description = it
                            },
                            onChangeRelationToPatient = {
                                relationToPatient = it
                            }
                        )
                    }
                    repeat(newImageFormCount){
                        var description by remember { mutableStateOf("") }
                        var relationToPatient by remember { mutableStateOf("") }
                        ImageForm(
                            userInfoPhoto = null,
                            onUploadImage = { file ->
                                val compressedImageFile = ImageUtil.compressImage(context, file)
                                uploadUserInfoPhoto(patientId, compressedImageFile, description, relationToPatient)
                            },
                            focusManager = focusManager,
                            onDeleteImage = deleteUserInfoPhoto,
                            description = description,
                            relationToPatient = relationToPatient,
                            onChangeDescription = {
                                description = it
                            },
                            onChangeRelationToPatient = {
                                relationToPatient = it
                            }
                        )
                    }
                }
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 40.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        //cancel
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MemoryTheme.colors.box,
                        contentColor = MemoryTheme.colors.textPrimary,
                    ),
                    shape = RectangleShape
                ) {
                    Text(
                        text = "취소",
                        style = MemoryTheme.typography.button,
                    )
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        //saveAll
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
                    ),
                    shape = RectangleShape
                ) {
                    Text(
                        text = "저장",
                        style = MemoryTheme.typography.button,
                    )
                }
            }
        }
    }
}

@Composable
fun ImageForm(
    userInfoPhoto: UserInfoPhoto?,
    onUploadImage: (File) -> Unit,
    onDeleteImage: (Long) -> Unit,
    focusManager: FocusManager,
    description: String? = null,
    relationToPatient: String? = null,
    onChangeDescription: (String) -> Unit,
    onChangeRelationToPatient: (String) -> Unit,
){
    val context = LocalContext.current
    var image by remember { mutableStateOf(userInfoPhoto?.url) }
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
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier.wrapContentSize().background(
                        color = MemoryTheme.colors.background
                    ),
                    contentAlignment = Alignment.Center
                ){
                    if(image == null){
                        Column(
                            modifier = Modifier.fillMaxSize().clickable {
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
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.camera),
                                contentDescription = "camera",
                                tint = Color.Unspecified,
                            )
                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )
                            Text(
                                text = "사진을 업로드하세요",
                                style = MemoryTheme.typography.textField,
                                color = MemoryTheme.colors.textSecondary
                            )
                        }
                    } else {
                        AsyncImage(
                            model = image,
                            contentDescription = "uploaded image",
                            alignment = Alignment.Center,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(100.dp)
                        )
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.close),
                            contentDescription = "close",
                            tint = Color.Unspecified,
                            modifier = Modifier.align(Alignment.TopEnd).clickable {
                                image = null
                                onDeleteImage(userInfoPhoto!!.id)
                            }
                        )
                    }
                }
                Button(
                    onClick = {
                        imageFile?.let {
                            onUploadImage(it)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MemoryTheme.colors.primary,
                        contentColor = MemoryTheme.colors.textOnPrimary,
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "저장",
                        style = MemoryTheme.typography.button,
                        color = MemoryTheme.colors.buttonText
                    )
                }
            }
            MemoryTextField(
                text = relationToPatient ?: "",
                placeHolder = Pair("관계", "예: 부인"),
                onChange = { text ->
                    onChangeRelationToPatient(text)
                },
                focusManager = focusManager,
            )
            MemoryTextField(
                text = description ?: "",
                placeHolder = Pair("사진 설명", "예: 젊은 시절 가족사진"),
                onChange = { text ->
                    onChangeDescription(text)
                },
                focusManager = focusManager,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PHONE)
@Composable
fun PromptContentPreview() {
    PreviewTheme {
        PromptContent(
            patientId = 0,
            patientInfo = UserInfoDetail(
                userInfoId = 0
            ),
            userInfoPhotos = emptyList(),
            updateUserInfo = {_,_,_ -> },
            uploadUserInfoPhoto = {_, _, _,_ -> },
            deleteUserInfoPhoto = {},
            modifyUserInfoPhoto = {_, _,_ -> }
        )
    }
}