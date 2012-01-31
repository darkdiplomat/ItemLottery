import java.util.List;
import java.util.Random;

/**
* ItemLottery v3.x
* Copyright (C) 2011 Visual Illusions Entertainment
* @author darkdiplomat <darkdiplomat@visualillusionsent.net>
*
* This file is part of ItemLottery.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see http://www.gnu.org/licenses/gpl.html.
*/

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
			server.messageAll("[§dILotto§f]§c Not enough tickets sold. Starting new round!");
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
				else{
					ILD.addItemWaiting(name, ILD.WinItem);
					WinItem = ILD.WinItem;
				}
			}
			else{
				List<Item> RandItems = ILD.getRandItemList();
				int randI = new Random().nextInt(RandItems.size());
				WinItem = RandItems.get(randI);
				if(player != null && player.isConnected()){
					addItem(player.getInventory(), WinItem.getItemId(), WinItem.getDamage(), WinItem.getAmount());
				}
				else{
					ILD.addItemWaiting(name, WinItem);
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
				String Tweet = ILD.Tweet.replace("<P>", name);
				Tweet = Tweet.replace("<A>", String.valueOf(WinItem.getAmount()));
				Tweet = Tweet.replace("<I>", Itemname);
				etc.getLoader().callCustomHook("tweet", new Object[] { Tweet });
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
	
	private void addItem(Inventory inv, int ID, int Damage, int amount){
		for (int i = 0; i < inv.getContentsSize(); i++){
			if (amount > 0){
				Item item = inv.getItemFromSlot(i);
				if (item != null){
					int iam = item.getAmount();
					if (item.getItemId() == ID){
						if (item.getDamage() == Damage){
							if (amount > 64){
								if(iam < amount){
									item.setAmount(64);
									amount -= (64 - iam);
								}
								else{
									if(iam < amount){
										item.setAmount(iam+amount);
										amount -= (64 - iam);
									}
								}
							}
							else{
								if(iam < 64 && (iam+amount < 64)){
									item.setAmount(iam+amount);
									amount -= (64 - iam);
								}
								else{
									item.setAmount(64);
									amount -= (64-iam);
								}
							}
						}
					}
				}
				else{
					if (amount > 64){
						inv.setSlot(ID, 64, Damage, i);
						amount -= 64;
					}
					else{
						inv.setSlot(ID, amount, Damage, i);
						amount = 0;
					}
				}
			}
			else{
				break;
			}
		}
	}
}
