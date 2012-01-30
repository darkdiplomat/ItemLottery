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

public class ItemLottery extends Plugin {
	Logger log = Logger.getLogger("Minecraft");
	
	ILListener ILL;
	ILData ILD;
	
	String name = "ItemLottery";
	String version = "3.0.1";
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
