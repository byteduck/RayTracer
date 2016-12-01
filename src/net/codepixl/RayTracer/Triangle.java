package net.codepixl.RayTracer;

import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by aaron on 11/7/2016.
 */
public class Triangle extends Renderable{
	public Triangle(Vector3f v0, Vector3f p1, Vector3f p2){
		super(null);
	}
	public Vector3f intersectPoint(Ray r){
		return null;
	}
	public ReadableColor shade(Ray r){
		return new Color(0,0,0,0);
	}
	public Vector3f getNormal(Ray r){
		return new Vector3f();
	}
}
