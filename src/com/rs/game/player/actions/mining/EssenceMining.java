package com.rs.game.player.actions.mining;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Animation;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.npc.others.randoms.LiquidGoldNymph;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.utils.Utils;

public class EssenceMining extends MiningBase {

    public static enum EssenceDefinitions {
	
    	Rune_Essence(1, 5, 1436, 1, Utils.random(3, 5)), 
    	Pure_Essence(30, 5, 7936, 1, Utils.random(3, 5));
    	
		private int level;
		private double xp;
		private int oreId;
		private int oreBaseTime;
		private int oreRandomTime;
	
		private EssenceDefinitions(int level, double xp, int oreId,
			int oreBaseTime, int oreRandomTime) {
		    this.level = level;
		    this.xp = xp;
		    this.oreId = oreId;
		    this.oreBaseTime = oreBaseTime;
		    this.oreRandomTime = oreRandomTime;
		}
	
		public int getLevel() {
		    return level;
		}
	
		public int getOreBaseTime() {
		    return oreBaseTime;
		}
	
		public int getOreId() {
		    return oreId;
		}
	
		public int getOreRandomTime() {
		    return oreRandomTime;
		}
	
		public double getXp() {
		    return xp;
		}
    }

    private WorldObject rock;
    private EssenceDefinitions definitions;

    public EssenceMining(WorldObject rock, EssenceDefinitions definitions) {
		this.rock = rock;
		this.definitions = definitions;
    }

    private void addOre(Player player) {
    	double xpBoost = 1.0;
    	xpBoost *= miningSuit(player);
    	player.getInventory().addItem(definitions.getOreId(), 1);
    	player.getSkills().addXp(Skills.MINING, definitions.getXp() * xpBoost);
    	String oreName = ItemDefinitions.getItemDefinitions(definitions.getOreId()).getName().toLowerCase();
    	player.getPackets().sendGameMessage("You mine some " + oreName + ".", true);
    }

    private boolean checkAll(Player player) {
		if (!hasPickaxe(player)) {
		    player.sendMessage("You need a pickaxe to mine this rock.");
		    return false;
		}
		if (!setPickaxe(player)) {
		    player.sendMessage("You don't have the required level to use this pickaxe.");
		    return false;
		}
		if (!hasMiningLevel(player))
		    return false;
		if (!player.getInventory().hasFreeSlots()) {
		    player.sendMessage("Inventory full. To make more room, sell, drop or bank something.");
		    return false;
		}
		return true;
    }

    private boolean checkRock(Player player) {
    	return World.containsObjectWithId(rock, rock.getId());
    }

    private int getMiningDelay(Player player) {
		int mineTimer = definitions.getOreBaseTime() - 
				player.getSkills().getLevel(Skills.MINING) - Utils.getRandom(pickaxeTime);
		if (mineTimer < 1 + definitions.getOreRandomTime())
		    mineTimer = 1 + Utils.getRandom(definitions.getOreRandomTime());
		mineTimer /= player.getAuraManager().getMiningAccurayMultiplier();
		if (player.getPerkManager().quarryMaster)
			mineTimer /= 1.33;
		return mineTimer;
    }

    private boolean hasMiningLevel(Player player) {
		if (definitions.getLevel() > player.getSkills().getLevel(Skills.MINING)) {
		    player.sendMessage("You need a mining level of " + definitions.getLevel()
				    + " to mine this rock.");
		    return false;
		}
		return true;
    }

    @Override
    public boolean process(Player player) {
    	setAnimationAndGFX(player);
    	if (Utils.random(750) == 0) {
    		new LiquidGoldNymph(new WorldTile(rock, 1), player);
    		player.sendMessage("<col=ff0000>A Liquid Gold Nymph emerges from the rock.");
    	}
    	return checkRock(player);
    }

    @Override
    public int processWithDelay(Player player) {
		addOre(player);
		if (!player.getInventory().hasFreeSlots()) {
		    player.setNextAnimation(new Animation(-1));
		    player.sendMessage("Inventory full. To make more room, sell, drop or bank something.");
		    return -1;
		}
		return getMiningDelay(player);
    }

    @Override
    public boolean start(Player player) {
		if (!checkAll(player))
		    return false;
		player.sendMessage("You swing your pickaxe at the rock..", true);
		setActionDelay(player, getMiningDelay(player));
		return true;
    }
    
    /**
     * XP modifier by wearing items.
     * @param player The player.
     * @return the XP modifier.
     */
    private double miningSuit(Player player) {
    	double xpBoost = 1.0;
    	if (player.getEquipment().getHatId() == 20789)
    		xpBoost *= 1.01;
    	if (player.getEquipment().getChestId() == 20791)
    		xpBoost *= 1.01;
    	if (player.getEquipment().getLegsId() == 20790)
    		xpBoost *= 1.01;
    	if (player.getEquipment().getBootsId() == 20788)
    		xpBoost *= 1.01;
    	if (player.getEquipment().getHatId() == 20789
    			&& player.getEquipment().getChestId() == 20791
    			&& player.getEquipment().getLegsId() == 20790
    			&& player.getEquipment().getBootsId() == 20788)
    		xpBoost *= 1.01;
    	return xpBoost;
    }
}