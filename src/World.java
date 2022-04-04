import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class World {
    PApplet processing = Main.processing;
    ArrayList<Agent> agents;
    int oscillator = 0;
    int food = 0;
    int predator = 0;
    World(int num) {
        agents = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            PVector l = new PVector(processing.random(processing.width),processing.random(processing.height));
            agents.add(new Oscillator(l));
            oscillator++;
        }
    }
    void born(float x, float y) {
        PVector l = new PVector(x,y);
        agents.add(new Fish(l));
    }
    void born(float x, float y, int type) {
        PVector l = new PVector(x, y);
        if(type == 0) {
            agents.add(new Food(l));
            food++;
        } else if(type == 1) {
            agents.add(new Predator(l));
            predator++;
        } else if(type == 2) {
            agents.add(new Oscillator(l));
            oscillator++;
        }
    }
    void run() {
        for (int i = agents.size()-1; i >= 0; i--) {
            Agent a = agents.get(i);
            a.run(agents);
            if (a.dead()) {
                if(a.type == 0) {
                    food--;
                } else if(a.type == 1) {
                    predator--;
                } else if(a.type == 100) {
                    oscillator--;
                }
                agents.remove(i);
            }
        }
    }
}
