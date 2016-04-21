package net.avenwu.overscrolllayout;

import android.annotation.SuppressLint;
import android.databinding.BindingAdapter;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by aven on 4/21/16.
 */
public class BindAdapter {
    @SuppressLint("SetJavaScriptEnabled")
    @BindingAdapter({"bind:url"})
    public static void loadWebView(WebView webView, String url) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                view.loadUrl("file:///android_asset/404.html");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.loadUrl(url);
    }
}
