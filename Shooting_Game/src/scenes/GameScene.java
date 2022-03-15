package scenes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Vector;

import GameLib.GameCanvas;
import GameLib.GameManager;
import subclasses.Bullet;
import subclasses.Effect;
import subclasses.Enemy;
import subclasses.Item;

public class GameScene extends GameCanvas{
	
	// 상태값을 지정 
	public final static int UP_PRESSED = 0x001;
	public final static int DOWN_PRESSED = 0x002;
	public final static int LEFT_PRESSED = 0x004;
	public final static int RIGHT_PRESSED = 0x008;
	public final static int FIRE_PRESSED = 0x010;
	// 변수 하나로 여러 가지 상태값을 중복 저장하기 위해 비트연산을 사용하는 것입니다 
	
	public final static int STATUS_PLAYON = 0;//3-7(1)
	public final static int STATUS_FALL = 1;//3-7(1)
	
	public final static int MAXTIME_TWIN = 600; // 무한하게 트윈샷 상태일 수는 없으니 트윈샷 상태를 종료하기 위한 타이머 변수 설
	public final static int MAXTIME_MAGNET = 400;
	
	int cnt;
	
	int bg1Y, bg2Y; // 배경화면 위치 
	
	int _speed; // 배경 스크롤 속도 
	
	int myX, myY; // 플레이어 캐릭터 위치 
	int status; // 플레이어 상태 
	
	int playerWidth; // 플레이어 캐릭터 그림 1프레임의 가로 폭. 연산으로도 구할 수 있지만 자주 사용되기 때문에 따로 저장해둔다
	int myFrame; // 플레이어 캐릭터 애니메이션 프레임 
	
	int _level;// 내부적으로 계산되는 게임 난이도
	
	int keybuff;
	int keyTime; // 키가 눌리거나 떼었을 때 얼마나 시간이 지났는가 카운팅한다 
	// 키보드 이벤트 함수에서는 바로 좌표를 변동하지 않고, 키 버퍼 변수(keybuff)만 변경시켜줍니다.
	// 눌렀을 때 버퍼에 값을 넣고, 떼었을 때 버퍼에서 값을 제거합니다.
	// 주로 멀티키를 처리하기 위한 방법이지만, 연속키(키 리피트)를 처리하기 위한 방법도 됩니다. 
	
//	다음은 좌우 기울어짐 구현입니다.
//	중립에 해당하는 그림은 3번째 프레임입니다.
//
//	왼쪽 방향키를 누르고 있으면 3->2->1 순서로 기울어지는 그림으로 바뀌고
//	오른쪽 방향키를 누르고 있으면 3->4->5 순서로 기울어지는 그림으로 바뀌는데, 프레임마다 바뀌는게 아니라 약간의 시차를 두고 바뀝니다.
//
//	이 시차를 계산하기 위한 변수를 둡니다. 이름은 keyTime 으로 정했습니다. 
	
	Vector bullets;// 총알 관리. 총알의 갯수를 예상할 수 없기 때문에 가변적으로 관리한다.
	Vector enemies;// 적 캐릭터 관리.
	Vector effects;// 이펙트 관리 //3-8.
	Vector items;//아이템 관리 
	
	boolean isPause;
	
	//트윈샷 처리
	int twinTime;
	boolean isTwin;
		
	
	// 게임 정보 변수 선언 
	int _score;
	int _gold; // 이번 게임에서 획득한 골드 
	int _range; // 비행거리 
	int regen;//적 캐릭터 생성 카운터 
	

	public GameScene(GameManager manager) {
		super(manager);
		
		manager.nowCanvas = (GameCanvas) this;
	}



