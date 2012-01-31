import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

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

public class ILTimer{
	Logger log = Logger.getLogger("Minecraft");
	ILActions ILA;
	ILData ILD;
	
	long Delay;
	long Reset;
	
	Timer RunTime;
	
	public ILTimer(ILActions ILA, ILData ILD){
		this.ILA = ILA;
		this.ILD = ILD;
	}
	
	public void startTimer(){
		Delay = ILD.Delay;
		Reset = ILD.Reset;
		
		if(Reset < 1){
			Reset = Delay;
		}
		else{
			Reset -= System.currentTimeMillis();
			if(Reset < 1){
				Reset = 60000;
			}
		}
		RunTime = new Timer();
		RunTime.schedule(new DrawLotto(), Reset);
	}
	
	public void RestartTimer(){
		RunTime.cancel();
		RunTime.purge();
		RunTime = new Timer();
		ILD.saveReset(Delay+System.currentTimeMillis());
		RunTime.schedule(new DrawLotto(), Delay);
	}
	
	public void cancelTimer(){
		RunTime.cancel();
		RunTime.purge();
	}
	
	
	public class DrawLotto extends TimerTask{
		public void run(){
			ILA.drawLotto();
			ILD.saveReset(Delay+System.currentTimeMillis());
			RunTime.schedule(new DrawLotto(), Delay);
		}
	}
}
