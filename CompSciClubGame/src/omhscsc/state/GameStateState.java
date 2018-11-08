package omhscsc.state;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;

import omhscsc.Camera;
import omhscsc.Game;
import omhscsc.RenderableGameObject;
import omhscsc.entities.Player;
import omhscsc.graphic.Renderable;
import omhscsc.util.Anchor;
import omhscsc.util.Hitbox;
import omhscsc.util.Location;
import omhscsc.util.Task;
import omhscsc.world.World;

public class GameStateState extends GameState {

	private Set<Task>go;
	private Set<Renderable>re;
	private Camera camera;
	private static World currentWorld;
	private Player player;
	private boolean key_a = false, key_d = false, key_w = false;
	
	public GameStateState(Game g)
	{
		super(g);
		go = new HashSet<Task>();
		re = new HashSet<Renderable>();
		currentWorld = World.getWorld(0);
		camera = new Camera(new Location(0,0), Game.getWidth(), Game.getHeight());
		camera.setScale(1.3f);
		player = new Player("Freddy",new Hitbox(70, 70,new Location(-200,-100)));
		camera.setAnchor((Anchor)player);
		placePlayerInWorld(currentWorld);
	}
	
	/*
	 * ---------- IMPORTANT ------------
	 * Players now have to exist in a world to be rendered / ticked.
	 */
	
	
	/**
	 * Place the player into the world. If the world is not the current world, that world will become the new current world.
	 * @param w
	 */
	public void placePlayerInWorld(World w) {
		if(w!=currentWorld) {
			currentWorld.removeEntity(player);
			currentWorld = w;
		}
		player.setLocation(w.getSpawnPoints()[0]);
		w.addEntity(player);
	}
	
	/**
	 * Place the player into the world at the specified spawn index. If the world is not the current world, that world will become the new current world.
	 * @param w
	 * @param spawnIndex
	 */
	public void placePlayerInWorldWithSpawnPoint(World w, int spawnIndex) {
		if(w!=currentWorld) {
			currentWorld.removeEntity(player);
			currentWorld = w;
		}
		player.setLocation(w.getSpawnPoints()[spawnIndex]);
		w.addEntity(player);
	}
	
	public void addTask(Task o)
	{
		go.add(o);
	}
	
	public void addRenderable(Renderable r)
	{
		re.add(r);
	}
	
	public void removeRenderable(Renderable r)
	{
		re.remove(r);
	}
	
	public void removeTask(Task g)
	{
		go.remove(g);
	}
	
	private final Font keyFont = new Font("Arial", 0, 96);
	
