package com.simpragma.magicrecipe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class RecipeDetails extends Activity {

	private WebView webView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recipe_details);
		webView = (WebView) findViewById(R.id.detailsView);
		String url = getIntent().getStringExtra("url");
		startWebView(url);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void startWebView(String url) {

		webView.setWebViewClient(new WebViewClient() {
			ProgressDialog progressDialog;

			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			// Show loader on url load
			public void onLoadResource(WebView view, String url) {
				if (progressDialog == null) {

					progressDialog = new ProgressDialog(RecipeDetails.this);
					progressDialog.setMessage("Loading...");
					progressDialog.show();
				}
			}

			public void onPageFinished(WebView view, String url) {
				try {
					if (progressDialog.isShowing()) {
						progressDialog.dismiss();
						progressDialog = null;
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}

		});
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(url);

	}

	@Override
	public void onBackPressed() {
		if (webView.canGoBack()) {
			webView.goBack();
		} else {
			super.onBackPressed();
		}
	}
}
