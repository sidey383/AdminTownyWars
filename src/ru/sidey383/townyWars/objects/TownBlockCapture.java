package ru.sidey383.townyWars.objects;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitTask;

import com.palmergames.bukkit.towny.exceptions.AlreadyRegisteredException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.WorldCoord;

import ru.sidey383.townyWars.TownyWars;
import ru.sidey383.townyWars.configuration.ConfigProperty;

public class TownBlockCapture implements Listener{

	private TownBlock block;
	private War war;
	private long time;
	private Block flag;
	private BukkitTask task;
	
	@ConfigProperty(Path = "start.captureAttacker", Name = "lang")
	public static String startCaptureDefender;
	@ConfigProperty(Path = "start.captureDefender", Name = "lang")
	public static String startCaptureAttacker;
	
	@ConfigProperty(Path = "end.captureAttacker", Name = "lang")
	public static String endCaptureDefender;
	@ConfigProperty(Path = "end.captureDefender", Name = "lang")
	public static String endCaptureAttacker;
	
	@ConfigProperty(Path = "townBlock.get", Name = "lang")
	public static String onTownBlockGet;
	@ConfigProperty(Path = "townBlock.lose", Name = "lang")
	public static String onTownBlockLose;
	
	public TownBlockCapture(War war, TownBlock block, Block flag, long time) {
		this.block = block;
		this.flag = flag;
		this.war = war;
		this.time = time;
		Bukkit.getPluginManager().registerEvents(this, TownyWars.getInstance());
		startTask();
		sendStartMessage();
	}
	
