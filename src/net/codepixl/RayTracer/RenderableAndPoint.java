package net.codepixl.RayTracer;

import org.lwjgl.util.vector.Vector3f;

/**
 * Created by aaron on 11/6/2016.
 */
public class RenderableAndPoint{
	public Renderable renderable;
	public Vector3f point;
	public RenderableAndPoint(Renderable renderable, Vector3f point){
		this.renderable = renderable;
		this.point = point;
	}
}
