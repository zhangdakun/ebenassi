package cn.eben.pcagent.receiver;

import cn.eben.pcagent.AgentLog;
import cn.eben.pcagent.App;
import cn.eben.pcagent.service.DaemonService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DaemonBroadcastReceiver extends BroadcastReceiver {
	public static final String TAG = "DaemonBroadcastReceiver";
	
//	adb shell am broadcast -a com.android.test 
//	--es test_string "this is test string" --ei test_int 100 --ez test_boolean true
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String s;
		if(null == intent) {
			AgentLog.error(TAG, "error ,recv a null intent");
			return;
		}
		int i = intent.getIntExtra("PCVersion", 0);

		s = intent.getAction();
		AgentLog.info(TAG, "onReceive ,actiong ," + s + ", version: " + i);
		if ("cn.eben.pcdaemon.NotifyServiceStart".equalsIgnoreCase(s)) {

			Intent intent1 = new Intent(context, DaemonService.class);
			intent1.putExtras(intent);
			context.startService(intent1);
		} else if ("cn.eben.pcdaemon.DaemonStatus".equalsIgnoreCase(s)) {

		} else if ("cn.eben.pcdaemon.NotifyServiceStop".equalsIgnoreCase(s)) {

			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					App.getInstance()
							.getApplicationContext()
							.stopService(
									new Intent(App.getInstance()
											.getApplicationContext(),
											DaemonService.class));

				}
			}).start();
		}

	}

}