	@Override
	public void dblpaint(Graphics gContext) {
//		UI의 특징은, 모든 표시의 최상단에 존재한다는 것입니다.
//		비트맵 방식 게임화면 구성에서는 나중에 그린 것이 제일 위에 보이게 됩니다.
//		그때문에 dblpaint에서는 배경-적 캐릭터-총알-플레이어 캐릭터-이펙트-UI 순서로 그리기 함수를 호출합니다. 
		
		// 배경을 그리고 
		drawBG(gContext);
		
		// 적 캐릭터를 그리고 
		drawEnemy(gContext);
		
		// 플레이어를 그리고
		drawPlayer(gContext);
		// 리소스가 여러 장의 프레임을 하나로 모은거라 drawImageRect를 사용합니다 
		// 어떤 프레임을 그려줘야 하는가 지시하기 위해 myFrame이란 변수를 설정했습니다 
		
		// 아래는 이미지 보면서!!!  
		// 리소스의 lyne.png를 보면 왼쪽으로부터 순서대로 왼->오른으로 기울어짐을 5프레임으로 표현하고 있습니다.
		// 중립 상태가 세 번째 프레임이므로 myFrame의 초기 값은 2가 됩니다. (프레임 값의 범위는 0~4 사이)
		
		// 플레이어가 발사한 총알을 그리고 
		drawBullet(gContext);
		
		// 아이템을 그린다 
		drawItem(gContext); 
		
		//이펙트를 그린다
		drawEffect(gContext);//3-8.
		
		// UI를 그린다 
		drawUI(gContext); 
	}

		

	@Override
	public void update() {
		
		if (isPause)
			return;
		
		cnt++;
		keyTime++;
		
		// 좌표값에 스크롤 스피드를 더해준다
		bg1Y += _speed;
		bg2Y += _speed;
		
		if (bg1Y >= GameManager.SCREEN_HEIGHT)
			bg1Y = -GameManager.SCREEN_HEIGHT + bg1Y % GameManager.SCREEN_HEIGHT;//화면을 벗어난 배경그림 1의 위치를 되돌린다
		if (bg2Y >= GameManager.SCREEN_HEIGHT)
			bg2Y = -GameManager.SCREEN_HEIGHT + bg2Y % GameManager.SCREEN_HEIGHT;//화면을 벗어난 배경그림 2의 위치를 되돌린다
		// 그림의 위치가 화면 아래로 완전히 벗어나면 다시 위로 되돌리는 식이다 
		
		
		processBullet();
		processEnemy();
		processEffect(); // 3-8.
		processItem();
		
		
		keyProcerss(); // update 함수에서 이 키 버퍼 값을 참조해 캐릭터 좌표를 변경합니다. 
		myProcess(); // 중립은 update에서 처리해 줍니다. 
		
// 총알과 별개로,
// 플레이어 캐릭터의 그림을 기울이는 처리도 있었고, 이제 총알도 발사해줘야 하니 이 부분은 묶어서 따로 함수로 빼는게 깔끔.
// myProcess 가 해당 함수입니다. 
		
// 그림을 그려줄 부분, 움직임을 처리해 줄 부분에서는 벡터를 읽어 해당 객체 클래스의 서브함수를 부르도록 해 주면 코드가 간단해집니다. 
		
		
		
		}

		
	@Override
	public void Destroy() {
		super.Destroy();
		manager.remove(this);// GameManager의 firstScene에서 이 씬(클래스)을 add했으므로,
								// remove하여 제거한다.
		releaseImage();
		
	}
	
	
	// 이미지 보기 쉽게 initImage 위에 넣어줌 
	Image bg1, bg2; // 게임 배경. 무한스크롤을 위해 2개 사용한다
	Image player; // 이미지 변수 위치는 initImage 함수 앞쪽에 두는게 관리하기 편함 
	Image bullet; // 총알 
	Image betman[]; // 적 캐릭터 
	Image effect;// effect_boom//3-8.
	Image effect2;// effect_fire//3-8.
	Image ui1;// 상단 UI
	Image ui2;// 하단 UI
	Image numpic;// 그림 숫자
	Image itempic[];//아이템

