package scenes;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;

import GameLib.GameCanvas;
import GameLib.GameManager;

public class TitleScene extends GameCanvas{
	
	int cnt = 0;

	public TitleScene(GameManager manager) {
		
		super(manager);

		manager.nowCanvas = (GameCanvas)this;
		

	}
	
	
	
	@Override
	public void dblpaint(Graphics gContext) {
		gContext.drawImage(bg, 0, 0, this);

		
		if(cnt%30<15)
			gContext.drawImage(pushany, (manager.SCREEN_WIDTH-pushany.getWidth(this)) / 2, 666, this);
		
	}

	@Override
	public void update() {
		// 모든 연산과 판단을 담당한다 
		// 메시지를 일정 시간마다 깜박이기 위해 카운터가 필요하다 
		cnt++;
		
	}

	@Override
	public void Destroy() {
		// TODO Auto-generated method stub
		super.Destroy();
		manager.remove(this);//GameManager의 firstScene에서 이 씬(클래스)을 add했으므로, remove하여 제거한다.
		releaseImage();
	}
	
	// 객체 변수 선언 
	Image bg; // 타이틀 바탕
	Image pushany; // Start  
	Image numpic; // 숫자그림 

	@Override
	public void initImage() {
		// initImage에서 Image 객체를 만들어주고,
		// releaseImage에서는 이 객체들을 null 처리 해 줍니다.
		bg = manager.makeImage("rsc/title/bground.png");
		pushany = manager.makeImage("rsc/title/start.png");
		numpic = manager.makeImage("rsc/numpic.png");
	}

	@Override
	public void releaseImage() {
		bg = null;
		pushany = null;
		numpic = null;
	}
	@Override
	public void keyReleased(KeyEvent e) {
		Destroy();
		manager.sceneChange((GameCanvas)new GameScene(manager));
	}

	

}
