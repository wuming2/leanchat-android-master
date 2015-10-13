package com.lxy.test.whv.ui.visahelper;

import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.lxy.test.whv.R;
import com.lxy.test.whv.ui.base_activity.BaseActivity;

public class VisaTrackingActivity extends BaseActivity {

    WebView myWebView;

    //TODO js inject  http://www.cnblogs.com/rayray/p/3680500.html

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visa_tracking);

        myWebView = (WebView) findViewById(R.id.webview_visa_tracking);

        WebSettings settings = myWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        myWebView.addJavascriptInterface(new JsInteration(), "control");
        myWebView.setWebChromeClient(new WebChromeClient() {
        });
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                testMethod(myWebView);
            }
        });
        myWebView.loadUrl("https://www.visaservices.org.in/DIAC-China-Tracking/TrackingParam.aspx?P=nTmGYIDe9zFAGIq3p43mMw==");
    }

    private void testMethod(WebView webView) {

        String vlnno="vlntest";

        String call = "javascript:document.getElementById(\"ctl00_CPH_txtVLNNo\").value=\""+vlnno+"\"";
        call = "javascript:sayHello()";
        //call = "javascript:toastMessage(\"" + "content" + "\")";
        //call = "javascript:sumToJava(1,2)";
        //webView.loadUrl(call);
    }

    public class JsInteration {

        @JavascriptInterface
        public void toastMessage(String message) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }

        @JavascriptInterface
        public void onSumResult(int result) {
            Log.i("webview", "onSumResult result=" + result);
        }
    }

}
