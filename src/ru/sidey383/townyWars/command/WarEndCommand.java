package ru.sidey383.townyWars.command;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import ru.sidey383.townyWars.TownyWars;
import ru.sidey383.townyWars.configuration.ConfigProperty;
import ru.sidey383.townyWars.objects.War;
import ru.sidey383.townyWars.objects.WarFactory;

public class WarEndCommand implements CommandExecutor, TabCompleter {

	@ConfigProperty(Path = "command.wrong.id", Name = "lang")
	public static String wrongIDMessage;
	@ConfigProperty(Path = "command.end", Name = "lang")
	public static String warEndMessage;
	@ConfigProperty(Path = "command.dontEnoughPermissions", Name = "lang")
	public static String dontEnoughPermissions;
	
	public WarEndCommand() {
		TownyWars.getLanguage().addClassToLoad(WarEndCommand.class);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args) {
		if(!sender.hasPermission("townywars.command.war.end"))
		{
			sender.sendMessage(dontEnoughPermissions);
			return true;
		}
		if(args.length < 2) return false;
		int i = -1;
		try 
		{
			i = Integer.parseInt(args[1]);
		}catch (Exception e) {}
		if(i == -1) 
		{
			sender.sendMessage(wrongIDMessage);
			return true;
		}
		War w = WarFactory.getInstance().getWars().get(i);
		if(w == null) 
		{
			sender.sendMessage(wrongIDMessage);
			return true;
		}
		w.stop();
		String str = warEndMessage.replace("%town1%", w.getTown1().getName()).replace("%town2%", w.getTown2().getName());
		sender.sendMessage(str);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String name, String[] args) {
		if(args.length < 2)
			return Arrays.asList("id");
		return null;
	}

}