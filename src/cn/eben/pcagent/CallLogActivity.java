package cn.eben.pcagent;

import cn.eben.pcagent.utils.CallLogUtil;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class CallLogActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.call_log);
	}

	public void onClick(View v) {
		new CallLogUtil().backupCalllog(this, "/mnt/sdcard/test/calllog.xml");
	}
}
