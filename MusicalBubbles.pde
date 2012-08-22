Bubble[] bubbles = {};

void bg() {
 background(0);
}

void setup() {
 size(320,480);
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

void mouseReleased() {
 for (Bubble bubble : bubbles) {
   bubble.setTouching(false);
 }
 addBubble();
}

void mouseMoved() {
 for (Bubble bubble : bubbles) {
   bubble.updateTouching();
 }
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

  public void setTouching(boolean updatedTouching) {
    if (touching == updatedTouching) {
      return;
    }
    //hearMe();
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
