package omhscsc.entities;

import java.awt.Graphics;

//Generic enemy; has mostly default methods from Enemy
import omhscsc.state.GameStateState;
import omhscsc.util.Hitbox;

public class NormalEnemy extends Enemy {
	//Tbh we need a name for this
	public NormalEnemy(double mH, Hitbox h) {
		super(mH,h);
	}
	public void tick(GameStateState g) {
	}
	public void render(Graphics g, int x, int y, float scale) {
		super.render(g, x, y,scale);
	}
	public void attack() {
	}
}