	@Override
	public void initImage() {
		//사용할 이미지 불러오기//
		
		// 배경 구현에 필요한 코드들 넣기 
		bg1 = manager.makeImage("rsc/game/ground.png");
		bg2 = manager.makeImage("rsc/game/ground.png");
		// 게임 배경. 무한 스크롤을 위해 2개 사용한다. 
		
		player = manager.makeImage("rsc/game/superman.png"); // 비트맵 이미지 확보 
		
		bullet = manager.makeImage("rsc/game/mybullet01.png"); // 총알 
		
		betman = new Image[6]; // 배열 형식의 Image 객체는 반드시 initImage 안에서 초기화한다. 
		for (int i = 0; i < 6; i++)
			betman[i] = manager.makeImage("rsc/game/betman_0" + (i + 1)
					+ ".png"); // 적 캐릭터 
		
		effect = manager.makeImage("rsc/game/effect_boom.png");//3-8.
		effect2 = manager.makeImage("rsc/game/effect_fire.png");//3-8. 
		
		numpic = manager.makeImage("rsc/numpic.png");// 그림숫자
		ui1 = manager.makeImage("rsc/game/gameui_01.png");// 상단UI
		ui2 = manager.makeImage("rsc/game/gameui_02.png");// 하단UI
		
		itempic = new Image[2];
		itempic[0] = manager.makeImage("rsc/game/coin.png");//골드
		itempic[1] = manager.makeImage("rsc/game/twinshot.png");//트윈샷

	}

