package ru.sidey383.townyWars.objects;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

import ru.sidey383.townyWars.TownyWars;
import ru.sidey383.townyWars.configuration.ConfigProperty;

public class War implements Listener {

	@ConfigProperty(Path = "start.war", Name = "lang")
	public static String warStartMessage;
	@ConfigProperty(Path = "end.war", Name = "lang")
	public static String warEndMessage;
	@ConfigProperty(Path = "joinMessage.joinOnWar", Name = "lang")
	public static String joinOnWar;
	
	private Town town1;
	private Town town2;
	private boolean firstIsAttacker;
	private Battle battle;
	public final int id;
	public boolean forceHaveBattle = false;
	
	public War(Town town1, Town town2, int id) 
	{
		this.id = id;
		this.town1 = town1;
		this.town2 = town2;
		sendMessage(warStartMessage.replace("%enotherTown%", town1.getName()), town2);
		sendMessage(warStartMessage.replace("%enotherTown%", town2.getName()), town1);
	}

	public War(Town town1, Town town2, int id,  boolean firstIsAttacker, boolean onLoad) 
	{
		this.id  = id;
		this.town1 = town1;
		this.town2 = town2;
		if(onLoad)
			startBattleOnload(firstIsAttacker);
		else
			startBattle(firstIsAttacker);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) 
	{
		if(haveBattle()) return;
		if(joinOnWar == null) return;
		if(isMemder(town1, e.getPlayer()))
			e.getPlayer().sendMessage(joinOnWar.replace("%enotherTown%", town2.getName()));
		if(isMemder(town2, e.getPlayer()))
			e.getPlayer().sendMessage(joinOnWar.replace("%enotherTown%", town1.getName()));
	}
	
	private void unregisterListener() 
	{
		HandlerList.unregisterAll(this);
	}
	
	public void stop() 
	{
		if(haveBattle())
			battle.stop();
		unregisterListener();
		TownyWars.getDataBase().remove(this);
		WarFactory.getInstance().getWars().remove(id);
		War.sendMessage(formString(warEndMessage.replace("%enotherTown%", town2.getName())), town1);
		War.sendMessage(formString(warEndMessage.replace("%enotherTown%", town1.getName())), town2);
	}
	
	/*
	 * use this only while the plugin is loading
	 * */
	public boolean startBattleOnload(boolean firstAttacker) 
	{
		if(haveBattle()) return false;
		this.firstIsAttacker = firstAttacker;
		forceHaveBattle = true;
		battle = new Battle(this);
		forceHaveBattle = false;
		return true;
	}
	
	public boolean startBattle(boolean firstAttacker) 
	{
		if(haveBattle()) return false;
		this.firstIsAttacker = firstAttacker;
		forceHaveBattle = true;
		battle = new Battle(this);
		forceHaveBattle = false;
		update();
		return true;
	}
	
	public boolean stopBattle() 
	{
		if(!haveBattle()) return false;
		battle.stop();
		battle = null;
		update();
		return true;
	}
	
	
	public boolean haveBattle() 
	{
		return battle != null || forceHaveBattle;
	}
	
	public Boolean firstIsAttacket() 
	{
		if(!haveBattle()) return null;
		return firstIsAttacker;
	}
	
	
	public Battle getBattle()
	{
		return battle;
	}
	
	public Town getAttacker() 
	{
		if(!haveBattle()) return null;
		if(firstIsAttacker)
			return getTown1();
		return getTown2();
	}
	
	public Town getDefender()
	{
		if(!haveBattle()) return null;
		if(firstIsAttacker)
			return getTown2();
		return getTown1();
	}
	
	public Town getTown1() 
	{
		return town1;
	}
	
	public Town getTown2() 
	{
		return town2;
	}
	
	public Town[] getTowns() 
	{
		return new Town[] {town1, town2};
	}
	
	
	public static void sendMessage(String message, Town town) 
	{
		for(Resident res: town.getResidents())
			if(res!= null && res.getPlayer() != null)
				res.getPlayer().sendMessage(message);
	}
	
	public static boolean isMemder(Town t, Player p) 
	{
		if(p == null || t == null) return false;
		for(Resident res: t.getResidents())
			if(res.getPlayer() != null && res.getPlayer().equals(p))
				return true;
		return false;
	}

	public String formString(String str) 
	{
		if(str == null) return null;
		if(this.getAttacker() != null)
			str = str.replace("%attacker%", this.getAttacker().getName());
		if(this.getDefender() != null)
			str = str.replace("%defender%", this.getDefender().getName());
		if(this.getTown1() != null)
			str = str.replace("%town1%", this.getTown1().getName());
		if(this.getTown2() != null)
			str = str.replace("%town2%", this.getTown2().getName());
		str = str.replace("%id%", id+"");
		return str;
	}
	
	public void update() 
	{
		TownyWars.getDataBase().update(this);
	}
	
}
