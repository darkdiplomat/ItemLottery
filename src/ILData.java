import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
* ItemLottery v1.x
* Copyright (C) 2011 Visual Illusions Entertainment
* @author darkdiplomat <darkdiplomat@hotmail.com>
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

public class ILData {
	Logger log = Logger.getLogger("Minecraft");
	PluginLoader Loader = etc.getLoader();
	
	List<String> TicketHolders;
	List<Item> RandItemList;
	HashMap<String, Item> ItemWaiting;
	Item WinItem = new Item(264, 1, -1, 0);
	
	boolean iCo = false, dCo = false, Proceed = false, RandItem = false, UTE = true;
	String Tweet = "<P> won <A> of <I> from ItemLottery!";
	int iCost = 5, MTS = 1;
	double dCost = 5.00;
	
	long Reset = 0, Delay = 30;
	
	PropertiesFile Reseter;
	PropertiesFile PropProp;
	
	String Dire = "plugins/config/ItemLottery/";
	String Props = "plugins/config/ItemLottery/ItemLotteryProperties.ini";
	String RandItemsFile = "plugins/config/ItemLottery/ItemLotteryRandomItems.txt";
	String PlayersFILE = "plugins/config/ItemLottery/ItemLotteryPlayers.list";
	String WaitingFile = "plugins/config/ItemLottery/ItemLotteryItemsWaiting.list";
	String ReseterFile = "plugins/config/ItemLottery/ItemLotteryTimer.DONOTEDIT";
	
	public ILData(){
		if(Loader.getPlugin("dConomy") != null && Loader.getPlugin("dConomy").isEnabled()){
			dCo = true;
		}
		else if(Loader.getPlugin("iConomy") != null && Loader.getPlugin("iConomy").isEnabled() && !dCo){
			iCo = true;
		}
		if(!iCo && !dCo){
			log.severe("[ILotto] - No sutible Economy Plugin Found! Disabling...");
		}
		else{
			Proceed = true;
			TicketHolders = new ArrayList<String>();
			RandItemList = new ArrayList<Item>();
			ItemWaiting = new HashMap<String, Item>();
			LoadSettings();
		}
	}
	
