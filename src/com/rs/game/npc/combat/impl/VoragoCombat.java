package com.rs.game.npc.combat.impl;

import java.util.ArrayList;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.vorago.Vorago;
import com.rs.game.npc.vorago.VoragoHandler;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * Handles Voragos combat script.
 * 
 * @author Zeus
 */
public class VoragoCombat extends CombatScript {

	@Override
	public int attack(final NPC npc, final Entity target) {
		final Vorago vorago = (Vorago) npc;
		final ArrayList<Entity> possibleTargets = vorago.getPossibleTargets();
	    
		if (Utils.random(5) == 0) {
			Entity newTarget = possibleTargets.get(Utils.getRandom(possibleTargets.size() - 1));
			vorago.setTarget(newTarget);
		}
		//Prevent attacks when locked
		if (vorago.isCantInteract() || vorago.isLocked())
			return 2;
		switch (vorago.phase) {
		case 1:
			if (vorago.attackCount == 3) {
				sendRedBomb(vorago);
				vorago.attackCount = 0;
			} else {
				sendMeleeAttack(vorago, target);
				vorago.attackCount++;
			}
			break;
		case 2:
			if (vorago.attackCount == 13) {
				sendMeleeAttack(vorago, target);
				vorago.attackCount = 0;
			} else if (vorago.attackCount == 9) {
				sendRedBomb(vorago);
				vorago.attackCount++;
			} else if (vorago.attackCount == 6) {
				sendSmash(vorago);
				vorago.attackCount++;
			} else if (vorago.attackCount == 3) {
				sendReflect(vorago);
				vorago.attackCount++;
			} else {
				sendMeleeAttack(vorago, target);
				vorago.attackCount++;
			}
			break;
		case 3:
			if (vorago.attackCount == 16) {
				sendMeleeAttack(vorago, target);
				vorago.attackCount = 0;
			} else if (vorago.attackCount == 12) {
				sendBlueBomb(vorago);
				vorago.attackCount++;
			} else if (vorago.attackCount == 9) {
				sendRedBomb(vorago);
				vorago.attackCount++;
			} else if (vorago.attackCount == 6) {
				sendSmash(vorago);
				vorago.attackCount++;
			} else if (vorago.attackCount == 3) {
				sendReflect(vorago);
				vorago.attackCount++;
			} else {
				sendMeleeAttack(vorago, target);
				vorago.attackCount++;
			}
			break;
		case 4:
			if (vorago.attackCount == 21) {
				sendWaterfall(vorago);
				vorago.attackCount = 0;
			} else if (vorago.attackCount == 18) {
				sendBlueBomb(vorago);
				vorago.attackCount++;
			} else if (vorago.attackCount == 15) {
				sendWaterfall(vorago);
				vorago.attackCount++;
			} else if (vorago.attackCount == 12) {
				sendRedBomb(vorago);
				vorago.attackCount++;
			} else if (vorago.attackCount == 9) {
				sendWaterfall(vorago);
				vorago.attackCount++;
			} else if (vorago.attackCount == 6) {
				sendReflect(vorago);
				vorago.attackCount++;
			} else if (vorago.attackCount == 3) {
				sendWaterfall(vorago);
				vorago.attackCount++;
			} else {
				sendMeleeAttack(vorago, target);
				vorago.attackCount++;
			}
			break;
		case 5:
			if (vorago.attackCount == 15) {
				sendBlueBomb(vorago);
				vorago.attackCount = 0;
			} else if (vorago.attackCount == 12) {
				sendMeleeAttack(vorago, target);
				vorago.attackCount++;
			} else if (vorago.attackCount == 9) {
				sendRedBomb(vorago);
				vorago.attackCount++;
			} else if (vorago.attackCount == 6) {
				sendSmash(vorago);
				vorago.attackCount++;
			} else if (vorago.attackCount == 3) {
				sendReflect(vorago);
				vorago.attackCount++;
			} else {
				sendMeleeAttack(vorago, target);
				vorago.attackCount++;
			}
			break;
		}
		return 7;
	}

	/**
	 * Executes Voragos melee attack.
	 * 
	 * @param vorago
	 *            Vorago.
	 * @param target
	 *            The Target.
	 */
	private void sendMeleeAttack(Vorago vorago, Entity target) {
		if (!Utils.isOnRange(vorago, target, 2))
			sendRedBomb(vorago);
		else {
			vorago.setNextAnimation(new Animation(20355));
			for (Entity t : VoragoHandler.getPlayers()) {// Hits all players in range
				if (Utils.isOnRange(vorago, t, 2)) {
					Hit hit = getMeleeHit(vorago, getRandomMaxHit(vorago, 650, NPCCombatDefinitions.MELEE, t));
					delayHit(vorago, 0, t, hit);
				}
			}
		}
	}

