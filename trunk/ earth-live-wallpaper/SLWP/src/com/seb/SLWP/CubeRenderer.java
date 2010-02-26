/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seb.SLWP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;

import com.seb.SLWP.GLWallpaperService.Renderer;

/**
 * Render a pair of tumbling cubes.
 */

class CubeRenderer implements Renderer, Serializable {
	private static final long serialVersionUID = -6907195147178147400L;
	private Context mContext;
	public float dx = 0;
	public float dy = 0;
	public static float rs = 0.6f;
	private GL10 mGl;
	public static float freespin = 0f;
	public static float direction = -1f;
	public static float zoomfactor = 1f;
	private float mRatio;
	private float realaxis = 0f;
	public float trot;
	public float xpos = 0f;
	public float ypos = 0f;
	public boolean showrings = false;
	public static boolean usebg = true;
	private float axisangle = 23.27f;
	int curtex = 1;
	boolean showmoon = true;
	public boolean deathstar2 = false;
	private boolean inited = false;
	private StarField mStarfield;
	
	public CubeRenderer(Context context) {
		mContext = context;
		mBg = new Background(mContext);
		mStarfield=new StarField();
		// mSphere = new Sphere(mContext);
		// mRings = new Rings(mContext);
		// new InitTask().execute();
	}
	
	public void setAnimbg(Boolean b){
		Background.animbg=b;
	}
	
	public void setTex(int t) {
		showrings = t == 15 ? true : false;
		switch (t) {
		case 0:
		case 1:
		case 2:
		case 3:
			axisangle = 23.27f;
			deathstar2 = false;
			break;
		case 4:
			axisangle = 5.145f;
			deathstar2 = false;
			break;
		case 5:
			deathstar2 = false;
			axisangle = 25.19f;
			break;
		case 6:
			deathstar2 = false;
			axisangle = 0.01f;
			break;
		case 7:
			deathstar2 = false;
			axisangle = 177.36f;
			break;
		case 8:
			deathstar2 = false;
			axisangle = 3.12f;
			break;
		case 9:
			deathstar2 = false;
			axisangle = 97.77f;
			break;
		case 10:
			deathstar2 = false;
			axisangle = 0.469f;
			break;
		case 11:
			deathstar2 = false;
			axisangle = 0.117f;
			break;
		case 12:
			deathstar2 = false;
			axisangle = 175.986f;
			break;
		case 15:
			deathstar2 = false;
			axisangle = 26.73f;
			break;
		case 19:
			deathstar2 = false;
			axisangle = 29.58f;
			break;
		case 21:
			deathstar2 = false;
			axisangle = 0f;
			break;
		case 22:
			deathstar2 = true;
			axisangle = 0f;
			break;
		default:
			deathstar2 = false;
			axisangle = 0f;
			break;
		}
		curtex = t;
		Sphere.InitTex();
	}

	public void setBg(int t) {
		mBg.InitTex(mGl, t);
	}

	public void setRA(boolean ra) {
		realaxis = ra ? 1f : 0f;
	}

