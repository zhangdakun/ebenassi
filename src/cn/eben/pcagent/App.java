package cn.eben.pcagent;



import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

public class App extends Application {
	private static App instance;
	private List<Activity> activityList = new ArrayList<Activity>(); 
    public App() {
        super();
        // This is the first instruction of the app, so no fear that instance is
        // null in any other part of the application
        instance = this;
    }
    
	public static App getInstance() {
		return instance;
	}
	
	private static MainActivity lockActivity;
	
    public MainActivity getLockActivity() {
    	AgentLog.debug("app", "getLockActivity");
		return App.lockActivity;
	}

	public void setLockActivity(MainActivity lockActivity) {
		AgentLog.debug("app", "setLockActivity, "+lockActivity);
		App.lockActivity = lockActivity;
	}

	public void addActivity(Activity activity)
    {
         try {
			activityList.add(activity);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    

    public void exit()
    {

        try {
			for(Activity activity:activityList)
			{
			   activity.finish();
		    }
			clearList();
		} catch (Exception e) {
			e.printStackTrace();
		}
   }
    
    public void clearList(){

	   	 try {
			if(null != activityList && activityList.size() != 0){
				 activityList.clear();
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}
   }
}