	@Override
	public void render(Graphics g) {
		try {
			float scale = camera.getScale();
			//currentWorld.renderBackground(g, camera.getHitbox(), scale);
			currentWorld.render(g, camera, scale);
			for (RenderableGameObject wo : currentWorld.getGameObjects())
			{
				if(camera.intersects(wo.getHitbox()))
				{
					int xoff = (int)((wo.getHitbox().getBounds().getX() - camera.getHitbox().getBounds().getX()) * scale);
					int yoff = (int)((wo.getHitbox().getBounds().getY() - camera.getHitbox().getBounds().getY()) * scale);
					wo.render(g, xoff, yoff, scale);
				}
			}
			
			for (Renderable r : re)
			{
				if(camera.intersects(r.getHitbox()))
				{
					int xoff = (int)((r.getHitbox().getBounds().getX() - camera.getHitbox().getBounds().getX()) * scale);
					int yoff = (int)((r.getHitbox().getBounds().getY() - camera.getHitbox().getBounds().getY()) * scale);
					r.render(g, xoff, yoff, scale);
				}
			}
			int xoff = (int)((camera.getHitbox().getBounds().getX() - camera.getHitbox().getBounds().getX()) * scale);
			int yoff = (int)((camera.getHitbox().getBounds().getY() - camera.getHitbox().getBounds().getY()) * scale);
			if(player.drawHitboxEnabled())
				g.drawRect(xoff, yoff, (int)(camera.getWidth() * scale),(int)(camera.getHeight() * scale));
			//The camera^
			//System.out.println(camera.getHeight() + "   "+ camera.getScale());
			Color l = g.getColor();
			Font lF = g.getFont();
			g.setFont(keyFont);
			if(key_a) {
				g.setColor(Color.green);
				g.drawRect(10, 120, 100, 100);
				g.drawString("a", 20, 200);
			} else {
				g.setColor(Color.red);
				g.drawRect(10, 120, 100, 100);
				g.drawString("a", 20, 200);
			}
			if(key_d) {
				g.setColor(Color.green);
				g.drawRect(230, 120, 100, 100);
				g.drawString("d", 240, 200);
			} else {
				g.setColor(Color.red);
				g.drawRect(230, 120, 100, 100);
				g.drawString("d", 240, 200);
			}
			if(key_w) {
				g.setColor(Color.green);
				g.drawRect(120, 10, 100, 100);
				g.drawString("w", 130, 80);
			} else {
				g.setColor(Color.red);
				g.drawRect(120, 10, 100, 100);
				g.drawString("w", 130, 80);
			}
			g.setColor(l);
			g.setFont(lF);
		} catch (ConcurrentModificationException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void tick() {
		try {
			HashSet<Task> completedTasks = new HashSet<Task>();
			for (Task g : go)
			{
				g.tick(this);
				if(g.isTaskComplete())
				{
					completedTasks.add(g);
				}
			
			}
			for(Task t : completedTasks)
				go.remove(t);
			
			completedTasks.clear();
			completedTasks = null;
			//This shouldn't be needed because every time this method ends all local variables are garbage collected I think?
			currentWorld.tick(this);
			camera.tick(this);
		} catch (ConcurrentModificationException e)
		{
			//When you don't want to deal
			//Hopefully Tasks should make this unneeded. Input should ==NEVER== directly add a game object 
			e.printStackTrace();
			
			//Continue
		}
		/*
		int choice = (int)Math.random();
		if (choice == 0) {
			addStatic();
		} else {
			addMoving();
		}
		*/
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_A)
		{
			player.setLeftHeld(true);
			key_a=true;
		}
		if (e.getKeyCode() == KeyEvent.VK_D)
		{
			player.setRightHeld(true);
			key_d = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_W)
		{
			player.jump();
			key_w=true;
		}
		if(e.getKeyCode() == KeyEvent.VK_F) {
			camera.setScale(1.0f);
		}
		if(e.getKeyCode() == KeyEvent.VK_G) { 
			camera.setScale(0.5f);
		}
		if(e.getKeyCode() == KeyEvent.VK_H) {
			camera.setScale(2.0f);
		}
		if(e.getKeyCode() == KeyEvent.VK_E) {
			camera.setScale((float)(camera.getScale()-0.1));
		}
		if(e.getKeyCode() == KeyEvent.VK_R) {
			camera.setScale((float)(camera.getScale()+0.1));
		}
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			//Usually switch to pause state, but not complete;
			this.getGame().setGameState(this.getGame().getGameState(MainMenuState.class));
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_A)
		{
			player.setLeftHeld(false);
			key_a=false;
		}
		if (e.getKeyCode() == KeyEvent.VK_D)
		{
			player.setRightHeld(false);
			key_d = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_W) {
			key_w = false;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public World getCurrentWorld() {
		return currentWorld;
	}

	public Player getPlayer() {
		return player;
	}

	public Set<Task> getTasks() {
		return go;
	}

	@Override
	public void lostFocus() {
		//Player quit
	}

	@Override
	public void gainedFocus() {
		//Player started
		//Use this to renitialize the world and other things
	}
}
	/*
	public void addStatic() {
		
		ArrayList<Integer> randx = new ArrayList<Integer>();
		ArrayList<Integer> randy = new ArrayList<Integer>();
		randx.add((int)((5009)*Math.random()+10));
	    randy.add((int)((500)*Math.random()));
		int randh = (int)((200)*Math.random()+10);
		int randw = (int)((200)*Math.random()+10);
		int xa = (int)(randx.get(randx.size()-1)-(0.5*randw));
		int xb = (int)(randx.get(randx.size()-1)+(0.5*randw));
		int xc;
		int xd;
		int iShit = 0;
		for (int i = 0; i <= World.wobjectx.size()-1; i++) {
			xc = (int)(World.wobjectx.get(i)-(0.5*World.wobjectw.get(i)));
			xd = (int)(World.wobjectx.get(i)+(0.5*World.wobjectw.get(i)));
			if (
					((xb < xd) && (xb > xc))
					&& ((randx.get(randx.size()-1) < xd)  
					&& (randx.get(randx.size()-1) >xc))
					&& ((xa < xd) && (xa > xc))) {
				iShit++;
			}
		}
		
		if (!(iShit>0)) {
			currentWorld.addWorldObject(new Box(currentWorld, randx.get(randx.size()-1), randy.get(randy.size()-1), randh, randw, Color.white));
	    }

	*/