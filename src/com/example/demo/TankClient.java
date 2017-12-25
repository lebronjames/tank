package com.example.demo;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * 窗口类
 * 
 * @author Administrator
 *
 */
public class TankClient extends Frame {
	private static final long serialVersionUID = 1L;
	public static final int GAME_WIDTH = 800;
	public static final int GAME_HEIGHT = 600;

	Tank myTank = new Tank(50, 50, this);
	// Missile m = new Missile(50, 50, Tank.Direction.R);//new一颗子弹
	List<Missile> missiles = new ArrayList<Missile>();

	Image offScreenImage = null;

	/**
	 * 画出tank,重写了paint（graphics g）方法
	 */
	public void paint(Graphics g) {// JDK Graphics类提供基本绘图方法
		g.drawString("missiles count:" + missiles.size(), 10, 50);

		for (int i = 0; i < missiles.size(); i++) {
			Missile m = missiles.get(i);
			m.draw(g);
		}
		myTank.draw(g);// 画出Tank
	}

	/**
	 * 使用双缓冲消除闪烁现象，用update来写双缓冲 闪烁原因：刷新重画频率太快,paint方法还没有完成；逐条显示
	 * 解决办法：将所有东西画在虚拟图片上,一次性显示出来
	 */
	public void update(Graphics g) {
		if (offScreenImage == null) {
			offScreenImage = this.createImage(GAME_WIDTH, GAME_HEIGHT);
		}
		Graphics gOffScreen = offScreenImage.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.GREEN);
		gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
		gOffScreen.setColor(c);
		paint(gOffScreen);
		g.drawImage(offScreenImage, 0, 0, null);
	}

	/**
	 * 显示Tank主窗口
	 */
	public void lanchFrame() {
		this.setLocation(400, 300);
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.setTitle("TankWar");
		// 用于接收窗口事件的侦听器接口
		this.addWindowListener(new WindowAdapter() {
			// 窗口被完全关闭时调用的方法
			public void windowClosing(WindowEvent e) {
				System.exit(0);// 终止虚拟机,退出Java程序.System.exit(0)是正常退出程序,非0表示非正常退出程序
			}
		});
		this.setResizable(false);
		this.setBackground(Color.GREEN);
		this.addKeyListener(new KeyMonitor());// 添加键盘监听器
		setVisible(true);
		new Thread(new paintThread()).start();
	}

	public static void main(String[] args) {
		TankClient tc = new TankClient();
		tc.lanchFrame();
	}

	/**
	 * 启动线程不断重画，而不是按一次，触发一次。均匀，能解决子弹自动飞行问题 内部类，优点：可以方便访问包装类的方法repaint()
	 */
	private class paintThread implements Runnable {

		@Override
		public void run() {
			while (true) {
				repaint();// awt.Component.repaint()，每次重画改变Tank的位置
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 添加键盘监听器类KeyMonitor
	 */
	private class KeyMonitor extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			myTank.keyPressed(e);
		}

		public void keyReleased(KeyEvent e) {// 处理按键抬起事件
			myTank.keyReleased(e);
		}
	}
}
