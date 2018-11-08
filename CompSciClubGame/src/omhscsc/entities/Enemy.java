package omhscsc.entities;

import omhscsc.util.Hitbox;

public abstract class Enemy extends LivingEntity {
	
	//private boolean isBoss;
	
	//private double tickCounter;
	//What is that for again^
	
	public Enemy(double mH, Hitbox h){
		super(h);
		maxHealth = mH;
		health = maxHealth;
	}
	public void takeDmg(double dmg) {
		health-=dmg;
	}
	
	public double getCurrentHp(){
		return health;
	}
	public double getMaxHp(){
		return maxHealth;
	}
	
	

}
