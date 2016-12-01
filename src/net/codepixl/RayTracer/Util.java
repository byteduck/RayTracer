package net.codepixl.RayTracer;

import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by aaron on 11/5/2016.
 */
public class Util{
	public static float distance(Vector3f a, Vector3f b){
		return (float)Math.sqrt(Math.pow(b.x-a.x,2)+Math.pow(b.y-a.y,2)+Math.pow(b.z-a.z,2));
	}
	public static ReadableColor multiply(ReadableColor a, ReadableColor b){
		return new Color((int)(255*((a.getRed()/255f)*(b.getRed()/255f))),(int)(255*((a.getGreen()/255f)*(b.getGreen()/255f))),(int)(255*((a.getBlue()/255f)*(b.getBlue()/255f))));
	}

	public static ReadableColor multiply(ReadableColor a, float b){
		return new Color((int)(a.getRed()*b),(int)(a.getGreen()*b),(int)(a.getBlue()*b));
	}

	public static Vector3f abs(Vector3f vec){
		return new Vector3f(Math.abs(vec.x), Math.abs(vec.y), Math.abs(vec.z));
	}

	public static ReadableColor blend(ReadableColor a, ReadableColor b){
		return new Color((a.getRed()+b.getRed())/2,(a.getGreen()+b.getGreen())/2,(a.getBlue()+b.getBlue())/2);
	}
	public static float max(float a, float b){
		if(a>b) return a; else return b;
	}

	public static ReadableColor add(ReadableColor a, ReadableColor b){
		return new Color(clamp(a.getRed()+b.getRed(),0,255), clamp(a.getGreen()+b.getGreen(),0,255), clamp(a.getBlue()+b.getBlue(),0,255));
	}

	public static Vector3f add(Vector3f vec, float add){
		return new Vector3f(vec.x+add, vec.y+add, vec.z+add);
	}

	public static int clamp(int i, int min, int max){
		if(i < min)
			i = min;
		if(i > max)
			i = max;
		return i;
	}

	public static float clamp(float i, float min, float max){
		if(i < min)
			i = min;
		if(i > max)
			i = max;
		return i;
	}

	public static float easeInOutQuad(float t, float b, float c, float d) {
		t = t%d;
		if ((t/=d/2) < 1) return c/2*t*t + b;
		return -c/2 * ((--t)*(t-2) - 1) + b;
	}

    public static Vector3f multiply(Vector3f vec, float m){
		return new Vector3f(vec.x*m, vec.y*m, vec.z*m);
    }
}
