package com.rs.game.npc.vorago;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.controllers.VoragoController;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

public class Vorago extends NPC {

	/**
	 * The Generated serial UID.
	 */
	private static final long serialVersionUID = -4516079809768658492L;

	/**
	 * Inits Vorago and constructs his parameters.
	 * 
	 * @param id
	 *            Voragos ID.
	 * @param tile
	 *            The WorldTile.
	 */
	public Vorago(int id, WorldTile tile) {
		super(id, tile, -1, true, true);
		setNoDistanceCheck(true);
		setIntelligentRouteFinder(true);
		setForceFollowClose(true);
		setCapDamage(Utils.random(1010, 1090));
		setFreezeImmune(true);
		setForceTargetDistance(124);
		phase = 1;
		VoragoController.voragoPhase = 1;
		attackCount = 0;
		reflecting = false;
		reflectee = null;
		rotation = 0;
	}

	/**
	 * Vorago's phase.
	 */
	public int phase;
	
	/**
	 * Vorago's attack count.
	 */
	public int attackCount;
	
	/**
	 * If Vorago is reflecting damage.
	 */
	public boolean reflecting;
	
	/**
	 * Voragos reflect enemy.
	 */
	public Entity reflectee;
	
	@Override
	public void handleIngoingHit(Hit hit) {
		if (isCantInteract())
			return;
		super.handleIngoingHit(hit);
		if (hit.getSource() != null && reflecting == true && reflectee != null) {
			int recoil = (int) (hit.getDamage());
			if (recoil > 0) {
				Hit hit2 = new Hit(this, recoil, hit.getLook());
				hit.setDamage(0);
				if (hit.isCriticalHit())
					hit2.setCriticalMark();
				reflectee.applyHit(hit2);
			}
		}
		if (isCantInteract())
			hit.setDamage(0);
	}

	@Override
	public void sendDeath(Entity source) {
		if (phase < 5) {
			setCantInteract(true);
			getCombat().removeTarget();
			endPhase();
		} else {
			VoragoController.voragoPhase = 0;
			setNextAnimation(new Animation(20352));
			setNextGraphics(new Graphics(4036));
			setCantInteract(true);

			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					World.spawnObject(new WorldObject(84960, 10, 0, new WorldTile(dtX - 2, dtY - 2, dtZ)));
					drop();
					reset();
					finish();
					VoragoHandler.endFight();
					stop();
				}
			}, 17);
		}
	}

	/**
	 * Center coordinates for each phase.
	 */
	public WorldTile Centre[] = { new WorldTile(3106, 6106, 0), new WorldTile(3104, 6048, 0),
			new WorldTile(3039, 6048, 0), new WorldTile(3040, 5984, 0), new WorldTile(3100, 5982, 0) };
	
	/**
	 * Getting of center tiles.
	 */
	int dtX = Centre[4].getX() + 11;// Drop tile centre x coordinate
	int dtY = Centre[4].getY() + 2;// Drop tile centre y coordinate
	int dtZ = Centre[4].getPlane();// Drop tile centre z coordinate
	
	/**
	 * Waterfall special attack.
	 */
	public int rotation;
	public WorldTile wfTile;
	public WorldTile[] safeTiles = { wfTile, wfTile, wfTile, wfTile, wfTile, wfTile };
	public List<Player> safePlayers = Collections.synchronizedList(new ArrayList<Player>());

	/**
	 * Gets the centre coordinate.
	 * 
	 * @return WorldTile
	 */
	public WorldTile getCentre() {
		return Centre[phase - 1];
	}

	/**
	 * Gets the random jump coordinate for vorago.
	 * 
	 * @return The WorldTile.
	 */
	public WorldTile getRandomJump() {
		int a = (int) (-6 + Math.random() * 12);
		int b = (int) (-6 + Math.random() * 12);
		int c = (int) (-2 + Math.random() * 4);
		int d = (int) (-2 + Math.random() * 4);
		if (phase < 5)
			return new WorldTile(getCentre().getX() + a, getCentre().getY() + b, 0);
		else
			return new WorldTile(getCentre().getX() - 6 + c, getCentre().getY() + 2 + d, 0);
	}

	/**
	 * Ends Voragos phase.
	 */
	public void endPhase() {
		final WorldTile A = getRandomJump();
		final WorldTile C = getRandomJump();
		setNextFaceWorldTile(A);
		attackCount = 0;
		setNextAnimationForce(new Animation(20365));
		World.sendGraphics(this, new Graphics(4037), A);

		WorldTasksManager.schedule(new WorldTask() {
			int count = 0;

			@Override
			public void run() {
				
				if (count == 2) {
					setNextWorldTile(C);
					setNextAnimationForce(new Animation(20367));
					setNextGraphics(new Graphics(4020));
					sendLandHit();
				}
				if (count == 5) {
					phase++;
					VoragoController.voragoPhase++;
					setNextAnimationForce(new Animation(20323));
					for (Entity p : VoragoHandler.getPlayers()) {
						p.setNextAnimation(new Animation(20402));
						((Player) p).sendMessage(Colors.red+"[Vorago] The ground breaks from under you.");
					}
				}
				if (count == 6) {
					for (Entity p : VoragoHandler.getPlayers()) {
						p.setNextWorldTile(new WorldTile(getRandomJump()));
						p.setNextAnimation(new Animation(20401));
					}
					setHitpoints(getMaxHitpoints());
				}
				if (count == 7) {
					setNextWorldTile(getCentre());
					setNextAnimationForce(new Animation(20367));
					setNextGraphics(new Graphics(4020));
					setCantInteract(false);
					stop();
				}
				count++;
			}
		}, 0, 1);
	}

	/**
	 * Applies damage if Vorago lands on a target.
	 */
	public void sendLandHit() {
		for (Entity target : getPossibleTargets()) {
			if (Utils.colides(this, target)) {
				target.applyHit(new Hit(Vorago.this, Utils.random(800, 900), HitLook.CRITICAL_DAMAGE));
				if (target instanceof Player)
					target.setNextAnimation(new Animation(10070));
			}
		}
	}
}