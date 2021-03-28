package ru.sidey383.townyWars.objects;

import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.event.town.TownLeaveEvent;
import com.palmergames.bukkit.towny.event.town.TownPreUnclaimEvent;
import com.palmergames.bukkit.towny.event.town.toggle.TownTogglePVPEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.WorldCoord;

import net.milkbowl.vault.economy.EconomyResponse;
import ru.sidey383.townyWars.RespawnLocationController;
import ru.sidey383.townyWars.TownyWars;
import ru.sidey383.townyWars.configuration.ConfigProperty;

public class Battle implements Listener {

	private War war;
	private boolean pvpWasEnabled;
	ArrayList<TownBlockCapture> captures = new ArrayList<TownBlockCapture>();
	
	@ConfigProperty(Path="start.battleDefender", Name = "lang")
	public static String battleStartDefender;
	@ConfigProperty(Path="start.battleAttacker", Name = "lang")
	public static String battleStartAttacker;
	@ConfigProperty(Path="end.battleDefender", Name = "lang")
	public static String battleEndDefender;
	@ConfigProperty(Path="end.battleAttacker", Name = "lang")
	public static String battleEndAttacker;
	@ConfigProperty(Path = "battle.alreadyCaputured", Name = "lang")
	public static String alreadyBeingCaptured;
	@ConfigProperty(Path = "battle.cantCaptureCenter", Name = "lang")
	public static String cantCaptureCenterBlock;
	@ConfigProperty(Path = "battle.cancelTogglePvp", Name = "lang")
	public static String cancelTogglePvpMessage;
	@ConfigProperty(Path = "battle.cancelLeaveTown", Name = "lang")
	public static String cancelLeaeveTown;
	@ConfigProperty(Path = "battle.cancelUnclaimMessage", Name = "lang")
	public static String cancelUnclaimMessage;
	@ConfigProperty(Path = "battle.cancelTeleport", Name = "lang")
	public static String cancelTeleport;
	@ConfigProperty(Path = "battle.cantWithdraw", Name = "lang")
	public static String cantWithdraw;
	
	@ConfigProperty(Path = "joinMessage.joinOnBattleDefender", Name = "lang")
	public static String joinOnBattleDefender;
	@ConfigProperty(Path = "joinMessage.joinOnBattleAttacker", Name = "lang")
	public static String joinOnBattleAttacker;
	
	 @ConfigProperty(Path = "capture.price", Name = "default")
	 public static double capturePrice;
	 @ConfigProperty(Path = "capture.time", Name = "default")
	 public static long captureTime;
	
	public Battle(War war)
	{
		//if(capturePrice == 0) capturePrice = 250;
		if(captureTime == 0) captureTime = 120;
		this.war = war;
		Town defender = war.getDefender();
		pvpWasEnabled = defender.isPVP();
		defender.setPVP(true);
		Bukkit.getPluginManager().registerEvents(this, TownyWars.getInstance());
		sendDefenderMessage(war.formString(war.formString(battleStartDefender)));
		sendAttackerMessage(war.formString(war.formString(battleStartAttacker)));
	}
	
	public void stop() 
	{
		war.getDefender().setPVP(pvpWasEnabled);
		for(TownBlockCapture capt: captures)
			capt.stop();
		HandlerList.unregisterAll(this);
		sendDefenderMessage(war.formString(war.formString(battleEndDefender)));
		sendAttackerMessage(war.formString(war.formString(battleEndAttacker)));
	}
	
	private boolean startCapture(Block b, Player p, TownBlock tBlock) 
	{
		if(tBlock == null || b == null) return false;
		for(TownBlockCapture capt: captures)
			if(tBlock.getWorldCoord().equals(capt.getWorldCoord()))
			{
				p.sendMessage(alreadyBeingCaptured);
				return false;
			}
		if(isCenter(tBlock.getWorldCoord(), war.getDefender()))
		{
			p.sendMessage(cantCaptureCenterBlock);
			return false;
		}
		if(TownyWars.hasVault && TownyWars.econ != null) 
		{
			double bal = TownyWars.econ.getBalance(p);
			if(bal < capturePrice)
			{
				p.sendMessage(cantWithdraw);
				return false;
			}
			EconomyResponse r = TownyWars.econ.withdrawPlayer(p, capturePrice);
			if(!r.transactionSuccess()) 
			{
				p.sendMessage(cantWithdraw);
				return false;
			}
		}else 
		{
			TownyWars.getLoggerStatic().log(Level.SEVERE, "cant load vault hasValur ="+ TownyWars.hasVault+" economic = "+ TownyWars.econ);
		}
		TownBlockCapture capture = new TownBlockCapture(war, tBlock, b, captureTime);
		TownyWars.getLoggerStatic().log(Level.INFO,p.getName()+" start capture town block "+tBlock.getX() +" "+tBlock.getZ()+" on coordinates "+b.getX()+" "+b.getY() +" "+b.getZ());
		captures.add(capture);
		return true;
	}

