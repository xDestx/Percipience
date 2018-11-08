package omhscsc.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import omhscsc.Game;
import omhscsc.GameObject;
import omhscsc.RenderableGameObject;
import omhscsc.state.GameStateState;
import omhscsc.util.Hitbox;
import omhscsc.util.Location;
import omhscsc.util.Velocity;
import omhscsc.world.WorldObject;

public abstract class LivingEntity extends Entity {

	protected double health,maxHealth,jumpHeight,speed,damage;
	protected boolean canJump;
	protected Velocity velocity;
	protected float relativeExistanceRate;
	/*
	 * Haven't finished, feel free to work on it. --Test comment--
	 */
	
	public LivingEntity(Hitbox h) {
		super(h);
		canJump = true;
		relativeExistanceRate = 1f;
	}
	
	public LivingEntity(int x, int y, int w, int h)
	{
		super(x,y,w,h);
		canJump = true;
		relativeExistanceRate = 1f;
	}
	
	public boolean canJump()
	{
		return canJump;
	}
	
	public void setCanJump(boolean jump) {
		this.canJump=jump;
	}

	
	@Override
	public void tick(GameStateState s)
	{
		super.tick(s);
		//Always super tick (Exceptions may happen)
		defaultMovement(s);
		//fixCollisions(s);
		//Removing fix collisions, add it wherever needed in subclasses
	}
	
	public void render(Graphics g, int x, int y, float scale)
	{
		super.render(g,x,y,scale);
		//place holder
	}
	
	
	public float getTimeRate() {
		return this.relativeExistanceRate;
	}
	
	public void setTimeRate(float f) {
		this.relativeExistanceRate = Math.abs(f);
	}

	protected void fixCollisions(GameStateState gs)
	{
		//Fixes collisions with boxes
		//If we want to change to collision to entities and boxes, change w to wo, remove the try/catch and wo declaration
		for(RenderableGameObject w: gs.getCurrentWorld().getGameObjects()){
			try {
				WorldObject wo = (WorldObject)w;
				if(wo.getTopBound().intersects(getBottomBound())){
					
					velocity.setY(0);
					hitbox.setY(wo.getHitbox().getLocation().getY()-hitbox.getBounds().getHeight());
					setCanJump(true);
				}
				if(wo.getHitbox().getBounds().intersects(getLeftBound())){
					//System.out.println("Colliding left");
					velocity.setX(0);
					hitbox.setX(wo.getHitbox().getLocation().getX()+wo.getHitbox().getBounds().getWidth());
				}
				if(wo.getHitbox().getBounds().intersects(getRightBound())){
				//	System.out.println("Colliding right");
					velocity.setX(0);
					hitbox.setX(wo.getHitbox().getLocation().getX()-hitbox.getBounds().getWidth());
				}
				if(wo.getHitbox().getBounds().intersects(getTopBound())){
				//	System.out.println("Colliding top");
					velocity.setY(0);
					hitbox.setY(wo.getHitbox().getLocation().getY()+wo.getHitbox().getBounds().getHeight());
				}
			} catch (ClassCastException e) {}
			catch (Exception e) {e.printStackTrace();}
		
		}
	}
	
	protected void defaultMovement(GameStateState s)
	{
		Location last = this.getLocation().clone();
		//double change = velocity.getX();
		velocity.addY(((double)Game.GRAVITY/(double)Game.TPS) * Game.getTimeRate() * this.getTimeRate());
		hitbox.addY((velocity.getY()/(double)Game.TPS)* Game.getTimeRate() * this.getTimeRate());
		hitbox.addX((velocity.getX()/(double)Game.TPS)* Game.getTimeRate() * this.getTimeRate());
		velocity.addX(((velocity.getX()*-.9)/(double)Game.TPS)* Game.getTimeRate() * this.getTimeRate());

		Location now = this.getLocation().clone();
		//System.out.println(change + "...but then - now is " + (now.getX() - last.getX()));
		
		checkChange(last, now, velocity.getX(), velocity.getY(), s);
		//Checking change BEFORE setting velocity to 0 because it could make checkChange infinite.
		if(velocity.getX() < 50)
			velocity.setX(0);
	}
	
