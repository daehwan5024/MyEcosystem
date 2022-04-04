import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

import static processing.core.PApplet.*;

public class Agent {
    static PApplet processing = Main.processing;
    PVector location;
    PVector velocity;
    int type = 100;

    float health;
    float x_offset;
    float y_offset;

    float r;
    float max_speed;
    Agent() {}
    Agent(PVector l) {
        location = l;
        health = processing.random(100, 255);
        x_offset = processing.random(1000);
        y_offset = processing.random(1000);
        max_speed = map(processing.random(1), 0, 1, 15, 0);
        r = map(processing.random(1), 0, 1, 0, 50);

        velocity = new PVector(0, 0);
    }
    void run(ArrayList<Agent> agents) {
        update(agents);
        borders();
        display();
    }
    void update(ArrayList<Agent> agents) {
        velocity.x = map(processing.noise(x_offset),0,1,-max_speed, max_speed);
        velocity.y = map(processing.noise(y_offset),0,1,-max_speed, max_speed);
        x_offset += 0.01;
        y_offset += 0.01;

        location.add(velocity);
        health -= 0.2;
    }
    void borders() {
        if (location.x < -r) location.x = processing.width+r;
        if (location.y < -r) location.y = processing.height+r;
        if (location.x > processing.width+r) location.x = -r;
        if (location.y > processing.height+r) location.y = -r;
    }
    void display() {
        processing.pushMatrix();
        if(!Main.simpleGraphics)
            drawBody();
        else
            simpleShape();
        processing.popMatrix();
    }
    void collision() {

    }
    //child class to override this
    void drawBody() {}
    void simpleShape() {
        processing.ellipseMode(processing.CENTER);
        processing.stroke(0,health);
        processing.fill(0, health);
        processing.ellipse(location.x, location.y, r, r);
    }
    // Death
    boolean dead() {
        return health < 0.0;
    }
}

class Fish extends Agent {
    float s = processing.random(-90, 90);
    float d = processing.random(0.1f, 0.3f);
    Fish(PVector l) {
        super(l);
    }
    @Override
    void drawBody() {
        processing.pushMatrix();
            processing.translate(location.x, location.y);
            processing.scale(d);
            processing.stroke(0,health);
            processing.fill(0, health);

            processing.rotate(velocity.heading()- radians(90));
            processing.beginShape();
                for (int i = 0; i <= 180; i+=20) {
                    float x = sin(radians(i)) * i/3;
                    float angle = sin(radians(i+s+processing.frameCount*5)) * 50;
                    processing.vertex(x-angle, i*2);
                    processing.vertex(x-angle, i*2);
                }
                for (int i = 180; i >= 0; i-=20) {
                    float x = sin(radians(i)) * i/3;
                    float angle = sin(radians(i+s+processing.frameCount*5)) * 50;
                    processing.vertex(-x-angle, i*2);
                    processing.vertex(-x-angle, i*2);
                }
            processing.endShape();
        processing.popMatrix();
    }
}

class Food extends Agent{
    Food(PVector l) {
        super(l);
        velocity = new PVector(0,0);
        max_speed = processing.random(0.1f, 3);
        type = 0;
        r = map(processing.random(1), 0, 1, 15, 25);
    }
    void update(ArrayList<Agent> agents) {
        // 평균적으로 멀어지는 쪽으로 움직임
        super.update(agents);
        for(int i=agents.size()-1; i>=0; i--) {
            if(i == agents.size()-1) {
                velocity = new PVector(0,0);
            }
            Agent a = agents.get(i);
            if(a.type != 1) {continue;}
            velocity.add(PVector.sub(location, a.location).normalize().mult(1000f/PVector.dist(a.location, location)));
        }
        velocity.normalize().mult(max_speed);
        location.add(velocity);
        health -= 0.5;
    }
    void drawBody() {
        processing.ellipseMode(CENTER);
        processing.stroke(0,health);
        processing.fill(0, 255, 0, health);
        processing.ellipse(location.x, location.y, r, r);
    }
}

class Predator extends Agent {
    Predator(PVector l) {
        super(l);
        max_speed = processing.random(2,4);
        type = 1;
        r = map(processing.random(1), 0, 1, 25, 50);
    }
    void update(ArrayList<Agent> agents) {
        //가장 가까운 곳으로 움직임
        super.update(agents);
        float distance = sqrt(sq(processing.width)+sq(processing.height));
        PVector closest = super.location.copy();
        for (int i = agents.size()-1; i >=0 ; i--) {
            Agent a = agents.get(i);
            if(a.type != 0) {continue;}
            float temp = PVector.dist(a.location, location);
            if(temp < distance){
                closest = a.location.copy();
                distance = temp;
            }
            if(temp < super.r) {
                health += a.health;
                a.health = 0;
                health = min(health, 255);
            }
        }
        velocity = PVector.sub(closest, location).normalize().mult(max_speed);
        location.add(velocity);
        health -= 2;
    }
    void drawBody() {
        processing.ellipseMode(CENTER);
        processing.stroke(0,health);
        processing.fill(255,0,0, health);
        processing.ellipse(location.x, location.y, r, r);
    }
}

class Oscillator extends Agent{
    int num = (int) processing.random(5, 10);
    float[] angle = new float[num];
    float[] d = new float[num];
    float offset = processing.random(10);
    float[] offsets = new float[num];
    Oscillator(PVector l) {
        super(l);
        for(int i=0;i<num;i++) {
            d[i] = processing.random(1f, 3f);
            offsets[i] = processing.random(1);
        }
    }
    void update(ArrayList<Agent> agents) {
        float init_angle = map(processing.noise(offset), 0, 1, 0,2*processing.PI);
        for(int i=0;i<num;i++) {
            angle[i] = init_angle+ map((offsets[i]), -1, 1, -1*processing.PI/5, processing.PI/5);
            offsets[i] += 0.01f;
        }
        offset += 0.01f;
        health -= 0.7f;
    }
    void display() {
        drawBody();
    }
    void drawBody() {
        for(int j=0;j<num;j++) {
            processing.pushMatrix();{
                processing.translate(location.x, location.y);
                processing.scale(d[j]);
                processing.stroke(0, 255, 0, health);
                processing.fill(0, 255, 0, health);

                processing.rotate(angle[j]);
                processing.beginShape();
                {
                    for (int i = 0; i < 180; i++) {
                        float x = processing.PI / 180.0f * (float) i;
                        float y = sin(x);
                        processing.vertex(x * 10, y);
                    }
                    for (int i = 180; i >= 0; i--) {
                        float x = processing.PI / 180.0f * (float) i;
                        float y = -1 * (sin(x));
                        processing.vertex(x, y);
                    }
                }
                processing.endShape();
            }processing.popMatrix();
        }
    }
}