	public void LoadSettings(){
		File PropFile = new File(Props);
		File DireDir = new File(Dire);
		File RandItemFile = new File(RandItemsFile);
		if(!DireDir.exists()){
			DireDir.mkdirs();
		}
		if(!RandItemFile.exists()){
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(RandItemFile));
				out.write("# Format ID,Amount,Damage (Amount and Damage optional [will default to Amount 1 Damage 0])#"); out.newLine();
				out.write("1,1,0"); out.newLine();
				out.write("35,2,2"); out.newLine();
				out.write("35,2,1"); out.newLine();
				out.write("35,2,0"); out.newLine();
				out.close();
			} catch (IOException e) {
				this.log.severe("[ILotto] - Unable to save players with tickets");
			}
		}
		if(!PropFile.exists()){
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(PropFile));
				out.write("##### ItemLottery Properties File #####"); out.newLine();
				out.write("#iConomy cost (if iConomy is used)#"); out.newLine();
				out.write("iCost="+iCost); out.newLine();
				out.write("#dConomy cost (if dConomy is used)#"); out.newLine();
				out.write("dCost="+dCost); out.newLine();
				out.write("#Use TwitterEvents (Requires TwitterEvents Plugin)#"); out.newLine();
				out.write("Use-TwitterEvents="+UTE); out.newLine();
				out.write("#Use Random Items#"); out.newLine();
				out.write("Use-RandomItems="+RandItem); out.newLine();
				out.write("#Win Item (if Random Items isn't used) Format ID,Amount,Damage (Amount and Damage optional [will default to Amount 1 Damage 0])#"); out.newLine();
				out.write("WinItem=264,1,0"); out.newLine();
				out.write("#Draw Delay (in minutes)#"); out.newLine();
				out.write("DrawDelay="+Delay); out.newLine();
				out.write("#Minimum Number of Tickets Sold#"); out.newLine();
				out.write("MinimumTicketsSold="+MTS);
			    out.close();
			} catch (IOException e) {
				log.severe("[ILotto] - Unable to create Properties File");
			}
			Delay *= 60000;
		}
		else{
			PropProp = new PropertiesFile(Props);
			Reseter = new PropertiesFile(ReseterFile);
			if(iCo){
				iCost = PropProp.getInt("iCost");
			}
			else{
				dCost = PropProp.getDouble("dCost");
			}
			RandItem = PropProp.getBoolean("Use-RandomItems");
			if(!RandItem){
				String ItemWin = PropProp.getString("WinItem");
				String[] ItemSplit = ItemWin.split(",");
				int ID = 264, Amount = 1, Damage = 0;
				if(ItemSplit != null && ItemSplit.length > 1){
					try{
						ID = Integer.parseInt(ItemSplit[0]);
					}catch(NumberFormatException NFE){
						log.severe("[ILotto] There was an issue with WinItem ID. Defaulting to Diamond!");
						ID = 264;
					}
					if(ID < 1){
						log.severe("[ILotto] There was an issue with WinItem 'ID'. Defaulting to Diamond!");
						ID = 264;
					}
					if(!Item.isValidItem(ID)){
						log.severe("[ILotto] There was an issue with WinItem 'ID'. Defaulting to Diamond!");
						ID = 264;
					}
					try{
						Amount = Integer.parseInt(ItemSplit[1]);
					}catch(NumberFormatException NFE){
						log.severe("[ILotto] There was an issue with WinItem 'Amount'. Defaulting to 1!");
						Amount = 1;
					}
					if(Amount < 1){
						log.severe("[ILotto] There was an issue with WinItem 'Amount'. Defaulting to 1!");
						Amount = 1;
					}
					if(ItemSplit.length > 2){
						try{
							Damage = Integer.parseInt(ItemSplit[2]);
						}catch(NumberFormatException NFE){
							log.severe("[ILotto] There was an issue with WinItem 'Damage'. Defaulting to 0!");
							Damage = 0;
						}
						if(Damage < 0){
							log.severe("[ILotto] There was an issue with WinItem 'Damage'. Defaulting to 0!");
							Damage = 0;
						}
					}
				}
				else{
					try{
						ID = Integer.parseInt(ItemWin);
					}catch(NumberFormatException NFE){
						log.severe("[ILotto] There was an issue with WinItem 'ID'. Defaulting to Diamond!");
						ID = 264;
					}
					if(ID < 0){
						log.severe("[ILotto] There was an issue with WinItem 'ID'. Defaulting to Diamond!");
						ID = 264;
					}
					if(!Item.isValidItem(ID)){
						log.severe("[ILotto] There was an issue with WinItem. Defaulting to Item:Diamond Amount: 1!");
					}
				}
				WinItem = new Item(ID, Amount, -1, Damage);
			}
			else{
				PopulateRandItem();
			}
			Delay = PropProp.getLong("DrawDelay")*60000;
			Reset = Reseter.getLong("TimerResetTo", 0);
			UTE = PropProp.getBoolean("Use-TwitterEvents");
			MTS = PropProp.getInt("MinimumTicketsSold");
			LoadTicketHolders();
			LoadItemsWaiting();
		}
	}
	
	public void PopulateRandItem(){
		try {
		    BufferedReader in = new BufferedReader(new FileReader(RandItemsFile));
		    String str;
		    int line = 1;
		    while ((str = in.readLine()) != null) {
		    	if(!str.contains("#")){
		    		String[] item = str.split(",");
		    		int ID = 264, Amount = 1, Damage = 0;
		    		if(item != null && item.length > 1){
		    			try{
		    				ID = Integer.parseInt(item[0]);
		    			}catch(NumberFormatException NFE){
		    				log.severe("[ILotto] There was an issue with RandItem 'ID' at line:"+line+".");
		    				continue;
		    			}
		    			if(ID < 1){
		    				log.severe("[ILotto] There was an issue with RandItem 'ID' at line:"+line+".");
		    				continue;
						}
						if(!Item.isValidItem(ID)){
							log.severe("[ILotto] There was an issue with RandItem 'ID' at line:"+line+".");
		    				continue;
						}
						try{
							Amount = Integer.parseInt(item[1]);
						}catch(NumberFormatException NFE){
							log.severe("[ILotto] There was an issue with RandItem 'Amount' at line:"+line+".");
		    				continue;
						}
						if(Amount < 1){
							log.severe("[ILotto] There was an issue with RandItem 'Amount' at line:"+line+".");
		    				continue;
						}
						if(item.length > 2){
							try{
								Damage = Integer.parseInt(item[2]);
							}catch(NumberFormatException NFE){
								log.severe("[ILotto] There was an issue with RandItem 'Damage' at line:"+line+".");
			    				continue;
							}
							if(Damage < 0){
								log.severe("[ILotto] There was an issue with RandItem 'Damage' at line:"+line+".");
			    				continue;
							}
						}
					}
					else{
						try{
							ID = Integer.parseInt(str);
						}catch(NumberFormatException NFE){
							log.severe("[ILotto] There was an issue with RandItem 'ID' at line:"+line+".");
		    				continue;
						}
						if(ID < 0){
							log.severe("[ILotto] There was an issue with RandItem 'ID' at line:"+line+".");
		    				continue;
						}
						if(!Item.isValidItem(ID)){
							log.severe("[ILotto] There was an issue with RandItem 'ID' at line:"+line+".");
		    				continue;
						}
					}
		    		Item randItem = new Item(ID, Amount, -1, Damage);
		    		RandItemList.add(randItem);
		    		line++;
		    	}	
		    }
		    in.close();
		}catch (IOException e){
			log.severe("[ILotto] - Unable to load RandItemFile - Using default Item");
			RandItem = false;
		}
		if(RandItemList.isEmpty()){
			log.severe("[ILotto] There were no Item in the RandItemFile. - Using default Item");
			RandItem = false;
		}
		
	}
	
	public String getCost(){
		String Cost = null;
		if(iCo){
			PropertiesFile iSet = new PropertiesFile("iConomy/settings.properties");
			if(iSet.containsKey("money-name")){
				Cost = "§e"+String.valueOf(iCost) + " §b" + iSet.getString("money-name");
			}
			else{
				Cost = String.valueOf(iCost);
			}
		}
		else{
			Cost = "§e"+priceForm(dCost) + " §b" + (String)Loader.callCustomHook("dCBalance", new Object[]{"MoneyName"});
		}
		return Cost;
	}
	
	public void clearTicketList(){
		TicketHolders.clear();
	}
	
	public boolean hasTicket(Player player){
		String name = player.getName();
		return TicketHolders.contains(name);
	}
	
	public void addItemWaiting(String name, Item item){
		ItemWaiting.put(name, item);
	}
	
	public boolean hadItemWaiting(Player player){
		String name = player.getName();
		return ItemWaiting.containsKey(name);
	}
	
	public Item getItemWaiting(String name){
		Item itemwaiting = ItemWaiting.get(name);
		ItemWaiting.remove(name);
		return itemwaiting;
	}
	
	public List<String> getTicketList(){
		return TicketHolders;
	}
	
	public List<Item> getRandItemList(){
		return RandItemList;
	}
	
	public void saveReset(long reset){
		Reset = reset;
		Reseter.setLong("TimerResetTo", reset);
	}
	
	public boolean hasMoney(Player player){
		if(iCo){
			int balance = (Integer)Loader.callCustomHook("iBalance", new Object[]{"balance", player.getName()});
			if(balance > iCost){
				return true;
			}
		}
		else if(dCo){
			double balance = (Double)Loader.callCustomHook("dCBalance", new Object[]{"Player-Balance", player.getName()});
			if(balance > dCost){
				return true;
			}
		}
		return false;
	}
	
	public void chargePlayer(Player player){
		String name = player.getName();
		if(iCo){
			Loader.callCustomHook("iBalance", new Object[]{"withdraw", name, iCost});
		}
		else if(dCo){
			Loader.callCustomHook("dCBalance", new Object[]{"Player-Charge", name, dCost});
		}
		TicketHolders.add(name);
	}
	
	public void SaveTicketHolders(){
		File file = new File(PlayersFILE);
		try {
			file.createNewFile();
		} catch (IOException e1) {
			//?
		}
		try {
		    BufferedWriter out = new BufferedWriter(new FileWriter(PlayersFILE));
		    for (int i = 0; i < TicketHolders.size(); i++){
		    	out.write(TicketHolders.get(i)); out.newLine();
		    }
		    out.close();
		} catch (IOException e) {
			log.severe("[ILotto] - Unable to save players with tickets");
		}
	}
	
	public void LoadTicketHolders(){
		try {
		    BufferedReader in = new BufferedReader(new FileReader(PlayersFILE));
		    String str;
		    while ((str = in.readLine()) != null) {
		    	if(!str.contains("#")){
		    		TicketHolders.add(str);
		    	}
		    }
		    in.close();
		} catch (IOException e) {
			log.severe("[ILotto] - Unable to load PlayersFILE");
		}
	}
	
	public void SaveItemsWaiting(){
		File file = new File(WaitingFile);
		try {
			file.createNewFile();
		} catch (IOException e1) {
			//?
		}
		try {
		    BufferedWriter out = new BufferedWriter(new FileWriter(WaitingFile));
		    for (String name : ItemWaiting.keySet()){
		    	Item item = ItemWaiting.get(name);
		    	out.write(name+","+item.getItemId()+","+item.getAmount()+","+item.getSlot()+","+item.getDamage());
		    }
		    out.close();
		} catch (IOException e) {
			log.severe("[ILotto] - Unable to save Item Waiting list");
		}
	}
	
	public void LoadItemsWaiting(){
		try {
		    BufferedReader in = new BufferedReader(new FileReader(WaitingFile));
		    String str;
		    while ((str = in.readLine()) != null) {
		    	if(!str.contains("#")){
		    		String[] it = str.split(",");
		    		ItemWaiting.put(it[0], new Item(Integer.valueOf(it[1]), Integer.valueOf(it[2]), Integer.valueOf(it[3]), Integer.valueOf(it[4])));
		    	}
		    }
		    in.close();
		} catch (IOException e) {
			log.severe("[ILotto] - Unable to load Item Waiting list");
		}
	}
	
	public String priceForm(double price){
		String newprice = String.valueOf(price);
		String[] form = newprice.split("\\.");
		if(form[1].length() == 1){
			newprice += "0";
		}
		else{
			newprice = form[0] + "." + form[1].substring(0, 2);
		}
		return newprice;
	}
}
