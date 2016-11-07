package net.codepixl.RayTracer;

/**
 * Created by aaron on 11/5/2016.
 */
public class Time{
	private static float deltaTime;
	public static float getDelta(){
		return deltaTime;
	}
	public static void setDelta(long nanos){
		deltaTime = nanos/1000000000f;
	}
}
