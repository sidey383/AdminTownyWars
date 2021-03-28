package ru.sidey383.townyWars.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Town;

import ru.sidey383.townyWars.TownyWars;
import ru.sidey383.townyWars.configuration.ConfigProperty;
import ru.sidey383.townyWars.objects.War;
import ru.sidey383.townyWars.objects.WarFactory;

public class WarDeclareCommand implements CommandExecutor, TabCompleter {

	@ConfigProperty(Path = "command.wrong.town", Name = "lang")
	public static String wrongTownMessage;
	@ConfigProperty(Path = "command.cantCreateWar", Name = "lang")
	public static String cantCreateWarMessage;
	@ConfigProperty(Path = "command.createWar", Name = "lang")
	public static String createWarMessage;
	@ConfigProperty(Path = "command.dontEnoughPermissions", Name = "lang")
	public static String dontEnoughPermissions;
	
	public WarDeclareCommand() {
		TownyWars.getLanguage().addClassToLoad(WarDeclareCommand.class);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args) {
		if(!sender.hasPermission("townywars.command.war.declare"))
		{
			sender.sendMessage(dontEnoughPermissions);
			return true;
		}
		if(args.length < 3) return false;
		Town town1 = TownyUniverse.getInstance().getTown(args[1]);
		Town town2 = TownyUniverse.getInstance().getTown(args[2]);
		if(town1 == null || town2 == null) 
		{
			sender.sendMessage(wrongTownMessage);
			return true;
		}
		War w = WarFactory.getInstance().createWar(town1, town2);
		if(w == null) 
		{
			sender.sendMessage(cantCreateWarMessage);
			return true;
		}
		sender.sendMessage(createWarMessage.replace("%id%", w.id+""));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String name, String[] args) {
		if(args.length == 2)
			return WarCommand.selectStrings(WarCommand.getTownNames(), args[1]);
		if(args.length == 3)
			return WarCommand.selectStrings(WarCommand.getTownNames(), args[2]);
		return null;
	}

}
