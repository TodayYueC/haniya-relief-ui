package com.example.relief.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import com.example.relief.component.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.relief.theme.ReliefTheme
import java.io.File

/**
 * 头像：凸面圆座 + 内嵌照片（或渐变底上的昵称首字）。
 * 照片文件丢失（例如从其它设备恢复备份）时自动回到首字。
 */
@Composable
fun UserAvatar(
    path: String?,
    name: String,
    size: Dp,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val file = remember(path) { path?.let(::File)?.takeIf { it.exists() } }
    val ring = (size.value * 0.07f).coerceIn(2f, 6f).dp
    Box(
        modifier.size(size).glass(CircleShape, if (size >= 64.dp) GlassLevel.Regular else GlassLevel.Thin).padding(ring),
        contentAlignment = Alignment.Center,
    ) {
        if (file != null) {
            AsyncImage(
                model = file, contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize().clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        } else {
            Box(Modifier.fillMaxSize().gradientSurface(CircleShape, shadow = false), contentAlignment = Alignment.Center) {
                Text(
                    name.trim().take(1).ifBlank { "H" },
                    style = ReliefTheme.type.title.copy(fontSize = (size.value * 0.38f).sp),
                    color = Color.White,
                )
            }
        }
    }
}
