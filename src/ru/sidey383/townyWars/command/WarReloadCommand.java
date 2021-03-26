package ru.sidey383.townyWars.command;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import ru.sidey383.townyWars.TownyWars;

public class WarReloadCommand implements CommandExecutor, TabCompleter  {

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args) {
		if(!sender.hasPermission("townywars.command.war.reload"))
			return true;
		TownyWars.getConfigurationLoader().reload();
		TownyWars.getInstance().reloadLanguage();
		sender.sendMessage("§2Configuration file was reloaded");
		TownyWars.getLoggerStatic().log(Level.INFO, "Configuration file war reloaded");
		return true;
	}

	
	
}