	/**
	 * Executes Voragos red bomb attack.
	 * 
	 * @param npc
	 *            Vorago.
	 */
	private void sendRedBomb(Vorago vorago) {
		final NPCCombatDefinitions defs = vorago.getCombatDefinitions();
		for (Player t : VoragoHandler.getPlayers()) {
			if (t == null || t.isDead() || t.hasFinished())
				continue;
			vorago.setNextAnimation(new Animation(20371));
			vorago.setNextGraphics(new Graphics(4022));
			World.sendProjectile(vorago.getMiddleWorldTile(), t, 4023, 60, 30, 45, 10, 5, 0);

			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					t.setNextGraphics(new Graphics(4024));
					delayHit(vorago, 0, t, getMagicHit(vorago,
							getRandomMaxHit(vorago, defs.getMaxHit(), NPCCombatDefinitions.MAGE, t)));
					stop();
				}
			}, 1);
		}
	}

	/**
	 * Executes Voragos blue bomb attack.
	 * 
	 * @param npc
	 *            Vorago.
	 */
	private void sendBlueBomb(Vorago vorago) {
		final NPCCombatDefinitions defs = vorago.getCombatDefinitions();
		for (Player t : VoragoHandler.getPlayers()) {
			if (t == null || t.isDead() || t.hasFinished())
				continue;
			vorago.setNextAnimation(new Animation(20371));
			vorago.setNextGraphics(new Graphics(4015));
			World.sendProjectile(vorago.getMiddleWorldTile(), t, 4016, 60, 30, 45, 10, 5, 0);

			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					t.setNextGraphics(new Graphics(4017));
					delayHit(vorago, 0, t, getRangeHit(vorago,
							getRandomMaxHit(vorago, defs.getMaxHit(), NPCCombatDefinitions.RANGE, t)));
					stop();
				}
			}, 1);
		}
	}

	/**
	 * Executes Voragos smash attack.
	 * 
	 * @param vorago
	 *            Vorago.
	 */
	private void sendSmash(Vorago vorago) {
		final NPCCombatDefinitions defs = vorago.getCombatDefinitions();
		for (Player t : VoragoHandler.getPlayers()) {
			if (t == null || t.isDead() || t.hasFinished())
				continue;
			vorago.setNextAnimation(new Animation(20363));
			vorago.setNextGraphics(new Graphics(4018));

			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					delayHit(vorago, 0, t, getMeleeHit(vorago,
							getRandomMaxHit(vorago, defs.getMaxHit(), NPCCombatDefinitions.MELEE, t)));
					stop();
				}
			}, 1);
		}
	}
	
	/**
	 * Executes Voragos reflect/gravity field - attack.
	 * @param vorago Vorago.
	 */
	private void sendReflect(Vorago vorago) {
		vorago.reflecting = true;
		vorago.setNextAnimation(new Animation(20319));
		vorago.setNextGraphics(new Graphics(4011));
		Entity targets[] = VoragoHandler.getPlayers().toArray(new Player[VoragoHandler.getPlayers().size()]);
		vorago.reflectee = (Entity) targets[Utils.random(targets.length)];
		vorago.reflectee.setNextGraphics(new Graphics(4012));
		((Player) vorago.reflectee).sendMessage("<col=ff0000>Vorago channels incoming damage to you.</col>");
		for (Player p : VoragoHandler.getPlayers()) {
			if (p == vorago.reflectee)
				continue;
			else
				p.sendMessage("<col=f0ff00>Vorago reflects incoming damage to one of his foes.</col");
		}
		
		WorldTasksManager.schedule(new WorldTask() {
			private int count = 0;

			@Override
			public void run() {
				//Just incase the reflectee dies/x-logs
				if (vorago.reflectee == null || vorago.reflectee.hasFinished() || vorago.reflectee.isDead()) {
					vorago.reflectee = null;
					for (Player p : VoragoHandler.getPlayers())
						p.sendMessage("<col=15ff00>Vorago has released his mental link with their foe.</col");
					stop();
					return;
				}
				if (count == 16) {
					vorago.reflectee.setNextGraphics(new Graphics(2670));
					((Player) vorago.reflectee).sendMessage("<col=15ff00>Vorago releases his mental link with you.</col");
					for (Player p : VoragoHandler.getPlayers()) {
						if (p == vorago.reflectee)
							continue;
						else
							p.sendMessage("<col=15ff00>Vorago has released his mental link with their foe.</col");
					}
					vorago.reflecting = false;
					stop();
					return;
				}
				count++;
			}
		}, 0, 1);
	}
	
	static int x;
	static int y;
	static int z;
	
	private void sendWaterfall(Vorago vorago) {
		if (vorago.getHitpoints() < 10000) {
			sendRedBomb(vorago);
			return;
		}
		vorago.setCantInteract(true);
		vorago.setLocked(true);
		vorago.setNextWorldTile(vorago.getCentre());
		vorago.getCombat().removeTarget();
		vorago.resetWalkSteps();
		vorago.setCantFollowUnderCombat(true);
		vorago.setFreezeDelay(100000);
		// for each place it sets the safe tiles
		switch (Utils.random(3)) {
		case 0:// North-West
			x = vorago.Centre[3].getX() - 12;
			y = vorago.Centre[3].getY() + 8;
			z = vorago.Centre[3].getPlane();
			vorago.safeTiles[0] = new WorldTile(x, y + 1, z);
			vorago.safeTiles[1] = new WorldTile(x, y + 2, z);
			vorago.safeTiles[2] = new WorldTile(x, y + 3, z);
			vorago.safeTiles[3] = new WorldTile(x + 1, y + 2, z);
			vorago.safeTiles[4] = new WorldTile(x + 1, y + 3, z);
			vorago.safeTiles[5] = new WorldTile(x + 2, y + 3, z);
			vorago.rotation = 0;
			vorago.wfTile = new WorldTile(x, y, z);
			break;
		case 1:// North-East
			x = vorago.Centre[3].getX() + 8;// 3048
			y = vorago.Centre[3].getY() + 8;// 5992
			z = vorago.Centre[3].getPlane();
			vorago.safeTiles[0] = new WorldTile(x + 1, y + 3, z);
			vorago.safeTiles[1] = new WorldTile(x + 2, y + 2, z);
			vorago.safeTiles[2] = new WorldTile(x + 2, y + 3, z);
			vorago.safeTiles[3] = new WorldTile(x + 3, y + 1, z);
			vorago.safeTiles[4] = new WorldTile(x + 3, y + 2, z);
			vorago.safeTiles[5] = new WorldTile(x + 3, y + 3, z);
			vorago.rotation = 1;
			vorago.wfTile = new WorldTile(x, y, z);
			break;
		case 2:// South-East
			x = vorago.Centre[3].getX() + 8;
			y = vorago.Centre[3].getY() - 12;
			z = vorago.Centre[3].getPlane();
			vorago.safeTiles[0] = new WorldTile(x + 1, y, z);
			vorago.safeTiles[1] = new WorldTile(x + 2, y, z);
			vorago.safeTiles[2] = new WorldTile(x + 3, y, z);
			vorago.safeTiles[3] = new WorldTile(x + 2, y + 1, z);
			vorago.safeTiles[4] = new WorldTile(x + 3, y + 1, z);
			vorago.safeTiles[5] = new WorldTile(x + 3, y + 2, z);
			vorago.wfTile = new WorldTile(x, y, z);
			vorago.rotation = 0;
			break;
		case 3:// South-West
			x = vorago.Centre[3].getX() - 12;
			y = vorago.Centre[3].getY() - 12;
			z = vorago.Centre[3].getPlane();
			vorago.safeTiles[0] = new WorldTile(x, y, z);
			vorago.safeTiles[1] = new WorldTile(x + 1, y, z);
			vorago.safeTiles[2] = new WorldTile(x + 2, y, z);
			vorago.safeTiles[3] = new WorldTile(x, y + 1, z);
			vorago.safeTiles[4] = new WorldTile(x, y + 2, z);
			vorago.safeTiles[5] = new WorldTile(x + 1, y + 1, z);
			vorago.rotation = 1;
			vorago.wfTile = new WorldTile(x, y, z);
			break;
		}
		for (Player p : VoragoHandler.getPlayers())
			(p).sendMessage("<col=ff0000>Vorago starts to charge all his power into a massive fire attack!</col");
		vorago.setNextAnimation(new Animation(20322));
		vorago.setNextGraphics(new Graphics(4014, 0, 200));
		vorago.safePlayers.removeAll(vorago.safePlayers);
		World.spawnObjectTemporary(new WorldObject(84967, 11, vorago.rotation, vorago.wfTile), 10200);

		WorldTasksManager.schedule(new WorldTask() {
			private int loop = 0;
			@Override
			public void run() {
				if (loop == 10) {
					vorago.setNextAnimation(new Animation(20383));
					vorago.setNextGraphics(new Graphics(4013));
					for (Player p : VoragoHandler.getPlayers()) {
	
						vorago.setCantInteract(false);
						vorago.setLocked(false);
						vorago.setCantFollowUnderCombat(false);
						vorago.setFreezeDelay(0);
						for (int i = 0; i < vorago.safeTiles.length; i++) {
							if (p.getX() == vorago.safeTiles[i].getX() && p.getY() == vorago.safeTiles[i].getY()) {
								vorago.safePlayers.add(p);
								break;
							}
						}
						// If the player isn't behind the waterfall they get hit
						if (!vorago.safePlayers.contains(p))
							p.applyHit(new Hit(vorago, Utils.random(901, 1002), HitLook.CRITICAL_DAMAGE));
					}
					stop();
				} else {
					loop++;
					vorago.setCantInteract(true);
					vorago.setLocked(true);
					vorago.setNextWorldTile(vorago.getCentre());
					vorago.getCombat().removeTarget();
					vorago.resetWalkSteps();
					vorago.setCantFollowUnderCombat(true);
					vorago.setFreezeDelay(100000);
					vorago.setNextAnimation(new Animation(20322));
					vorago.setNextGraphics(new Graphics(4014, 0, 200));
				}
				return;
			}
		}, 0, 1);
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { 17182 };
	}
}