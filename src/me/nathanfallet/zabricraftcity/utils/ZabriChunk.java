package me.nathanfallet.zabricraftcity.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.nathanfallet.zabricraftcity.ZabriCraftCity;

public class ZabriChunk {
	
	// Stored properties
	private int x;
	private int z;
	
	// Initializer
	public ZabriChunk(int x, int z) {
		this.x = x;
		this.z = z;
	}
	
	// Get owner
	public String getOwner(){
		try {
			PreparedStatement state = ZabriCraftCity.getInstance().getConnection().prepareStatement("SELECT owner FROM chunks WHERE x = ? AND z = ?");
			state.setInt(1, x);
			state.setInt(2, z);
			ResultSet result = state.executeQuery();
			result.next();
			String owner = result.getString("owner");
			result.close();
			state.close();
			return owner;
		} catch (SQLException e) {
		}
		return "";
	}
	
	// Set owner
	public void setOwner(String owner){
		try {
			PreparedStatement state = ZabriCraftCity.getInstance().getConnection().prepareStatement("INSERT INTO chunks (x, z, owner) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE owner = ?");
			state.setInt(1, x);
			state.setInt(2, z);
			state.setString(3, owner);
			state.setString(4, owner);
			state.executeUpdate();
			state.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
