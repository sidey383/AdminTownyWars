package ru.sidey383.townyWars.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Town;

public class WarCommand implements CommandExecutor, TabCompleter {

	private  WarBattleCommand battleCommand = new WarBattleCommand();
	private WarDeclareCommand declearCommand = new WarDeclareCommand();
	private WarListCommand listCommand = new WarListCommand();
	private WarEndCommand endCommand = new WarEndCommand();
	private WarReloadCommand reloadCommand = new WarReloadCommand();
	private WarHelpCommand helpCommand = new WarHelpCommand();
	
	private static final List<String> tabComplete = Arrays.asList("list","battle","end","declare","help","reload");
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args) {
		if(args.length == 0) return false;
		if(args[0].equals("declare"))
			return declearCommand.onCommand(sender, cmd, name, args);
		if(args[0].equals("battle"))
			return battleCommand.onCommand(sender, cmd, name, args);
		if(args[0].equals("list"))
			return listCommand.onCommand(sender, cmd, name, args);
		if(args[0].equals("end"))
			return endCommand.onCommand(sender, cmd, name, args);
		if(args[0].equals("help"))
			return helpCommand.onCommand(sender, cmd, name, args);
		if(args[0].equals("reload"))
			return reloadCommand.onCommand(sender, cmd, name, args);
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String name, String[] args) {
		if(args.length == 1)
			return tabComplete;
		if(args[0].equals("declare"))
			return declearCommand.onTabComplete(sender, cmd, name, args);
		if(args[0].equals("battle"))
			return battleCommand.onTabComplete(sender, cmd, name, args);
		if(args[0].equals("list"))
			return listCommand.onTabComplete(sender, cmd, name, args);
		if(args[0].equals("end"))
			return endCommand.onTabComplete(sender, cmd, name, args);
		if(args[0].equals("help"))
			return helpCommand.onTabComplete(sender, cmd, name, args);
		if(args[0].equals("reload"))
			return reloadCommand.onTabComplete(sender, cmd, name, args);
		return null;
	}
	
	
	public static List<String> getTownNames()
	{
		ArrayList<String> list = new ArrayList<String>();
		for(Town t: TownyUniverse.getInstance().getTowns()) 
		{
			if(t!= null)
				list.add(t.getName());
		}
		return list;
	}
	
	public static List<String> selectStrings(Collection<String> col, String contain)
	{
		if(col == null) return null;
		if(contain == null) return new ArrayList<String>(col);
		ArrayList<String> list = new ArrayList<String>();
		contain = contain.toLowerCase();
		for(String str: col)
			if(str.toLowerCase().contains(contain))
				list.add(str);
		return list;
	}

}
