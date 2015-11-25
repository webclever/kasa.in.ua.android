package webclever.sliding_menu;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
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
        if (getActivity().getActionBar() != null){
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        getActivity().getActionBar().setHomeButtonEnabled(false);}
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Увага!");
        alertDialog.setMessage("Ви бажаєте покинути сторінку оплати замовлення?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivitySuccessful();
                        dialog.cancel();
                    }
                });
        alertDialog.setNegativeButton("Відміна", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            List <String> stringListPath = uri.getPathSegments();
            Log.i("url_path",stringListPath.toString());
           if (stringListPath.get(1).equals("paySuccess")) {
               Intent intent = new Intent(getActivity(), ActivitySuccessfulOrder.class);
               intent.putExtra("payment_method",paymentMethod);
               intent.putExtra("message","");
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

    private void startActivitySuccessful(){
        Intent intent = new Intent(getActivity(),ActivitySuccessfulOrder.class);

        switch (paymentMethod){
            case 2:
                intent.putExtra("payment_method",1);
                break;
            case 4:
                intent.putExtra("payment_method",3);
                break;
            case 6:
                intent.putExtra("payment_method",5);
                break;
            case 7:
                intent.putExtra("payment_method",7);
                break;
            default:
                intent = null;
                break;
        }


        if (intent != null) {
            intent.putExtra("order_id",getArguments().getString("order_id"));
            startActivity(intent);
        }
    }


}
