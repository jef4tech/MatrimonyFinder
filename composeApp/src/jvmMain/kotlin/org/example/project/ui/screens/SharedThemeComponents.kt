package org.example.project.ui.screens

import org.example.project.data.remote.models.CandidateProfile
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

// --- Theme & Style Tokens ---
val BaseBlack = Color(0xFF000000)
val SurfaceCharcoal = Color(0xFF121212)
val ChampagneGold = Color(0xFFC5A059)
val OffWhite = Color(0xFFE5E2E1)
val OutlineVariant = Color(0x14FFFFFF) // 8% white
val OutlineHover = Color(0x33FFFFFF) // 20% white

val LabelCaps = TextStyle(
    fontFamily = FontFamily.Default,
    fontSize = 12.sp,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 12.sp,
    letterSpacing = 0.1.em,
    color = ChampagneGold
)

val HeadlineLg = TextStyle(
    fontFamily = FontFamily.Default,
    fontSize = 32.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 38.sp,
    letterSpacing = (-0.02).em,
    color = OffWhite
)

val BodyLg = TextStyle(
    fontFamily = FontFamily.Default,
    fontSize = 18.sp,
    fontWeight = FontWeight.Light,
    lineHeight = 28.sp,
    color = Color(0xFFC4C7C8)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumTopAppBar(
    title: String,
    onBack: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(title, style = HeadlineLg) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = OffWhite
                    )
                }
            }
        },
        actions = {
            if (onLogout != null) {
                TextButton(onClick = onLogout) {
                    Text("LOGOUT", style = LabelCaps.copy(color = OffWhite))
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BaseBlack.copy(alpha = 0.8f),
            titleContentColor = OffWhite,
            navigationIconContentColor = OffWhite,
            actionIconContentColor = OffWhite
        )
    )
}

@Composable
fun GalleryProfileCard(
    match: CandidateProfile,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val borderColor = if (isHovered) OutlineHover else OutlineVariant
    val surfaceColor = if (isHovered) SurfaceCharcoal.copy(alpha = 0.9f) else SurfaceCharcoal

    val photoUrl = match.photos?.candidatePhotos?.firstOrNull()?.displayPhotoUrl ?: match.photo?.candidatePhotos?.firstOrNull()?.displayPhotoUrl
    val displayName = match.profileId ?: "Unknown"
    val age = match.age?.toString() ?: ""
    val profession = match.profession?.details ?: match.profession?.name ?: ""
    val location = listOfNotNull(match.workingState, match.workingCountry).joinToString(", ")
    val finalLocation = location.ifEmpty { match.branch ?: "" }

    Box(
        modifier = Modifier
            .aspectRatio(3f / 4f)
            .clip(RoundedCornerShape(16.dp))
            .background(surfaceColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        if (!photoUrl.isNullOrEmpty()) {
            AsyncImage(
                url = photoUrl,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Bottom gradient overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            BaseBlack.copy(alpha = 0.9f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {
            val nameText = if (age.isNotEmpty()) "$displayName, $age" else displayName
            Text(
                text = nameText.uppercase(),
                style = LabelCaps
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (finalLocation.isNotEmpty()) {
                Text(
                    text = finalLocation.uppercase(),
                    style = BodyLg.copy(fontSize = 12.sp, color = OffWhite.copy(alpha = 0.7f)),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (profession.isNotEmpty()) {
                Text(
                    text = profession.uppercase(),
                    style = BodyLg.copy(fontSize = 12.sp, color = OffWhite.copy(alpha = 0.7f)),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        
        // Premium Badge or Indicator
        if (match.isPremium == true) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(ChampagneGold)
            )
        }
    }
}