	private void sendStartMessage() 
	{
		if(startCaptureAttacker != null) 
		{
			War.sendMessage(formString(startCaptureAttacker), war.getAttacker());
		}
		if(startCaptureDefender != null) 
		{
			War.sendMessage(formString(startCaptureDefender), war.getDefender());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent e) 
	{
		Block b = e.getBlock();
		if(e.isCancelled()) return;
		if(flag.getX() == b.getX() && flag.getY() == b.getY() && flag.getZ() == b.getZ())
		{
			TownyWars.getLoggerStatic().log(Level.INFO,e.getPlayer().getName()+" stop capture town block "+block.getX() +" "+block.getZ()+" on coordinates "+flag.getX()+" "+flag.getY() +" "+flag.getZ() );
			stop();
		}
	}
	
	private void startTask() 
	{
		task = Bukkit.getScheduler().runTaskLater(TownyWars.getInstance(), ()->
		{
			war.getBattle().captures.remove(this);
			transferTownBlock();
			HandlerList.unregisterAll(this);
			onCaptureMessage();
		}, time);
	}
	
	private void transferTownBlock() 
	{
		try {
			block.toString();
			if(block != null && block.hasTown() && block.getTown().equals(war.getDefender())) 
			{
				Town def = war.getDefender();
				Town at = war.getAttacker();
				if(block.isHomeBlock()) 
				{
					try {
						transferHomeBlock(block, def, at);
					}catch (TownyException e) {
						TownyWars.getInstance().getLogger().log(Level.WARNING, " transfer home blokc error ", e);
					}
				}else
				if(block.isOutpost()) 
				{
					try {
					transferOutpostTownBlock(block, def, at);
					}catch (TownyException e) {
						TownyWars.getInstance().getLogger().log(Level.WARNING, " transfer outpost block error ", e);
					}
				}else {
					try {
						transferTownBlock(block, def, at);
					}catch (TownyException e) {
						TownyWars.getInstance().getLogger().log(Level.WARNING, " transef town block error ", e);
					}
				}
			}else
				TownyWars.getInstance().getLogger().log(Level.WARNING, " incorrect town block for tarsfer in enother town ");
		} catch (NotRegisteredException e) {
			TownyWars.getInstance().getLogger().log(Level.WARNING, "idn \''/", e);
		}
		war.getDefender().save();
		war.getAttacker().save();
		block.save();
	}
	
	private void transferTownBlock(TownBlock block, Town defender, Town attacker) throws TownyException 
	{
		block.setTown(attacker);
	}
	
	private void transferOutpostTownBlock(TownBlock block, Town defender, Town attacker) throws TownyException, AlreadyRegisteredException
	{
		Location outp = null;
		for(Location loc: defender.getAllOutpostSpawns())
			if(block.getCoord().equals(Coord.parseCoord(loc)))
			{
				outp = loc;
				break;
			}
		defender.removeOutpostSpawn(block.getCoord());
		block.setTown(attacker);
		if(outp	!= null)
			attacker.addOutpostSpawn(outp);
	}
	
	private void transferHomeBlock(TownBlock block, Town defender, Town attacker) throws TownyException 
	{
		TownBlock newHome = null;
		int dist = -1;
		Location newSpawnLoc = null;
		for(Location op : defender.getAllOutpostSpawns()) 
		{
			WorldCoord coord = WorldCoord.parseWorldCoord(op);
			TownBlock b;
			if(coord.hasTownBlock() && (b = coord.getTownBlock()) != null) 
			{
				if(!b.getTown().equals(defender)) continue;
				int c = Math.abs(block.getX() -  b.getX()) + Math.abs(block.getZ() -  b.getZ());
				if(c != 0)
					if(dist == -1)
					{
						TownyWars.getLoggerStatic().log(Level.INFO, "find new home block dist = "+c);
						dist =c;
						newHome = b;
						newSpawnLoc = op;
					}else
						if(dist > c)
						{
							TownyWars.getLoggerStatic().log(Level.INFO, "find new home block dist = "+c);
							dist = c;
							newHome = b;
							newSpawnLoc = op;
						}
			}								
		}
		if(newHome == null)
			for(TownBlock b: defender.getTownBlocks()) 
			{
				int c = Math.abs(block.getX() -  b.getX()) + Math.abs(block.getZ() -  b.getZ());
				if(c != 0)
					if(dist == -1)
					{
						TownyWars.getLoggerStatic().log(Level.INFO, "! find new home block dist = "+c);
						dist = c;
						newHome = b;
					}else
						if(dist > c) 
						{
							TownyWars.getLoggerStatic().log(Level.INFO, "! find new home block dist = "+c);
							dist = c;
							newHome = b;
						}
			}
		else
			TownyWars.getLoggerStatic().log(Level.INFO, "newHome != null");
		Location oldSpawn = null;
		if(defender.hasSpawn())
			oldSpawn = defender.getSpawn();
		block.setTown(attacker);
		block.setOutpost(true);
		if(oldSpawn != null)
			attacker.addOutpostSpawn(oldSpawn);
		if(newHome != null) 
		{
			TownyWars.getLoggerStatic().log(Level.INFO, "set new home block");
			if(newHome.isOutpost() && newSpawnLoc != null)
				defender.removeOutpostSpawn(newHome.getCoord());
			newHome.setOutpost(false);
			defender.setHomeBlock(newHome);
			if(newSpawnLoc != null)
				defender.setSpawn(newSpawnLoc);
			newHome.save();
		}
	}
	
	public void onCaptureMessage() 
	{
		if(onTownBlockGet != null)
			war.getBattle().sendAttackerMessage(formString(onTownBlockGet));
		if(onTownBlockLose != null)
			war.getBattle().sendDefenderMessage(formString(onTownBlockLose));
	}
	
	public boolean stop() 
	{
		stopMessage();
		if(task != null && !task.isCancelled()) 
			task.cancel();
		HandlerList.unregisterAll(this);
		war.getBattle().captures.remove(this);
		return true;
	}
	
	private void stopMessage() 
	{
		if(endCaptureAttacker != null)
			War.sendMessage(formString(endCaptureAttacker), war.getAttacker());
		if(endCaptureDefender != null)
			War.sendMessage(formString(endCaptureDefender), war.getDefender());
	}
	
	public TownBlock getTownBlock()
	{
		return block;
	}
	
	public WorldCoord getWorldCoord() 
	{
		return block.getWorldCoord();
	}
	
	public War getWarBattle() 
	{
		return war;
	}
	
	private String formString(String str) 
	{
		if(str == null) return null;
		str = war.formString(str);
		if(block != null)
		{
			str = str.replace("%TownBlock%", block.toString());
			str = str.replace("%X%", block.getX()+"");
			str = str.replace("%Z%", block.getZ() +"");
			str = str.replace("%world%", block.getWorld().getName());
		}
		if(flag != null) 
		{
			str = str.replace("%x%", flag.getX()+"");
			str = str.replace("%y%", flag.getY()+"");
			str = str.replace("%z%", flag.getZ()+"");
		}
		if(war.getAttacker() != null)
			str = str.replace("%attacker%", war.getAttacker().getName());
		if(war.getDefender() != null)
			str = str.replace("%defender%", war.getDefender().getName());
		return str;
	}
	
}
