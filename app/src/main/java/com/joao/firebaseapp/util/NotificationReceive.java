package com.joao.firebaseapp.util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.joao.firebaseapp.NavigationActivity;

public class NotificationReceive extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("toast");
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        Intent intent1 = new Intent();
        intent1.setClassName("com.joao.firebaseapp","com.joao.firebaseapp.NavigationActivity");
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);

        //PendingIntent intentFra

        context.startActivity(intent1);
    }
}
