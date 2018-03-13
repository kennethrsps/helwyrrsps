package com.rs.cache.loaders;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.rs.cache.Cache;
import com.rs.stream.InputStream;
import com.rs.utils.Logger;

public final class NPCDefinitions {

	private static final ConcurrentHashMap<Integer, NPCDefinitions> npcDefinitions = new ConcurrentHashMap<Integer, NPCDefinitions>();

	

	public int[] headIconSprites;
	int ambience;
	byte aByte3014;
	public int origonalId;
	public String name = "null";
	int[][] translations;
	public int[] models;
	public int[] headModels;
	public int renderEmote;
	short[] originalColours;
	public short[] replacementColours;
	public byte aByte3024;
	short[] originalTextures;
	public short[] replacementTextures;
	public byte[] aByteArray3027;
	public byte[] aByteArray3028;
	public int anInt3029;
	byte aByte3030;
	int[] cursorOps;
	byte aByte3032;
	public boolean aBool3033;
	public int attackCursor;
	public boolean drawMapdot;
	public int combatLevel;
	int scaleX;
	public int anInt3039;
	public boolean aBool3040;
	public boolean aBool3041;
	public int anInt3042;
	int anInt3044;
	public short[] headIconSubSprites;
	public int armyIcon;
	public int contrast;
	public int[] morphisms;
	public int anInt3050;
	int varp;
	public boolean aBool3052;
	public boolean slowWalk;
	int varbit;
	public short aShort3055;
	byte aByte3056;
	public byte aByte3058;
	public short aShort3059;
	public byte aByte3060;
	public byte movementCapabilities;
	int scaleY;
	byte[] recolourPalette;
	public int anInt3065;
	public int size = 1;
	public short aShort3067;
	public int anInt3068;
	public int anInt3069;
	public int anInt3070;
	public HashMap<Integer, Object> clientScriptData;
	public int height;
	public int anInt3073;
	public int mapFunction;
	public int[] anIntArray3075;
	public byte aByte3076;
	public String[] actions;
	public boolean aBool3078;
	public int anInt3079;
	public int anInt3080;
	public int anInt3081;
	public boolean animateIdle;
	public boolean hidePlayer;
	public int id;
	public int respawnDirection;
	public int headIcons;

	public static void main(String[] args) throws IOException {
		Cache.init();
		NPCDefinitions def = getNPCDefinitions(41);
		if (def.clientScriptData == null) {
			return;
		}
		Integer status = (Integer) def.clientScriptData.get(2849);
		Integer status2 = (Integer) def.clientScriptData.get(3);
		if (status != null && status != status2) {
			System.out.println(def.clientScriptData);
			System.out.println(41 + ", " + status + ", " + status2 + ", " + def.name);
		}

	}

	public static final NPCDefinitions getNPCDefinitions(int id) {
		NPCDefinitions def = npcDefinitions.get(id);
		if (def == null) {
			def = new NPCDefinitions(id);
			def.method694();
			byte[] data = Cache.STORE.getIndexes()[18].getFile(id >>> 134238215, id & 0x7f);
			if (data == null) {
				// System.out.println("Failed loading NPC " + id + ".");
				Logger.log("Null CS data for: "+id+" - "+def.getName()+".");
			} else
				def.readValueLoop(new InputStream(data));
			npcDefinitions.put(id, def);
		}
		return def;
	}

	public void method694() {
		if (models == null)
			models = new int[0];
	}

	private void readValueLoop(InputStream stream) {
		while (true) {
			int opcode = stream.readUnsignedByte();
			if (opcode == 0) {
				break;
			}
			readValues(stream, opcode);
		}
	}

