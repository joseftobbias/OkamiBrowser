package com.jhaiian.clint.browser.delegates

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.jhaiian.clint.browser.MainActivity
import com.jhaiian.clint.ui.theme.LocalClintColors

@SuppressLint("SetJavaScriptEnabled")
internal fun MainActivity.createPopupWindow(isUserGesture: Boolean = true): WebView? {
    if (isFinishing || isDestroyed) return null
    if (!isUserGesture) return null
    val popup = WebView(this)
    val settings = popup.settings
    settings.javaScriptEnabled = true
    settings.domStorageEnabled = true
    settings.javaScriptCanOpenWindowsAutomatically = true
    settings.setSupportMultipleWindows(true)
    settings.mediaPlaybackRequiresUserGesture = false
    settings.userAgentString = buildUserAgent()
    applyUserAgentMetadata(popup)
    val cookieManager = CookieManager.getInstance()
    cookieManager.setAcceptCookie(true)
    cookieManager.setAcceptThirdPartyCookies(popup, true)
    popup.webViewClient = com.jhaiian.clint.browser.webview.ClintWebViewClient(
        prefs = prefs,
        isActive = { false },
        getDesktopHeaders = { buildRequestHeaders() },
        getTabId = { "popup_${System.identityHashCode(popup)}" },
        onWebsiteBlockedCallback = { closePopupWindow(popup) },
        onQuiverGuardDocumentBlocked = { closePopupWindow(popup) }
    )
    popup.webChromeClient = PopupWindowChromeClient { closePopupWindow(it) }
    uiState.popupWebViews = uiState.popupWebViews + popup
    return popup
}

internal fun MainActivity.closePopupWindow(webView: WebView) {
    uiState.popupWebViews = uiState.popupWebViews - webView
    webView.stopLoading()
    webView.webViewClient = WebViewClient()
    webView.webChromeClient = WebChromeClient()
    webView.destroy()
}

private class PopupWindowChromeClient(
    private val onClose: (WebView) -> Unit
) : WebChromeClient() {
    override fun onCloseWindow(window: WebView) {
        onClose(window)
    }
}

@Composable
internal fun PopupWindowOverlay(
    webView: WebView,
    onClose: () -> Unit
) {
    val colors = LocalClintColors.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.78f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.background),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(Modifier.fillMaxSize()) {
                AndroidView(
                    factory = { webView },
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(colors.cardBackground, RoundedCornerShape(50))
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = colors.onSurface)
                }
            }
        }
    }
}
