package GameLib;

import java.awt.Rectangle;

public class RectCheck {

	static public boolean check(int x1, int y1, Rectangle rect1, int x2, int y2, Rectangle rect2){
		// static 함수로 두 개 사각형의 충돌을 감지할 수 있도록 단순화했다 
		// rect1 = 플레이어 , ret2 = 적 
		// 사각형 주체의 좌표도 함께 넣어주는 것은, 비교 시 실제 화면상의 절대좌표로 해야 하므로 그 좌표를 산출하기 위한 것입니다. 
		
		boolean ret = false;

		if(rect1==null)
			return false;
		
		if(rect2==null)
			return false;

		Rectangle _rect1 = new Rectangle(x1+rect1.x, y1+rect1.y, rect1.width, rect1.height);
		Rectangle _rect2 = new Rectangle(x2+rect2.x, y2+rect2.y, rect2.width, rect2.height);
		
		if(
		_rect1.x < (_rect2.x+_rect2.width) &&
		_rect2.x < (_rect1.x+_rect1.width) &&
		_rect1.y < (_rect2.y+_rect2.height) &&
		_rect2.y < (_rect1.y+_rect1.height)
				)
			ret = true;
		
		return ret;
	}

	
//	충돌 확인
//
// 플레이어를 rect1, 적을 rect2라고 하면 
//
//	- rect1의 오른쪽 끝이 rect2의 왼쪽 끝보다 왼쪽에 있으면 안됩니다.
//	- rect1의 왼쪽 끝이 rect2의 오른쪽 끝보다 오른쪽에 있으면 안됩니다.
//	- rect1의 천정이 rect2의 바닥보다 아래 있으면 안됩니다.
//	- rect1의 바닥이 rect2의 천정보다 위에 있으면 안됩니다.
//
//	이상 네 개의 조건문이 모두 true가 성립하면 rect1과 rect2는 충돌입니다.
}
