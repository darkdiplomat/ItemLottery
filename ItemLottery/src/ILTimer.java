import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

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
