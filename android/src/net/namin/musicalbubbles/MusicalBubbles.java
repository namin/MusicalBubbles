package net.namin.musicalbubbles;

import org.puredata.android.processing.PureDataP5Android;

import processing.core.PApplet; 

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;

public class MusicalBubbles extends PApplet implements SensorEventListener {

  float deltaX;
  float deltaY;
  float deltaZ;
  private SensorManager mSensorManager;
  private Sensor mAccelerometer;
  private boolean mFresh;
  private float mLastX;
  private float mLastY;
  private float mLastZ;
  @Override protected void onResume() {
    super.onResume();
    mFresh = true;
    deltaX = 0.0f;
    deltaY = 0.0f;
    deltaZ = 0.0f;
    mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
    mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
  }
  @Override protected void onPause() {
    mSensorManager.unregisterListener(this);
    super.onPause();
  }
  @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}
  @Override public void onSensorChanged(SensorEvent event) {
    float x = event.values[0];
    float y = event.values[1];
    float z = event.values[2];
    if (mFresh) {
      mLastX = x;
      mLastY = y;
      mLastZ = z;
      mFresh = false;
    } else {
      deltaX = mLastX - x;
      deltaY = -(mLastY - y);
      deltaZ = mLastZ - z;
      mLastX = x;
      mLastY = y;
      mLastZ = z;
    }
  }
  
  PureDataP5Android pd;
  private void initPd() {
	  pd = new PureDataP5Android(this, 44100, 0, 2);
	  int zipId = net.namin.musicalbubbles.R.raw.pd;
	  pd.unpackAndOpenPatch(zipId, "main.pd");
	  pd.start();
  }

Bubble[] bubbles = {};

public void bg() {
 background(0);
}

public void fg() {
 fill(255);
}

public void setup() {
 initPd();
 bg();
 smooth();
 strokeWeight(1);
}

public void draw() {
 bg();
 fg();
 for (Bubble bubble : bubbles) {
   bubble.updateMe();
 }
}

public boolean surfaceTouchEvent(MotionEvent event) {
  boolean ret = super.surfaceTouchEvent(event);
  int action = event.getAction() & MotionEvent.ACTION_MASK;
  if (action == MotionEvent.ACTION_UP) {
    for (Bubble bubble : bubbles) {
      bubble.setTouching(false); 
    }
    addBubble();
  } else if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
    for (Bubble bubble : bubbles) {
      bubble.updateTouching();
    }
  }
  return ret;
}
  
public void addBubble() {
 Bubble bubble = new Bubble(bubbles.length);
 bubble.drawMe();
 bubble.hearMe();
 bubbles = (Bubble[])append(bubbles, bubble);
}

class Bubble {
  int index;
  float x,y;
  float radius;
  int linecol, fillcol;
  float xmove, ymove;
  boolean touching;
  int touchingCount;

  Bubble(int index) {
    this.index = index;
    x = mouseX;
    y = mouseY;
    radius = random(100) + 10;
    linecol = color(random(255), random(255), random(255));
    fillcol = color(random(255), random(255), random(255));
    xmove = random(10) - 5;
    ymove = random(10) - 5;
    touching = false;
    touchingCount = 0;
  }
  
  public void drawMe() {
    noStroke();
  	fill(fillcol, 255/(touchingCount+1));
  	float delta = touching ? 10 : 0;
  	ellipse(x, y, (radius+delta)*2, (radius+delta)*2);
	  stroke(linecol);
	  noFill();
	  ellipse(x, y, 10+delta, 10+delta);
  }
  
  public void updateMe() {
    radius += deltaZ;
    if (radius < 10) {
      radius = 10;
      hearMe();
      die();
      return;
    }

    float ratio = touching ? 0.5f : 1;
    xmove += deltaX * Math.sqrt(displayWidth)/100f;
    ymove += deltaY * Math.sqrt(displayHeight)/100f;
    x += xmove*ratio;
    y += ymove*ratio;
    if (x > (width+radius)) { x = 0 - radius; }
    if (x < (0-radius)) { x = width + radius; }
    if (y > (height+radius)) { y = 0 - radius; }
    if (y < (0-radius)) { y = height + radius; }
   
    drawMe(); 
  }

  private String sel() {
    int s = index % 4;
    return s == 0 ? "" : (s + "");
  }

  private void sendFloats() {
    pd.sendFloat("freq"+sel(), 20*(110-Math.min(radius, 110))+250);
    pd.sendFloat("volume"+sel(), 1-0.1f*min(touchingCount, 10));
  }

  public void hearMe() {
    sendFloats();
	  pd.sendBang("trigger"+sel());
  }

  public void hearMeDie() {
    pd.sendBang("kill"+sel());
  }

  public void die() {
    hearMeDie();
    Bubble last = bubbles[bubbles.length-1];
    last.index = index;
    bubbles[index] = last;
    bubbles = (Bubble[])shorten(bubbles);
  }

  public void setTouching(boolean updatedTouching) {
    if (touching == updatedTouching) {
      return;
    }
    hearMe();
    if (updatedTouching) {
      touchingCount++;
      if (touchingCount >= 10) {
        die();
      }
    } else {
      xmove = -xmove;
      ymove = -ymove;
    }
    touching = updatedTouching; 
  }
  
  void updateTouching() {
	  setTouching(isTouchingNow());
  }

  boolean isTouchingNow() {
  	float d = dist(mouseX, mouseY, x, y);
	  return (d - radius) < 0;
  }
}

}
