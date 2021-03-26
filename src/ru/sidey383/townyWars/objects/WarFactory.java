package ru.sidey383.townyWars.objects;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Town;

import ru.sidey383.townyWars.TownyWars;

public class WarFactory {
	
	private static WarFactory instance;
	private HashMap<Integer, War> wars = new HashMap<Integer, War>();
	
	private WarFactory() 
	{
		instance = this;
	}
	
	public War createWar(Town t1, Town t2) 
	{
		for(War war: wars.values())
			if(war.getTown1().equals(t1) || war.getTown1().equals(t2) ||war.getTown2().equals(t1) || war.getTown2().equals(t2))
				return null;
		int id = TownyWars.getDataBase().insert(t1.getName(), t2.getName(), null);
		if(id == -1) return null;
		War w = new War(t1, t2, id);
		wars.put(id, w);
		return w;
	}
	
	public static void init() throws SQLException
	{
		WarFactory f = new WarFactory();
		Connection con = TownyWars.getDataBase().getConnection();
		Statement st = con.createStatement();
			ResultSet set = st.executeQuery("SELECT * FROM wars");
			while(set.next()) 
			{
				int id = set.getInt("id");
				String town1N = set.getString("town1");
				String town2N = set.getString("town2");
				int fistIsAttacker = set.getInt("firstIsAttacker");
				Town town1, town2;
				try 
				{
					town1 = TownyUniverse.getInstance().getTown(town1N);
					town2 = TownyUniverse.getInstance().getTown(town2N);
				}catch (Exception e) {
					TownyWars.getLoggerStatic().log(Level.WARNING, "cant load towns "+town1N+" and "+town2N, e);
					continue;
				}
				War w;
				if(fistIsAttacker == 1 || fistIsAttacker == 0) 
				{
					w = new War(town1, town2, id, fistIsAttacker == 1, true);
				}else 
				{
					w = new War(town1, town2, id);
				}
				f.wars.put(id, w);
			}
			set.close();
			st.close();
			con.close();	
	}
	
	public HashMap<Integer, War> getWars()
	{
		return wars;
	}
	
	public static WarFactory getInstance() 
	{
		if(instance == null) instance = new WarFactory();
		return instance;
	}

}