	@Override
	public void releaseImage() {
		// 이미지 해제하기 // 
		bg1 = null;
		bg2 = null; // 게임 배경. 무한스크롤을 위해 2개 사용한다
		
		player = null; // 비트맵 이미지 해제 
		
		bullet = null; // 총알 
		for (int i = 0; i < 4; i++)
			betman[i] = null;// 적 캐릭터 
		
		effect  = null;//3-8.
		effect2  = null;//3-8. 
		
		numpic = null;// 그림 숫자
		ui1 = null;// 상단UI
		ui2 = null;// 하단UI 
		
		for(int i=0;i<2;i++)
			itempic[i] = null;
		
	}
	@Override
	public void SceneStart() {
		// 초기값 
		// 별도의 씬 초기화를 위해 SceneStart를 오버라이드하고, 마지막에 super를 호출한다
		cnt = 0;
		// TitleScene.java의 전역 변수로 cnt라는 변수를 선언하고,
					// cnt 값을 초기화한 후, update에서 계속 증가 시켜줍니다 
		
		
		// 배경용 좌표 (계산하기 편하게)
		bg1Y = 0;
		bg2Y = -800;// 배경화면 위치
		
		// 게임 관련 정보 초기화
		_speed = 4;// 배경 스크롤 속도

		//플레이어 정보 초기화
		playerWidth = player.getWidth(this); // 애니메이션 프레임의 폭을 미리 지정
		myX = (GameManager.SCREEN_WIDTH - playerWidth) / 2;// 화면 중앙
		myY = 550;// 고정
		myFrame = 2;//정중앙 = 중립 상태 프레임부터
		
		isPause = false;
		
		// 프로그래밍에서 개념적으로 발사 = 객체 생성입니다 
		// 객체를 생성해 목록에 얹어줘야 하니 Vector가 필요합니다 
		// 벡터의 초기화는 SceneStart 함수에서 해주고 있습니다. GameScene 클래스의 변수 선언부에서 초기화하지 않도록 주의합니다.
		bullets = new Vector(); // 총알 관리, 총알의 갯수를 예상할 수 없기 때문에 가변적으로 관리합니다 
		// 총알의 생성자 인자로 비트맵 객체, 총알의 타격 판정 범위, 총알을 발생시키는 위치를 각각 전달합니다.
		enemies = new Vector();// 적 캐릭터 관리. 
		effects = new Vector();//이펙트 관리 //3-8.
		items = new Vector();// 아이템 관리 
		
		
		
		bullets.clear();
		enemies.clear();
		effects.clear();//3-8.
		items.clear();
		
		super.SceneStart();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		keyTime = 0;
		
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			keybuff |= LEFT_PRESSED;// 멀티키의 누르기 처리
			break;
		case KeyEvent.VK_RIGHT:
			keybuff |= RIGHT_PRESSED;
			break;
		default:
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// 키 반응을 만들어서 스크롤 속도를 조절할 수 있도록 했다 
		keyTime = 0;

		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			keybuff &= ~LEFT_PRESSED;// 멀티키의 떼기 처리
			break;
		case KeyEvent.VK_RIGHT:
			keybuff &= ~RIGHT_PRESSED;
			break;
		case KeyEvent.VK_1:
			isPause = !isPause;
			break;
		}
	}
	
	
	// 여기서부터 오리지널 함수
	
    void drawBG(Graphics gContext){
		
		gContext.drawImage(bg1, 0, bg1Y, this);
		gContext.drawImage(bg2, 0, bg2Y, this);
	}
    
    void drawPlayer(Graphics gContext) {
		
		manager.drawImageRect(gContext, player, myX, myY,
				0, 0, playerWidth, player.getHeight(this),
				manager.ANC_LEFT);
		
		// 트윈샷 시간이 얼마나 남았나, 타이머 바를 표시해 보겠습니다.
		// 가장 간단한 방법은, 속이 채워진 상자를, 남은 유지시간만큼 그리는 것입니다. 
		if(isTwin){ 
			gContext.setColor(new Color(1,1,1));//배경 상자의 색을 지정
			gContext.fillRect(myX, myY-10, playerWidth, 7);//최대치일때 기준으로 박스를 그린다
			gContext.setColor(new Color(255,0,0));//막대그래프의 색을 지정
			gContext.fillRect(myX+1, myY-9, (playerWidth-2) * twinTime/MAXTIME_TWIN, 5);//남은 시간을 기준으로 막대그래프를 그린다 
		}


	
	}
     void drawEnemy(Graphics gContext) {
		
		Enemy _buff;
		for (int i = 0; i < enemies.size(); i++) {
			_buff = (Enemy) enemies.elementAt(i);
			_buff.draw(manager, gContext, this);
		}
		
	}
	

	//총알 소거 방법 2
	// 삭제 대상이 된 총알을 화면에서 제거하기 위한 추가 함수 
