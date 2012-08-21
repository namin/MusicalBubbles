Bubble[] bubbles = {};

void bg() {
 background(0);
}

void setup() {
 bg();
 smooth();
 strokeWeight(1);
}

void draw() {
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
  
void addBubble() {
 Bubble bubble = new Bubble();
 bubble.drawMe();
 bubbles = (Bubble[])append(bubbles, bubble);
}

class Bubble {
  float x,y;
  float radius;
  color linecol, fillcol;
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
  
  void drawMe() {
    noStroke();
    fill(fillcol, 255/(touchingCount+1));
    ellipse(x, y, radius*2, radius*2);
    stroke(linecol);
    noFill();
    ellipse(x, y, 10, 10);
  }
  
  void updateMe() {
    x += xmove;
    y += ymove;
    if (x > (width+radius)) { x = 0 - radius; }
    if (x < (0-radius)) { x = width + radius; }
    if (y > (height+radius)) { y = 0 - radius; }
    if (y < (0-radius)) { y = height + radius; }
   
    drawMe(); 
  }
  
  void setTouching(boolean updatedTouching) {
    if (touching == updatedTouching) {
      return;
    }
    if (updatedTouching) {
      touchingCount++; 
    }
    touching = updatedTouching; 
  }
  
  void updateTouching() {
    float d = dist(mouseX, mouseY, x, y);
    setTouching((d - radius) < 0);
  }
}
