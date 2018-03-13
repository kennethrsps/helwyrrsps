package com.rs.game.player.controllers;

import java.util.concurrent.TimeUnit;

import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.MapBuilder;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.barrelchest.BarrelChest;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

import mysql.impl.NewsManager;

public class BarrelchestController extends Controller {

    private static enum Stages {
	LOADING, RUNNING, DESTROYING
    }

    public static final WorldTile OUTSIDE = new WorldTile(4610, 5130, 0);
    private int[] boundChuncks;
    private Stages stage;
    private boolean logoutAtEnd;

    public boolean spawned;

    /*
     * logout or not. if didnt logout means lost, 0 logout, 1, normal, 2 tele
     */
    public void exitCave() {
		stage = Stages.DESTROYING;
		player.setNextWorldTile(new WorldTile(3804, 2844, 0));
		player.setForceMultiArea(false);
		player.reset();
		removeControler();
		CoresManager.slowExecutor.schedule(new Runnable() {
		    @Override
		    public void run() {
		    	MapBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8, 8);
		    }
		}, 1200, TimeUnit.MILLISECONDS);
    }

    @Override
    public void forceClose() {
	/*
	 * shouldnt happen
	 */
	if (stage != Stages.RUNNING)
	    return;
	exitCave();
    }

    public int getCurrentWave() {
	if (getArguments() == null || getArguments().length == 0)
	    return 0;
	return (Integer) getArguments()[0];
    }

    public WorldTile getSpawnTile() {
	return getWorldTile(12, 5);
    }

    /*
     * gets worldtile inside the map
     */
    public WorldTile getWorldTile(int mapX, int mapY) {
    	return new WorldTile(boundChuncks[0] * 8 + mapX, boundChuncks[1] * 8 + mapY, 0);
    }

    public void loadCave() {
		stage = Stages.LOADING;
		player.lock(); // locks player
		boundChuncks = MapBuilder.findEmptyChunkBound(2, 2);
		MapBuilder.copyAllPlanesMap(475, 355, boundChuncks[0], boundChuncks[1], 8);
		player.setNextWorldTile(getWorldTile(12, 5));
		player.setForceMultiArea(true);
		player.unlock(); // unlocks player
		stage = Stages.RUNNING;
		startWave();
    }

    @Override
    public boolean login() {
	loadCave();
	return false;
    }

    /*
     * return false so wont remove script
     */
    @Override
    public boolean logout() {
	/*
	 * only can happen if dungeon is loading and system update happens
	 */
	if (stage != Stages.RUNNING)
	    return false;
	exitCave();
	return false;

    }

    @Override
    public void magicTeleported(int type) {
	exitCave();
    }

    @Override
    public void moved() {
	if (stage != Stages.RUNNING)
	    return;
    }

    public void nextWave() {
		setCurrentWave(getCurrentWave() + 1);
		if (logoutAtEnd) {
		    player.logout(false);
		    return;
		}
		startWave();
    }

    @Override
    public boolean processButtonClick(int interfaceId, int componentId,
	    int slotId, int packetId) {
	if (stage != Stages.RUNNING)
	    return false;
	return true;
    }

    @Override
    public boolean processItemTeleport(WorldTile toTile) {
	return false;
    }

    @Override
    public boolean processMagicTeleport(WorldTile toTile) {
	return false;
    }

    /**
     * return process normaly
     */
    @Override
    public boolean processObjectClick1(WorldObject object) {
	if (object.getId() == 22119) {
	    if (stage != Stages.RUNNING)
		return false;
	    exitCave();
	    return false;
	}
	return false;
    }

    @Override
    public boolean processObjectTeleport(WorldTile toTile) {
	return false;
    }

    @Override
    public boolean sendDeath() {
	player.lock(7);
	player.stopAll();
	WorldTasksManager.schedule(new WorldTask() {
	    int loop;

	    @Override
	    public void run() {
		if (loop == 0) {
		    player.setNextAnimation(new Animation(836));
		} else if (loop == 1) {
		    player.getPackets().sendGameMessage(
			    "Oh dear you have died!");
		} else if (loop == 3) {
		    player.reset();
		    exitCave();
		    player.setNextAnimation(new Animation(-1));
		} else if (loop == 4) {
		    player.getPackets().sendMusicEffect(90);
		    stop();
		}
		loop++;
	    }
	}, 0, 1);
	return false;
    }

    public void setCurrentWave(int wave) {
		if (getArguments() == null || getArguments().length == 0)
		    this.setArguments(new Object[1]);
		getArguments()[0] = wave;
    }

    @Override
    public void start() {
    	loadCave();
    }

    public void startWave() {
		if (stage != Stages.RUNNING)
		    return;
		new BarrelChest(5666, getSpawnTile(), this);
    }

    public void win() {
		if (stage != Stages.RUNNING)
		    return;
		/** Low chance of receiving the barrelchest anchor **/
		if (Utils.getRandom(100) == 0) {
			player.addItem(new Item(10887));
    		World.sendWorldMessage(Colors.orange + "<shad=000000><img=6>News: "
    				+ player.getDisplayName() + " received a Barrelchest anchor from Barrelchest.", false);
			new Thread(new NewsManager(player, "<b><img src=\"./bin/images/news/drop.png\" height=17> "
					+ player.getDisplayName()+" received a Barrelchest anchor from Barrelchest.")).start();
		}
		player.sendMessage("You've killed a total of "+Colors.red+player.increaseKillStatistics("barrelchest", true)
				+ "</col> x "+Colors.red+"Barrelchest's</col>.", true);
		player.addMoney(Utils.random(5000, 100000));
		exitCave();
    }
}