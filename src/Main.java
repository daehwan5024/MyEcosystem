/*
Right Click : Creates food (green dot)
Left Click : Creates Predator (red dot)
Wheel Click : Creates Oscillator (green grass)
 */

import processing.core.PApplet;

public class Main extends PApplet {
    public static PApplet processing;
    public static void main(String[] args) {
        PApplet.main("Main", args);
    }
    public void settings() {
        size(800, 800);
    }
    World world;
    static boolean simpleGraphics;
    public void setup() {
        processing = this;
        simpleGraphics = false;
        world = new World(20);
        frameRate(20);
    }
    public void draw() {
        background(255);
        world.run();
        fill(0);
        text("Press 's' to view SimpleMode", 30, height-16);
        text("Predator : "+(world.predator), 30, height-32);
        text("Food : "+(world.food), 30, height-48);
        text("Oscillator : "+(world.oscillator), 30, height-64);
    }
    public void mousePressed() {
        if(mouseButton == LEFT) {
            world.born(mouseX, mouseY, 1);
        } else if (mouseButton == RIGHT) {
            world.born(mouseX, mouseY, 0);
        } else if(mouseButton == CENTER) {
            world.born(mouseX, mouseY, 2);
        }
    }
    public void mouseDragged() {

    }
    public void keyPressed() {
        if (key == 's') {
            changeGraphicsMode();
        }
    }
    void changeGraphicsMode() {
        simpleGraphics =! simpleGraphics;
    }
}