	public void onDrawFrame(GL10 gl) {
		/*
		 * Usually, the first thing one might want to do is to clear the screen.
		 * The most efficient way of doing this is to use glClear().
		 */
		if (gl == null)
			return;
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		/*
		 * Now we're ready to draw some 3D objects
		 */
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		if (usebg) {
			gl.glDepthMask(false);
			mBg.draw(gl);
			gl.glDepthMask(true);
		}
		
		//mStarfield.draw(gl);
		
		gl.glPushMatrix();
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(xpos, ypos, -6f);// *Math.max(0.6f,zoomfactor));
		gl.glScalef(zoomfactor, zoomfactor, zoomfactor);

		// gl.glPushMatrix();
		// gl.glRotatef(mAngleX += ((dy *= 0.95f) % 360) * freespin, 1, 0, 0);
		// gl.glRotatef(mAngleY += ((dx *= 0.95f) % 360), 0, 1, 0);
		// gl.glPopMatrix();

		gl.glRotatef(90, 1, 0, 0);

		// gl.glRotatef(5,1,0,0);

		// gl.glRotatef(mAngleZ += ((0.6f * rs) % 360) * direction, 0, 0, 1);

		// gl.glPushMatrix();
		gl.glRotatef(axisangle * realaxis, 0, 1, 0);
		if (curtex == 15)
			gl.glRotatef(15, 1, 0, 0);// pour anneaux saturne

		gl.glRotatef(mAngleX += ((dy *= 0.95f) * 0.2f % 360) * freespin, 1, 0,
				0);
		gl.glRotatef(mAngleY += ((dx *= 0.95f) * 0.2f % 360) * direction, 0, 0,
				1);

		gl.glRotatef(mAngleZ += ((0.6f * rs) % 360) * direction, 0, 0, 1);

		if (deathstar2) {
			gl.glEnable(GL10.GL_BLEND);
			if(mDs!=null)mDs.draw(gl);
			gl.glDisable(GL10.GL_BLEND);
		} else if (mSphere != null)
			mSphere.draw(gl, showmoon);

		if (showrings&&mRings!=null)
			mRings.draw(gl);

		gl.glPopMatrix();
		
	}

	public int[] getConfigSpec() {

		// We want a depth buffer, don't care about the
		// details of the color buffer.
		int[] configSpec = { EGL10.EGL_DEPTH_SIZE, 0, EGL10.EGL_NONE };

		return configSpec;

	}

	public void SurfaceDestroyed() {
		Sphere.freeHardwareBuffers();
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {

		if (gl == null)
			return;
		gl.glViewport(0, 0, width, height);

		/*
		 * Set our projection matrix. This doesn't have to be done each time we
		 * draw, but usually a new projection needs to be set when the viewport
		 * is resized.
		 */

		mRatio = (float) width / height;
		mBg.setDims(width, height);
		// Background.vW=width;
		// Background.vH=height;

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-mRatio, mRatio, -1f, 1f, 2f, 15f);
		gl.glEnable(GL10.GL_TEXTURE_2D);

		if (mDs == null && deathstar2) {
			mDs = new DeathStar(mContext);
		}
		if (deathstar2)
			mDs.Init(gl);
		else {
			if (mSphere == null)
				mSphere = new Sphere(mContext);
			mSphere.Init(gl);
			if (mRings == null)
				mRings = new Rings(mContext);
			mRings.Init(gl);
		}

	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		/*
		 * By default, OpenGL enables features that improve quality but reduce
		 * performance. One might want to tweak that especially on software
		 * renderer.
		 */
		if (gl == null)
			return;
		mGl = gl;

		gl.glDisable(GL10.GL_DITHER);

		/*
		 * Some one-time OpenGL initialization can be made here probably based
		 * on features of this particular context
		 */
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

		gl.glClearColor(0.0f, 0.0f, 0.0f, 1f);
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_TEXTURE_2D);

		// gl.glFrontFace(GL10.GL_CW);
		if (usebg)
			mBg.Init(gl);
		// mBg.setDims(480, 800);
		// mBg.draw(gl);
	}

	public void shutdown(GL10 gl) {
		if (gl == null)
			return;
		Sphere.freeHardwareBuffers();
	}

	public void resetAngles() {
		if (realaxis == 1f) {
			mAngleX = 0f;
			mAngleY = 0f;
			mAngleZ = 0f;
		}
	}

	class InitTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			if (mSphere == null)
				mSphere = new Sphere(mContext);
			// mSphere.Init(mGl);
			if (mDs == null)
				mDs = new DeathStar(mContext);
			// mDs.Init(mGl);
			if (mRings == null)
				mRings = new Rings(mContext);
			// mRings.Init(mGl);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (SLWP.Randomtex) {
				setTex(SLWP.Tex);
			}
			mSphere.Init(mGl);
			mDs.Init(mGl);
			mRings.Init(mGl);
		}

	}

	private static Sphere mSphere;
	private static Rings mRings;
	private DeathStar mDs;
	private Background mBg;
	public static float mAngleX = 0f;
	public static float mAngleY = 0f;
	public static float mAngleZ = 0f;
}