	private void readValues(InputStream buffer, int opcode) {
		if (opcode == 1) {
			int count = buffer.readUnsignedByte();
			models = new int[count];
			for (int index = 0; index < count; index++) {
				models[index] = buffer.readBigSmart();
			}
		} else if (2 == opcode) {
			name = buffer.readString();
		} else if (opcode == 12) {
			size = buffer.readUnsignedByte();
		} else if (opcode >= 30 && opcode < 35) {
			actions[opcode - 30] = buffer.readString();
		} else if (40 == opcode) {
			int count = buffer.readUnsignedByte();
			originalColours = new short[count];
			replacementColours = new short[count];

			for (int index = 0; index < count; index++) {
				originalColours[index] = (short) buffer.readUnsignedShort();
				replacementColours[index] = (short) buffer.readUnsignedShort();
			}
		} else if (opcode == 41) {
			int count = buffer.readUnsignedByte();
			originalTextures = new short[count];
			replacementTextures = new short[count];

			for (int index = 0; index < count; index++) {
				originalTextures[index] = (short) buffer.readUnsignedShort();
				replacementTextures[index] = (short) buffer.readUnsignedShort();
			}
		} else if (42 == opcode) {
			int count = buffer.readUnsignedByte();
			recolourPalette = new byte[count];
			for (int index = 0; index < count; index++) {
				recolourPalette[index] = (byte) buffer.readByte();
			}
		} else if (opcode == 44) {
			int i_68_ = buffer.readUnsignedShort();
			int i_69_ = 0;
			for (int i_70_ = i_68_; i_70_ > 0; i_70_ >>= 1) {
				i_69_++;
			}
			aByteArray3027 = new byte[i_69_];
			byte i_71_ = 0;
			for (int i_72_ = 0; i_72_ < i_69_; i_72_++) {
				if ((i_68_ & 1 << i_72_) > 0) {
					aByteArray3027[i_72_] = i_71_;
					i_71_++;
				} else {
					aByteArray3027[i_72_] = (byte) -1;
				}
			}
		} else if (45 == opcode) {
			int i_73_ = buffer.readUnsignedShort();
			int i_74_ = 0;
			for (int i_75_ = i_73_; i_75_ > 0; i_75_ >>= 1) {
				i_74_++;
			}
			aByteArray3028 = new byte[i_74_];
			byte i_76_ = 0;
			for (int i_77_ = 0; i_77_ < i_74_; i_77_++) {
				if ((i_73_ & 1 << i_77_) > 0) {
					aByteArray3028[i_77_] = i_76_;
					i_76_++;
				} else {
					aByteArray3028[i_77_] = (byte) -1;
				}
			}
		} else if (60 == opcode) {
			int i_78_ = buffer.readUnsignedByte();
			headModels = new int[i_78_];
			for (int i_79_ = 0; i_79_ < i_78_; i_79_++) {
				headModels[i_79_] = buffer.readBigSmart();
			}
		} else if (opcode == 93) {
			drawMapdot = false;
		} else if (opcode == 95) {
			combatLevel = buffer.readUnsignedShort();
		} else if (opcode == 97) {
			scaleX = buffer.readUnsignedShort();
		} else if (opcode == 98) {
			scaleY = buffer.readUnsignedShort();
		} else if (opcode == 99) {
			aBool3040 = true;
		} else if (100 == opcode) {
			ambience = buffer.readByte();
		} else if (101 == opcode) {
			anInt3044 = buffer.readByte();
		} else if (opcode == 102) {
			int i_80_ = buffer.readUnsignedByte();
			int i_81_ = 0;
			for (int i_82_ = i_80_; 0 != i_82_; i_82_ >>= 1) {
				i_81_++;
			}
			headIconSprites = new int[i_81_];
			headIconSubSprites = new short[i_81_];
			for (int i_83_ = 0; i_83_ < i_81_; i_83_++) {
				if ((i_80_ & 1 << i_83_) == 0) {
					headIconSprites[i_83_] = -1;
					headIconSubSprites[i_83_] = (short) -1;
				} else {
					headIconSprites[i_83_] = buffer.readBigSmart();
					headIconSubSprites[i_83_] = (short) buffer.readDecoratedSmart();
				}
			}
		} else if (103 == opcode) {
			contrast = buffer.readUnsignedShort();
		} else if (opcode == 106 || opcode == 118) {
			varbit = buffer.readUnsignedShort();
			if (varbit == 65535) {
				varbit = -1;
			}

			varp = buffer.readUnsignedShort();
			if (65535 == varp) {
				varp = -1;
			}

			int last = -1;
			if (118 == opcode) {
				last = buffer.readUnsignedShort();
				if (last == 65535) {
					last = -1;
				}
			}
			int count = buffer.readUnsignedByte();
			morphisms = new int[count + 2];
			for (int index = 0; index <= count; index++) {
				morphisms[index] = buffer.readUnsignedShort();
				if (morphisms[index] == 65535) {
					morphisms[index] = -1;
				}
			}
			morphisms[1 + count] = last;
		} else if (opcode == 107) {
			aBool3052 = false;
		} else if (109 == opcode) {
			slowWalk = false;
		} else if (111 == opcode) {
			animateIdle = false;
		} else if (opcode == 113) {
			aShort3055 = (short) buffer.readUnsignedShort();
			aShort3067 = (short) buffer.readUnsignedShort();
		} else if (opcode == 114) {
			aByte3024 = (byte) buffer.readByte();
			aByte3058 = (byte) buffer.readByte();
		} else if (119 == opcode) {
			movementCapabilities = (byte) buffer.readByte();
		} else if (opcode == 121) {
			translations = new int[models.length][];
			int count = buffer.readUnsignedByte();

			for (int i = 0; i < count; i++) {
				int index = buffer.readUnsignedByte();
				int[] translations = this.translations[index] = new int[3];
				translations[0] = buffer.readByte();
				translations[1] = buffer.readByte();
				translations[2] = buffer.readByte();
			}
		} else if (opcode == 123) {
			height = buffer.readUnsignedShort();
		} else if (opcode == 125) {
			respawnDirection = buffer.readByte();
		} else if (127 == opcode) {
			renderEmote = buffer.readUnsignedShort();
		} else if (128 == opcode) {
			buffer.readUnsignedByte();
		} else if (134 == opcode) {
			anInt3029 = buffer.readUnsignedShort();
			if (anInt3029 == 65535) {
				anInt3029 = -1;
			}

			anInt3065 = buffer.readUnsignedShort();
			if (anInt3065 == 65535) {
				anInt3065 = -1;
			}

			anInt3050 = buffer.readUnsignedShort();
			if (anInt3050 == 65535) {
				anInt3050 = -1;
			}

			anInt3042 = buffer.readUnsignedShort();
			if (anInt3042 == 65535) {
				anInt3042 = -1;
			}
			anInt3068 = buffer.readUnsignedByte();
		} else if (opcode == 135 || opcode == 136) {
			buffer.readUnsignedByte();
			buffer.readUnsignedShort();
		} else if (137 == opcode) {
			attackCursor = buffer.readUnsignedShort();
		} else if (138 == opcode) {
			armyIcon = buffer.readBigSmart();
		} else if (opcode == 140) {
			anInt3070 = buffer.readUnsignedByte();
		} else if (opcode == 141) {
			aBool3041 = true;
		} else if (142 == opcode) {
			mapFunction = buffer.readUnsignedShort();
		} else if (143 == opcode) {
			aBool3033 = true;
		} else if (opcode >= 150 && opcode < 155) {
			actions[opcode - 150] = buffer.readString();
		} else if (155 == opcode) {
			aByte3014 = (byte) buffer.readByte();
			aByte3030 = (byte) buffer.readByte();
			aByte3056 = (byte) buffer.readByte();
			aByte3032 = (byte) buffer.readByte();
		} else if (opcode == 158) {
			aByte3076 = (byte) 1;
		} else if (159 == opcode) {
			aByte3076 = (byte) 0;
		} else if (160 == opcode) {
			int i_90_ = buffer.readUnsignedByte();
			anIntArray3075 = new int[i_90_];
			for (int i_91_ = 0; i_91_ < i_90_; i_91_++) {
				anIntArray3075[i_91_] = buffer.readUnsignedShort();
			}
		} else if (opcode != 162) {
			if (163 == opcode) {
				anInt3073 = buffer.readUnsignedByte();
			} else if (opcode == 164) {
				anInt3079 = buffer.readUnsignedShort();
				anInt3080 = buffer.readUnsignedShort();
			} else if (opcode == 165) {
				anInt3081 = buffer.readUnsignedByte();
			} else if (168 == opcode) {
				anInt3069 = buffer.readUnsignedByte();
			} else if (169 == opcode) {
				aBool3078 = false;
			} else if (opcode >= 170 && opcode < 176) {
				if (null == cursorOps) {
					cursorOps = new int[6];
					Arrays.fill(cursorOps, -1);
				}
				int i_92_ = buffer.readUnsignedShort();
				if (i_92_ == 65535) {
					i_92_ = -1;
				}
				cursorOps[opcode - 170] = i_92_;
			} else if (178 != opcode) {
				if (179 == opcode) {
					buffer.readSmart();
					buffer.readSmart();
					buffer.readSmart();
					buffer.readSmart();
					buffer.readSmart();
					buffer.readSmart();
				} else if (180 == opcode) {
					anInt3039 = (buffer.readUnsignedByte() & 0xff);
				} else if (181 == opcode) {
					aShort3059 = (short) buffer.readUnsignedShort();
					aByte3060 = (byte) buffer.readUnsignedByte();
				} else if (182 == opcode) {
					hidePlayer = true;
				} else if (249 == opcode) {
					int i = buffer.readUnsignedByte();
					if (clientScriptData == null) {
						clientScriptData = new HashMap<Integer, Object>(i);
					}
					for (int i_60_ = 0; i > i_60_; i_60_++) {
						boolean stringInstance = buffer.readUnsignedByte() == 1;
						int key = buffer.read24BitInt();
						Object value;
						if (stringInstance)
							value = buffer.readString();
						else
							value = buffer.readInt();
						clientScriptData.put(key, value);
					}
				}
			}
		}
	}

