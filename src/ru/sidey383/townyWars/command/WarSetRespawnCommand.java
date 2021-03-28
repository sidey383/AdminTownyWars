package ru.sidey383.townyWars.command;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Town;

import ru.sidey383.townyWars.RespawnLocationController;
import ru.sidey383.townyWars.TownyWars;
import ru.sidey383.townyWars.configuration.ConfigProperty;

public class WarSetRespawnCommand implements CommandExecutor, TabCompleter {

	@ConfigProperty(Path = "command.wrong.town", Name = "lang")
	public static String wrongTownMessage;
	@ConfigProperty(Path = "command.setRespawn", Name = "lang")
	public static String setRespawn;
	@ConfigProperty(Path = "command.dontEnoughPermissions", Name = "lang")
	public static String dontEnoughPermissions;
	
	public WarSetRespawnCommand() {
		TownyWars.getLanguage().addClassToLoad(WarSetRespawnCommand.class);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args) {
		if(!sender.hasPermission("townywars.command.war.respawn"))
		{
			sender.sendMessage(dontEnoughPermissions);
			return true;
		}
		if(!(sender instanceof Player))
			sender.sendMessage("you are not player!");
		Player send = (Player) sender;
		String tname = null;
		if(args.length  > 1)
		{
			Town town = TownyUniverse.getInstance().getTown(args[1]);
			if(town == null) 
			{
				sender.sendMessage(wrongTownMessage);
				return true;
			}
			tname = town.getName();
		}
		Location loc = send.getLocation();
		RespawnLocationController.getInstance().setLocation(tname, loc);
		sender.sendMessage(setRespawn.replace("%town%", tname==null?"defaut":tname));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String name, String[] args) {
		if(args.length == 2)
			return WarCommand.selectStrings(WarCommand.getTownNames(), args[args.length - 1]);
		return null;
	}

}
