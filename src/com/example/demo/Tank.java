package com.example.demo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;

/**
 * Tank 类 将坦克单独包装成类
 */
public class Tank {
	public static final int XSPEED = 5;
	public static final int YSPEED = 5;

	public static final int WIDTH = 30;
	public static final int HEIGHT = 30;
	
	private boolean live = true;//标识是否存活
	
	private BloodBar bb = new BloodBar();//图形化显示tank生命值
	
	private int life = 100;//设置tank生命值

	TankClient tc;

	private boolean good;// 标识敌我tank
	
	private static Random r = new Random();
	
	private int x, y;
	private int oldX, oldY;//记录tank上一次位置

	private boolean bL = false, bU = false, bR = false, bD = false;// 记录按键状态的boolean

	enum Direction {
		L, LU, U, RU, R, RD, D, LD, STOP
	};// 方向枚举，坦克向8个方向行走

	private Direction dir = Direction.STOP;
	private Direction ptDir = Direction.D;// 炮筒位置
	
	private int step = r.nextInt(12) + 3;

	public Tank(int x, int y, boolean good) {
		this.x = x;
		this.y = y;
		this.oldX = x;
		this.oldY = y;
		this.good = good;
	}

	public Tank(int x, int y, boolean good,Direction dir, TankClient tc) {
		this(x, y, good);
		this.dir = dir;
		this.tc = tc;
	}