//	public void deleteBullet(Bullet _deleteObj){
//
//		bullets.remove(_deleteObj);
//	}
     
     void drawBullet(Graphics gContext) {
 		// 그림은 그려주기만 하면 되고, 
 		Bullet _buff;
 		for (int i = 0; i < bullets.size(); i++) {
 			_buff = (Bullet) bullets.elementAt(i);
 			_buff.draw(gContext, this);
 		}
 		
 		// 움직임은 직선으로 위로 올라가므로 적절하게 y좌표를 감소시켜주면 됩니다. 

 	}
  // 3-8. 
 	void drawEffect(Graphics gContext) {
 		Effect _buff;
 		for (int i = 0; i < effects.size(); i++) {
 			_buff = (Effect) effects.elementAt(i);
 			_buff.draw(manager, gContext, this);
 		}
 	}
 	
 // 3-8. 
 	void drawUI(Graphics gContext) {
 		
 		// drawUI에서 UI용 리소스와, 
 		// GameManager에 준비된 그림숫자 표시 함수를 사용해서 그려주면 됩니다.
 		
 		gContext.drawImage(ui1, 6, 14, this);
 		gContext.drawImage(ui2, 6, 741, this);

 		manager.drawnum(gContext, numpic, 197, 39, _score, 8, manager.ANC_RIGHT);
 		manager.drawnum(gContext, numpic, 464, 39, _range / 10, 6,
 				manager.ANC_RIGHT);
 		manager.drawnum(gContext, numpic, 147, 766, _gold, 6, manager.ANC_RIGHT);
 		
 	}
 	
 	void drawItem(Graphics gContext) {
 		Item _buff;
 		for (int i = 0; i < items.size(); i++) {
 			_buff = (Item) items.elementAt(i);
 			_buff.draw(gContext, this);
 		}
 	}
	
	void keyProcerss() {
		// 일단 눌리거나 손을 떼면 keyTime을 0으로 리셋합니다 
		// 그리고 루프에서 1씩 증가시켜서, 적당한 수 (현재는 7)이 될 때마다 프레임을 변경해줍니다
		// 오른쪽으로 가고 있는 상태와 왼족으로 가고 있는 상태는 키 처리 부분에서. 
		
		// 중립은 update에서 처리해 줍니다 
		
		if(status == STATUS_FALL)//3-7.(1)
			return;
		
		switch (keybuff) {
		case LEFT_PRESSED:
		
			if (myX > -20)
				myX -= 10;


			break;
		case RIGHT_PRESSED:
			
			if (myX < GameManager.SCREEN_WIDTH + 20 - playerWidth)
				myX += 10;

			break;
		}
	}
	void processBullet() {
		// 메인 루프에서 총알이 화면 밖으로 나갔다면 이 총알을 이제 소멸시켜줘야 합니다. 
		// 소멸이란 것은, 벡터에서 객체를 제거하여 더 이상 처리하지 않는 상태입니다.
		// 여기서는 두 가지 방식을 소개합니다 ( 방법 1, 방법 2)

//		방법 1.
//		총알이 화면 위로 나가면 특정한 리턴값을 돌려주고, 메인 루프에서 리턴값을 참조하여 해당 객체를 Vector에서 제거하는 방식입니다.
		Bullet _buff;
		for (int i = bullets.size()-1; i >=0 ; i--) {
			_buff = (Bullet) bullets.elementAt(i);

			int idx = _buff.process(enemies);

			switch (idx) {
			case Bullet.NO_PROCESS:// 아무런 변화도 없다
				break;
			case Bullet.MOVEOUT:// 화면 밖으로 사라졌다
				bullets.remove(i);
				break;
			default://적에게 명중
				System.out.println(""+ idx + "번 적에게 명"); 
						
						
				Effect _effect2 = new Effect( effect2, _buff.getX() + bullet.getWidth(this)/2 + manager.RAND(-5, 5), _buff.getY() + player.getHeight(this)/2 + manager.RAND(-2, 2), 3, 2);
				effects.add(_effect2);
						
				// 3-7.(2) 
				Enemy _temp = (Enemy) enemies.elementAt(idx);
						
				int eHp = _temp.hp;//총알의 파워를 감소시키기 위한 버퍼값

				_temp.hp -= _buff.pow;//적의 hp를 깎는다
						
				_buff.pow -= eHp;//총알의 파워를 깎는다

				if(_buff.pow<=0){
					//총알의 파워가 바닥나 소멸 처리
					bullets.remove(_buff);
				}
						
				if (_temp.hp <= 0) {
					// 적이 HP가 바닥나 파괴 처리 
							
					Effect _effect = new Effect( effect, _temp.getX() + betman[0].getWidth(this)/2, _temp.getY() + betman[0].getHeight(this)/2, 6, 4);//3-8.
					effects.add(_effect);//3-8.
							
					enemies.remove(_temp);
							
					// 점수 가산 
					_score += (50 + _level * 10); // 적을 격파했을 경우에 점수가 얻어짐 
							
					// 아이템 등장 
					// 적 캐릭터가 파괴되는 시점에서 아이템이 생성됩니다. 
					// 어떤 아이템을 생성할 것인지, 우선 종류를 결정하고 아이템 객체를 생성, 아이템 관리용 Vector에 더해줍니다. 
					// (물론 아이템 벡터 Vector items; 를 준비하고, 벡터 초기화도 해 줍니다)
					int itemkind = 0;
					if(manager.RAND(1, 20)==5)
						itemkind = 0;//빅골드
					else if(manager.RAND(1, 50)==10)
						itemkind = 1;//트윈샷
					Item newitem = new Item(manager, itempic[itemkind], _temp.getX(), _temp.getY(), itemkind);
					items.add(newitem);
				}
					// 3-7.(2)
					break;
					}

					//방법 2
                    //총알이 화면 위로 나가면, 메인 루프의 특정 함수에게 자신을 삭제해달라고 요청하는 방식입니다.
                    //_buff.process_another(this);
				}
			}
	//3-8.
		void processEffect() {
			
			for(int i=effects.size()-1; i>=0; i--){
				
				Effect _buff = (Effect)effects.get(i);
				
				if ( !_buff.process()){
					effects.remove(_buff);
				}
			}
			
		}
	//3-8. 
	void myProcess(){
		
		//3-7.(1)
				if(status == STATUS_FALL){
					
					if(cnt%5==0){
						Effect _effect = new Effect( effect, myX + playerWidth/2 + manager.RAND(-30, 30), myY + player.getHeight(this)/2 + manager.RAND(-30, 30), 6, 4);
						effects.add(_effect);
					}
					
					myY+=5;//아래로 내려보낸다
					
					if(myY>GameManager.SCREEN_HEIGHT + 60){//화면 밖으로 사라졌으면
						
						Destroy();
						manager._getGold =_gold;
						manager._getRange = _range;
						manager._getScore = _score;
						manager.sceneChange((GameCanvas)new ResultScene(manager));//결과화면으로 전환
					}
					
					return;
				}
		//3-7.(1) 
		_range += (_speed / 2); // 비행거리는 속도에 연동하여 증가합니다 
		
//		if (keybuff == 0 && keyTime > 1 && keyTime % 7 == 0) {
//			if (myFrame < 2)
//				myFrame++;
//			else if (myFrame > 2)
//				myFrame--;
			// 키에서 손을 놓았으면 캐릭터를 다시 중립 상태로 되돌린다.
//		}
		
		// 이전엔 키 처리 해주는 부분에서 발사버튼(스페이스 키 라던지)이 눌려 있으면 발사하도록 했는데, 이번엔 자동 발사입니다. 
		// "일정 간격마다" 발사해야 하니, 역시 update 루프에서 처리하면 됩니다  
		
		if (cnt % 7 == 0) { // 20을 작게 해주면 더 빠르게, 크게 해주면 느리게 발사합니다. 

			int _x = myX + playerWidth / 2 - 12;
			int _y = myY - 17;

			if(isTwin){
				//트윈샷
				for(int i=0;i<2;i++){
					
					Bullet _bullet = new Bullet(bullet,
							new Rectangle(6, 1, 12, 33), _x-15+i*30, _y, 1);
					bullets.add(_bullet);
				}
				
			}else{
				// 싱글샷 (기존의 총알 발사 명령) 
				Bullet _bullet = new Bullet(bullet,
						new Rectangle(6, 1, 12, 33), _x, _y, 1);
				bullets.add(_bullet);
			}

		}
		//트윈샷 처리중
		if(isTwin){
			if(twinTime--==0)
				isTwin = false; // 이렇게 나란히 두 발씩 생성해주고, 트윈샷 유지 시간을 줄여서 0이 되면 해제합니다.
		}

		
	}
	//총알 소거 방법 2
