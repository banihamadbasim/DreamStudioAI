package com.dreamstudioai.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
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
        )

        FeatureCard(
            "🎬  صورة → فيديو",
            "حرّك الصورة وأنشئ فيديو بالذكاء الاصطناعي"
        )

        FeatureCard(
            "✨  مؤثرات AI",
            "اكتب ما تريد بالعربي ودع المحرك ينفذه"
        )

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
            onValueChange = { prompt = it },
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
            onClick = { },
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
            "الخطوة الحالية: واجهة التطبيق فقط. ربط محرك الذكاء الاصطناعي سيتم في المرحلة التالية.",
            color = Color.Gray,
            fontSize = 13.sp
        )
    }
}

@Composable
fun FeatureCard(
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
