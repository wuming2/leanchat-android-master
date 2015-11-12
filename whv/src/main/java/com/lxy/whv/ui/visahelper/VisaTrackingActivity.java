package com.lxy.whv.ui.visahelper;

import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.lxy.whv.R;
import com.lxy.whv.ui.base_activity.BaseActivity;
import com.lxy.whv.util.LogUtils;

public class VisaTrackingActivity extends BaseActivity {

    WebView myWebView;
    private String vlnNo;
    private String dateOfBrith;

    //TODO js inject  http://www.cnblogs.com/rayray/p/3680500.html

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visa_tracking);

        myWebView = (WebView) findViewById(R.id.webview_visa_tracking);

        vlnNo = "AUX-CH-02-285570-X";
        dateOfBrith = "10/05/1990";

        WebSettings settings = myWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        myWebView.addJavascriptInterface(new JsInteration(), "control");
        myWebView.setWebChromeClient(new WebChromeClient() {
        });
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                LogUtils.d("onPageFinished");
                testMethod(myWebView);
            }
        });
        String url = "http://1.gifbox1.sinaapp.com/visaTracking.html";
        //https://www.visaservices.org.in/DIAC-China-Tracking/TrackingParam.aspx?P=nTmGYIDe9zFAGIq3p43mMw==
        myWebView.loadUrl(url);
    }

    private void testMethod(WebView webView) {

        String call = "javascript:track();";
//        call +="javascript:document.getElementById('ctl00_CPH_txtVLNNo').value=\"" + vlnNo + "\";";//
        //call = "javascript:toastMessage(\"" + "content" + "\")";
        //call = "javascript:sumToJava(1,2)";
        webView.loadUrl(call);

//        String js = "var newscript = document.createElement(\"script\");";
//        js += "newscript.src=\"https://1.gifbox1.sinaapp.com/whv_visa_tracking.js\";";
//        js += "document.body.appendChild(newscript);";
//        webView.loadUrl("javascript:" + js);
    }

    public class JsInteration {

        @JavascriptInterface
        public void toastMessage(String message) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }

        @JavascriptInterface
        public String getVlnNo() {
            return vlnNo;
        }

        @JavascriptInterface
        public String getDateOfBrith() {
            return dateOfBrith;
        }

        @JavascriptInterface
        public void onSumResult(int result) {
            Log.i("webview", "onSumResult result=" + result);
        }
    }

}
