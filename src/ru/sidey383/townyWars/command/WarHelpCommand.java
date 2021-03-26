package ru.sidey383.townyWars.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import ru.sidey383.townyWars.configuration.ConfigProperty;

public class WarHelpCommand implements CommandExecutor, TabCompleter  {

	@ConfigProperty(Path = "help", Name = "lang")
	public static String warHelp;
	
	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		return null;
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if(!arg0.hasPermission("townywars.command.war.help")) return true;
		arg0.sendMessage(warHelp);
		return true;
	}

	
	
}
