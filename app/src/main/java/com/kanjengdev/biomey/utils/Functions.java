package com.kanjengdev.biomey.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kanjengdev.biomey.MainActivity;
import com.kanjengdev.biomey.R;
import com.kanjengdev.biomey.ui.activity.RegisterActivity;
import com.kanjengdev.biomey.ui.activity.SplashActivity;

public class Functions {

    private final Context context;

    public Functions(Context context){
        this.context = context;
    }

    public void dialogGenuine(Boolean finish) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bottomSheetDialog.setContentView(R.layout.layout_bottom_sheet_genuine);
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        Button close = bottomSheetDialog.findViewById(R.id.close);

        assert close != null;
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();

        if(finish){
            bottomSheetDialog.setCancelable(false);
            bottomSheetDialog.setCanceledOnTouchOutside(false);
        }
    }

    public void dialogImpostor(Boolean finish) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bottomSheetDialog.setContentView(R.layout.layout_bottom_sheet_impostor);
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        Button close = bottomSheetDialog.findViewById(R.id.close);

        assert close != null;
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();

        if(finish){
            bottomSheetDialog.setCancelable(false);
            bottomSheetDialog.setCanceledOnTouchOutside(false);
        }
    }

    public void dialogLoading(Boolean finish) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bottomSheetDialog.setContentView(R.layout.layout_bottom_sheet_get_data);
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bottomSheetDialog.dismiss();
            }
        }, 2000L);

        bottomSheetDialog.show();

        if(finish){
            bottomSheetDialog.setCancelable(false);
            bottomSheetDialog.setCanceledOnTouchOutside(false);
        }
    }

}
