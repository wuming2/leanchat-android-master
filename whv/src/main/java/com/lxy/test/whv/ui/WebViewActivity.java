package com.lxy.test.whv.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.lxy.test.whv.R;
import com.lxy.test.whv.ui.base_activity.BaseActivity;
import com.lxy.test.whv.util.LogUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WebViewActivity extends BaseActivity {

    @InjectView(R.id.webview)
    WebView myWebView;

    @InjectView(R.id.webviw_progressBar)
    ProgressBar bar;

    String url;

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.inject(this);

        Intent i = this.getIntent();
        url = i.getStringExtra("url");
        String title = i.getStringExtra("title");
        initActionBar(title);


        //TODO 要不要禁用JS?
        WebSettings settings = myWebView.getSettings();
        settings.setJavaScriptEnabled(true);

        myWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                LogUtils.d("onProgressChanged " + newProgress);
                if (newProgress == 100) {
                    bar.setVisibility(View.GONE);
                } else {
                    if (View.GONE == bar.getVisibility()) {
                        bar.setVisibility(View.VISIBLE);
                    }
                    bar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });


        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                LogUtils.d("onPageFinished URL = " + url);
            }
        });
        myWebView.loadUrl(url);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.webview_ativity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int menuId = item.getItemId();
        LogUtils.d("onMenuItemSelected" + featureId + menuId);
        if (menuId == R.id.openInBrowser) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            startActivity(intent);
//                Intent intent = new Intent(ChatRoomActivity.this, ConversationDetailActivity.class);
//                intent.putExtra(Constants.CONVERSATION_ID, conversation.getConversationId());
//                startActivityForResult(intent, QUIT_GROUP_REQUEST);
        }
        return super.onMenuItemSelected(featureId, item);
    }

}