package com.johnymuffin.beta.pistoncontroller;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PistonBaseMaterial;

import java.util.*;
import java.util.logging.Level;

import static com.johnymuffin.beta.pistoncontroller.Utility.getPlayersInRadius;

public class PCListener implements Listener {

    private PistonController plugin;

    //List of notifications already sent
    private ArrayList<CordNotification> notifications = new ArrayList<>();


    public PCListener(PistonController plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPistonExtendEvent(BlockPistonExtendEvent event) {
        onBlockPistonEvent(event, event.getBlocks());
    }

    @EventHandler(ignoreCancelled = true)
    public void pistonRetractionEvent(BlockPistonRetractEvent event) {
        if (!event.isSticky()) {
            return;
        }

        //Direction of the piston
        BlockFace direction = event.getDirection();

        //Block that is one in the direction of the piston
        Block block = event.getBlock().getRelative(direction).getRelative(direction);

        ArrayList<Block> blocks = new ArrayList<>();
        blocks.add(block);
        if(onBlockPistonEvent(event, blocks)) {
            //If the event is cancelled, we need to recreate the sticky piston.

            // Get the piston block and its state
            Block piston = event.getBlock();

            // Set the piston block to air to remove it
            piston.setType(Material.AIR);

            // Re-create the piston block with the same direction
            piston.setType(Material.PISTON_STICKY_BASE);
            PistonBaseMaterial pistonData = (PistonBaseMaterial) piston.getState().getData();
            pistonData.setFacingDirection(direction);
            piston.setData(pistonData.getData());
        }
    }

    public boolean onBlockPistonEvent(BlockPistonEvent event, List<Block> blocks) {
        if (event.isCancelled()) {
            return false;
        }


        boolean blocked = false;
        String blockName = "";

        boolean whitelistMode = this.plugin.getPCConfig().isWhitelist();

        for (int i = 0; i < blocks.size(); i++) {
            //I am unsure if this is needed, but I am adding it just in case.
            if (blocks.get(i).getType() == null) {
                continue;
            }

            boolean inList = isBlockInList(blocks.get(i));


            if (whitelistMode) {
                //If the block is not in the list, cancel the event.
                if (!inList) {
                    blocked = true;
                    blockName = blocks.get(i).getType().name();
                    break;
                }
            } else {
                //If the block is in the list, cancel the event.
                if (inList) {
                    blocked = true;
                    blockName = blocks.get(i).getType().name();
                    break;
                }
            }
        }

        if (blocked) {
            event.setCancelled(true);

            //Send message to players in radius.
            String message = ChatColor.RED + "Sorry, " + blockName + " is not allowed to be moved by pistons. ";
            Player[] players = getPlayersInRadius(event.getBlock().getLocation(), 10);

            long currentTime = System.currentTimeMillis() / 1000L;

            for (Player player : players) {
                CordNotification notification = new CordNotification(event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ(), event.getBlock().getWorld().getUID(), player.getUniqueId(), currentTime);

                //Remove old notifications that are older than 60 seconds.
                //TODO: This is not the most efficient way to do this, but it works for now.
                Iterator<CordNotification> iterator = notifications.iterator();
                while (iterator.hasNext()) {
                    CordNotification oldNotification = iterator.next();
                    if (oldNotification.getTime() < currentTime - 60) {
                        iterator.remove();
                    }
                }

                if (notifications.contains(notification)) {
                    continue;
                }
                player.sendMessage(message);
                notifications.add(notification);
            }
        }

        return blocked;
    }

    private boolean isBlockInList(Block block) {
        boolean inList = false;
        for (PCMaterial material : this.plugin.getMaterials()) {
            if (material.matches(block)) {
                inList = true;
                break;
            }
        }
        return inList;
    }


    public class CordNotification {
        private int x;
        private int y;
        private int z;
        private UUID world;

        private long time;

        private UUID player;

        public CordNotification(int x, int y, int z, UUID world, UUID player, long time) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.world = world;
            this.player = player;
            this.time = time;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CordNotification cords = (CordNotification) o;
            return x == cords.x &&
                    y == cords.y &&
                    z == cords.z &&
                    player.equals(cords.player) &&
                    world.equals(cords.world);

        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z, world);
        }

        public long getTime() {
            return time;
        }
    }
}
