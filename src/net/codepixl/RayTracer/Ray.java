package net.codepixl.RayTracer;

import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;

public class Ray {

	public Vector3f pos, dir, fineScaledDir, coarseScaledDir, origPos;
	public ArrayList<Vector3f> poses = new ArrayList<Vector3f>();
	public float distance;
	public float scalar;
	public Renderable intersect;
	public RayTracer rayTracer;

	public static float scaleFactor = 3;

	public Ray(Vector3f orig, Vector3f pos2, float scalar, RayTracer rayTracer){
		this.pos = orig;
		this.origPos = new Vector3f(pos);
		this.dir = new Vector3f();
		new Vector3f(pos2.x - orig.x, pos2.y - orig.y, pos2.z - orig.z).normalise(dir);
		this.scalar = scalar;
		this.fineScaledDir = scale(dir, scalar);
		this.coarseScaledDir = scale(dir, scalar*scaleFactor);
		this.poses.add(pos);
		this.rayTracer = rayTracer;
	}

	public static Ray fromDir(Vector3f pos, Vector3f dir, float scalar, RayTracer m){
		Ray r = new Ray(pos,dir,scalar,m);
		r.dir = dir;
		r.fineScaledDir = scale(dir, scalar);
		r.coarseScaledDir = scale(dir, scalar*scaleFactor);
		return r;
	}

	public ReadableColor next(Renderable exclude){
		ReadableColor ret = rayTracer.skyColor;
		RenderableAndPoint index = null;
		for(Renderable g: rayTracer.renderables){
			if(g != exclude){
				Vector3f intersect = g.intersectPoint(this);
				if(intersect != null){
					if(index == null || Util.distance(origPos, intersect) < Util.distance(origPos, index.point))
						index = new RenderableAndPoint(g, intersect);
				}
			}
		}
		/*while(distance < 300 && go == null && intersectsAnything){
			for(Renderable g : rayTracer.renderables){
				if(g != exclude && g.closeEnough(this)){
					Vector3f opos = new Vector3f(pos);
					float odist = distance;
					for(int i = 0; i < scaleFactor; i++){
						pos = Vector3f.add(pos, fineScaledDir, null);
						distance += scalar;
						if(g.intersectPoint(this)){
							ReadableColor c = g.shade(this);
							ReadableColor light = light(g);
							ret = Util.multiply(c,Util.add(light, rayTracer.ambientLight));
							go = g;
							break;
						}
					}
					distance = odist;
					pos = opos;
				}
			}
			pos = Vector3f.add(pos, coarseScaledDir, null);
			distance += scalar * scaleFactor;
		}*/
		if(index != null){
			this.pos = new Vector3f(index.point);
			ReadableColor c = index.renderable.shade(this);
			ReadableColor light = light(index.renderable);
			ret = Util.multiply(c,Util.add(light, rayTracer.ambientLight));
		}
		return ret;
	}

	private ReadableColor light(Renderable from){
		Ray r = new Ray(pos, rayTracer.light, scalar, rayTracer);
		Vector3f normal = from.getNormal(this);
		Vector3f.add(new Vector3f(normal.x*scalar*10,normal.y*scalar*10,normal.z*scalar*10), r.pos, r.pos);
		r.origPos = new Vector3f(r.pos);
		float dist = Util.distance(r.pos, rayTracer.light);
		for(Renderable g : rayTracer.renderables)
			if(g.intersectPoint(r) != null)
				return Color.BLACK;
		float diffuse = Util.max(Vector3f.dot(r.dir, normal),0.1f);
		diffuse = diffuse * (1.0f / (1.0f + (0.25f * dist/10 * dist/10)));
		float lightLevel = diffuse;
		int iLightLevel = (int)(lightLevel*255);
		return new Color(iLightLevel,iLightLevel,iLightLevel);
	}

	public void step(){
		pos = Vector3f.add(pos, fineScaledDir, null);
		distance += scalar;
	}

	public void stepCoarse(){
		pos = Vector3f.add(pos, coarseScaledDir, null);
		distance += scalar*scaleFactor;
	}

	private static Vector3f scale(Vector3f vec, float scalar) {
		Vector3f tmp = new Vector3f();
		tmp.x = vec.x * scalar;
		tmp.y = vec.y * scalar;
		tmp.z = vec.z * scalar;
		return tmp;
	}

	@Override
	public String toString() {
		return String.format("Ray: Pos = (%s) Dir = (%s)", pos, dir);
	}

	public static Ray reflect(Ray r, Vector3f normal){
		normal.normalise(normal);
		float dot = Vector3f.dot(r.dir, normal);
		dot *= 2f;
		Vector3f right = new Vector3f(normal.x * dot, normal.y * dot, normal.z * dot);
		Vector3f dir = new Vector3f(r.dir.x - right.x, r.dir.y - right.y, r.dir.z - right.z);
		return Ray.fromDir(new Vector3f(r.pos), dir, r.scalar, r.rayTracer);
	}

	public Vector3f get(float distance){
		return Vector3f.add(origPos, new Vector3f(dir.x*distance, dir.y*distance, dir.z*distance), null);
	}

	public static Ray refract(Ray r, Vector3f normal){
		normal.normalise(normal);
		return Ray.fromDir(new Vector3f(r.pos), normal, r.scalar, r.rayTracer);
	}
}