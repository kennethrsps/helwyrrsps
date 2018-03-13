package com.rs.network.protocol.codec.decode;

import com.rs.Protocol;
import com.rs.Settings;
import com.rs.network.Session;
import com.rs.stream.InputStream;

public final class ClientPacketsDecoder extends Decoder {

    public ClientPacketsDecoder(Session connection) {
    	super(connection);
    }

    @Override
    public final void decode(Session session, InputStream stream) {
		session.setDecoder(-1);
		int packetId = stream.readUnsignedByte();
		switch (packetId) {
		case 14:
		    decodeLogin(stream);
		    break;
		case 15:
		    decodeGrab(stream);
		    break;
		default:
		    session.getChannel().close();
		    break;
		}
    }

    private final void decodeGrab(InputStream stream) {
		int size = stream.readUnsignedByte();
		if (stream.getRemaining() < size) {
		    session.getChannel().close();
		    System.err.println("Incorrect size");
		    return;
		}
		session.setEncoder(0);
		if (stream.readInt() != Settings.SUB_REVISION || stream.readInt() != Settings.REVISION) {
		    session.setDecoder(-1);
		    session.getGrabPackets().sendOutdatedClientPacket();
		    System.err.println("Outdated data");
		    return;
		}
		if (!stream.readString().equals(Protocol.GRAB_SERVER_TOKEN)) {
		    session.getChannel().close();
		    System.err.println("Wrong server token");
		    return;
		}
		session.setDecoder(1);
		session.getGrabPackets().sendStartUpPacket();
    }

    private final void decodeLogin(InputStream stream) {
		if (stream.getRemaining() != 0) {
		    session.getChannel().close();
		    return;
		}
		session.setDecoder(2);
		session.setEncoder(1);
		session.getLoginPackets().sendStartUpPacket();
    }
}
