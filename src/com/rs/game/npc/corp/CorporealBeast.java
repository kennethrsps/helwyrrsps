package com.rs.game.npc.corp;

import com.rs.game.Entity;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;

@SuppressWarnings("serial")
public class CorporealBeast extends NPC {

    private DarkEnergyCore core;

    public CorporealBeast(int id, WorldTile tile, int mapAreaNameHash,
	    boolean canBeAttackFromOutOfArea, boolean spawned) {
    	super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setCapDamage(1000);
		setLureDelay(3000);
		setForceTargetDistance(64);
		setForceFollowClose(false);
		setIntelligentRouteFinder(true); 
    }

    @Override
    public double getMagePrayerMultiplier() {
    	return 0.6;
    }

    @Override
    public void processNPC() {
		super.processNPC();
		if (isDead())
		    return;
		int maxhp = getMaxHitpoints();
		if (maxhp > getHitpoints() && getPossibleTargets().isEmpty()) {
		    setCapDamage(1000);
		    setHitpoints(maxhp);
		}
    }

    public void removeDarkEnergyCore() {
		if (core == null)
		    return;
		core.finish();
		core = null;
    }

    @Override
    public void sendDeath(Entity source) {
		super.sendDeath(source);
		if (core != null)
		    core.sendDeath(source);
    }

    public void spawnDarkEnergyCore() {
		if (core != null)
		    return;
		core = new DarkEnergyCore(this);
    }
}