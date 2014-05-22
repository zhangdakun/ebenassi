package cn.eben.pcagent;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {
	public static MainActivity mInstace = null;
	
	Button btn;
	TextView text;
	ProgressBar progressBar1;
	
	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(null == intent || null == intent.getAction()) {
				return;
			}
			if("cn.eben.pcmsg".equalsIgnoreCase(intent.getAction())) {
				int lock = intent.getExtras().getInt("lock",1);

				AgentLog.debug("MainActivity","onReceive, "+lock);
				switch (lock) {
				case 0:
					text.setText(R.string.recovery);
					btn.setVisibility(View.INVISIBLE);
					progressBar1.setVisibility(View.VISIBLE);
					findViewById(R.id.textView2).setVisibility(View.VISIBLE);
					break;
				case 1:
					text.setText(R.string.recovery_ok);
					btn.setVisibility(View.VISIBLE);
					progressBar1.setVisibility(View.INVISIBLE);
					findViewById(R.id.textView2).setVisibility(View.INVISIBLE);
					finish();
					break;

				default:
					break;
				}
			} else if(Intent.ACTION_POWER_DISCONNECTED.equalsIgnoreCase(intent.getAction())) {
				finish();
			}
				
		}
		
	};
	public static String TAG = "agent";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		Display display = this.getWindowManager().getDefaultDisplay();
//		Point size = new Point();
//		display.getSize(size);
//		int width = size.x;
//		int height = size.y;
//		AgentLog.debug("agent", "windows x,"+width+", y , "+height);
//		
//		DisplayMetrics dm = new DisplayMetrics();  
//		dm = getResources().getDisplayMetrics();  
//		  
//		float density  = dm.density;        // ÆÁÄ»ÃÜ¶È£¨ÏñËØ±ÈÀý£º0.75/1.0/1.5/2.0£©  
//		int densityDPI = dm.densityDpi;     // ÆÁÄ»ÃÜ¶È£¨Ã¿´çÏñËØ£º120/160/240/320£©  
//		float xdpi = dm.xdpi;             
//		float ydpi = dm.ydpi;  
//		  
//		Log.e(TAG + "  DisplayMetrics", "xdpi=" + xdpi + "; ydpi=" + ydpi);  
//		Log.e(TAG + "  DisplayMetrics", "density=" + density + "; densityDPI=" + densityDPI);  
//		  
//		int screenWidth  = dm.widthPixels;      // ÆÁÄ»¿í£¨ÏñËØ£¬Èç£º480px£©  
//		int screenHeight = dm.heightPixels;     // ÆÁÄ»¸ß£¨ÏñËØ£¬Èç£º800px£©  
//		  
//		Log.e(TAG + "  DisplayMetrics(111)", "screenWidth=" + screenWidth + "; screenHeight=" + screenHeight); 
		setContentView(R.layout.activity_main);
//		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		AgentLog.debug("MainActivity","oncreate");
//		App.getInstance().addActivity(this);
		App.getInstance().setLockActivity(this);
		mInstace = this;
		
		Typeface tf = Typeface.createFromAsset(getAssets(),
	            "jinglei.TTF");
//	TextView tv = (TextView) findViewById(R.id.CustomFontText);
		
	
		btn = (Button) findViewById(R.id.button1);
		text = (TextView) findViewById(R.id.textView1);
		text.setTypeface(tf);
		
		progressBar1  = (ProgressBar) findViewById(R.id.progressBar1);
//		progressBar1.setm
		

		

		IntentFilter filter = new IntentFilter();
		filter.addAction("cn.eben.pcmsg");
		filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
		
		this.registerReceiver(receiver, filter);
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		
		Intent intent = getIntent();
		int lock = intent.getExtras().getInt("lock",1);
		AgentLog.debug("MainActivity","onStart, "+lock);
		String msgString = intent.getExtras().getString("showmsg");
		if(null != msgString && !msgString.isEmpty()) {
			text.setText(msgString);
		}
		switch (lock) {
		case 0:
//			text.setText(R.string.recovery);
			btn.setVisibility(View.INVISIBLE);
			progressBar1.setVisibility(View.VISIBLE);
			findViewById(R.id.textView2).setVisibility(View.VISIBLE);
			break;
		case 1:
//			text.setText(R.string.recovery_ok);
			btn.setVisibility(View.VISIBLE);
			progressBar1.setVisibility(View.INVISIBLE);
			findViewById(R.id.textView2).setVisibility(View.INVISIBLE);
			break;

		default:
			text.setText("");
			btn.setVisibility(View.INVISIBLE);
			progressBar1.setVisibility(View.INVISIBLE);
			findViewById(R.id.textView2).setVisibility(View.INVISIBLE);
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onclick(View v) {
//		startService(new Intent("cn.eben.pcagent.start"));
		finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
//		App.getInstance().setLockActivity(null);
		this.unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	public void exit() {
		AgentLog.debug("MainActivity","exit");
		finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		super.onBackPressed();
	}
	
	
}
