package GameLib;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

import scenes.TitleScene;



public class GameManager extends Frame implements KeyListener, MouseListener, MouseMotionListener{

	
	public static int SCREEN_WIDTH = 480;
	public static int SCREEN_HEIGHT = 800;
	
	public static String fname = "wflight";
	
	static public int ANC_LEFT = 0;
	static public int ANC_CENTER = 1;
	static public int ANC_RIGHT = 2;
	
	////////////////////////////////////////////////////////////////
	////이 영역에 게임 전체에서 공유할 전역변수 설정/////
	////////////////////////////////////////////////////////////////
	
	
	//결과화면에서 보여주기 위한 공용값
	public int _getScore;
	public int _getRange;
	public int _getGold;
	
	// 게임 공용 변수 여기까지 
	
	public GameManager(){

		//현재 화면 정보 획득
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		
		//기본 윈도우 생성
		setIconImage(makeImage("./rsc/icon.png"));
		setBackground(new Color(0xffffff));
		setTitle("Game Main Frame");
		setLayout(null);
		setBounds((screen.width-SCREEN_WIDTH)/2,(screen.height-SCREEN_HEIGHT)/2,SCREEN_WIDTH,SCREEN_HEIGHT);//화면 정보를 참조하여, 윈도우를 화면 한 가운데 나타나도록 한다
		setResizable(false);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				Window wnd = e.getWindow();
				wnd.setVisible(false);
				wnd.dispose();
				System.exit(0);
			}
		});
		setVisible(true);

		addKeyListener(this);//키 리스너를 붙인다


		firstScene();//최초의 화면을 불러낸다
	}
	
	//게임매니저 내부 함수
	private void firstScene(){
		//최초에 보여줄 Scene-Canvas를 상속한 화면-을 호출한다
		
		TitleScene firstCanvas = new TitleScene(this);//최초 씬에 한해서 GameCanvas가 아니라, GameCanvas를 상속해 구현한 별도의 클래스임에 유의
		firstCanvas.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		add(firstCanvas);

	}
	
	public void sceneChange(GameCanvas newScene){
		
		//인수로 받은 신규 씬을 새로 바운딩한다
		newScene.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		add(newScene);

	}
	
	
	//게임 공용 기본함수/변수
	public GameCanvas nowCanvas;//현재 화면에 보여지고 있는 캔버스
	
	Random rnd = new Random();
	
	public int RAND(int startnum, int endnum){
		
		//startnum ~ endnum 사이의 정수 난수를 생성한다
		
		int a, b;
		if( startnum < endnum )
			b = endnum - startnum;
		else
			b = startnum - endnum;

		a = Math.abs(rnd.nextInt()%(b+1));

		return (a+startnum);
	}
	
	public Image makeImage(String furl){
		
		//rsc 폴더 내의 이미지를 불러들여 Image 객체로 받는다
		
		System.out.println(furl);

		Image img;

		Toolkit tk = Toolkit.getDefaultToolkit();
		img = tk.getImage(furl);
		try {
			MediaTracker mt  =  new MediaTracker(this);
			mt.addImage(img, 0);
			mt.waitForID(0);
		} catch (Exception ee) {
			ee.printStackTrace();
			return null;
		}	

		return img;
	}

	public void drawImageRect(Graphics gc, Image img, int x, int y, int sx,int sy, int wd,int ht, int anc){//sx,sy부터 wd,ht만큼 클리핑해서 그린다.
		if(x<0) {
			wd+=x;
			sx-=x;
			x=0;
		}
		if(y<0) {
			ht+=y;
			sy-=y;
			y=0;
		}
		if(wd<0||ht<0) return;
		x=x-(anc%3)*(wd/2);
		y=y-(anc/3)*(ht/2);
		gc.setClip(x, y, wd, ht);
		gc.drawImage(img, x-sx, y-sy, this);
		gc.setClip(0,0, SCREEN_WIDTH+10,SCREEN_HEIGHT+30);
	}
	public void drawnum(Graphics gc, Image img, int x, int y, int value, int digit, int anc){
		//숫자를 이미지로 변환해 보여준다
		
		int width = img.getWidth(this)/10;
		int height = img.getHeight(this);
		
		String valueStr = String.valueOf(value);
		if(valueStr.length()<digit)
			valueStr = "0000000000".substring(0, digit-valueStr.length()) + valueStr;
		
		int _xx = x;
		if(anc==ANC_CENTER)
			_xx = x-valueStr.length()*width/2;
		if(anc==ANC_RIGHT)
			_xx = x-valueStr.length()*width;
		for(int i=0;i<valueStr.length();i++)
			drawImageRect(gc, img, _xx+i*width, y, (valueStr.charAt(i)-'0')*width, 0, width,height, 0);
	}
     public float getRange(float x1, float y1, float x2, float y2){
		
		return Math.abs((y2-y1)*(y2-y1)+(x2-x1)*(x2-x1));
	}

	
	
   //입력수단 오버라이드
 	@Override
 	public void keyTyped(KeyEvent e) {
 		// TODO Auto-generated method stub
 		
 		nowCanvas.keyTyped(e);
 	}

 	@Override
 	public void keyPressed(KeyEvent e) {
 		// TODO Auto-generated method stub
 		
 		nowCanvas.keyPressed(e);
 	}

 	@Override
 	public void keyReleased(KeyEvent e) {
 		// TODO Auto-generated method stub
 		
 		nowCanvas.keyReleased(e);
 	}

 	@Override
 	public void mouseClicked(MouseEvent e) {
 		// TODO Auto-generated method stub
 		nowCanvas.mouseClicked(e);
 	}

 	@Override
 	public void mousePressed(MouseEvent e) {
 		// TODO Auto-generated method stub
 		nowCanvas.mousePressed(e);
 	}

 	@Override
 	public void mouseReleased(MouseEvent e) {
 		// TODO Auto-generated method stub
 		nowCanvas.mouseReleased(e);
 	}

 	@Override
 	public void mouseEntered(MouseEvent e) {
 		// TODO Auto-generated method stub
 		nowCanvas.mouseEntered(e);
 	}

 	@Override
 	public void mouseExited(MouseEvent e) {
 		// TODO Auto-generated method stub
 		nowCanvas.mouseExited(e);
 	}

 	@Override
 	public void mouseDragged(MouseEvent e) {
 		// TODO Auto-generated method stub
 		nowCanvas.mouseDragged(e);
 	}

 	@Override
 	public void mouseMoved(MouseEvent e) {
 		// TODO Auto-generated method stub
 		nowCanvas.mouseMoved(e);
 	}


 }
