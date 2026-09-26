package com.dreamstudioai.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
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

                    items.forEachIndexed { i, item ->
                        NavigationBarItem(
                            selected = selected == i,
                            onClick = { selected = i },
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
        ) { pad ->
            when (selected) {
                0 -> HomeScreen(Modifier.padding(pad))
                1 -> ExploreScreen(Modifier.padding(pad))
                2 -> LibraryScreen(Modifier.padding(pad))
                else -> SettingsScreen(Modifier.padding(pad))
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var prompt by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    var activeTool by remember { mutableStateOf<String?>(null) }

    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImage = uri
    }

    Column(
        modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        Text(
            "DreamStudio AI",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "اصنع صورك وفيديوهاتك بالذكاء الاصطناعي",
            color = Color.LightGray
        )

        FeatureCard(
            "🖼️  صورة → صورة",
            "تعديل الصورة، الخلفية، الملابس وتحسين الجودة"
        ) {
            activeTool = "image_to_image"
        }

        FeatureCard(
            "🎬  صورة → فيديو",
            "حرّك الصورة وأنشئ فيديو بالذكاء الاصطناعي"
        ) {
            activeTool = "image_to_video"
        }

        FeatureCard(
            "✨  مؤثرات AI",
            "اكتب ما تريد بالعربي ودع المحرك ينفذه"
        ) {
            activeTool = "effects"
        }

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

            Spacer(Modifier.width(8.dp))

            Text(
                if (selectedImage == null)
                    "اختيار صورة من الهاتف"
                else
                    "تم اختيار الصورة ✓",
                fontSize = 17.sp
            )
        }

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

        Button(
            onClick = {
                activeTool = "create"
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

            Spacer(Modifier.width(8.dp))

            Text(
                "إنشاء",
                fontSize = 18.sp
            )
        }

        Text(
            "اختر أداة من الأعلى للبدء.",
            color = Color.Gray,
            fontSize = 13.sp
        )

        activeTool?.let { tool ->
            ToolDialog(tool) {
                activeTool = null
            }
        }
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
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                Modifier.weight(1f)
            ) {
                Text(
                    title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(4.dp))

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
        "image_to_image" -> "صورة → صورة"
        "image_to_video" -> "صورة → فيديو"
        "effects" -> "مؤثرات AI"
        else -> "إنشاء"
    }

    val message = when (tool) {
        "image_to_image" ->
            "تم فتح أداة صورة → صورة. سيتم ربط محرك الذكاء الاصطناعي في المرحلة التالية."

        "image_to_video" ->
            "تم فتح أداة صورة → فيديو. سيتم ربط محرك الفيديو في المرحلة التالية."

        "effects" ->
            "تم فتح أداة مؤثرات AI. سيتم ربط المؤثرات في المرحلة التالية."

        else ->
            "زر الإنشاء يعمل الآن، وسيتم ربطه بمحرك الذكاء الاصطناعي في المرحلة التالية."
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title)
        },
        text = {
            Text(
                message,
                textAlign = TextAlign.Right
            )
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("حسنًا")
            }
        }
    )
}

@Composable
fun ExploreScreen(
    modifier: Modifier = Modifier
) {
    SimplePage(
        modifier,
        "استكشف",
        "مؤثرات AI • تحويل سينمائي • كرتون • تغيير الملابس • مشاهد جديدة"
    )
}

@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier
) {
    SimplePage(
        modifier,
        "المكتبة",
        "هنا ستظهر الصور والفيديوهات والمشاريع المحفوظة."
    )
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    SimplePage(
        modifier,
        "الإعدادات",
        "إعدادات المحرك • مفتاح API • اللغة • الجودة • الحساب"
    )
}

@Composable
fun SimplePage(
    modifier: Modifier,
    title: String,
    body: String
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        Text(
            title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Card
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(
                body,
                Modifier.padding(18.dp),
                color = Color.LightGray,
                fontSize = 16.sp
            )
        }
    }
}
