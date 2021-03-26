package ru.sidey383.townyWars.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import ru.sidey383.townyWars.configuration.ConfigProperty;
import ru.sidey383.townyWars.objects.War;
import ru.sidey383.townyWars.objects.WarFactory;

public class WarListCommand implements CommandExecutor, TabCompleter {

	@ConfigProperty(Path = "command.dontEnoughPermissions", Name = "lang")
	public static String dontEnoughPermissions;
	
	@ConfigProperty(Path = "command.list.battle", Name = "lang")
	public static String battleWarInfo;
	@ConfigProperty(Path = "command.list.war", Name = "lang")
	public static String declaredWarInfo;
	@ConfigProperty(Path = "command.list.frame", Name = "lang")
	public static String listFrame;
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args) {
		if(!sender.hasPermission("townywars.command.war.list"))
		{
			sender.sendMessage(dontEnoughPermissions);
			return true;
		}
		HashMap<Integer, War> wars = WarFactory.getInstance().getWars();
		String warlist = "";
		ArrayList<Integer> ids = new ArrayList<Integer>();
		ids.addAll(wars.keySet());
		Collections.sort(ids);
		for(Integer id: ids) 
		{
			War w = wars.get(id);
			if(w.haveBattle()) 
			{
				String str = battleWarInfo;
				if(str!= null)
				{
					str = w.formString(str);
					warlist+=str;
				}
			}else 
			{
				String str = declaredWarInfo;
				if(str!= null)
				{
					str = w.formString(str);
					warlist+=str;
				}
			}
		}
		if(listFrame != null)
			sender.sendMessage(listFrame.replace("%wars%", warlist));
		return true;
	}


	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		return null;
	}

}
