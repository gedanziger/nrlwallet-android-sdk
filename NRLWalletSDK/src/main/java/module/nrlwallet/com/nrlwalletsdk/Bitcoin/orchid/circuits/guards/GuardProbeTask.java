package module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.circuits.guards;

import java.util.logging.Level;
import java.util.logging.Logger;

import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.ConnectionCache;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.ConnectionIOException;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.GuardEntry;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.orchid.Router;

public class GuardProbeTask implements Runnable{
	private final static Logger logger = Logger.getLogger(GuardProbeTask.class.getName());
	private final ConnectionCache connectionCache;
	private final EntryGuards entryGuards;
	private final GuardEntry entry;
	
	public GuardProbeTask(ConnectionCache connectionCache, EntryGuards entryGuards, GuardEntry entry) {
		this.connectionCache = connectionCache;
		this.entryGuards = entryGuards;
		this.entry = entry;
	}
	
	public void run() {
		final Router router = entry.getRouterForEntry();
		if(router == null) {
			entryGuards.probeConnectionFailed(entry);
			return;
		}
		try {
			connectionCache.getConnectionTo(router, false);
			entryGuards.probeConnectionSucceeded(entry);
			return;
		} catch (ConnectionIOException e) {
			logger.fine("IO exception probing entry guard "+ router + " : "+ e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch(Exception e) {
			logger.log(Level.WARNING, "Unexpected exception probing entry guard: "+ e, e);
		}
		entryGuards.probeConnectionFailed(entry);
	}
}
