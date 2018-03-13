package com.rs.game.player.content;

import com.rs.game.WorldObject;
import com.rs.game.player.Player;

public class PossessedObject extends WorldObject {

    private static final long serialVersionUID = -543150569322118775L;

    private Player owner;

    public PossessedObject(Player owner, int id, int type, int rotation, int x, int y, int plane) {
		super(id, type, rotation, x, y, plane);
		setOwner(owner);
    }

    public Player getOwner() {
    	return owner;
    }

    public void setOwner(Player owner) {
    	this.owner = owner;
    }
}
