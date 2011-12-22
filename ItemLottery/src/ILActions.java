import java.util.List;
import java.util.Random;


public class ILActions {
	Server server = etc.getServer();
	ILData ILD;
	ILListener ILL;
	boolean drawing;
	
	public ILActions(ILData ILD, ILListener ILL){
		this.ILD = ILD;
		this.ILL = ILL;
	}
	
	public boolean buyTicket(Player player){
		if(!ILD.hasMoney(player)){
			player.sendMessage("[§dILotto§f]§c You do not have enough money to buy a ticket...");
		}
		else if(ILD.hasTicket(player)){
			player.sendMessage("[§dILotto§f]§c You already have a ticket...");
		}
		else{
			ILD.chargePlayer(player);
			player.sendMessage("[§dILotto§f]§b You have purchased a ticket!");
		}
		return true;
	}
	
	public void drawLotto(){
		drawing = true;
		List<String> Tickets = ILD.getTicketList();
		if(Tickets.isEmpty()) {
			server.messageAll("[§dILotto§f]§c No one bought a ticket. Starting new round!");
			ILD.log.info("[ILotto] No one bought a ticket. Starting new round!");
			drawing = false;
		}
		else if(Tickets.size() < ILD.MTS){
			server.messageAll("[§dILotto§f]§c Not enought tickets sold. Starting new round!");
			ILD.log.info("[ILotto] Not enought tickets sold. Starting new round!");
			drawing = false;
		}
		else{
			int rand = new Random().nextInt(Tickets.size());
			String name = Tickets.get(rand);
			Player player = etc.getServer().getPlayer(name);
			Item WinItem = null;
			if (!ILD.RandItem){
				if(player != null && player.isConnected()){
					addItem(player.getInventory(), ILD.WinItem.getItemId(), ILD.WinItem.getDamage(), ILD.WinItem.getAmount());
					WinItem = ILD.WinItem;
				}
			}
			else{
				if(player.isConnected()){
					List<Item> RandItems = ILD.getRandItemList();
					int randI = new Random().nextInt(RandItems.size());
					WinItem = RandItems.get(randI);
					addItem(player.getInventory(), WinItem.getItemId(), WinItem.getDamage(), WinItem.getAmount());
				}
			}
			String Itemname = WinItem.itemType.name();
			if(Itemname == null){
				Itemname = String.valueOf(WinItem.getItemId());
			}
			server.messageAll("[§dILotto§f]§b Congratulations to §e" + name);
			server.messageAll("[§dILotto§f]§b for winning §e" + WinItem.getAmount() + " " + Itemname);
			server.messageAll("[§dILotto§f]§b There was in total §e" + Tickets.size() + " §btickets sold.");
			ILD.log.info("[ILotto] Winner was " + name + ". Item won was "+WinItem.getAmount()+" "+Itemname+" Starting new round!");
			if (ILD.UTE && (etc.getLoader().getPlugin("TwitterEvents") != null) && (etc.getLoader().getPlugin("TwitterEvents").isEnabled())){
				etc.getLoader().callCustomHook("tweet", new Object[] { ILD.Tweet.replace("<P>", name).replace("<A>", String.valueOf(WinItem.getAmount()).replace("<I>", Itemname))});
			}
			ILD.clearTicketList();
			drawing = false;
		}
	}
	
	public boolean drawNOW(){
		drawLotto();
		ILL.ILT.RestartTimer();
		return true;
	}
	
	public void giveItemWaiting(Player player){
		String name = player.getName();
		Item WinItem = ILD.getItemWaiting(name);
		addItem(player.getInventory(), WinItem.getItemId(), WinItem.getDamage(), WinItem.getAmount());
		player.sendMessage("[§dILotto§f]§b Congratz! You won: §e" + WinItem.getAmount() + " " + WinItem.itemType.name());
	}
	
	public boolean displayTimeTill(Player player){
		player.sendMessage("[§dILotto§f]§b Pulling winner in:");
		player.sendMessage("§e"+timeUntil(ILD.Reset));
		return true;
	}
	
	public boolean ConsoledisplayTimeTill(){
		ILD.log.info("[ILotto] Pulling winner in:");
		ILD.log.info(timeUntil(ILD.Reset));
		return true;
	}
	
	public boolean broadcastTimeTill(){
		server.messageAll("[§dILotto§f]§b Pulling winner in:");
		server.messageAll("§e"+timeUntil(ILD.Reset));
		ILD.log.info("[ILotto] Pulling winner in:");
		ILD.log.info(timeUntil(ILD.Reset));
		return true;
	}
	
	public String timeUntil(long time) {
		if(!drawing){
			double timeLeft = Double.parseDouble(Long.toString(((time - System.currentTimeMillis()) / 1000)));
			StringBuffer Time = new StringBuffer();
			if(timeLeft >= 60 * 60 * 24) {
				int days = (int) Math.floor(timeLeft / (60 * 60 * 24));
				timeLeft -= 60 * 60 * 24 * days;
				if(days == 1) {
					Time.append(days + " day, ");
				} 
				else{
					Time.append(days + " days, ");
				}
			}
			if(timeLeft >= 60 * 60) {
				int hours = (int) Math.floor(timeLeft / (60 * 60));
				timeLeft -= 60 * 60 * hours;
				if(hours == 1) {
					Time.append(hours + " hour, ");
				} else {
					Time.append(hours + " hours, ");
				}
			}
			if(timeLeft >= 60) {
				int minutes = (int) Math.floor(timeLeft / (60));
				timeLeft -= 60 * minutes;
				if(minutes == 1) {
					Time.append(minutes + " minute ");
				} else {
					Time.append(minutes + " minutes ");
				}
			}
			int secs = (int) timeLeft;
			if(Time != null) {
				Time.append("and ");
			}
			if(secs == 1) {
				Time.append(secs + " second.");
			}
			else if(secs > -1){
				Time.append(secs + " seconds.");
			}
			else{
				Time = new StringBuffer();
				Time.append("Derp (manual draw lotto)");
			}
			return Time.toString();
		}
		else{
			return "NOW!";
		}
	}
	
	public void addItem(Inventory inv, int ID, int Damage, int amount){
		for (int i = 0; i < inv.getContentsSize(); i++){
			if (amount > 0){
				Item item = inv.getItemFromSlot(i);
				if (item != null){
					if (item.getItemId() == ID){
						if (item.getDamage() == Damage){
							if (amount > 64){
								if(item.getAmount() < 64){
									inv.removeItem(i);
									inv.setSlot(ID, 64, Damage, i);
									inv.update();
									amount -= item.getAmount();
								}
								else{
									continue;
								}
							}
							else{
								if(item.getAmount() < 64){
									inv.removeItem(i);
									inv.setSlot(ID, amount+item.getAmount(), Damage, i);
									inv.update();
									amount -= item.getAmount();
								}
								else{
									continue;
								}
							}
						}
					}
				}
				else{
					if (amount > 64){
						inv.setSlot(ID, 64, Damage, i);
						inv.update();
						amount -= 64;
					}
					else{
						inv.setSlot(ID, amount, Damage, i);
						inv.update();
						break;
					}
				}
			}
			else{
				break;
			}
		}
		return;
	}
}
