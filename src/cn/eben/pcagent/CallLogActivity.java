package cn.eben.pcagent;

import cn.eben.pcagent.utils.CallLogUtil;
import cn.eben.pcagent.utils.MmsUtil;
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
//		new CallLogUtil().backupCalllog(this, "/mnt/sdcard/test/calllog.xml");
		AgentLog.debug("agent", "onClick");
		switch (v.getId()) {
		case R.id.button1:
//			new MmsUtil().backupMms(this);
			new MmsUtil().restoreMms(this, "/mnt/sdcard/mms1.vmg");
//			new MmsUtil().restoreMms(this, "/mnt/sdcard/mms.vmsg");
			
			break;

		default:
			break;
		}

	}
}
