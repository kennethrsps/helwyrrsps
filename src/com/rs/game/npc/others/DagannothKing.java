package com.rs.game.npc.others;

import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;

/**
 * Creates the Dagannoth King NPC's.
 * @author Zeus
 */
public class DagannothKing extends NPC {

	/**
	 * Generated serial UID.
	 */
	private static final long serialVersionUID = 2820530565457808392L;

	public DagannothKing(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		if(id != 2883){
			setForceAgressive(true);
		}
		setForceTargetDistance(96);
	}
	
	/**
	 * 2881 supreme
	 * 2882 prime
	 * 2883 rex
	 */
	@Override
	public void handleIngoingHit(Hit hit) {
		if (hit.getLook() != HitLook.MELEE_DAMAGE 
				&& hit.getLook() != HitLook.RANGE_DAMAGE 
				&& hit.getLook() != HitLook.MAGIC_DAMAGE)
			return;
		if ((getId() == 2881 && hit.getLook() != HitLook.MELEE_DAMAGE)
				|| (getId() == 2882 && hit.getLook() != HitLook.RANGE_DAMAGE)
				|| (getId() == 2883 && hit.getLook() != HitLook.MAGIC_DAMAGE))
			hit.setDamage(hit.getDamage() / 5);
		super.handleIngoingHit(hit);
	}
}