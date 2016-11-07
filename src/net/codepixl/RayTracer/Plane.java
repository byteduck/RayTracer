package net.codepixl.RayTracer;

import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by aaron on 11/5/2016.
 */
public class Plane extends Renderable{

	public Vector3f normal;
	public boolean reflective = false;

	public Plane(Vector3f pos, Vector3f rotation){
		super(pos);
		this.normal = rotation.normalise(null);
	}

	@Override
	public Vector3f intersectPoint(Ray r){
		float denom = Vector3f.dot(r.dir, normal);
		if (Math.abs(denom) > 1e-6) {
			float t = Vector3f.dot(Vector3f.sub(pos,r.origPos,null), normal) / denom;
			if(t >= 0)
				return r.get(t);
		}
		return null;
	}

	@Override
	public ReadableColor shade(Ray r){
		Vector3f pos = r.pos;
		float xpos = Math.round(pos.x*10f)/10f;
		float zpos = Math.round(pos.z*10f)/10f;
		ReadableColor c = Math.floor(xpos+Math.floor(zpos%2))%2 == 0 ? Color.WHITE : Color.BLACK;
		if(reflective){
			Ray r2 = Ray.reflect(r, new Vector3f(0, 1, 0));
			return Util.blend(Util.multiply(r2.next(this),2), c);
		}else{
			return c;
		}
	}

	@Override
	public Vector3f getNormal(Ray r){
		return new Vector3f(normal);
	}
}
