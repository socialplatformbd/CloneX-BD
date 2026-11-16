package com.clonexbd.app

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import java.util.Locale

private val Blue = Color(0xFF5265D8)
private val Purple = Color(0xFF7656C9)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("clonex_settings", MODE_PRIVATE)
        setContent {
            var dark by remember { mutableStateOf(prefs.getBoolean("dark", false)) }
            var language by remember { mutableStateOf(prefs.getString("language", "en") ?: "en") }
            CloneXTheme(dark) {
                CloneXApp(
                    dark = dark,
                    language = language,
                    onDarkChange = { dark = it; prefs.edit().putBoolean("dark", it).apply() },
                    onLanguageChange = { language = it; prefs.edit().putString("language", it).apply() }
                )
            }
        }
    }
}

@Composable
private fun CloneXTheme(dark: Boolean, content: @Composable () -> Unit) {
    val scheme = if (dark) darkColorScheme(
        primary = Color(0xFF9AA7FF), secondary = Color(0xFFC4B5FD),
        background = Color(0xFF121418), surface = Color(0xFF20242B),
        onBackground = Color(0xFFF5F6F8), onSurface = Color(0xFFF5F6F8)
    ) else lightColorScheme(
        primary = Blue, secondary = Purple, background = Color(0xFFF7F8FA),
        surface = Color.White, onBackground = Color(0xFF17191D), onSurface = Color(0xFF17191D)
    )
    MaterialTheme(colorScheme = scheme, content = content)
}

private data class AppEntry(val label: String, val packageName: String, val icon: Drawable)

@Composable
private fun CloneXApp(
    dark: Boolean, language: String,
    onDarkChange: (Boolean) -> Unit, onLanguageChange: (String) -> Unit
) {
    val bn = language == "bn"
    val context = LocalContext.current
var tab by remember { mutableIntStateOf(0) }
    var showInfo by remember { mutableStateOf(false) }
    var selectedApps by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    val titles = if (bn) listOf("হোম", "অ্যাপস", "ক্লোন", "সেটিংস") else listOf("Home", "Apps", "Clones", "Settings")
    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf(Icons.Default.Home, Icons.Default.Apps, Icons.Default.Layers, Icons.Default.Settings).forEachIndexed { i, icon ->
                    NavigationBarItem(selected = tab == i, onClick = { tab = i },
                        icon = { Icon(icon, null) }, label = { Text(titles[i]) })
                }
            }
        }
    ) { padding ->
        when (tab) {
            0 -> HomeScreen(bn, Modifier.padding(padding), onGoApps = { tab = 1 }, onInfo = { showInfo = true })
            1 -> AppsScreen(
                bn, Modifier.padding(padding), selectedApps.keys,
                onSelectionChange = { pkg, label, checked ->
                    selectedApps = if (checked) selectedApps + (pkg to label)
                    else selectedApps - pkg
                }
            )
            2 -> CloneScreen(
                bn, Modifier.padding(padding), selectedApps,
                onRemove = { pkg -> selectedApps = selectedApps - pkg }
            )
            else -> SettingsScreen(
                bn, dark, onDarkChange, onLanguageChange,
                Modifier.padding(padding),
                onInfo = { showInfo = true }
            )
        }
    }
    if (showInfo) {
        AlertDialog(onDismissRequest = { showInfo = false },
            confirmButton = { TextButton(onClick = { showInfo = false }) { Text(if (bn) "বন্ধ" else "Close") } },
            title = { Text("CloneX BD") },
            text = { Text(if (bn) "Made by Mohammad Rakib\nবাংলা ও English\n\nএই প্রাথমিক সংস্করণে অ্যাপ তালিকা, থিম ও ভাষা সেটিংস আছে। এই সংস্করণ অন্য অ্যাপের ক্লোন তৈরি বা ব্যক্তিগত ডেটা অ্যাক্সেস করে না।" else "Made by Mohammad Rakib\nBangla & English\n\nThis starter build includes app discovery, theme and language settings. This version does not create app clones or access other apps' private data.") })
    }
}

