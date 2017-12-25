package com.example.demo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

/**
 * 炮弹类
 */
public class Missile {
	public static final int XSPEED = 10;
	public static final int YSPEED = 10;
	
	public static final int WIDTH = 10;
	public static final int HEIGHT = 10;
	
	int x, y;
	Tank.Direction dir;
	private boolean good;//炮弹好坏标识
	private boolean live = true;//子弹存活标识
	
	private TankClient tc;
	
	public Missile(int x, int y, Tank.Direction dir) {
		this.x = x;
		this.y = y;
		this.dir = dir;
	}
	
	public Missile(int x, int y, boolean good, Tank.Direction dir, TankClient tc) {
		this(x, y, dir);
		this.good = good;
		this.tc = tc;
	}
	
	/**
	 * 画出子弹
	 */
	public void draw(Graphics g) {
		if(!live) {
			tc.missiles.remove(this);
			return;
		}
		Color c = g.getColor();
		g.setColor(Color.BLACK);
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.setColor(c);
		
		move();
	}
	
	/**
	 * 根据不同方向，进行不同的运动
	 */
	private void move() {
		switch(dir) {
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
		}
		//控制子弹是否出边界
		if(x < 0 || y < 0 || x > TankClient.GAME_WIDTH || y > TankClient.GAME_HEIGHT) {
			live = false;
		}
	}
	public boolean isLive() {
		return live;
	}
	
	//炮弹矩形范围
	public Rectangle getRect(){
		return new Rectangle(x, y, WIDTH, HEIGHT);
	} 
	
	//判断炮弹打中tank
	public boolean hitTank(Tank tank){
		if(this.getRect().intersects(tank.getRect()) && tank.isLive()&& this.good != tank.isGood()){//判断矩形是否相交,并且tank存活
			if(tank.isGood()){//我方坦克
				tank.setLife(tank.getLife() - 20);
				if(tank.getLife() <= 0) tank.setLive(false);
			} else {
				tank.setLive(false);
			}
			this.live = false;
			//击毙tank发生爆炸
			Explode e = new Explode(x, y, tc);
			tc.explodes.add(e);
			return true;
		}
		return false;
	}
	
	//炮弹打一系列多辆tank
	public boolean hitTanks(List<Tank> tanks){
		if(tanks != null && tanks.size()>0){
			for(int i = 0; i< tanks.size(); i++){
				if(this.hitTank(tanks.get(i))){
					return true;
				}
			}
		}
		return false;
	}
	
	//子弹打击墙
	public boolean hitWall(Wall w) {
		//子弹有效，并且打到墙上
		if(this.live && this.getRect().intersects(w.getRect())) {
			this.live = false;
			return true;
		}
		return false;
	}
}
