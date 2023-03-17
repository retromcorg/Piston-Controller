package com.johnymuffin.beta.pistoncontroller;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Utility {

    public static Player[] getPlayersInRadius(Location location, int radius) {
        return location.getWorld().getPlayers().stream().filter(player -> player.getLocation().distance(location) <= radius).toArray(Player[]::new);
    }
}
