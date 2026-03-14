package com.aaditya.smartfit.ui.screens.welcome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── SmartFit Login Design Tokens ─────────────────────────────────────────────
private val BgDeep      = Color(0xFF0D1117)
private val BgMid       = Color(0xFF0D1C1B)
private val BgBottom    = Color(0xFF0A1628)
private val SurfaceCard = Color(0xFF161B22)
private val TealPrimary = Color(0xFF00C9A7)
private val TealDark    = Color(0xFF007B6E)
private val TextHigh    = Color(0xFFF0F6FC)
private val TextMed     = Color(0xFF8B949E)
private val BorderIdle  = Color(0xFF30363D)

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit = {}
) {
    var email      by remember { mutableStateOf("") }
    var password   by remember { mutableStateOf("") }
    var showPass   by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    // Ambient glow pulse animation
    val pulse = rememberInfiniteTransition(label = "ambient")
    val glowAlpha by pulse.animateFloat(
        initialValue  = 0.12f,
        targetValue   = 0.28f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgDeep, BgMid, BgBottom)))
    ) {

        // Ambient glow blob — top-left
        Box(
            modifier = Modifier
                .size(380.dp)
                .offset(x = (-110).dp, y = (-90).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(TealPrimary.copy(alpha = glowAlpha), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        // Ambient glow blob — bottom-right
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 90.dp, y = 90.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(TealDark.copy(alpha = glowAlpha * 0.8f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        // ── Main scrollable content ───────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(68.dp))

            // ── Logo ──────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(TealPrimary.copy(alpha = glowAlpha * 1.8f), Color.Transparent)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(TealPrimary, TealDark),
                                start  = Offset(0f, 0f),
                                end    = Offset(64f, 64f)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Filled.FavoriteBorder,
                        contentDescription = "SmartFit Logo",
                        tint               = Color.White,
                        modifier           = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text          = "SmartFit",
                color         = TextHigh,
                fontSize      = 30.sp,
                fontWeight    = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Text(
                text          = "Your fitness journey starts here",
                color         = TextMed,
                fontSize      = 13.sp,
                letterSpacing = 0.2.sp,
                modifier      = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ── Welcome heading ───────────────────────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text       = "Welcome Back 👋",
                    color      = TextHigh,
                    fontSize   = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text     = "Sign in to continue tracking your progress",
                    color    = TextMed,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Email Field ───────────────────────────────────────────────────
            FieldLabel("Email Address")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value         = email,
                onValueChange = { email = it },
                placeholder   = {
                    Text("you@example.com", color = TextMed.copy(alpha = 0.45f), fontSize = 14.sp)
                },
                leadingIcon = {
                    Icon(
                        imageVector        = Icons.Outlined.Email,
                        contentDescription = null,
                        tint               = if (email.isNotBlank()) TealPrimary else TextMed,
                        modifier           = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = email.isNotBlank(),
                        enter   = fadeIn() + scaleIn(),
                        exit    = fadeOut()
                    ) {
                        Icon(
                            imageVector        = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint               = TealPrimary,
                            modifier           = Modifier.size(20.dp)
                        )
                    }
                },
                singleLine      = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier        = Modifier.fillMaxWidth(),
                shape           = RoundedCornerShape(12.dp),
                colors          = loginFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Password Field ────────────────────────────────────────────────
            FieldLabel("Password")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value         = password,
                onValueChange = { password = it },
                placeholder   = {
                    Text("••••••••", color = TextMed.copy(alpha = 0.45f), fontSize = 14.sp)
                },
                leadingIcon = {
                    Icon(
                        imageVector        = Icons.Outlined.Lock,
                        contentDescription = null,
                        tint               = if (password.isNotBlank()) TealPrimary else TextMed,
                        modifier           = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector        = if (showPass) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (showPass) "Hide password" else "Show password",
                            tint               = TextMed,
                            modifier           = Modifier.size(20.dp)
                        )
                    }
                },
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine      = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier        = Modifier.fillMaxWidth(),
                shape           = RoundedCornerShape(12.dp),
                colors          = loginFieldColors()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Remember me + Forgot password ─────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null
                    ) { rememberMe = !rememberMe }
                ) {
                    Checkbox(
                        checked         = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors          = CheckboxDefaults.colors(
                            checkedColor   = TealPrimary,
                            checkmarkColor = Color.White,
                            uncheckedColor = BorderIdle
                        )
                    )
                    Text(text = "Remember me", color = TextMed, fontSize = 13.sp)
                }
                Text(
                    text       = "Forgot Password?",
                    color      = TealPrimary,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier   = Modifier.clickable { }
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            // ── Sign In Button ────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.horizontalGradient(listOf(TealPrimary, TealDark)))
                    .clickable { onLoginClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text          = "Sign In",
                        color         = Color.White,
                        fontSize      = 16.sp,
                        fontWeight    = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint               = Color.White,
                        modifier           = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // ── Divider ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderIdle)
                Text(text = "  or continue with  ", color = TextMed, fontSize = 12.sp)
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderIdle)
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ── Social Login Buttons ──────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                SocialButton("G",  Color(0xFFDB4437), Color(0x15DB4437))
                Spacer(modifier = Modifier.width(16.dp))
                SocialButton("f",  Color(0xFF4267B2), Color(0x154267B2))
                Spacer(modifier = Modifier.width(16.dp))
                SocialButton("in", Color(0xFF0A66C2), Color(0x150A66C2))
            }

            Spacer(modifier = Modifier.height(36.dp))

            // ── Register prompt ───────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Don't have an account?  ", color = TextMed, fontSize = 14.sp)
                Text(
                    text       = "Register Now",
                    color      = TealPrimary,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier   = Modifier.clickable { onRegisterClick() }
                )
            }

            Spacer(modifier = Modifier.height(44.dp))
        }
    }
}

// ── Helper composables ────────────────────────────────────────────────────────

@Composable
private fun FieldLabel(text: String) {
    Text(
        text       = text,
        color      = TextMed,
        fontSize   = 12.sp,
        fontWeight = FontWeight.Medium,
        modifier   = Modifier.fillMaxWidth()
    )
}

@Composable
private fun loginFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = TealPrimary,
    unfocusedBorderColor    = BorderIdle,
    focusedContainerColor   = SurfaceCard,
    unfocusedContainerColor = SurfaceCard,
    focusedTextColor        = TextHigh,
    unfocusedTextColor      = TextHigh,
    cursorColor             = TealPrimary
)

@Composable
private fun SocialButton(label: String, labelColor: Color, bgColor: Color) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.dp, labelColor.copy(alpha = 0.28f), RoundedCornerShape(14.dp))
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = label,
            color      = labelColor,
            fontSize   = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

