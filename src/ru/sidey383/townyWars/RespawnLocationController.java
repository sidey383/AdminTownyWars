package ru.sidey383.townyWars;

import java.util.HashMap;

import org.bukkit.Location;

import ru.sidey383.townyWars.db.WarsDataBase;

public class RespawnLocationController {

	private static RespawnLocationController instance;
	
	private RespawnLocationController() {}
	
	public HashMap<String, Location> locations = new HashMap<String, Location>();
	
	public Location getLoction(String town) 
	{
		if(locations.containsKey(town))
			return locations.get(town);
		Location loc = loadTown(town);
		if(loc != null) 
			return loc;
		loadTown(null);
		return locations.get(null);
	}
	
	private Location loadTown(String town) 
	{
		Location loc = TownyWars.getDataBase().getLocation(town);
		if(loc != null)
			locations.put(town, loc);
		return loc;
	}
	
	public void setLocation(String town, Location loc) 
	{
		if(loc == null) 
		{
			removeLocation(town);
			return;
		}
		locations.put(town, loc);
		WarsDataBase db = TownyWars.getDataBase();
		if(db.getLocation(town) == null)
			db.insertLocation(town, loc);
		else
			db.updateLocation(town, loc);
	}
	
	public void removeLocation(String town) 
	{
		locations.remove(town);
		WarsDataBase db = TownyWars.getDataBase();
		db.removeLocation(town);
	}
	
	public static RespawnLocationController getInstance() 
	{
		if(instance == null) instance = new RespawnLocationController();
		return instance;
	}
	
}
