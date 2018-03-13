package com.rs.game.npc.others;

import java.util.ArrayList;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.controllers.BorkController;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * Handles and Initializes Bork.
 * @author Zeus
 */
public class Bork extends NPC {

	/**
	 * The Generated serial UID.
	 */
	private static final long serialVersionUID = 7598477828536008806L;

	/**
	 * Ork Legion messages.
	 */
	private static final String[] MINION_MESSAGES = {"Hup! 2.... 3.... 4!", "Resistance is futile!", "We are the collective!", "Form a triangle!"};
	
	private boolean spawnedMinions;
	private final BorkController controller;
	private NPC[] borkMinion;
	
	public Bork(WorldTile tile, BorkController controller) {
		super(7134, tile, -1, true, true);
		setCantInteract(true);
		setDirection(Utils.getAngle(1, 0));
		setNoDistanceCheck(true);
		setForceAgressive(true);
		this.controller = controller;
	}

	public boolean isSpawnedMinions() {
		return spawnedMinions;
	}

	@Override
	public void drop() {
		int size = getSize();
		ArrayList<Item> drops = new ArrayList<Item>();
		drops.add(new Item(532, 1)); //big bones
		drops.add(new Item(995, 4000 + Utils.random(20000))); //coins
		drops.add(new Item(12163, 5)); //blue charm
		drops.add(new Item(12160, 6)); //crimson charm
		drops.add(new Item(12159, 7)); //green charm
		drops.add(new Item(12158, 8)); //gold charm
		drops.add(new Item(1618, 1)); //uncut diamond
		drops.add(new Item(1620, 3)); //uncut ruby
		drops.add(new Item(1622, 6)); //uncut emerald
		drops.add(new Item(1624, 9)); //uncut sapphire
		for (Item item : drops) {
			if (item.getDefinitions().isStackable())
				item.setAmount(item.getAmount() * 5);
			World.addGroundItem(item, new WorldTile(getCoordFaceX(size), getCoordFaceY(size), getPlane()));
		}
	}

	public void setMinions() {
		borkMinion = new NPC[3];
		for (int i = 0; i < borkMinion.length; i++) {
			borkMinion[i] = World.spawnNPC(7135, new WorldTile(this, 1), -1, true, true);
			borkMinion[i].setNextForceTalk(new ForceTalk("For bork!"));
			borkMinion[i].setNextGraphics(new Graphics(1314));
			borkMinion[i].setTarget(controller.getPlayer());
			borkMinion[i].setForceMultiArea(true);
		}
		setNextForceTalk(new ForceTalk("Destroy the intruder, my Legions!"));
		spawnedMinions = true;
		setCantInteract(false);
		setTarget(controller.getPlayer());
	}
	
	@Override
	public void processNPC() {
		if(borkMinion != null && Utils.random(20) == 0) {
			for(NPC n : borkMinion) {
				if(n == null || n.isDead())
					continue;
				n.setNextForceTalk(new ForceTalk(MINION_MESSAGES[Utils.random(MINION_MESSAGES.length)]));
			}
		}
		super.processNPC();
	}
	
	@Override
	public void sendDeath(Entity source) {
		if (!spawnedMinions) {
			setHitpoints(1);
			return;
		}
		controller.killBork();
		for (NPC n : borkMinion) {
			if (n == null || n.isDead())
				continue;
			n.sendDeath(source);
		}
		super.sendDeath(source);
	}
	
	public void spawnMinions() {
		setCantInteract(true);
		setNextForceTalk(new ForceTalk("Come to my aid, brothers!"));
		setNextAnimation(new Animation(8757));
		setNextGraphics(new Graphics(1315));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				controller.spawnMinions();
			}
		}, 2);
	}
	
    public static boolean atBork(WorldTile tile) {
    	if ((tile.getX() >= 3083 && tile.getX() <= 3120) && (tile.getY() >= 5522 && tile.getY() <= 5550))
    		return true;
    	return false;
    }
}