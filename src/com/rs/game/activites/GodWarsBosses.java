package com.rs.game.activites;

import com.rs.game.npc.NPC;

public final class GodWarsBosses {


	/**
	 * Checks if the NPC is at the GodWars Dungeon.
	 * 
	 * @param npc
	 *            The NPC to check.
	 * @return if is in the dungeon.
	 */
	public static boolean isAtGodwars(NPC npc) {
		if (npc.getBossInstance() != null)
			return true;
		int destX = npc.getX();
		int destY = npc.getY();
		return /* South West */(destX >= 2817 && destY >= 5210 &&
		/* North East */destX <= 2954 && destY <= 5371);
	}
}