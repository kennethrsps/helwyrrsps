package com.rs.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;

public final class ObjectSpawns {

	private static BufferedReader in;

	public static final void init() {
		if (!new File("data/map/packedSpawns").exists())
			packObjectSpawns();
	}

	private static final void packObjectSpawns() {
		Logger.log("ObjectSpawns", "Packing object spawns...");
		if (!new File("data/map/packedSpawns").mkdir())
			throw new RuntimeException("Couldn't create packedSpawns directory.");
		try {
			in = new BufferedReader(new FileReader("data/map/unpackedSpawnsList.txt"));
			while (true) {
				String line = in.readLine();
				if (line == null)
					break;
				if (line.startsWith("//"))
					continue;
				String[] splitedLine = line.split(" - ");
				if (splitedLine.length != 2)
					throw new RuntimeException("Invalid Object Spawn line: " + line);
				String[] splitedLine2 = splitedLine[0].split(" ");
				String[] splitedLine3 = splitedLine[1].split(" ");
				if (splitedLine2.length != 3 || splitedLine3.length != 4)
					throw new RuntimeException("Invalid Object Spawn line: " + line);
				int objectId = Integer.parseInt(splitedLine2[0]);
				int type = Integer.parseInt(splitedLine2[1]);
				int rotation = Integer.parseInt(splitedLine2[2]);
				
				WorldTile tile = new WorldTile(Integer.parseInt(splitedLine3[0]), Integer.parseInt(splitedLine3[1]),
						Integer.parseInt(splitedLine3[2]));
				addObjectSpawn(objectId, type, rotation, tile.getRegionId(), tile,
						Boolean.parseBoolean(splitedLine3[3]));
			}
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public static final void loadObjectSpawns(int regionId) {
		File file = new File("data/map/packedSpawns/" + regionId + ".os");
		if (!file.exists())
			return;
		try {
			RandomAccessFile in = new RandomAccessFile(file, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0, channel.size());
			while (buffer.hasRemaining()) {
				int objectId = buffer.getShort() & 0xffff;
				int type = buffer.get() & 0xff;
				int rotation = buffer.get() & 0xff;
				int plane = buffer.get() & 0xff;
				int x = buffer.getShort() & 0xffff;
				int y = buffer.getShort() & 0xffff;
				@SuppressWarnings("unused")
				boolean cliped = buffer.get() == 1;
				World.spawnObject(new WorldObject(objectId, type, rotation, x, y, plane));
			}
			channel.close();
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static final void addObjectSpawn(int objectId, int type, int rotation, int regionId, WorldTile tile,
			boolean cliped) {
		try {
			DataOutputStream out = new DataOutputStream(
					new FileOutputStream("data/map/packedSpawns/" + regionId + ".os", true));
			out.writeShort(objectId);
			out.writeByte(type);
			out.writeByte(rotation);
			out.writeByte(tile.getPlane());
			out.writeShort(tile.getX());
			out.writeShort(tile.getY());
			out.writeBoolean(cliped);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Add's custom object spawns after world has initialized.
	 */
	public static void addCustomSpawns() {
		//NEW HOME MAZCAB
		//BANK (MAZCAB)
		World.spawnObject(new WorldObject(104414, 10, 3, new WorldTile(4314, 874, 0)), true);
		World.spawnObject(new WorldObject(24914, 10, 3, new WorldTile(4315, 874, 0)), true);
		World.spawnObject(new WorldObject(24914, 10, 3, new WorldTile(4316, 874, 0)), true);
		World.spawnObject(new WorldObject(101924, 10, 3, new WorldTile(4317, 874, 0)), true);
		//Fountain (MAZCAB)
		World.spawnObject(new WorldObject(91174, 10, 0, new WorldTile(4315, 864, 0)), true);
		//thieving stalls (MAZCAB)
		World.spawnObject(new WorldObject(4874, 10, 1, new WorldTile(4304, 860, 0)), true);
		World.spawnObject(new WorldObject(4875, 10, 1, new WorldTile(4304, 861, 0)), true);
		World.spawnObject(new WorldObject(4876, 10, 1, new WorldTile(4304, 862, 0)), true);
		World.spawnObject(new WorldObject(4877, 10, 1, new WorldTile(4304, 863, 0)), true);
		World.spawnObject(new WorldObject(4878, 10, 1, new WorldTile(4304, 864, 0)), true);
		//fire (MAZCAB)
		World.spawnObject(new WorldObject(70765, 10, 0, new WorldTile(4314, 868, 0)), true);		
		World.spawnObject(new WorldObject(70765, 10, 0, new WorldTile(4317, 868, 0)), true);
		//House Portal (MAZCAB)
		World.spawnObject(new WorldObject(15477, 10, 1, new WorldTile(4327, 861, 0)), true);
		//Score Board (MAZCAB)
		World.spawnObject(new WorldObject(30205, 10, 3, new WorldTile(4303, 869, 0)), true);
		//Altar (MAZCAB)
		World.spawnObject(new WorldObject(51061, 10, 1, new WorldTile(4315, 851, 0)), true);
		//shooting star stuff (MAZCAB)
		World.spawnObject(new WorldObject(38669, 10, 1, new WorldTile(4327, 854, 0)), true);
		World.spawnObject(new WorldObject(7092, 10, 0, new WorldTile(4327, 852, 0)), true);
		//Ports (MAZCAB)
		World.spawnObject(new WorldObject(93249, 10, 3, new WorldTile(4320, 850, 0)), true);
		//reaper portal (MAZCAB)
		World.spawnObject(new WorldObject(92120, 10, 0, new WorldTile(4311, 851, 0)), true);
		//ckey chest (MAZCAB)
		World.spawnObject(new WorldObject(2079, 10, 0, new WorldTile(4314, 851, 0)), true);
		//ckey sign (MAZCAB)
		World.spawnObject(new WorldObject(31299, 10, 2, new WorldTile(4313, 851, 0)), true);
		//damage area
		World.spawnObject(new WorldObject(16118, 10, 0, new WorldTile(2143, 5525, 3)), true);
		//Kethsi's world gate
		World.spawnObject(new WorldObject(89742, 10, 0, new WorldTile(2367, 3353, 0)), true);
				
		//Harps in prifddinas
		World.spawnObject(new WorldObject(94059, 10, 0, new WorldTile(2135, 3335, 1)), true);
		World.spawnObject(new WorldObject(94059, 10, 2, new WorldTile(2129, 3335, 1)), true);
		World.spawnObject(new WorldObject(94059, 10, 3, new WorldTile(2132, 3338, 1)), true);
		World.spawnObject(new WorldObject(94059, 10, 1, new WorldTile(2132, 3332, 1)), true);
		
		//Lleyta
		World.spawnObject(new WorldObject(55309, 10, 3, new WorldTile(2309, 3172, 0)), true); //interdimensional portal
		World.spawnObject(new WorldObject(92120, 10, 0, new WorldTile(2347, 3181, 0)), true); //reaper portal
		World.spawnObject(new WorldObject(70765, 10, 0, new WorldTile(2334, 3171, 0)), true); //fire		
		World.spawnObject(new WorldObject(70765, 10, 0, new WorldTile(4382, 5918, 0)), true); //fire		
		World.spawnObject(new WorldObject(30205, 10, 1, new WorldTile(2353, 3157, 0)), true);
		World.spawnObject(new WorldObject(3166, 10, 1, new WorldTile(2354, 3158, 0)), true);
		World.spawnObject(new WorldObject(2562, 10, 3, new WorldTile(2343, 3172, 1)), true);

		//upstairs banks left
		World.spawnObject(new WorldObject(66667, 10, 1, new WorldTile(2348, 3179, 1)), true);
		World.spawnObject(new WorldObject(66666, 10, 1, new WorldTile(2348, 3180, 1)), true);
		World.spawnObject(new WorldObject(66665, 10, 1, new WorldTile(2348, 3181, 1)), true);	
		//upstairs banks right
		World.spawnObject(new WorldObject(66667, 10, 1, new WorldTile(2348, 3162, 1)), true);
		World.spawnObject(new WorldObject(66666, 10, 1, new WorldTile(2348, 3163, 1)), true);
		World.spawnObject(new WorldObject(66665, 10, 1, new WorldTile(2348, 3164, 1)), true);
		World.spawnObject(new WorldObject(15477, 10, 0, new WorldTile(2352, 3178, 1)), true);
		//furnace building (downstairs cooking area)
		World.spawnObject(new WorldObject(3044, 10, 3, new WorldTile(2340, 3155, 0)), true);
		World.spawnObject(new WorldObject(2079, 10, 3, new WorldTile(2340, 3180, 0)), true); //ckey chest
		World.spawnObject(new WorldObject(31299, 10, 0, new WorldTile(2341, 3181, 0)), true); //ckey sign
		World.spawnObject(new WorldObject(8772, 10, 2, new WorldTile(2335, 3157, 0)), true); //bench
		World.spawnObject(new WorldObject(3166, 10, 2, new WorldTile(2334, 3157, 0)), true); //nulls to take up space
		World.spawnObject(new WorldObject(3166, 10, 2, new WorldTile(2336, 3157, 0)), true); //nulls to take up space
		World.spawnObject(new WorldObject(2783, 10, 1, new WorldTile(2336, 3158, 0)), true); //anvil
		World.spawnObject(new WorldObject(2783, 10, 1, new WorldTile(2334, 3158, 0)), true); //anvil
		World.spawnObject(new WorldObject(24016, 10, 1, new WorldTile(2334, 3157,0 )), true);
		//thieving stalls upstairs
		World.spawnObject(new WorldObject(4874, 10, 1, new WorldTile(2352, 3165, 1)), true);
		World.spawnObject(new WorldObject(4875, 10, 1, new WorldTile(2353, 3165, 1)), true);
		World.spawnObject(new WorldObject(4876, 10, 1, new WorldTile(2354, 3165, 1)), true);
		World.spawnObject(new WorldObject(4877, 10, 1, new WorldTile(2355, 3165, 1)), true);
		World.spawnObject(new WorldObject(4878, 10, 1, new WorldTile(2356, 3165, 1)), true);
		//shooting star stuff
		World.spawnObject(new WorldObject(38669, 10, 3, new WorldTile(2323, 3162, 1)), true);
		World.spawnObject(new WorldObject(7092, 10, 0, new WorldTile(2326, 3161, 1)), true);
		//Ashdale
		World.spawnObject(new WorldObject(72695, 10, 0, new WorldTile(4070, 7279, 0)), true); //port portal
		World.spawnObject(new WorldObject(93249, 10, 1, new WorldTile(2332, 3165, 0)), true); //port portal
		
		  //Gold Zone
        //Divine Patch
        World.spawnObject(new WorldObject(87280, 10, 0, new WorldTile(2126, 6955, 0)), true);
        World.spawnObject(new WorldObject(87281, 10, 0, new WorldTile(2127, 6955, 0)), true);
        World.spawnObject(new WorldObject(87282, 10, 0, new WorldTile(2128, 6955, 0)), true);
        World.spawnObject(new WorldObject(36786, 10, 0, new WorldTile(2129, 6955, 0)), true);
        //Divine Patch
        World.spawnObject(new WorldObject(87271, 10, 0, new WorldTile(2130, 6955, 0)), true);
        World.spawnObject(new WorldObject(87272, 10, 0, new WorldTile(2131, 6955, 0)), true);
        World.spawnObject(new WorldObject(87273, 10, 0, new WorldTile(2132, 6955, 0)), true);
        World.spawnObject(new WorldObject(36786, 10, 0, new WorldTile(2133, 6955, 0)), true);
        //Divine Patch
        World.spawnObject(new WorldObject(87268, 10, 0, new WorldTile(2134, 6955, 0)), true);
        World.spawnObject(new WorldObject(87269, 10, 0, new WorldTile(2135, 6955, 0)), true);
        World.spawnObject(new WorldObject(36786, 10, 0, new WorldTile(2136, 6955, 0)), true);
        //Divine Patch
        World.spawnObject(new WorldObject(90229, 10, 0, new WorldTile(2137, 6955, 0)), true);
        World.spawnObject(new WorldObject(90230, 10, 0, new WorldTile(2138, 6955, 0)), true);
        World.spawnObject(new WorldObject(36786, 10, 0, new WorldTile(2139, 6955, 0)), true);
        //Divine Patch
        World.spawnObject(new WorldObject(87278, 10, 0, new WorldTile(2140, 6955, 0)), true);
        World.spawnObject(new WorldObject(87279, 10, 0, new WorldTile(2141, 6955, 0)), true);
        //Mineral Deposit
        World.spawnObject(new WorldObject(5999, 10, 0, new WorldTile(2161, 6951, 0)), true);
        World.spawnObject(new WorldObject(45076, 10, 0, new WorldTile(2161, 6953, 0)), true);
        World.spawnObject(new WorldObject(5999, 10, 0, new WorldTile(2161, 6955, 0)), true);
        World.spawnObject(new WorldObject(45076, 10, 0, new WorldTile(2161, 6957, 0)), true);
        //Magic Tree
        World.spawnObject(new WorldObject(1306, 10, 0, new WorldTile(2161, 6934, 0)), true);
        World.spawnObject(new WorldObject(1292, 10, 0, new WorldTile(2161, 6932, 0)), true);
        World.spawnObject(new WorldObject(1306, 10, 0, new WorldTile(2161, 6930, 0)), true);
        World.spawnObject(new WorldObject(1292, 10, 0, new WorldTile(2161, 6928, 0)), true);
        //Ore
        World.spawnObject(new WorldObject(2561, 10, 0, new WorldTile(2126, 6931, 0)), true);
        World.spawnObject(new WorldObject(2561, 10, 0, new WorldTile(2127, 6931, 0)), true);
        World.spawnObject(new WorldObject(2561, 10, 0, new WorldTile(2128, 6931, 0)), true);
        World.spawnObject(new WorldObject(36786, 10, 0, new WorldTile(2129, 6931, 0)), true);
        World.spawnObject(new WorldObject(92713, 10, 0, new WorldTile(2130, 6931, 0)), true);
        World.spawnObject(new WorldObject(92713, 10, 0, new WorldTile(2131, 6931, 0)), true);
        World.spawnObject(new WorldObject(92713, 10, 0, new WorldTile(2132, 6931, 0)), true);
        //Harp
        World.spawnObject(new WorldObject(94059, 10, 0, new WorldTile(2140, 6931, 0)), true);
        World.spawnObject(new WorldObject(94059, 10, 0, new WorldTile(2138, 6931, 0)), true);
        World.spawnObject(new WorldObject(94059, 10, 0, new WorldTile(2136, 6931, 0)), true);
        //Altar
        World.spawnObject(new WorldObject(51061, 10, 2, new WorldTile(2126, 6942, 0)), true);
        //Corrupted Seren Stone
        World.spawnObject(new WorldObject(94048, 10, 2, new WorldTile(2135, 6942, 0)), true);
        //death tele
        World.spawnObject(new WorldObject(92120, 10, 2, new WorldTile(2166, 6943, 0)), true);
    }
}