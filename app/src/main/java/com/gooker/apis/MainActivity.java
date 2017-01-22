package com.gooker.apis;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private WebView wvData;
    private String setCookie = "http://192.168.31.109/qa/cookie";
    private String getCookie = "http://192.168.31.109/qa/cookie_get";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wvData = (WebView) findViewById(R.id.wv_data);
    }


    public void getCookie(View view) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                netRequest();
            }
        }).start();
    }

    public void cookieRequest(View view) {

    }

    public void netRequest() {
        String ul = setCookie;
        try {
            URL url = new URL(ul);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.e("test", "ok");
                final String cookie = connection.getHeaderField("Set-Cookie");
                wvData.post(new Runnable() {
                    @Override
                    public void run() {
                        loadUrl(cookie);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUrl(String cookie) {
        syncCookie(getCookie, cookie);
        wvData.loadUrl(getCookie);
    }

    /**
     * 将cookie同步到WebView
     *
     * @param url    WebView要加载的url
     * @param cookie 要同步的cookie
     * @return true 同步cookie成功，false同步cookie失败
     * @Author JPH
     */
    public boolean syncCookie(String url, String cookie) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(MainActivity.this);
        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie(url, cookie);//如果没有特殊需求，这里只需要将session id以"key=value"形式作为cookie即可
        String newCookie = cookieManager.getCookie(url);
        return TextUtils.isEmpty(newCookie) ? false : true;
    }

}
