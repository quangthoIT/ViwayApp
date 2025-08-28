package com.example.test.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.test.R;

public class NotifyDialogHelper {

    public static void showNotifyDialog(Context context, String contentText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_notify_error, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        Button btnClose = view.findViewById(R.id.error_btnClose);
        TextView title = view.findViewById(R.id.error_title);
        TextView content = view.findViewById(R.id.error_content);

        content.setText(contentText);

        btnClose.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

}