//	public void deleteBullet(Bullet _deleteObj){
//
//		bullets.remove(_deleteObj);
//	}
	
	void processEnemy() {
		//등장 규칙은
		//- 일정한 시간 간격으로 등장한다.
		//- 랜덤하게 이 시간 간격의 1/2 간격으로 등장한다.
		//- 한 번 등장하면 일렬로 다섯이 나란히 등장한다.
		
		// 적을  생성합니다. 한꺼번에 일렬로 5대를 생성합니다. 
		
		if (cnt > 100
				&& (cnt % 90 == 0 || (manager.RAND(0, 10) == 5 && cnt % 45 == 0))) {
			 //기본적으로는 일정 시간마다 생성하지만, 게임에 변화를 주기 위해 10% 확률로 그 반 간격으로도 생성합니다.

			int localLevel = (regen%20) / 5;
//			한꺼번에 다섯대 씩 생성하므로 레벨이 실제 오르기 전까지 높은 레벨의 적이 등장하는 것은 0, 1, 2, 3, 4개가 됩니다.
//			20회 생성 시 레벨이 1 올라가므로, 4회 생성시 마다 하나씩 추가해 주면 됩니다.
			
			
			//다섯개 중 어느것에 레벨이 높은 걸 할당할 것인지, 생성 전에 정해놓으면 생성할 때 로직이 간단해집니다. 
			int neuroiLevel[] = { 0, 0, 0, 0, 0 }; 
			int setCnt = 0;
			while (setCnt < localLevel) {
				int idx = manager.RAND(0, 4);
				if (neuroiLevel[idx] == 0) {
					neuroiLevel[idx] = 1;
					setCnt++;
				}
			}
			
			for (int i = 0; i < 5; i++) {

				int imsiLevel = _level + neuroiLevel[i] + (1<=_level&&_level<=3&&manager.RAND(1,20)==5&&neuroiLevel[i]==1?1:0);

				Enemy _enemy;
				_enemy = new Enemy(betman[imsiLevel],new Rectangle(33, 6, 76, 81),
								i * 96 - 23, -80,5 * (imsiLevel), 6 + _speed);
				enemies.add(_enemy);
			}
			regen++;
			
			if(regen%20==0)
				levelup(); // 여기서는 적 캐릭터 생성이 20회 이루어지면 레벨이 1올라갑니다 
			
			return;
		}

		Enemy _buff;
		for (int i = enemies.size()-1; i >=0; i--) {
			_buff = (Enemy) enemies.elementAt(i);
			int ret = _buff.process(myX, myY, new Rectangle(12, 20, 55, 50));
			
			if( ret == Enemy.EVENT_CRASH && status == STATUS_PLAYON ){//3-7.(1)
				
				status = STATUS_FALL;//3-7.(1)
				isTwin = false;
				System.out.println("충돌 발생");
			}
			
			if (ret == Enemy.MOVEOUT){
				enemies.remove(_buff);// 화면 밖으로 나감
			}
		}

	}

	
	
