/**
 * Achievement.java
 *
 * Created by Jonodonozym on Jul 2, 2017 4:05:40 PM
 * Copyright � 2017. All rights reserved.
 * 
 * Last modified on Jul 7, 2017 8:41:34 AM
 */

package farmKing.achievements;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import farmKing.crops.Farm;
import farmKing.upgrades.Upgrade;

/**
 * Abstract class for achievements
 * Each achievement has a name, description, icon, upgrade and an isAchieved flag
 * Important methods include the checkAchieved abstract method and the doFirework method, which shoots off a firework
 * the getters and setters can be overridden for more complicated implementations (like AchievementSeries)
 *
 * Each achievement needs but a single instance for the whole server, and the flags should be handled on a per-player basis
 * elsewhere
 * @author Jonodonozym
 */
public abstract class StaticAchievement {	
	// static field for the firework effect
	private static final FireworkEffect fwe;
	static{
		List<Color> c = new ArrayList<Color>();
			c.add(Color.LIME);
		fwe = FireworkEffect.builder().flicker(true).withColor(c).withFade(c).with(Type.BALL_LARGE).trail(true).build();
	}
	
	// fields
	protected final String name, description;
	protected final Material icon;
	protected final Upgrade[] upgrades;
	
	/**
	 * Basic constructor for the Achievement class
	 * @param name
	 * @param description
	 * @param icon
	 * @param upgrade
	 */
	public StaticAchievement(String name, String description, Material icon, Upgrade[] upgrades){
		this.name = name;
		this.description = description;
		this.icon = icon;
		this.upgrades = upgrades;
	}
	
	/**
	 * Special constructor for Achievements with a single upgrade
	 * @param name
	 * @param description
	 * @param icon
	 */
	public StaticAchievement(String name, String description, Material icon, Upgrade upgrade){
		this(name, description, icon, new Upgrade[]{upgrade});
	}
	
	/**
	 * Special constructor for Achievements that doesn't have an upgrade
	 * @param name
	 * @param description
	 * @param icon
	 */
	public StaticAchievement(String name, String description, Material icon){
		this(name, description, icon, new Upgrade[0]);
	}

	/**
	 * Abstract method that returns true if the criteria for being achieved is met
	 * @param f
	 * @return
	 */
	protected abstract boolean checkAchieved(Farm f);
	
	/**
	 * Shoots off a firework above a player's head
	 * @param p
	 */
	public void doFirework(Farm f){
		if (!f.owner.isOnline())
			return;
		
		Player p = (Player) f.owner;
		Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation(), org.bukkit.entity.EntityType.FIREWORK);
		FireworkMeta fwm = fw.getFireworkMeta();
		fwm.addEffect(fwe);
		fwm.setPower(3);
		fw.setFireworkMeta(fwm);
		
		p.sendMessage(ChatColor.GOLD+"| Achievement '"+getDescription()+"' Unlocked!");
		for (Upgrade u: getUpgrades()){
			p.sendMessage(ChatColor.GOLD+"| Reward: "+u.getDescription());
		}
	}
	
	// getters
	public String getName(){ return name; }
	public String getDescription(){ return description; }
	public Material getIcon(){ return icon; }
	public Upgrade[] getUpgrades(){ return upgrades; }
}