	public static final void clearNPCDefinitions() {
		npcDefinitions.clear();
	}

	public NPCDefinitions(int id) {
		this.id = id;
		name = "null";
		renderEmote = -1;
		attackCursor = -1;
		drawMapdot = true;
		combatLevel = -1;
		scaleX = 0;
		scaleY = 0;
		headIconSprites = null;
		headIconSubSprites = null;
		armyIcon = -1;
		respawnDirection = (byte) 7;
		contrast = 0;
		varbit = -1;
		varp = -1;
		aBool3052 = true;
		slowWalk = true;
		animateIdle = true;
		aByte3024 = 0;
		aByte3058 = 0;
		aShort3059 = (short) -1;
		anInt3029 = -1;
		anInt3065 = -1;
		anInt3050 = -1;
		anInt3042 = -1;
		anInt3068 = -1;
		anInt3070 = 0;
		height = 0;
		mapFunction = 0;
		aByte3076 = (byte) -1;
		anInt3073 = 0;
		anInt3079 = 0;
		anInt3080 = 0;
		aBool3078 = true;
		origonalId = id;
		actions = new String[5];
		headIcons = -1;
	}

	public boolean hasMarkOption() {
		for (String option : actions) {
			if (option != null && option.equalsIgnoreCase("mark"))
				return true;
		}
		return false;
	}

	public boolean hasOption(String op) {
		for (String option : actions) {
			if (option != null && option.equalsIgnoreCase(op))
				return true;
		}
		return false;
	}

	public boolean hasAttackOption() {
		if (id == 14899)
			return true;
		for (String option : actions) {
			if (option != null && (option.equalsIgnoreCase("attack") || option.equalsIgnoreCase("destroy")))
				return true;
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}
}