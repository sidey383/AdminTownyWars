package ru.sidey383.townyWars.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import ru.sidey383.townyWars.TownyWars;
import ru.sidey383.townyWars.objects.War;

public class WarsDataBase {
	
	private String url;
	private int lastid = 0;
	
	public WarsDataBase(File file) throws ClassNotFoundException, SQLException, IOException 
	{
		File dir = file.getParentFile();
		if(!dir.exists())
			dir.mkdirs();
		if(!file.exists())
			file.createNewFile();
		Class.forName("org.sqlite.JDBC");
		url = "jdbc:sqlite:"+file.getAbsolutePath();
		Connection con = getUnsafeConnection();
		Statement st = con.createStatement();
		st.execute("CREATE TABLE IF NOT EXISTS wars (id INT PRIMARY KEY, town1 VARCHAR(255), town2 VARCHAR(255), firstIsAttacker INT)");
		st.close();
		st = con.createStatement();
		st.execute("CREATE TABLE IF NOT EXISTS respawn (town VARCHAR(255) PRIMARY KEY, x FLOAT, y FLOAT, z FLOAT, yaw FLOAT, pitch FLOAT, world VARCHAR(255))");
		st.close();
		st = con.createStatement();
		ResultSet set = st.executeQuery("SELECT MAX(id) FROM wars");
		if(set.next())
			lastid = set.getInt(1);
		set.close();
		st.close();
		con.close();
	}
	
	public Connection getUnsafeConnection() throws SQLException 
	{
		return DriverManager.getConnection(url); 
	}
	
	public Connection getConnection() 
	{
		Connection con = null;
		try 
		{
			con = DriverManager.getConnection(url);
		}catch (Exception e) {
			TownyWars.getInstance().getLogger().log(Level.SEVERE, "database connection error", e);
		}
		return con;
	}

	public void update(War war) 
	{
		try(Connection con = getUnsafeConnection())
		{
			PreparedStatement st = con.prepareStatement("UPDATE wars SET town1 = ?, town2 = ?, firstIsAttacker = ? WHERE id = ?", Statement.RETURN_GENERATED_KEYS);
			st.setString(1, war.getTown1().getName());
			st.setString(2, war.getTown2().getName());
			if(war.haveBattle())
				st.setInt(3, war.firstIsAttacket()?1:0);
			else
				st.setInt(3, 2);
			st.setInt(4, war.id);
			st.execute();
			st.close();
			con.close();
			
		}catch (Exception e) {
			TownyWars.getLoggerStatic().log(Level.SEVERE, "database exception", e);
		}
	}
	
	public void remove(War war) 
	{
		try(Connection con = getUnsafeConnection())
		{
			PreparedStatement st = con.prepareStatement("DELETE FROM wars WHERE id = ?");
			st.setInt(1, war.id);
			st.execute();
			st.close();
			con.close();
			
		}catch (Exception e) {
			TownyWars.getLoggerStatic().log(Level.SEVERE, "database exception", e);
		}
	}
	
	public int insert(String town1, String town2, Boolean firstIsAttacker) 
	{
		try(Connection con = getUnsafeConnection())
		{
			PreparedStatement st = con.prepareStatement("INSERT INTO wars (id, town1, town2, firstIsAttacker) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			st.setInt(1, ++lastid);
			st.setString(2, town1);
			st.setString(3, town2);
			if(firstIsAttacker == null)
				st.setInt(4, 2);
			else
				st.setInt(3, firstIsAttacker?1:0);
			st.execute();
			st.close();
			con.close();
			return lastid;
			
		}catch (Exception e) {
			TownyWars.getLoggerStatic().log(Level.SEVERE, "database exception", e);
		}
		return -1;
	}
	
	public void insertLocation(String town, Location loc) 
	{
		try(Connection con = getUnsafeConnection())
		{
			PreparedStatement st = con.prepareStatement("INSERT INTO  respawn (town , x, y, z, yaw, pitch, world) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			if(town == null)
				st.setNull(1, Types.VARCHAR);
			else
				st.setString(1, town);
			st.setDouble(2, loc.getX());
			st.setDouble(3, loc.getY());
			st.setDouble(4, loc.getZ());
			st.setDouble(5, loc.getYaw());
			st.setDouble(6, loc.getPitch());
			st.setString(7, loc.getWorld().getName());
			st.execute();
			st.close();
			con.close();
		}catch (Exception e) {
			TownyWars.getLoggerStatic().log(Level.SEVERE, "database exception", e);
		}
	}
	
	public void updateLocation(String town, Location loc) 
	{
		try(Connection con = getUnsafeConnection())
		{
			PreparedStatement st;
			if(town != null)
			{
				st = con.prepareStatement("UPDATE respawn SET x = ?, y = ?, z = ?, yaw = ?, pitch = ?, world = ? WHERE town = ?", Statement.RETURN_GENERATED_KEYS);
				st.setString(7, town);
			}
			else
				st = con.prepareStatement("UPDATE respawn SET x = ?, y = ?, z = ?, yaw = ?, pitch = ?, world = ? WHERE town IS NULL", Statement.RETURN_GENERATED_KEYS);
			st.setDouble(1, loc.getX());
			st.setDouble(2, loc.getY());
			st.setDouble(3, loc.getZ());
			st.setDouble(4, loc.getYaw());
			st.setDouble(5, loc.getPitch());
			st.setString(6, loc.getWorld().getName());
			st.execute();
			st.close();
			con.close();
			
		}catch (Exception e) {
			TownyWars.getLoggerStatic().log(Level.SEVERE, "database exception", e);
		}
	}
	
	public void removeLocation(String town) 
	{
		try(Connection con = getUnsafeConnection())
		{
			PreparedStatement st;
			if(town != null)
			{
			st = con.prepareStatement("DELETE FROM respawn WHERE town = ?");
			st.setString(1, town);
			}
			else
				st = con.prepareStatement("DELETE FROM respawn WHERE town IS NUll");
			st.execute();
			st.close();
			con.close();
			
		}catch (Exception e) {
			TownyWars.getLoggerStatic().log(Level.SEVERE, "database exception", e);
		}
	}
	
	public Location getLocation(String town) 
	{
		Location toReturn = null;
		try(Connection con = getUnsafeConnection())
		{
			PreparedStatement st;
			if(town != null)
			{
				st = con.prepareStatement("SELECT * FROM respawn WHERE town = ?");
				st.setString(1, town);
			}else
				st = con.prepareStatement("SELECT * FROM respawn WHERE town IS NULL");
				ResultSet set = st.executeQuery();
			if(set.next()) 
			{
				try {
					toReturn = new Location(Bukkit.getWorld(set.getString("world")), set.getDouble("x"), set.getDouble("y"), set.getDouble("z"), set.getFloat("yaw"), set.getFloat("pitch"));
				}catch (Exception e) {}
			}
			st.close();
			con.close();
			
		}catch (Exception e) {
			TownyWars.getLoggerStatic().log(Level.SEVERE, "database exception", e);
		}
		return toReturn;
	}
	
}
