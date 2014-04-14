package cn.eben.pcagent;



import android.app.Application;

public class App extends Application {
	private static App instance;
	
    public App() {
        super();
        // This is the first instruction of the app, so no fear that instance is
        // null in any other part of the application
        instance = this;
    }
    
	public static App getInstance() {
		return instance;
	}
}
