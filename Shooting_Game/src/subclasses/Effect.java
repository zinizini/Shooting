package subclasses;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;

import GameLib.GameManager;

public class Effect {
	// 기본 준비 // 
	// 기본정보인 
	Image pic; // 그림 
	int x, y; // 표시 좌표 
	
	// 필요한 정보 
	int totalframe; // 총 프레임 수 
	int step; //다음 프레임으로 넘어가기 위해 필요한 클럭 
	int frame; //현재 보여주는 프레임 
	int nowstep; //현재 클럭 // 시간(클럭)은, GameScene의 공통된 클럭인 cnt를 써도 되지만, 이펙트 객체에게 별도로 할당하겠습니다. 
	
	
	public Effect (Image pic, int x, int y, int step, int totalframe) {
		// 그림, 위치, 시간간격, 총 프레임 수는 생성자의 인자로 받아들이고, 
		// 현재 보여주는 프레임과 현재 클럭의 초기값은 0입니다. 
		
		this.pic = pic;
		this.x = x;
		this.y = y;
		this.step = step;
		this.totalframe = totalframe;
		
		nowstep = 0;
		frame = 0;
		
		
	}
	public void draw(GameManager manager, Graphics gContext, ImageObserver _ob) {
		
		// draw 좀 복잡한데 .. 
//		이미지의 가로폭/프레임수 를 해 주면 프레임 단위 가로폭이 나옵니다.
//		이것을 갖고 현재 프레임만을 보여주도록 하고 있는 것입니다.
//		GameManager에 구현해놓은 부분그리기 함수를 쓰기 때문에, 그려줄 때 GameManager 객체를 인수로 추가 전달해야 합니다. 
		manager.drawImageRect(gContext, pic, x, y, (pic.getWidth(_ob)/totalframe)*frame, 0, pic.getWidth(_ob)/totalframe, pic.getHeight(_ob), GameManager.ANC_CENTER);
	}
		
	
	public boolean process() {
//		process에서는 현재 클럭(nowstep)을 1씩 증가시켜, 지정된 시간간격(step)이 되면 
//		nowstep를 리셋하고 현재 프레임(frame)을 1 증가시켜줍니다.
//		이때 frame 값이 총 프레임 값에 도달하면 이펙트 애니메이션이 종료됩니다.
		nowstep++;
		if(nowstep == step) {
			frame++;
			nowstep = 0;
			if(frame == totalframe)
				return false;
		}
		return true;
		
	}

}
