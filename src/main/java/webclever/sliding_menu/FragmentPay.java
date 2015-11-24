package webclever.sliding_menu;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.List;

import interfaces.OnBackPressedListener;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("SetJavaScriptEnabled")
public class FragmentPay extends Fragment implements OnBackPressedListener {

    private WebView webView;
    private String url_pay = "http://kasa.tms.webclever.in.ua/event/pay?order_id=";
    private Bundle bundle;
    private Integer paymentMethod;

    public FragmentPay() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_pay, container, false);

        if(getArguments() != null){
            bundle = getArguments();
            paymentMethod = bundle.getInt("payment_method");
        }

        webView = (WebView) rootView.findViewById(R.id.webView);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url_pay + getArguments().getString("order_id"));
        webView.setWebChromeClient(new WebChromeClient());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        Log.i("url_pay: ", url_pay + getArguments().getString("order_id"));

        return rootView;
    }

    @Override
    public void onBackPressed() {

        Fragment fragment;

        switch (paymentMethod){
            case 2:
                fragment = new FragmentUserDataKasa();
                break;
            case 4:
                fragment = new UserDataPost();
                break;
            case 6:
                fragment = new UserDataCourier();
                break;
            case 8:
                fragment = new UserDataETicket();
                break;
            default:
                fragment = null;
                break;
        }


        if (fragment != null) {
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragments_container, fragment).commit();
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            List <String> stringListPath = uri.getPathSegments();
            Log.i("url_path",stringListPath.toString());
           if (stringListPath.get(1).equals("paySuccess")) {
               Intent intent = new Intent(getActivity(), ActivitySuccessfulOrder.class);
               intent.putExtra("order_id", uri.getQueryParameter("order"));
               startActivity(intent);
                Log.i("url_success: " , url);
                return false;
            }else if (stringListPath.get(1).equals("payError")){
               Toast.makeText(getActivity(),"Error pay!",Toast.LENGTH_SHORT).show();
               return false;
           }
            view.loadUrl(url);
            Log.i("url_pay: ", url);
            return true;
        }
    }


}
