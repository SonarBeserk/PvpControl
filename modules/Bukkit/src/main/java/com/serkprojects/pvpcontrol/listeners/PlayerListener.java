/**
 * ********************************************************************************************************************
 * PVPControl - Provides a pvp tagging system
 * ====================================================================================================================
 * Copyright (C) 2015 by SonarBeserk, SerkProjects
 * https://gitlab.com/serkprojects/pvpcontrol
 * *********************************************************************************************************************
 * *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * *
 * *********************************************************************************************************************
 * Please refer to LICENSE for the full license. If it is not there, see <http://www.gnu.org/licenses/>.
 * *********************************************************************************************************************
 */

package com.serkprojects.pvpcontrol.listeners;

import com.serkprojects.pvpcontrol.PVPControl;
import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private PVPControl plugin;

    public PlayerListener(PVPControl plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void join(PlayerJoinEvent e) {
        if (plugin.getConfig().getBoolean("settings.pvp.informPvpStatusLogin")) {
            if(plugin.getConfig().getBoolean("settings.pvp.world.defaultStatus")) {
                plugin.getTracker().enablePVP(e.getPlayer().getUniqueId(), e.getPlayer().getWorld().getUID());
            }

            boolean pvpStatus = plugin.getTracker().pvpEnabled(e.getPlayer().getUniqueId(), e.getPlayer().getWorld().getUID());
            plugin.getMessaging().sendMessage(e.getPlayer(), true, plugin.getLanguage().getMessage("pvpStatus").replace("{world}", e.getPlayer().getWorld().getName()).replace("{pvp}", String.valueOf(pvpStatus)));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void Command(PlayerCommandPreprocessEvent e) {
        if (!plugin.getTracker().isTagged(e.getPlayer().getUniqueId(), e.getPlayer().getWorld().getUID())) {
            return;
        }

        if (plugin.getConfig().getBoolean("settings.pvp." + e.getPlayer().getWorld().getName() + ".disableCommands")) {
            for (String disabledCommand : plugin.getConfig().getStringList("settings.pvp." + e.getPlayer().getWorld().getName() + ".disabledCommands")) {
                String[] split = e.getMessage().replace("/", "").split(" +");

                if (split[0].equalsIgnoreCase(disabledCommand)) {
                    plugin.getMessaging().sendMessage(e.getPlayer(), true, plugin.getLanguage().getMessage("canNotUseCommand").replace("{name}", disabledCommand));
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void damageEvent(EntityDamageByEntityEvent e) {
        if (e.getEntity() == null || e.getDamager() == null || e.getEntity().getType() != EntityType.PLAYER || e.getDamager().getType() != EntityType.PLAYER && e.getDamager().getType() != EntityType.ARROW) {return;}

        if(e.getDamage() == 0) {return;}

        Player damagingPlayer;

        if(e.getDamager().getType() == EntityType.ARROW) {
            Arrow arrow = (Arrow) e.getDamager();

            if (arrow.getShooter() == null || !(arrow.getShooter() instanceof Player)) {
                return;
            }

            damagingPlayer = (Player) arrow.getShooter();
        } else {
            damagingPlayer = (Player) e.getDamager();
        }

        Player damagedPlayer = (Player) e.getEntity();

        if (plugin.getConfig().get("settings.pvp." + e.getEntity().getWorld().getName() + ".enabled") == null) {
            return;
        }

        if (!plugin.getConfig().getBoolean("settings.pvp." + e.getEntity().getWorld().getName() + ".enabled")) {
            plugin.getMessaging().sendMessage(damagingPlayer, false, plugin.getLanguage().getMessage("canNotPvp"));
            e.setCancelled(true);
            return;
        }

        if (plugin.getConfig().getBoolean("settings.pvp." + e.getEntity().getWorld().getName() + ".requirePvp")) {
            plugin.getTracker().enablePVP(damagedPlayer.getUniqueId(), damagedPlayer.getWorld().getUID());
            plugin.getTracker().enablePVP(damagingPlayer.getUniqueId(), damagingPlayer.getWorld().getUID());
        }

        if (!plugin.getTracker().pvpEnabled(damagingPlayer.getUniqueId(), damagingPlayer.getWorld().getUID())) {
            plugin.getMessaging().sendMessage(damagingPlayer, false, plugin.getLanguage().getMessage("pvpNotEnabled"));
            e.setCancelled(true);
            return;
        }

        if (!plugin.getTracker().pvpEnabled(damagedPlayer.getUniqueId(), damagedPlayer.getWorld().getUID())) {
            plugin.getMessaging().sendMessage(damagingPlayer, false, plugin.getLanguage().getMessage("canNotPvpNotEnabled"));
            e.setCancelled(true);
            return;
        }

        if (plugin.getConfig().getBoolean("settings.pvp." + e.getEntity().getWorld().getName() + ".disableCreativePvp")) {
            if (damagingPlayer.getGameMode() == GameMode.CREATIVE) {
                plugin.getMessaging().sendMessage(damagingPlayer, false, plugin.getLanguage().getMessage("canNotCreativePvp"));
                e.setCancelled(true);
                return;
            }
        }

        if (plugin.getConfig().getBoolean("settings.pvp." + e.getEntity().getWorld().getName() + ".disableFlyingPvp")) {
            if (damagingPlayer.isFlying() && !damagedPlayer.isFlying()) {
                plugin.getMessaging().sendMessage(damagingPlayer, false, plugin.getLanguage().getMessage("canNotFlyPvp"));
                e.setCancelled(true);
                return;
            }
        }

        if (plugin.getConfig().getBoolean("settings.pvp." + e.getEntity().getWorld().getName() + ".protectFlyingPlayers")) {
            if (!damagingPlayer.isFlying() && damagedPlayer.isFlying()) {
                plugin.getMessaging().sendMessage(damagingPlayer, false, plugin.getLanguage().getMessage("canNotPvpFlying"));
                e.setCancelled(true);
                return;
            }
        }

        if (!plugin.getConfig().getBoolean("settings.pvp." + e.getEntity().getWorld().getName() + ".allowDoubleFlyingPvp")) {
            if (damagingPlayer.isFlying() && damagedPlayer.isFlying()) {
                plugin.getMessaging().sendMessage(damagingPlayer, false, plugin.getLanguage().getMessage("canNotFlyPvp"));
                e.setCancelled(true);
                return;
            }
        }

        if (!plugin.getTracker().isTagged(damagingPlayer.getUniqueId(), damagingPlayer.getWorld().getUID())) {
            plugin.getTracker().tagPlayer(damagingPlayer.getUniqueId(), damagingPlayer.getWorld().getUID(), true);
        } else {
            plugin.getTracker().tagPlayer(damagingPlayer.getUniqueId(), damagingPlayer.getWorld().getUID(), false);
        }

        if (!plugin.getTracker().isTagged(damagedPlayer.getUniqueId(), damagedPlayer.getWorld().getUID())) {
            plugin.getTracker().tagPlayer(damagedPlayer.getUniqueId(), damagedPlayer.getWorld().getUID(), true);
        } else {
            plugin.getTracker().tagPlayer(damagedPlayer.getUniqueId(), damagedPlayer.getWorld().getUID(), false);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void playerQuit(PlayerQuitEvent e) {
        plugin.getTracker().untagPlayer(e.getPlayer().getUniqueId(), e.getPlayer().getWorld().getUID(), false);
    }

    @EventHandler(ignoreCancelled = true)
    public void playrKick(PlayerKickEvent e) {
        plugin.getTracker().untagPlayer(e.getPlayer().getUniqueId(), e.getPlayer().getWorld().getUID(), false);
    }
}
