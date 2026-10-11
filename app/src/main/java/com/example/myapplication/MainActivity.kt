package com.example.myapplication

import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import android.webkit.CookieManager
import androidx.core.view.WindowCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import android.net.Uri

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView

    private fun hideReelsScript(): String {
        return """
        (function() {
            const selector = 'a[href="/reels/"]';

            function hideReels(root) {
                if (root.nodeType !== Node.ELEMENT_NODE) return;

                if (root.matches(selector)) {
                    root.style.setProperty('display', 'none', 'important');
                }

                root.querySelectorAll(selector).forEach(function(link) {
                    link.style.setProperty('display', 'none', 'important');
                });
            }

            // Hide any Reels links already on the page.
            hideReels(document.documentElement);

            // Avoid creating multiple observers on the same page.
            if (window.__hideReelsObserver) return;

            // Hide Reels links whenever Instagram adds or recreates them.
            window.__hideReelsObserver = new MutationObserver(function(mutations) {
                mutations.forEach(function(mutation) {
                    if (mutation.type === 'childList') {
                        mutation.addedNodes.forEach(hideReels);
                    } else if (mutation.type === 'attributes') {
                        hideReels(mutation.target);
                    }
                });
            });

            window.__hideReelsObserver.observe(document.documentElement, {
                childList: true,
                subtree: true,
                attributes: true,
                attributeFilter: ['href']
            });
        })();
    """.trimIndent()
    }

    private val homeUrl = "https://www.instagram.com/"
    private var clearHistoryAfterHome = false

    private fun isMainPage(url: String?): Boolean {
        if (url == null) return false

        val uri = Uri.parse(url)
        if (uri.host !in setOf("www.instagram.com", "instagram.com")) {
            return false
        }

        val segments = uri.path.orEmpty()
            .split("/")
            .filter { it.isNotBlank() }

        val path = segments.joinToString("/")

        // Search / Explore
        if (path == "explore" || path.startsWith("explore/")) {
            return true
        }

        // Main Messages inbox, not individual conversations
        if (path == "direct/inbox") {
            return true
        }

        // A profile's main page has one URL path segment.
        val reservedPaths = setOf(
            "accounts", "about", "api", "challenge",
            "direct", "directory", "emails", "explore",
            "graphql", "legal", "oauth", "p", "reels",
            "settings", "stories", "web"
        )

        return segments.size == 1 &&
                segments[0] !in reservedPaths
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        webView = WebView(this)

        val container = FrameLayout(this)
        container.addView(webView)

        setContentView(container)

        ViewCompat.setOnApplyWindowInsetsListener(container) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                0,
                systemBars.top,
                0,
                systemBars.bottom
            )

            insets
        }

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        webView.settings.userAgentString =
            "Mozilla/5.0 (Linux; Android 15) AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/154.0.0.0 Mobile Safari/537.36"

        webView.webViewClient = object : android.webkit.WebViewClient() {
            override fun onPageFinished(
                view: WebView,
                url: String
            ) {
                if (clearHistoryAfterHome &&
                    Uri.parse(url).path.orEmpty().trim('/') == ""
                ) {
                    view.clearHistory()
                    clearHistoryAfterHome = false
                }

                view.evaluateJavascript(hideReelsScript(), null)
            }
        }

        CookieManager.getInstance().setAcceptCookie(true)

        WebView.setWebContentsDebuggingEnabled(false)

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {
                    val currentUrl = webView.url

                    val isHome = currentUrl?.let {
                        val uri = Uri.parse(it)
                        uri.host in setOf("www.instagram.com", "instagram.com") &&
                                uri.path.orEmpty().trim('/') == ""
                    } ?: false

                    if (isHome) {
                        finish()
                    } else if (isMainPage(currentUrl)) {
                        val history = webView.copyBackForwardList()
                        val currentIndex = history.currentIndex

                        val homeIndex = (currentIndex - 1 downTo 0).firstOrNull { index ->
                            val uri = Uri.parse(history.getItemAtIndex(index).url)
                            uri.host in setOf("www.instagram.com", "instagram.com") &&
                                    uri.path.orEmpty().trim('/') == ""
                        }

                        if (homeIndex != null) {
                            webView.goBackOrForward(homeIndex - currentIndex)
                        } else {
                            clearHistoryAfterHome = false
                            webView.loadUrl(homeUrl)
                        }
                    } else if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        finish()
                    }
                }
            }
        )
        webView.loadUrl("https://www.instagram.com/")
    }
}