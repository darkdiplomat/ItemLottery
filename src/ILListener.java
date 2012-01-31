
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

public class ILListener extends PluginListener {
	ILActions ILA;
	ILData ILD;
	ILTimer ILT;
	
	public ILListener(ILData ILD){
		this.ILD = ILD;
		ILA = new ILActions(ILD, this);
		ILT = new ILTimer(ILA, ILD);
	}
	
	public boolean onCommand(Player player, String[] cmd){
		if(cmd[0].equals("/lotto")){
			if(cmd.length < 2){
				player.sendMessage("§e------§dItemLottery v.3.1§e------");
				player.sendMessage("§dCost = §b"+ILD.getCost());
				player.sendMessage("§d/lotto time §b- displays time till drawing");
				player.sendMessage("§d/lotto play §b- buys a ItemLottery ticket");
				if(player.isAdmin()){
					player.sendMessage("§d/lotto broadcast §b- broadcast time till drawing");
					player.sendMessage("§d/lotto draw §b- draws lotto immediately");
				}
				return true;
			}
			else{
				if(cmd[1].equals("time")){
					return ILA.displayTimeTill(player);
				}
				else if(cmd[1].equals("play")){
					return ILA.buyTicket(player);
				}
				else if(cmd[1].equals("broadcast")){
					if(player.isAdmin()){
						return ILA.broadcastTimeTill();
					}
					else{
						player.sendMessage("[§dILotto§f]§c You do not have permission to use that command!");
						return true;
					}
				}
				else if(cmd[1].equals("draw")){
					if(player.isAdmin()){
						return ILA.drawNOW();
					}
					else{
						player.sendMessage("[§dILotto§f]§c You do not have permission to use that command!");
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void onLogin(Player player){
		if(ILD.hadItemWaiting(player)){
			ILA.giveItemWaiting(player);
		}
	}
	
	public boolean onConsoleCommand(String[] cmd){
		if (cmd[0].equals("lotto")){
			if(cmd.length > 1){
				if (cmd[1].equals("time")){
					return ILA.ConsoledisplayTimeTill();
				}else if (cmd[1].equals("broadcast")){
					return ILA.broadcastTimeTill();
				}else if (cmd[1].equals("draw")){
					return ILA.drawNOW();
				}
			}
		}
		return false;
	}
}
