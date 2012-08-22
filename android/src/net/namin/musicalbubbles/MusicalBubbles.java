package net.namin.musicalbubbles;

import org.puredata.android.processing.PureDataP5Android;

import processing.core.PApplet; 

import android.view.MotionEvent;  

public class MusicalBubbles extends PApplet {

  PureDataP5Android pd;
  private void initPd() {
	  pd = new PureDataP5Android(this, 44100, 0, 2);
	  int zipId = net.namin.musicalbubbles.R.raw.bubblesound;
	  pd.unpackAndOpenPatch(zipId, "bubblesound/make.pd");
	  pd.start();
  }

Bubble[] bubbles = {};

public void bg() {
 background(0);
}

public void setup() {
 initPd();
 bg();
 smooth();
 strokeWeight(1);
}

public void draw() {
 bg();
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
 Bubble bubble = new Bubble();
 bubble.drawMe();
 bubble.hearMe();
 bubbles = (Bubble[])append(bubbles, bubble);
}

class Bubble {
  float x,y;
  float radius;
  int linecol, fillcol;
  float xmove, ymove;
  boolean touching;
  int touchingCount;

  Bubble() {
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
    float ratio = touching ? 0.5f : 1;
    x += xmove*ratio;
    y += ymove*ratio;
    if (x > (width+radius)) { x = 0 - radius; }
    if (x < (0-radius)) { x = width + radius; }
    if (y > (height+radius)) { y = 0 - radius; }
    if (y < (0-radius)) { y = height + radius; }
   
    drawMe(); 
  }

  public void hearMe() {
	  pd.sendFloat("freq", 20*(110-radius)+250);
	  pd.sendFloat("volume", 1-0.1f*min(touchingCount, 10));
	  pd.sendBang("trigger");
  }

  public void setTouching(boolean updatedTouching) {
    if (touching == updatedTouching) {
      return;
    }
    hearMe();
    if (updatedTouching) {
      touchingCount++;
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
