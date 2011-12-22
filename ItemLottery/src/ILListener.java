
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
				player.sendMessage("§e------§dItemLottery§e------");
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
