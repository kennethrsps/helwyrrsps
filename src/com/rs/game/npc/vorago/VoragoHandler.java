package com.rs.game.npc.vorago;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.rs.Settings;
import com.rs.cores.CoresManager;
import com.rs.game.Entity;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Logger;

public final class VoragoHandler {

	private static final List<Player> players = Collections.synchronizedList(new ArrayList<Player>());
	
	public static int getPlayersCount() {
		return players.size();
	}
	
	public static List<Player> getPlayers() {
		return players;
	}

	public static void addPlayer(Player player) {
		synchronized (players) {
		    players.add(player);
		    if (players.size() == (Settings.DEBUG ? 1 : 4))
		    	startFight();
		}
	}

	public static void removePlayer(Player player) {
		synchronized (players) {
		    players.remove(player);
		    if (players.size() == 0)
		    	endFight();
		}
	}

	public static ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>(players.size());
		for (Player player : players) {
			if (player == null || player.isDead() || player.hasFinished() || !player.isRunning())
				continue;
			possibleTarget.add(player);
		}
		return possibleTarget;
	}
	
	 /**
     * Starts vorago.
     */
    public static void startFight() {
    	WorldTasksManager.schedule(new WorldTask() {
    	    int stage = 0;

    	    @Override
    	    public void run() {
	    		if (players.isEmpty()) {
	    		    //Just incase.
	    		    if (vorago != null) {
	    		    	vorago.finish();
	    		    	Logger.log("Vorago force finished!");
	    		    }
	    		    stop();
	    		    return;
	    		}
	    		if (stage == 0)
	    			vorago = new Vorago(17182, new WorldTile(3104, 6112, 0));
	    		else if (stage == 3) {
	    			vorago.setCantInteract(false);
	    		    stop();
	    		}
	    		stage++;
    	    }
    	}, 5, 2);
    }
    
    public static Vorago vorago;
    
    /**
     * Ends vorago NPC.
     */
    public static void endFight() {
		synchronized (players) {
		    if (vorago != null) {
		    	vorago.finish();
		    	vorago = null;
		    }
		    //Just incase.
		    if (vorago != null) {
		    	vorago.finish();
		    	Logger.log("vorago force finished!");
		    }
		    if (players.isEmpty())
		    	return;
		    
		    CoresManager.slowExecutor.schedule(new Runnable() {
	
				@Override
				public void run() {
				    try {
						if (players.isEmpty() || vorago != null)
						    return;
						startFight();
				    } catch (Throwable e) {
				    	e.printStackTrace();
				    }
				}
		    }, 1, TimeUnit.MINUTES);
		}
    }
    
    

	/**
	 * Vorago challenger.
	 */
	public static boolean startedChallenge;
}