	private boolean isCenter(WorldCoord coord, Town town) 
	{
		try {
			WorldCoord block = coord.add(1, 0);
			if(!block.hasTownBlock() || !block.getTownBlock().getTown().equals(town)) 
			{
				return false;
			}
		}catch (Exception e) {
			TownyWars.getLoggerStatic().log(Level.WARNING, "", e);
		}
		try {
			WorldCoord block = coord.add(-1, 0);
			if(!block.hasTownBlock() || !block.getTownBlock().getTown().equals(town)) 
			{
				return false;
			}
		}catch (Exception e) {
			TownyWars.getLoggerStatic().log(Level.WARNING, "", e);
		}
		try {
			WorldCoord block = coord.add(0, 1);
			if(!block.hasTownBlock() || !block.getTownBlock().getTown().equals(town)) 
			{
				return false;
			}
		}catch (Exception e) {
			TownyWars.getLoggerStatic().log(Level.WARNING, "", e);
		}
		try {
			WorldCoord block = coord.add(0, -1);
			if(!block.hasTownBlock() || !block.getTownBlock().getTown().equals(town)) 
			{
				return false;
			}
		}catch (Exception e) {
			TownyWars.getLoggerStatic().log(Level.WARNING, "", e);
		}
		return true;
	}
	

	public void sendDefenderMessage(String message) 
	{
		for(Resident res: war.getDefender().getResidents())
			if(res.getPlayer() != null)
				res.getPlayer().sendMessage(message);
	}

	public void sendAttackerMessage(String message) 
	{
		for(Resident res: war.getAttacker().getResidents())
			if(res.getPlayer() != null)
				res.getPlayer().sendMessage(message);
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void placeBlock(BlockPlaceEvent e) 
	{
		if(!(e.getBlock().getType() == Material.STANDING_BANNER || e.getBlock().getType() == Material.WALL_BANNER)) return;
		try {
			if(war.getAttacker().hasResident(e.getPlayer().getName())) 
			{
				TownBlock block = TownyUniverse.getInstance().getTownBlock(WorldCoord.parseWorldCoord(e.getBlock()));
				if(war.getDefender().equals(block.getTown())) 
				{
					e.setCancelled(!startCapture(e.getBlock(), e.getPlayer(), block));
				}
			}
		} catch (NotRegisteredException ex) {}
	}
	
	@EventHandler
	public void toggleDamageEvent(TownTogglePVPEvent e) 
	{
		if(e.getResident()!= null && e.getResident().getPlayer() != null && e.getResident().getPlayer().hasPermission("townywars.toggle.pvp")) return;
		if(e.getTown().equals(war.getDefender()))
		{
			e.setCancellationMsg(cancelTogglePvpMessage);
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void townLeaveEvent(TownLeaveEvent e) 
	{
		if(e.getResident()!= null && e.getResident().getPlayer() != null && e.getResident().getPlayer().hasPermission("townywars.leavetown")) return;
		if(e.getTown().equals(war.getDefender()) || e.getTown().equals(war.getAttacker())) 
		{
			e.setCancelMessage(cancelLeaeveTown);
			e.setCancelled(true);
		} 
	}
	
	@EventHandler
	public void townUnclaimEvent(TownPreUnclaimEvent e) 
	{
		Town t = e.getTown();
		if(t == null) return;
		if(t.equals(war.getDefender()) || t.equals(war.getAttacker())) 
		{
			War.sendMessage(cancelUnclaimMessage, t);
			e.setCancelled(true);
		} 
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) 
	{
		if(war.getAttacker().hasResident(e.getPlayer().getName())) 
		{
			Location loc = RespawnLocationController.getInstance().getLoction(war.getAttacker().getName());
			if(loc != null)
				e.setRespawnLocation(loc);
			return;
		}
		if(war.getDefender().hasResident(e.getPlayer().getName())) 
		{
			Location loc = RespawnLocationController.getInstance().getLoction(war.getDefender().getName());
			if(loc != null)
				e.setRespawnLocation(loc);
			return;
		}
	}
	
	@EventHandler
	public void onJoinEvent(PlayerJoinEvent e) 
	{
		if(war.getAttacker().hasResident(e.getPlayer().getName()))
			e.getPlayer().sendMessage(war.formString(joinOnBattleAttacker));
		if(war.getDefender().hasResident(e.getPlayer().getName()))
			e.getPlayer().sendMessage(war.formString(joinOnBattleDefender));
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent e) 
	{
		if(e.getCause() == TeleportCause.CHORUS_FRUIT || e.getCause() == TeleportCause.END_GATEWAY || e.getCause() == TeleportCause.END_PORTAL || e.getCause() == TeleportCause.ENDER_PEARL || e.getCause() == TeleportCause.NETHER_PORTAL || e.getCause() == TeleportCause.SPECTATE) return;
		if(e.getPlayer() != null && e.getPlayer().hasPermission("townywars.teleport")) return;
		if(war.getDefender().hasResident(e.getPlayer().getName()) || war.getAttacker().hasResident(e.getPlayer().getName())) 
		{
			e.getPlayer().sendMessage(cancelTeleport);
			e.setCancelled(true);
		}
	}
	
	
	
}
