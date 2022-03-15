package subclasses;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

import GameLib.GameManager;
import GameLib.RectCheck;

public class Enemy {
	
	// 기본적인 내용은 Bullet 클래스와 유사하다 ( 비트맵, 위치, 충돌범위 )
	// 그리고 총알엔 없던 것으로 속도(게임이 진행될수록 점점 빨라지기 때문에) 내구력(hp)이 있습니다 
	// 총알 클래스의 내용을 복붙하고 Bullet을 Enemy로 바꾸면 간단. 
	// 단, 처리 방법은 달라지므로 process의 내용은 삭제합니다.
	public static final int NO_PROCESS = -1;
	public static final int MOVEOUT = -2;
	static public final int EVENT_CRASH = -3; 
	
	
	Image pic;//그림
	Rectangle rect;//충돌체크 대상이 되는 사각형 (총알이나 플레이어)
	int x, y;//적을 표시해 줄 좌표
	public int hp;//내구 //3-7.(2)
	int speed;//아래로 내려오는 속도 
	
	public Enemy(Image pic, Rectangle rect, int x, int y, int hp, int speed){
		this.pic = pic;
		this.rect = rect;
		this.x = x;
		this.y = y;
		this.hp = hp;
		this.speed = speed;
		// 순서대로 그리기에 사용할 적의 비트맵 이미지 
		// 적의 충돌 판정 영역 
		// x, y 좌표 
		// 내구력, 이동속도 입니다 
		// 비트맵 이미지, 내구력과 이동속도를 외부에서 받아들이는 건, 이들은 레벨에 따라 변하는 요소들이고 레벨의 변화는 GameScene에서 관리하도록 할 것이기 때문입니다. 
		
		
		System.out.println("new y = " + y);
	}
	
	public void draw(GameManager manager, Graphics gContext, ImageObserver _ob){
		
		gContext.drawImage(pic, x, y, _ob);
	}
	
	public int process(int myX, int myY, Rectangle myRect){
		
		y+=speed;
		
		if(myRect==null) return NO_PROCESS;
		
		if(RectCheck.check(x, y, rect, myX, myY, myRect))
			return EVENT_CRASH;
		
		if (y >= GameManager.SCREEN_HEIGHT + 60) 
			return MOVEOUT;
		// 이동 : 총알과 마찬가지로 화면 밖으로 나가면 MOVEOUT 값을 리턴해서 처리를 종료시켜줍니다 
		
		return NO_PROCESS;
	}
	
	//3-8.
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	//3-8. 
	
//	적이 파괴되어 사라질 때, 그 자리에 effect_boom 이펙트가 나타나도록 해 보겠습니다.
//	우선 이펙트를 출력해 줄 좌표는 적의 위치가 되겠습니다.
//	Enemy 클래스에서 좌표 변수를 public으로 선언해줘도 되겠습니다만,
//	여기서는 getX, getY라는, 변수값을 얻어오기 위한 함수를 따로 준비해서 썼습니다. 


}