	/**
	 * 添加draw方法，使Tank类独立控制自己的画法
	 */
	public void draw(Graphics g) {
		if(!live){
			if(!good){
				tc.tanks.remove(this);
			}
			return;
		}
		Color c = g.getColor();
		if (good)
			g.setColor(Color.RED);
		else
			g.setColor(Color.BLUE);
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.setColor(c);
		// 画出炮筒
		switch (ptDir) {
		case L:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x, y + Tank.HEIGHT / 2);
			break;
		case LU:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x, y);
			break;
		case U:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x + Tank.WIDTH / 2, y);
			break;
		case RU:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x + Tank.WIDTH, y);
			break;
		case R:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x + Tank.WIDTH, y + Tank.HEIGHT / 2);
			break;
		case RD:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x + Tank.WIDTH, y + Tank.HEIGHT);
			break;
		case D:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x + Tank.WIDTH / 2, y + Tank.HEIGHT);
			break;
		case LD:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x, y + Tank.HEIGHT);
			break;
		}

		// 根据按键方向进行下一步的移动
		move();
	}

	void move() {
		switch (dir) {
		case L:
			x -= XSPEED;
			break;
		case LU:
			x -= XSPEED;
			y -= YSPEED;
			break;
		case U:
			y -= YSPEED;
			break;
		case RU:
			x += XSPEED;
			y -= YSPEED;
			break;
		case R:
			x += XSPEED;
			break;
		case RD:
			x += XSPEED;
			y += YSPEED;
			break;
		case D:
			y += YSPEED;
			break;
		case LD:
			x -= XSPEED;
			y += YSPEED;
			break;
		case STOP:
			break;
		}
		if (this.dir != Direction.STOP) {
			this.ptDir = this.dir;
		}
		// 判断tank出边界
		if (x < 0)
			x = 0;
		if (y < 30)
			y = 30;
		if (x + Tank.WIDTH > TankClient.GAME_WIDTH)
			x = TankClient.GAME_WIDTH - Tank.WIDTH;
		if (y + Tank.HEIGHT > TankClient.GAME_HEIGHT)
			y = TankClient.GAME_HEIGHT - Tank.HEIGHT;
		
		if(!good) {//地方tank随机移动
			Direction[] dirs = Direction.values();
			if(step == 0) {
				step = r.nextInt(12) + 3;
				int rn = r.nextInt(dirs.length);
				dir = dirs[rn];
			}			
			step --;
			//随机开火
			if(r.nextInt(40) > 38) this.fire();
		}
	}

	//tank碰到墙，回到移动前的位置
	private void stay() {
		x = oldX;
		y = oldY;
	}
	
	/**
	 * 添加Tank类处理按键的方法
	 */
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_F2 :
			if(!this.live) {
				this.live = true;
				this.life = 100;
			}
			break;
		case KeyEvent.VK_LEFT:
			bL = true;
			break;
		case KeyEvent.VK_UP:
			bU = true;
			break;
		case KeyEvent.VK_RIGHT:
			bR = true;
			break;
		case KeyEvent.VK_DOWN:
			bD = true;
			break;
		}
		locateDirection();
	}

	void locateDirection() {
		if (bL && !bU && !bR && !bD)
			dir = Direction.L;
		else if (bL && bU && !bR && !bD)
			dir = Direction.LU;
		else if (!bL && bU && !bR && !bD)
			dir = Direction.U;
		else if (!bL && bU && bR && !bD)
			dir = Direction.RU;
		else if (!bL && !bU && bR && !bD)
			dir = Direction.R;
		else if (!bL && !bU && bR && bD)
			dir = Direction.RD;
		else if (!bL && !bU && !bR && bD)
			dir = Direction.D;
		else if (bL && !bU && !bR && bD)
			dir = Direction.LD;
		else if (!bL && !bU && !bR && !bD)
			dir = Direction.STOP;
	}

	/**
	 * 处理键抬起的消息
	 */
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_CONTROL:// 按住Ctrl，开火
			fire();
			break;
		case KeyEvent.VK_LEFT:
			bL = false;
			break;
		case KeyEvent.VK_UP:
			bU = false;
			break;
		case KeyEvent.VK_RIGHT:
			bR = false;
			break;
		case KeyEvent.VK_DOWN:
			bD = false;
			break;
		case KeyEvent.VK_A ://按键A发射超级炮弹
			superFire();
			break;
		}
		
		locateDirection();
	}

	/**
	 * Tank打子弹
	 */
	public Missile fire() {
		if(!live) return null;
		int x = this.x + Tank.WIDTH / 2 - Missile.WIDTH / 2;
		int y = this.y + Tank.HEIGHT / 2 - Missile.HEIGHT / 2;
		Missile m = new Missile(x, y, good, ptDir, this.tc);
		tc.missiles.add(m);
		return m;
	}
	
	public Missile fire(Direction dir) {
		if(!live) return null;
		int x = this.x + Tank.WIDTH/2 - Missile.WIDTH/2;
		int y = this.y + Tank.HEIGHT/2 - Missile.HEIGHT/2;
		Missile m = new Missile(x, y, good, dir, this.tc);
		tc.missiles.add(m);
		return m;
	}
	
	//设置超级炮弹，打击8个方向
	private void superFire(){
		Direction[] dirs = Direction.values();
		for(int i=0;i<8;i++){
			fire(dirs[i]);
		}
	}

	// tank矩形范围
	public Rectangle getRect() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}
	
	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}
	
	public boolean isGood() {
		return good;
	}
	
	/**
	 * 撞墙
	 * @param w 被撞的墙
	 * @return 撞上了返回true，否则false
	 */
	public boolean collidesWithWall(Wall w) {
		if(this.live && this.getRect().intersects(w.getRect())) {
			this.stay();
			return true;
		}
		return false;
	}
	
	//判断tank不能互相穿越
	public boolean collodesWithTanks(List<Tank> tanks){
		if(tanks != null && tanks.size()>0){
			for(int i=0;i<tanks.size();i++){
				Tank t = tanks.get(i);
				if(this.live&&this.getRect().intersects(t.getRect())){
					this.stay();
					t.stay();
					return true;
				}
			}
		}
		return false;
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}
	
	private class BloodBar {
		public void draw(Graphics g) {
			Color c = g.getColor();
			g.setColor(Color.RED);
			g.drawRect(x, y-10, WIDTH, 10);
			int w = WIDTH * life/100;
			g.fillRect(x, y-10, w, 10);
			g.setColor(c);
		}
	}
	
	//tank吃血块补血
	public boolean eat(Blood b) {
		if(this.live && b.isLive() && this.getRect().intersects(b.getRect())) {
			this.life = 100;
			b.setLive(false);
			return true;
		}
		return false;
	}
}
