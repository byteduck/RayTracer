package net.codepixl.RayTracer;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;
import org.lwjgl.util.vector.Vector3f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by aaron on 11/5/2016.
 */
public class RayTracer{

	public static int WIDTH = 1000, HEIGHT = 1000, DEPTH = 3, RWIDTH = 300, RHEIGHT = 300;
	public static float QUALITY = 100f, RATIO = (float)RWIDTH/(float)RHEIGHT; //QUALITY doesn't have any affect any more
	public static boolean REPEAT = true; //If REPEAT, then it will render over and over again. If not, it will render once and save it as render.png.

	private ByteBuffer displayBuffer;
	public ArrayList<Renderable> renderables = new ArrayList<Renderable>();
	Plane plane,plane2;
	Sphere sphere,sphere2,sphere3;
	public volatile boolean quit = false;
	public Vector3f light = new Vector3f(0f,6f,5);
	public Color ambientLight = new Color(75,75,75), skyColor = new Color(20,20,20);
	public int time = 0;

	public RayTracer() throws LWJGLException{
		if(RATIO != 1) {
			System.err.println("Render ratio must be 1:1.");
			System.exit(2);
		}
		displayBuffer = ByteBuffer.allocateDirect(RWIDTH*RHEIGHT*DEPTH);
		Display.setDisplayMode(new DisplayMode(WIDTH,HEIGHT));
		Display.setTitle("RayTracer");
		Display.create(new PixelFormat(8,8,8));
		plane = new Plane(new Vector3f(0,-2f,0),new Vector3f(0f,1f,0f));
		plane.reflective = true;
		renderables.add(plane);
		sphere = new Sphere(new Vector3f(0f,-1f,8),1,true,new Color(50,0,0));
		renderables.add(sphere);
		sphere2 = new Sphere(new Vector3f(-2.1f,0,8),1,true,new Color(0,50,0));
		renderables.add(sphere2);
		sphere3 = new Sphere(new Vector3f(2.1f,0,8),1,true,new Color(0,0,50));
		renderables.add(sphere3);
		new Thread(() -> {
			raytrace();
			while(!quit && REPEAT)
				raytrace();
		}).start();
		while(!Display.isCloseRequested()){
			displayBuffer.rewind();
			GL11.glPixelZoom((float)WIDTH/(float)RWIDTH,(float)HEIGHT/(float)RHEIGHT);
			GL11.glDrawPixels(RWIDTH,RHEIGHT,GL11.GL_RGB,GL11.GL_UNSIGNED_BYTE,displayBuffer);
			Display.update();
		}
		quit = true;
	}

	public void raytrace(){
		long time = System.nanoTime();
		this.time+=Time.getDelta()*1000;
		update();
		ArrayList<Thread> threads = new ArrayList<Thread>();
		for(int x = 0; x < RWIDTH; x+=RWIDTH/5+1)
			for(int y = 0; y < RHEIGHT; y+=RHEIGHT/5+1){
				Thread t = new Thread(new PixelRunnable(x, y));
				threads.add(t);
				t.start();
			}
		for(Thread t : threads)
			try{
				t.join();
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		/*ByteBuffer tmp = fgDisplayBuffer;
		fgDisplayBuffer = displayBuffer;
		displayBuffer = tmp;*/
		if(!REPEAT){
			BufferedImage img = new BufferedImage(RWIDTH, RHEIGHT, BufferedImage.TYPE_3BYTE_BGR);
			for(int x = 0; x < RWIDTH; x++)
				for(int y = 0; y < RHEIGHT; y++){
					int r = displayBuffer.get((x + y * RWIDTH) * 3);
					int g = displayBuffer.get((x + y * RWIDTH) * 3 + 1);
					int b = displayBuffer.get((x + y * RWIDTH) * 3 + 2);
					img.setRGB(-x + RWIDTH - 1, -y + RHEIGHT - 1, (r << 16) + (g << 8) + b);
				}
			try{
				ImageIO.write(img, "png", new File("render.png"));
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		Time.setDelta(System.nanoTime()-time);
	}

	public void update(){
		//plane.pos.y+=0.1f;
		//plane2.pos.z+=Time.getDelta();
		if(time%2000 <= 1000){
			sphere.pos.y = Util.easeInOutQuad((float)time/1000f, 0f, 1f, 1f);
			sphere2.pos.y = Util.easeInOutQuad((float)time/1000f, 1f, -1f, 1f);
			sphere3.pos.y = Util.easeInOutQuad((float)time/1000f, 1f, -1f, 1f);
		}else{
			sphere.pos.y = Util.easeInOutQuad((float)time/1000f, 1f, -1f, 1f);
			sphere2.pos.y = Util.easeInOutQuad((float)time/1000f, 0f, 1f, 1f);
			sphere3.pos.y = Util.easeInOutQuad((float)time/1000f, 0f, 1f, 1f);
		}
		//sphere.pos.y-=0.1f;
	}

	private class PixelRunnable implements Runnable{
		int x,y;
		public PixelRunnable(int x, int y){
			this.x = x; this.y = y;
		}
		@Override
		public void run(){
			for(int x = this.x; x < this.x+RWIDTH/5+1; x++)
				for(int y = this.y; y < this.y+RHEIGHT/5+1; y++)
					if(!(x > RWIDTH-1 || x < 0 || y > RHEIGHT-1 || y < 0))
						putPixel(x,y,getPixel(x,y));
		}
	}

	public ReadableColor getPixel(int x, int y){
		Ray r = new Ray(new Vector3f(),new Vector3f((float)x/(float)RWIDTH-0.5f, (float)y/(float)RHEIGHT-0.5f, 1), 1/QUALITY, this);
		return r.next(null);
	}

	public void putPixel(int x, int y, int r, int g, int b){
		if(x > RWIDTH-1 || x < 0 || y > RHEIGHT-1 || y < 0)
			return;
		displayBuffer.put((x+y*RWIDTH)*DEPTH,(byte)r);
		displayBuffer.put((x+y*RWIDTH)*DEPTH+1,(byte)g);
		displayBuffer.put((x+y*RWIDTH)*DEPTH+2,(byte)b);
	}

	public void putPixel(int x, int y, ReadableColor color){
		putPixel(x, y, color.getRed(), color.getGreen(), color.getBlue());
	}

	public static void main(String[] args) throws LWJGLException{new RayTracer();}
}
