package com.multi.icyblocker;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

/**
 * Created by MultiMote on 24.11.2014.
 */
public class PlayerActionsHandler implements Listener {

    private void warnPlayer(Player player){
        player.sendMessage(ChatColor.RED + "Этот предмет вне закона.");
    }

    private boolean hasPerm(Player player) {
        return (player.hasPermission("icyblocker.ignore") || player.isOp());
    }

    @EventHandler
    public void placeBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if(this.hasPerm(player))return;
        if (BlockedItems.instance.matches(event.getBlock(), true)) {
            event.setCancelled(true);
            player.setItemInHand(null);
            this.warnPlayer(player);
        }
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(this.hasPerm(player))return;
        ItemStack stack = event.getItem();
        if (stack != null) {
            if (BlockedItems.instance.matches(stack, true)) {
                event.setCancelled(true);
                player.setItemInHand(null);
                this.warnPlayer(player);
            }
        }
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(this.hasPerm(player))return;
        ItemStack[] items = player.getInventory().getContents();
        for (int i = 0; i < items.length; i++){
            if (items[i] != null) {
                if (BlockedItems.instance.matches(items[i], false)) {
                    player.getInventory().setItem(i, null);
                }
            }
        }
    }

    @EventHandler
    public void itemCrafted(CraftItemEvent event) {
        Player player = (Player)event.getWhoClicked();
        if(this.hasPerm(player))return;
        if (BlockedItems.instance.matches(event.getCurrentItem(), false)){
            event.setCancelled(true);
            this.warnPlayer(player);
        }
    }


    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if(this.hasPerm(player))return;
        ItemStack stack = event.getCurrentItem();

        if (stack != null) {
            if (BlockedItems.instance.matches(stack, false)) {
                event.setCurrentItem(null);
                this.warnPlayer(player);
            }
        }
    }

    @EventHandler
     public void playerItemPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if(this.hasPerm(player))return;
        if (BlockedItems.instance.matches(event.getItem(), false)) {
            event.setCancelled(true);
            event.getItem().remove();
            this.warnPlayer(player);
        }
    }

    @EventHandler
    public void playerItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if(this.hasPerm(player))return;
        if (BlockedItems.instance.matches(event.getItemDrop(), false)) {
            event.setCancelled(true);
            event.getItemDrop().remove();
            this.warnPlayer(player);
        }
    }

    @EventHandler
    public void playerHeldItem(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if(this.hasPerm(player))return;
        int slot = event.getNewSlot();
        ItemStack stack = player.getInventory().getItem(slot);

        if (stack != null) {
            if (BlockedItems.instance.matches(stack, false)) {
                player.getInventory().setItem(slot, null);
                this.warnPlayer(event.getPlayer());
            }
        }
    }
}
