package com.dreamstudioai.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private val Bg = Color(0xFF0B0B12)
private val Card = Color(0xFF151522)
private val Accent = Color(0xFF8B5CF6)
private val Accent2 = Color(0xFF38BDF8)

data class Project(
    val title: String,
    val prompt: String,
    val imageUri: Uri? = null
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DreamStudioApp()
        }
    }
}

@Composable
fun DreamStudioApp() {

    var selected by remember { mutableIntStateOf(0) }

    var projects by remember {
        mutableStateOf(listOf<Project>())
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Bg,
            surface = Card,
            primary = Accent,
            secondary = Accent2
        )
    ) {

        Scaffold(
            containerColor = Bg,

            bottomBar = {

                NavigationBar(
                    containerColor = Color(0xFF10101A)
                ) {

                    val items = listOf(
                        "الرئيسية" to Icons.Default.Home,
                        "استكشف" to Icons.Default.Explore,
                        "المكتبة" to Icons.Default.Folder,
                        "الإعدادات" to Icons.Default.Settings
                    )

                    items.forEachIndexed { index, item ->

                        NavigationBarItem(
                            selected = selected == index,

                            onClick = {
                                selected = index
                            },

                            icon = {
                                Icon(
                                    item.second,
                                    contentDescription = item.first
                                )
                            },

                            label = {
                                Text(item.first)
                            }
                        )
                    }
                }
            }

        ) { padding ->

            when (selected) {

                0 -> HomeScreen(
                    modifier = Modifier.padding(padding),
                    onSaveProject = { project ->
                        projects = projects + project
                    }
                )

                1 -> ExploreScreen(
                    modifier = Modifier.padding(padding),
                    onOpenHome = {
                        selected = 0
                    }
                )

                2 -> LibraryScreen(
                    modifier = Modifier.padding(padding),
                    projects = projects,
                    onDelete = { project ->
                        projects = projects - project
                    }
                )

                3 -> SettingsScreen(
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onSaveProject: (Project) -> Unit
) {

    var prompt by remember {
        mutableStateOf("")
    }

    var selectedImage by remember {
        mutableStateOf<Uri?>(null)
    }

    var activeTool by remember {
        mutableStateOf<String?>(null)
    }

    var message by remember {
        mutableStateOf("")
    }

    val pickImage =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            selectedImage = uri

            if (uri != null) {
                message = "تم اختيار الصورة ✓"
            }
        }

    Column(
        modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            item {

                Text(
                    "DreamStudio AI",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    Modifier.height(6.dp)
                )

                Text(
                    "اصنع صورك وفيديوهاتك بالذكاء الاصطناعي",
                    color = Color.LightGray
                )
            }

            item {

                FeatureCard(
                    title = "🖼️ صورة → صورة",
                    subtitle = "تعديل الصورة، الخلفية، الملابس وتحسين الجودة"
                ) {
                    activeTool = "image_to_image"
                }
            }

            item {

                FeatureCard(
                    title = "🎬 صورة → فيديو",
                    subtitle = "حرّك الصورة وأنشئ فيديو بالذكاء الاصطناعي"
                ) {
                    activeTool = "image_to_video"
                }
            }

            item {

                FeatureCard(
                    title = "✨ مؤثرات AI",
                    subtitle = "اكتب ما تريد بالعربي ودع المحرك ينفذه"
                ) {
                    activeTool = "effects"
                }
            }

            item {

                OutlinedButton(
                    onClick = {
                        pickImage.launch("image/*")
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),

                    shape = RoundedCornerShape(16.dp)
                ) {

                    Icon(
                        Icons.Default.AddPhotoAlternate,
                        contentDescription = null
                    )

                    Spacer(
                        Modifier.width(8.dp)
                    )

                    Text(
                        if (selectedImage == null)
                            "اختيار صورة من الهاتف"
                        else
                            "تم اختيار الصورة ✓",

                        fontSize = 17.sp
                    )
                }
            }

            item {

                selectedImage?.let { uri ->

                    AsyncImage(
                        model = uri,
                        contentDescription = "الصورة المختارة",

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp),

                        contentScale = ContentScale.Crop
                    )
                }
            }

            item {

                OutlinedTextField(
                    value = prompt,

                    onValueChange = {
                        prompt = it
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),

                    label = {
                        Text("اكتب ماذا تريد...")
                    },

                    placeholder = {
                        Text(
                            "مثال: خلّي الشخص راكب حصان وخلفه صحراء وقت الغروب"
                        )
                    },

                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Right
                    )
                )
            }

            item {

                Button(
                    onClick = {

                        if (prompt.isBlank() && selectedImage == null) {

                            message =
                                "اكتب وصفًا أو اختر صورة أولًا"

                        } else {

                            val project = Project(
                                title = "مشروع جديد",
                                prompt = prompt,
                                imageUri = selectedImage
                            )

                            onSaveProject(project)

                            message =
                                "تم حفظ المشروع في المكتبة ✓"
                        }
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),

                    shape = RoundedCornerShape(16.dp)
                ) {

                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null
                    )

                    Spacer(
                        Modifier.width(8.dp)
                    )

                    Text(
                        "إنشاء",
                        fontSize = 18.sp
                    )
                }
            }

            item {

                if (message.isNotEmpty()) {

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF20202E)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            message,
                            modifier = Modifier.padding(14.dp),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            item {

                Text(
                    "اختر أداة من الأعلى للبدء.",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        }
    }

    activeTool?.let { tool ->

        ToolDialog(
            tool = tool,
            onDismiss = {
                activeTool = null
            }
        )
    }
}

@Composable
fun FeatureCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },

        colors = CardDefaults.cardColors(
            containerColor = Card
        ),

        shape = RoundedCornerShape(18.dp)
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    Modifier.height(4.dp)
                )

                Text(
                    subtitle,
                    color = Color.LightGray,
                    fontSize = 13.sp
                )
            }

            Icon(
                Icons.Default.ChevronLeft,
                contentDescription = null
            )
        }
    }
}