void processItem(){
		
		Item _buff;
		for(int i = items.size()-1; i>=0; i--){
			
			_buff = (Item)items.elementAt(i);
			switch(_buff.process(myX, myY, new Rectangle(12, 20, 55, 50))){
			case Item.MOVEOUT:
				
				items.remove(_buff);
				break;
			case Item.TAKED:
				
				switch(_buff.getKind()){
				case 0://코인
					_gold += 1;
					break;
				case 1://트윈샷
					twinTime = MAXTIME_TWIN;
					isTwin = true;
					break;
				}
				items.remove(_buff);
				break;
			}
		}
	}
	


	void levelup() {
//		levelup() 함수 자체는 레벨업 조건이 될 때마다 호출됩니다.
//		이때, level은 5 까지만 올라가지만, speed는 계속해서 증가합니다. 
		
		if (_level < 5)
			_level++; // 레벨은 5까지 
		//  레벨 상한이 5로 되어 있는데, 레벨 5 상태에서 레벨 6 베트맨이  섞여 나오기 때문입니다 
		
		if (_speed < 20)
			_speed += 2; // 속도는 레벨에 연동하여 빨라집니다 
		
		// 레벨은 적의 등장 빈도에 비례하여 올라갑니다 
		
		
		
	}
	
	
}
