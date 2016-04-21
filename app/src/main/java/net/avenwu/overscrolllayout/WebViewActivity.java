package net.avenwu.overscrolllayout;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class WebViewActivity extends AppCompatActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.webview_layout)
                .setVariable(net.avenwu.overscrolllayout.BR.data, "http://avenwu.net");
    }
}