	/**
	 * This method is to check the change from the last location to the most recent location. If there is a Box somewhere in between the last and most recent, it will do a more accurate change of location.
	 * @param lastLoc
	 * @param newLoc
	 * @param xchange The x velocity
	 * @param ychange The y velocity
	 */
	protected void checkChange(Location lastLoc, Location newLoc, double xchange, double ychange, GameStateState gs) {
		double w = (newLoc.getX() - lastLoc.getX());
		//System.out.println("Check change width: " + w);
		if(Math.abs(w) < 1 || w == 0)
			w=1 * (w < 0 ? -1:1);
		int x = (int)((w < 0) ? newLoc.getX():lastLoc.getX());
		
		w = (w < 0) ? w*-1:w;
		/*Find the width of the change, if it's negative (moving left) set the starting x value of the
		*hit box to the new loc instead of the old one.
		*If w less than 0 make positive
		*/
		/*
		 * Same thing here, if h < 0 (moving up), change starting y
		 */
		double h = (int)(newLoc.getY() - lastLoc.getY());
		if(Math.abs(h) < 1 || h == 0)
			h=1 * (h < 0 ? -1:1);
		int y = (int)((h < 0) ? lastLoc.getY():newLoc.getY());
		Hitbox changeBox = new Hitbox((int)w,(int)h,x,y);
		//System.out.println(x + " " + y + " " + w + " " + h);
		for (GameObject rgo : gs.getCurrentWorld().getGameObjects()) {
			try {
				WorldObject wo = (WorldObject)rgo;
				if(wo.getHitbox().getBounds().intersects(changeBox.getBounds())) {
					//Need to be more specific with movement because there should have been a collision in the distance between then and now, but we missed it.
					//int xdiff = (int)(wo.getHitbox().getLocation().getX() - changeBox.getLocation().getX());
				//	int ydiff = (int)(wo.getHitbox().getLocation().getY() - changeBox.getLocation().getY());
					
					preciseMovement(lastLoc,xchange, ychange, wo);
					break;
				}
			} catch (ClassCastException e) {}
			catch (Exception e) {e.printStackTrace();}
		}
		
	}
	
	protected boolean isColliding(WorldObject wo) {
		return (wo.getHitbox().getBounds().intersects(this.getHitbox().getBounds()));
	}
	
	protected void preciseMovement(Location startingLocation, double xv, double yv, WorldObject wo)
	{
		//Using xv and yv because of player (literally). The players input is a seperate thing, and because of that this is required
		//double currentX = 0;
		//double currentY = 0;
		//System.out.print("USING PRECISE ");
//		int t = 0;
		do {
			double xChange = xv/500.0;
			double yChange = yv/500.0;
			velocity.addY(yChange);
			hitbox.addX(xChange);
//			t++;
			//currentX+=xChange;
			//currentY+=yChange;
		} while (!isColliding(wo));
	//	System.out.print(t + " times\n");
	}
	
	@Override
	public Location getLocation() {
		return hitbox.getLocation();
	}
	
	
	public void drawHitBoxes(Graphics g, int x, int y, float scale)
	{
		g.setColor(Color.ORANGE);
		Graphics2D g2 = (Graphics2D)g;
		g2.draw(new Rectangle((int)x, (int)y+(int)(5*scale), (int)(10 * scale), (int)((this.hitbox.getBounds().getHeight()-10) * scale)));
		g2.setColor(Color.RED);
		g2.draw(new Rectangle((int)x + (int)((hitbox.getBounds().getWidth()-10)*scale), (int)(y+(5*scale)), (int)(10 * scale), (int)((this.hitbox.getBounds().getHeight()-10) * scale)));
		g2.setColor(Color.PINK);
		g2.draw(new Rectangle((int)(x+(scale*5)), (int)y, (int)((this.hitbox.getBounds().getWidth()-10) * scale), (int)(10 * scale)));
		g2.setColor(Color.BLUE);
		g2.draw(new Rectangle((int)(x+(scale*5)), (int)(y+((hitbox.getBounds().getHeight()-10)*scale)), (int)((this.hitbox.getBounds().getWidth()-10)*scale), (int)(10 * scale)));
		g2.setColor(Color.GREEN);
		g2.draw(new Rectangle((int)x,(int)y,(int)(getHitbox().getBounds().getWidth() * scale), (int)(getHitbox().getBounds().getHeight() * scale)));
		
	}
	
	public void drawHealthBar(Graphics g, int x, int y, float scale) {
		
		g.setColor(Color.white);
		g.drawRect(x, y-(int)(15 * scale), (int)(100*scale), (int)(10*scale));
		g.setColor(Color.green);
		g.fillRect(x+(int)(1*scale)-1, y-(int)(14*scale)-1, (int)((100 * (this.getCurrentHp() / this.getMaxHp())) * scale), (int)(10 * scale));
		
	}
	

	public abstract void attack();
	
	
	public double getCurrentHp() {
		return health;
	}

	public double getMaxHp() {
		return maxHealth;
	}
	

	public void takeDmg(double dmg) {
		health -= dmg;
	}

	

	public void jump()
	{
		if(!canJump)
			return;
		velocity.setY(-jumpHeight*400);
		hitbox.addY(-5);
		canJump = false;
	}
}