@Composable
fun ToolDialog(
    tool: String,
    onDismiss: () -> Unit
) {

    val title = when (tool) {

        "image_to_image" ->
            "صورة → صورة"

        "image_to_video" ->
            "صورة → فيديو"

        "effects" ->
            "مؤثرات AI"

        else ->
            "إنشاء"
    }

    val description = when (tool) {

        "image_to_image" ->
            "هذه الأداة مخصصة لتعديل الصور وتغيير الخلفية والملابس وتحسين الجودة."

        "image_to_video" ->
            "هذه الأداة مخصصة لتحريك الصور وتحويل الصورة إلى فيديو."

        "effects" ->
            "اكتب وصفًا للتأثير الذي تريده وسيتم استخدامه لاحقًا مع محرك الذكاء الاصطناعي."

        else ->
            "أداة إنشاء المشروع."
    }

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text(title)
        },

        text = {
            Text(
                description,
                textAlign = TextAlign.Right
            )
        },

        confirmButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("ابدأ")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("إلغاء")
            }
        }
    )
}

@Composable
fun ExploreScreen(
    modifier: Modifier = Modifier,
    onOpenHome: () -> Unit
) {

    val effects = listOf(
        "🎨 تحويل الصورة إلى كرتون",
        "🎬 تأثير سينمائي",
        "👕 تغيير الملابس",
        "🌅 تغيير الخلفية",
        "✨ تحسين جودة الصورة",
        "🪄 إزالة العناصر من الصورة",
        "🌌 تحويل الصورة إلى عالم خيالي",
        "📸 تحسين الإضاءة والألوان"
    )

    Column(
        modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {

        Text(
            "استكشف",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            Modifier.height(8.dp)
        )

        Text(
            "اختر تأثيرًا لتجربته",
            color = Color.LightGray
        )

        Spacer(
            Modifier.height(16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(effects) { effect ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onOpenHome()
                        },

                    colors = CardDefaults.cardColors(
                        containerColor = Card
                    ),

                    shape = RoundedCornerShape(18.dp)
                ) {

                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            effect,
                            fontSize = 17.sp,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            Icons.Default.ChevronLeft,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
    projects: List<Project>,
    onDelete: (Project) -> Unit
) {

    Column(
        modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {

        Text(
            "المكتبة",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            Modifier.height(8.dp)
        )

        if (projects.isEmpty()) {

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Card
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Icon(
                        Icons.Default.Folder,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )

                    Spacer(
                        Modifier.height(10.dp)
                    )

                    Text(
                        "لا توجد مشاريع محفوظة حتى الآن.",
                        textAlign = TextAlign.Center
                    )

                    Spacer(
                        Modifier.height(6.dp)
                    )

                    Text(
                        "أنشئ مشروعًا من الصفحة الرئيسية وسيظهر هنا.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items = projects,
                    key = { it.hashCode() }
                ) { project ->

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Card
                        ),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {

                            project.imageUri?.let { uri ->

                                AsyncImage(
                                    model = uri,
                                    contentDescription = null,

                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp),

                                    contentScale = ContentScale.Crop
                                )

                                Spacer(
                                    Modifier.height(10.dp)
                                )
                            }

                            Text(
                                project.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(
                                Modifier.height(5.dp)
                            )

                            Text(
                                if (project.prompt.isBlank())
                                    "مشروع بدون وصف"
                                else
                                    project.prompt,

                                color = Color.LightGray
                            )

                            Spacer(
                                Modifier.height(10.dp)
                            )

                            OutlinedButton(
                                onClick = {
                                    onDelete(project)
                                },

                                modifier = Modifier.fillMaxWidth()
                            ) {

                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null
                                )

                                Spacer(
                                    Modifier.width(6.dp)
                                )

                                Text("حذف المشروع")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {

    var arabic by remember {
        mutableStateOf(true)
    }

    var highQuality by remember {
        mutableStateOf(true)
    }

    var notifications by remember {
        mutableStateOf(true)
    }

    LazyColumn(
        modifier
            .fillMaxSize()
            .padding(18.dp),

        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        item {

            Text(
                "الإعدادات",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {

            SettingSwitch(
                title = "اللغة العربية",
                subtitle = "استخدام اللغة العربية داخل التطبيق",
                checked = arabic,
                onCheckedChange = {
                    arabic = it
                }
            )
        }

        item {

            SettingSwitch(
                title = "جودة عالية",
                subtitle = "استخدام إعدادات جودة أعلى عند توفر المحرك",
                checked = highQuality,
                onCheckedChange = {
                    highQuality = it
                }
            )
        }

        item {

            SettingSwitch(
                title = "الإشعارات",
                subtitle = "السماح بإشعارات التطبيق",
                checked = notifications,
                onCheckedChange = {
                    notifications = it
                }
            )
        }

        item {

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Card
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        "محرك الذكاء الاصطناعي",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        Modifier.height(6.dp)
                    )

                    Text(
                        "لم يتم ربط محرك AI خارجي حتى الآن.",
                        color = Color.LightGray
                    )

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    Text(
                        "سنضيف محركًا مجانيًا أو مفتوح المصدر في المرحلة القادمة.",
                        color = Accent2
                    )
                }
            }
        }

        item {

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Card
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        "حول التطبيق",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        Modifier.height(6.dp)
                    )

                    Text(
                        "DreamStudio AI",
                        color = Color.LightGray
                    )

                    Text(
                        "الإصدار 1.0",
                        color = Color.Gray
                    )

                    Spacer(
                        Modifier.height(6.dp)
                    )

                    Text(
                        "منصة لإنشاء وتعديل الصور والفيديو باستخدام الذكاء الاصطناعي.",
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}

@Composable
fun SettingSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Card
        ),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    Modifier.height(3.dp)
                )

                Text(
                    subtitle,
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}