@Composable
private fun Page(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Box(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            Modifier.fillMaxWidth().widthIn(max = 720.dp).align(Alignment.TopCenter)
                .statusBarsPadding().padding(horizontal = 18.dp, vertical = 12.dp),
            content = content
        )
    }
}

@Composable
private fun HomeScreen(bn: Boolean, modifier: Modifier, onGoApps: () -> Unit, onInfo: () -> Unit) {
    Page(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.weight(1f)) {
                Text("CloneX BD", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text(if (bn) "স্মার্টভাবে ক্লোন করুন। সহজে পরিচালনা করুন।" else "Clone Smarter. Manage Better.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
            IconButton(onClick = onInfo) { Icon(Icons.Default.Info, contentDescription = "About") }
        }
        Spacer(Modifier.height(22.dp))
        Text(if (bn) "স্বাগতম" else "Welcome", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(if (bn) "ইনস্টল করা অ্যাপ" else "Installed apps", "—", Modifier.weight(1f))
            StatCard(if (bn) "ক্লোন" else "Clones", "0", Modifier.weight(1f))
        }
        Spacer(Modifier.height(14.dp))
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(Modifier.padding(18.dp)) {
                Icon(Icons.Default.Apps, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(30.dp), contentDescription = null)
                Spacer(Modifier.height(8.dp))
                Text(if (bn) "আপনার অ্যাপগুলো দেখুন" else "Explore installed apps", fontWeight = FontWeight.Bold)
                Text(if (bn) "ডিভাইসে লঞ্চার-সমর্থিত অ্যাপের তালিকা দেখুন।" else "Browse launcher apps available on this device.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                Button(onClick = onGoApps) { Text(if (bn) "অ্যাপ দেখুন" else "Browse apps") }
            }
        }
        Spacer(Modifier.height(18.dp))
        Text(if (bn) "ডেভেলপার" else "Developer", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Made by Mohammad Rakib", fontWeight = FontWeight.Medium)
        TextButton(onClick = onInfo) { Text(if (bn) "অ্যাপ সম্পর্কে" else "About CloneX BD") }
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier) {
    Card(modifier, shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(value, fontSize = 27.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
    }
}


@Composable
private fun AppsScreen(
    bn: Boolean,
    modifier: Modifier,
    selectedPackages: Set<String>,
    onSelectionChange: (String, String, Boolean) -> Unit
) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var apps by remember { mutableStateOf(emptyList<AppEntry>()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        apps = runCatching {
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_LAUNCHER)

            @Suppress("DEPRECATION")
            pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
                .mapNotNull { info ->
                    val pkg = info.activityInfo.packageName
                    if (pkg == context.packageName) null
                    else AppEntry(
                        info.loadLabel(pm).toString(),
                        pkg,
                        info.loadIcon(pm)
                    )
                }
                .distinctBy { it.packageName }
                .sortedBy { it.label.lowercase(Locale.getDefault()) }
        }.getOrDefault(emptyList())

        loading = false
    }

    val filtered = apps.filter {
        it.label.contains(query, true) ||
            it.packageName.contains(query, true)
    }

    Page(modifier) {
        Text(
            if (bn) "ইনস্টল করা অ্যাপ" else "Installed Apps",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            if (bn) "নির্বাচিত: ${selectedPackages.size} টি"
            else "Selected: ${selectedPackages.size}",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            query,
            { query = it },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, null) },
            placeholder = {
                Text(if (bn) "অ্যাপ খুঁজুন" else "Search apps")
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(Modifier.height(10.dp))

        when {
            loading -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            filtered.isEmpty() -> Text(
                if (bn) "কোনো অ্যাপ পাওয়া যায়নি" else "No apps found"
            )

            else -> LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtered, key = { it.packageName }) { app ->
                    val checked = app.packageName in selectedPackages

                    Card(shape = RoundedCornerShape(16.dp)) {
                        Row(
                            Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                app.icon.toBitmap(96, 96).asImageBitmap(),
                                null,
                                Modifier.size(42.dp)
                            )

                            Spacer(Modifier.width(12.dp))

                            Column(Modifier.weight(1f)) {
                                Text(
                                    app.label,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    app.packageName,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Checkbox(
                                checked = checked,
                                onCheckedChange = {
                                    onSelectionChange(
                                        app.packageName,
                                        app.label,
                                        it
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CloneScreen(
    bn: Boolean,
    modifier: Modifier,
    selectedApps: Map<String, String>,
    onRemove: (String) -> Unit
) {
    Page(modifier) {
        Text(
            if (bn) "ক্লোন ম্যানেজার" else "Clone Manager",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(14.dp))

        Text(
            if (bn) "নির্বাচিত অ্যাপ: ${selectedApps.size} টি"
            else "Selected apps: ${selectedApps.size}",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(10.dp))

        if (selectedApps.isEmpty()) {
            Card(shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Icon(
                        Icons.Default.Layers,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(34.dp),
                        contentDescription = null
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        if (bn) "কোনো অ্যাপ নির্বাচন করা হয়নি"
                        else "No apps selected",
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        if (bn) "Apps থেকে অ্যাপ নির্বাচন করুন।"
                        else "Select apps from the Apps tab."
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedApps.toList(), key = { it.first }) { item ->
                    Card(shape = RoundedCornerShape(16.dp)) {
                        Row(
                            Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    item.second,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    item.first,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            TextButton(onClick = { onRemove(item.first) }) {
                                Text(if (bn) "বাদ দিন" else "Remove")
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Text(
            if (bn) "গুরুত্বপূর্ণ" else "Important",
            fontWeight = FontWeight.SemiBold
        )

        Text(
            if (bn)
                "এগুলো শুধু নির্বাচিত অ্যাপ। এখনো কোনো ক্লোন তৈরি হয়নি বা অন্য অ্যাপের ডেটা কপি করা হয়নি।"
            else
                "These are selected apps only. No clones have been created and no other app data has been copied.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsScreen(
    bn: Boolean, dark: Boolean, onDarkChange: (Boolean) -> Unit,
    onLanguageChange: (String) -> Unit,
    modifier: Modifier,
    onInfo: () -> Unit
) {
    val context = LocalContext.current
    Page(modifier) {
        Text(if (bn) "সেটিংস" else "Settings", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Card(shape = RoundedCornerShape(18.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("CloneX BD", fontWeight = FontWeight.Bold, fontSize = 19.sp)
                Text("Made by Mohammad Rakib", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Version 1.0.0 · Starter build", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(14.dp))


        Text(if (bn) "চেহারা" else "Appearance", fontWeight = FontWeight.SemiBold)
        Row(Modifier.fillMaxWidth().clickable { onDarkChange(!dark) }.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.DarkMode, null)
            Spacer(Modifier.width(12.dp))
            Text(if (bn) "ডার্ক থিম" else "Dark theme", Modifier.weight(1f))
            Switch(checked = dark, onCheckedChange = onDarkChange)
        }
        HorizontalDivider()
        Text(if (bn) "ভাষা" else "Language", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            FilterChip(selected = !bn, onClick = { onLanguageChange("en") }, label = { Text("English") })
            Spacer(Modifier.width(8.dp))
            FilterChip(selected = bn, onClick = { onLanguageChange("bn") }, label = { Text("বাংলা") })
        }
        HorizontalDivider(Modifier.padding(vertical = 12.dp))
        Text(if (bn) "কমিউনিটি" else "Community", fontWeight = FontWeight.SemiBold)
        TextButton(onClick = { openUrl(context, "https://t.me/appdevbyrakib") }) { Text("Telegram Channel") }
        TextButton(onClick = { openUrl(context, "https://chat.whatsapp.com/HkvJCqWFf6G4bcQRKspKta?s=cl&p=a&iam=2") }) { Text("WhatsApp Group") }
        TextButton(onClick = onInfo) { Text(if (bn) "অ্যাপ সম্পর্কে" else "About CloneX BD") }
    }
}

private fun openUrl(context: android.content.Context, url: String) {
    runCatching {
        context.startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url)))
    }
}
