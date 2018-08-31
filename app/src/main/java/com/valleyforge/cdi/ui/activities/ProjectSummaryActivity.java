package com.valleyforge.cdi.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.valleyforge.cdi.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProjectSummaryActivity extends AppCompatActivity {


    @BindView(R.id.webview)
    WebView wvProjectSummary;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_summary);
        ButterKnife.bind(this);
        wvProjectSummary.getSettings().setJavaScriptEnabled(true);
        wvProjectSummary.setWebViewClient(new WebViewClient());
        wvProjectSummary.loadUrl("http://myhostapp.com/vff-staging-new/admin/projects/1/summary");

    }

}
