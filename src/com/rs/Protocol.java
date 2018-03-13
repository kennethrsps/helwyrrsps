package com.rs;

import java.math.BigInteger;

/**
 * A class used to store all server/world configurations.
 * @author Zeus
 */
public final class Protocol {

    /* Data location file */
    public static final String CACHE_PATH = "data/cache/";
    public static final String LOGS_PATH = "data/playersaves/logs/";

    /* Server limits */
    public static final int SV_RECEIVE_DATA_LIMIT = 10000; // bytes
    public static final int SV_PACKET_SIZE_LIMIT = 10000; // bytes
    public static final int SV_PLAYERS_LIMIT = 2000;
    public static final int SV_LOCAL_PLAYERS_LIMIT = 2000;
    public static final int SV_NPCS_LIMIT = Short.MAX_VALUE;
    public static final int SV_LOCAL_NPCS_LIMIT = 250;

    /* Map configuration size */
    public static final int[] MAP_SIZES = { 104, 120, 136, 168, 72 };

    // CLIENT TOKENS
    public static final String GRAB_SERVER_TOKEN = "hAJWGrsaETglRjuwxMwnlA/d5W6EgYWx";
	
    public static final int[] GRAB_SERVER_KEYS = new int[] { 175, 9857, 5907, 4981, 113897, 5558, 0, 2534, 4895, 
			52303, 129809, 45253, 64569, 92184, 135106, 3940, 3909, 2447, 150, 7416, 266, 15, 147354, 153189, 493, 436 };
	
	public static final BigInteger GRAB_SERVER_PRIVATE_EXPONENT = new BigInteger("95776340111155337321344029627634"
			+ "1788886261017915822452285867506979967134540193547165770775775581569761779944798377609896913564389"
			+ "7487964729306417755551818756732765979333143142115320393191493385852685739642805226692650786060316"
			+ "6705084302845740310178306001400777670591958466653637275131498866778592148380588481");
	
	public static final BigInteger GRAB_SERVER_MODULUS = new BigInteger("119555331260995530494627322191654816613"
			+ "15547661260381710307968992599540226345789589082914809341413534242080728782003241745842876349656560"
			+ "597016393669681148550055350674397952146548980174697339290188558877746202316525248398843187741102181"
			+ "6445058706597607453280166045122971960003629860919338852061972113876035333");
	
	public static final BigInteger PRIVATE_EXPONENT = new BigInteger("90587072701551327129007891668787349509347630"
			+ "408215045082807628285770049664232156776755654198505412956586289981306433146503308411067358680117206"
			+ "73209160808841845822058047908111136065644680439756075245536786262037053746105033422444816707136774"
			+ "3407184852057833323917170323302797356352672118595769338616589092625");
	
	public static final BigInteger MODULUS = new BigInteger("10287663727111612473233850066363964311350446478933924"
			+ "949039931265967477203931487590417680926747503377236770788287377329178601447522217865493244225412573"
			+ "16227815244132085234655207585370604085416102546191669071425937313376184908798314014619456794780468"
			+ "11438574041131738117063340726565226753787565780501845348613");

}