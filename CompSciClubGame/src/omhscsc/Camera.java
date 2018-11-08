package omhscsc;

import omhscsc.state.GameStateState;
import omhscsc.util.Anchor;
import omhscsc.util.Constants;
import omhscsc.util.Hitbox;
import omhscsc.util.Location;

/**
 * The Camera class is used to view the world. Anything inside the view bounds will be rendered, and anything out of it will not be
 * @author xDest 
 *
 */
public class Camera extends GameObject {

	//Hitbox / Area of camera (View box)
	private Hitbox box;
	//What the camera is hooked too
	private Anchor a;
	private boolean smoothFollow;
	private float followSpeed;
	
	/**
	 * Create a new Camera with a Location and size.
	 * @param l The location of the camera
	 * @param w The camera view width
	 * @param h The camera view height.
	 */
	public Camera(Location l, int w, int h)
	{
		box = new Hitbox(w,h,l);
		smoothFollow = true;
		followSpeed = 0.2f;
	}
	
	/**
	 * Get the hitbox of this Camera.
	 * @return The hitbox
	 */
	public Hitbox getHitbox()
	{
		return box;
	}
	
	/**
	 * Check if the Hitbox b interests the camera view bounds.
	 * @param b The hitbox
	 * @return True, if it intersects. False, if else.
	 */
	public boolean intersects(Hitbox b)
	{
		return b.getBounds().intersects(box.getBounds());
	}

	/**
	 * Set the camera anchor. The camera will center the Anchor in its bounds.
	 * @param a The anchor 
	 */
	public void setAnchor(Anchor a)
	{
		this.a = a;
	}
	/**
	 * Get the width of the camera.
	 * @return The width
	 */
	public double getWidth() {
		return this.getHitbox().getBounds().getWidth();
	}
	
	/**
	 * Get the height of the camera.
	 * @return The height
	 */
	public double getHeight() {
		return this.getHitbox().getBounds().getHeight();
	}
	
	/**
	 * Returns the scale of the camera relative to the size of the current window size.
	 * @return The scale
	 */
	public float getScale() {
		return (float)(Game.getWidth()/getWidth());
	}
	
	/**
	 * Remove the anchor from the camera so it doesn't move.
	 */
	public void removeAnchor()
	{
		a = null;
	}

	
	@Override
	public void tick(GameStateState s) {
		if(a==null)
			return;
		/*
		 * Set's the anchor to the center of the camera.
		 */
		if(!smoothFollow) {
			box.setX(a.getCenterLocation().getX() - (int)((double)this.getHitbox().getBounds().getWidth()/2.0));
			box.setY(a.getCenterLocation().getY() - (int)((double)this.getHitbox().getBounds().getHeight()*2/3.0));
		} else {
			//camera should reach in 1/2 second
			double xDist = a.getCenterLocation().getX()-this.getHitbox().getCenterLocation().getX();
			double yDist = a.getCenterLocation().getY()-(this.getHitbox().getCenterLocation().getY()+(this.getHeight()/6));
			double newX, newY;
			if(Math.abs(xDist) <= 5) {
				newX = a.getCenterLocation().getX() - (int)((double)this.getHitbox().getBounds().getWidth()/2.0);
			} else {
				xDist/=(followSpeed*Game.TPS);
				newX = box.getLocation().getX()+xDist;
			}
			if(Math.abs(yDist) <= 5) {
				newY = a.getCenterLocation().getY() - (int)((double)this.getHeight()*2/3.0);
			} else {
				yDist/=(followSpeed*Game.TPS);
				newY = box.getLocation().getY()+yDist;
			}
			box.setX(newX);
			box.setY(newY);
		}
	}

	/**
	 * Manually change the scale. This resizes the camera.
	 * @param d The new scale
	 */
	public void setScale(float d) {
		//System.out.println("Scale: " + d);
		if (d > Constants.MAXCAMSCALE || d < Constants.MINCAMSCALE) return; //Keeps camera scale at reasonable levels
		int width = (int)(Game.getWidth() / d);
		int height = (int)(Game.getHeight() / d);
		box.setWidth(width);
		box.setHeight(height);
	}
	
}
