package webclever.sliding_menu;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPay extends Fragment {

    private WebView webView;
    private String url_pay = "http://kasa.tms.webclever.in.ua/event/pay?order_id=";

    public FragmentPay() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_pay, container, false);
        webView = (WebView) rootView.findViewById(R.id.webView);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url_pay + getArguments().getString("order_id"));
        //webView.loadUrl("https://www.google.com.ua");
        Log.i("url_pay: ", url_pay + getArguments().getString("order_id"));
        // Inflate the layout for this fragment
        return rootView;
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
           /* if (Uri.parse(url).getHost().equals("www.example.com")) {
                // This is my web site, so do not override; let my WebView load the page
                Log.i("url_pay: " , url);


                return false;
            }*/
            view.loadUrl(url);
            Log.i("url_pay: ", url);

            return true;
        }
    }


}
