package ro.pub.cs.systems.eim.lab05.startedserviceactivity.view;

import android.content.Context;
import android.content.IntentFilter;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import ro.pub.cs.systems.eim.lab05.startedserviceactivity.R;

public class StartedServiceActivity extends AppCompatActivity {

    private TextView messageTextView;
    private StartedServiceBroadcastReceiver startedServiceBroadcastReceiver;
    private IntentFilter startedServiceIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_started_service);

        messageTextView = (TextView)findViewById(R.id.message_text_view);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // TODO: exercise 8c - register the broadcast receiver with the corresponding intent filter
        // Note: RECEIVER_NOT_EXPORTED is required for Android 13+ (API 33+)
        if (startedServiceBroadcastReceiver != null && startedServiceIntentFilter != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(startedServiceBroadcastReceiver, startedServiceIntentFilter, Context.RECEIVER_NOT_EXPORTED);
            } else {
                registerReceiver(startedServiceBroadcastReceiver, startedServiceIntentFilter);
            }
        }
    }

    @Override
    protected void onPause() {
        // TODO: exercise 8c - unregister the broadcast receiver
        if (startedServiceBroadcastReceiver != null) {
            unregisterReceiver(startedServiceBroadcastReceiver);
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
