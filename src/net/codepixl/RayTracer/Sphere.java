package net.codepixl.RayTracer;

import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;
import org.lwjgl.util.vector.Vector3f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by aaron on 11/5/2016.
 */
public class Sphere extends Renderable{

	private BufferedImage tex;

	float size;
	boolean reflective,refractive;
	Color color;

	public Sphere(Vector3f pos, float size, boolean reflective, Color color){
		super(pos);
		this.size = size;
		this.reflective = reflective;
		this.color = color;
		try{
			this.tex = ImageIO.read(getClass().getClassLoader().getResourceAsStream("sphere.jpg"));
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public Vector3f intersectPoint(Ray r){
		float[] sols = new float[2];
		Vector3f L = Vector3f.sub(r.origPos,pos,null);
		float a = Vector3f.dot(r.dir, r.dir);
		float b = 2 * Vector3f.dot(r.dir, L);
		float c = Vector3f.dot(L,L) - size;

		if (!solveQuadratic(a, b, c, sols)) return null;

		float t0 = sols[0], t1 = sols[1];

		if (t0 > t1){
			float tmp = t0;
			t0 = t1;
			t1 = tmp;
		}

		if (t0 < 0) {
			t0 = t1; // if t0 is negative, let's use t1 instead
			if (t0 < 0) return null; // both t0 and t1 are negative
		}
		return r.get(t0);
	}

	boolean solveQuadratic(float a, float b, float c, float[] solutions){
		float discr = b * b - 4 * a * c;
		if (discr < 0) return false;
		else if (discr == 0) solutions[0] = solutions[1] = - 0.5f * b / a;
		else {
			float q = (b > 0) ?
					-0.5f * (b + (float)Math.sqrt(discr)) :
					-0.5f * (b - (float)Math.sqrt(discr));
			solutions[0] = q / a;
			solutions[1] = c / q;
		}
		if (solutions[0] > solutions[1]){
			float tmp = solutions[0];
			solutions[0] = solutions[1];
			solutions[1] = tmp;
		}
		return true;
	}

	@Override
	public ReadableColor shade(Ray r){
		Vector3f pos = r.pos;
		Vector3f normal = new Vector3f(pos.x - this.pos.x, pos.y - this.pos.y, pos.z - this.pos.z).normalise(null);
		/*Vector3f texNormal = new Vector3f((-normal.x+1f)/2f,(-normal.y+1f)/2f,(-normal.z+1f)/2f);
		int px = (int)(texNormal.x * tex.getWidth());
		int py = (int)(texNormal.y * tex.getHeight());
		int rgb = tex.getRGB(px,py);
		Color texCol = new Color((rgb>>16)&0xFF, (rgb>>8)&0xFF, rgb&0xFF);*/
		if(!reflective && !refractive){
			return color;
		}else if(!refractive){
			Ray r2 = Ray.reflect(r, normal);
			if(color.getAlpha() > 0)
				return Util.blend(r2.next(this), color);
			else
				return r2.next(this);
		}else{
			Ray r2 = Ray.refract(r, normal);
			if(color.getAlpha() > 0)
				return Util.blend(r2.next(this), color);
			else
				return r2.next(this);
		}
	}

	@Override
	public Vector3f getNormal(Ray r){
		Vector3f pos = r.pos;
		return new Vector3f(pos.x - this.pos.x, pos.y - this.pos.y, pos.z - this.pos.z).normalise(null);
	}
}
