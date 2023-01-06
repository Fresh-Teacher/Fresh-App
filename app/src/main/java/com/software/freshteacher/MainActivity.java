package com.software.freshteacher;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    static String websiteURL = "https://fresh-teacher.github.io"; // sets web url
    private static WebView webview;
    public static FrameLayout layoutNestedWebView;
    private BroadcastReceiver myReceiver = null;
    public static RelativeLayout internetConnectionLayout;
    Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // progress dialog
        progressDialog = new Dialog(this);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        progressDialog.getWindow().setContentView(R.layout.custom_loading_layout);
        progressDialog.setCanceledOnTouchOutside(false);
        //progressDialog.show();

        layoutNestedWebView = findViewById(R.id.swipeRefresh);
        internetConnectionLayout = findViewById(R.id.layout_net_connection);
        webview = findViewById(R.id.webView);

        // init receiver
        myReceiver = new MyReceiver();

        new Handler()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initViews();
                        broadcastIntent();
                    }
                },2500);



    }


    private void initViews() {

        String status = getConnectivityStatusString(MainActivity.this);
        //  Dialog dialog = alert.create();

        if (status.isEmpty() || status.equals("No internet is available") || status.equals("No Internet Connection")) {
            // status="No Internet Connection";
            return;
        }
        //webview stuff
        webview.setVerticalScrollBarEnabled(true);
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.requestFocus();
        webview.getSettings().setDefaultTextEncodingName("utf-8");
        webview.getSettings().setAllowFileAccessFromFileURLs(true);
        webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setAllowContentAccess(true);
        webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webview.getSettings().setSupportZoom(false);
        webview.getSettings().setSupportMultipleWindows(true);
        webview.getSettings().setMediaPlaybackRequiresUserGesture(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        webview.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webview.getSettings().setSafeBrowsingEnabled(true);
        }
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webview.setSoundEffectsEnabled(true);
        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webview.getSettings().setUseWideViewPort(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
        }


        webview.setOverScrollMode(webview.OVER_SCROLL_NEVER);
        webview.setWebChromeClient(new WebChromeClient());
        webview.setWebViewClient(new WebViewClientDemo());
        webview.loadUrl(websiteURL);


    }



    public void tryAgainClick(View view) {
        if (isInternetAvailable(MainActivity.this)) {
            initViews();
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }


    private class WebViewClientDemo extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (progressDialog != null && !progressDialog.isShowing() && MainActivity.this != null) {
                progressDialog.show();
                findViewById(R.id.logo)
                        .setVisibility(View.GONE);

            }
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.startsWith("whatsapp:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            } else if (url.contains("instagram")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            } else if (url.contains("facebook")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            } else if (url.contains("twitter")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            }


            return false;
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (progressDialog != null && MainActivity.this != null) {
                progressDialog.dismiss();
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    }

    //set back button functionality
    @Override
    public void onBackPressed() { //if user presses the back button do this
        if (webview.isFocused() && webview.canGoBack()) { //check if in webview and the user can go back
            webview.goBack(); //go back in webview
        } else { //do this if the webview cannot go back any further

            View myView = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_closing_dialog, null);
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setView(myView);
            final AlertDialog dialog = alert.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);

            // click on yes button of closing dialog
            myView.findViewById(R.id.yesButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    MainActivity.this.finish();
                }
            });

            // click on N0 button of closing dialog
            myView.findViewById(R.id.noButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        }
    }


    public boolean isInternetAvailable(Context context) {
        NetworkInfo info = (NetworkInfo) ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info == null) {
            Log.d("TAG", "no internet connection");
            return false;
        } else {
            if (info.isConnected()) {
                Log.d("TAG", " internet connection available...");
                return true;
            } else {
                Log.d("TAG", " internet connection");
                return true;
            }

        }
    }


    public static String getConnectivityStatusString(Context context) {
        String status = null;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                status = "Wifi enabled";
                return status;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                status = "Mobile data enabled";
                return status;
            }
        } else {
            status = "No internet is available";
            return status;
        }
        return status;
    }


    public void broadcastIntent() {
        registerReceiver(myReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myReceiver != null) {
            unregisterReceiver(myReceiver);
        }
    }

    public static class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {

            String status = getConnectivityStatusString(context);

            //  Dialog dialog = alert.create();
            if (status.isEmpty() || status.equals("No internet is available") || status.equals("No Internet Connection")) {
                // status="No Internet Connection";

                layoutNestedWebView.setVisibility(View.GONE);
                internetConnectionLayout.setVisibility(View.VISIBLE);
            } else if (status.equals("Wifi enabled") || status.equals("Mobile data enabled")) {
                if (webview != null)
                    webview.loadUrl(websiteURL);

                layoutNestedWebView.setVisibility(View.VISIBLE);
                internetConnectionLayout.setVisibility(View.GONE);
            }

        }
    }




}