import java.util.logging.Logger;


public class ItemLottery extends Plugin {
	Logger log = Logger.getLogger("Minecraft");
	
	ILListener ILL;
	ILData ILD;
	
	String name = "ItemLottery";
	String version = "3.0";
	String author = "darkdiplomat";
	
	
	public void disable(){
		if(ILD.Proceed){
			ILL.ILT.cancelTimer();
			ILD.SaveTicketHolders();
			ILD.SaveItemsWaiting();
		}
		etc.getInstance().removeCommand("/lotto");
		log.info(name + " version " + version + " disabled!");
	}
	
	public void enable() {
		etc.getInstance().addCommand("/lotto", " - display info for ItemLottery");
		log.info(name + " version " + version + " by " + author + " enabled!");
	}
	
	public void initialize() {
		ILD = new ILData();
		if(ILD.Proceed){
			ILL = new ILListener(ILD);
			etc.getLoader().addListener(PluginLoader.Hook.LOGIN, ILL, this, PluginListener.Priority.MEDIUM);
			etc.getLoader().addListener(PluginLoader.Hook.COMMAND, ILL, this, PluginListener.Priority.MEDIUM);
			etc.getLoader().addListener(PluginLoader.Hook.SERVERCOMMAND, ILL, this, PluginListener.Priority.MEDIUM);
			ILL.ILT.startTimer();
			log.info(name + " version " + version + " initialized");
		}
		else{
			etc.getLoader().disablePlugin(name);
		}
	}
}
