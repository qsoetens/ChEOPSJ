package be.ac.ua.ansymo.cheopsj.model;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "be.ac.ua.ansymo.cheopsj.model"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	private static Timer timer;
	
	
	private class AutoSaveModelTask extends TimerTask {
		@Override
		public void run() {
			ModelManager.getInstance().saveModel();
		}
	}

	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		ModelManager.getInstance().loadModel();
		
		timer = new Timer();
		AutoSaveModelTask task = new AutoSaveModelTask();
		timer.schedule(task,60000,60000);
		//timer.schedule(task,300000,300000);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		ModelManager.getInstance().saveModel();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
