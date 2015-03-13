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

package com.serkprojects.pvpcontrol.trackers;

import com.serkprojects.pvpcontrol.PVPControl;
import lombok.Getter;
import lombok.NonNull;

import java.util.*;

public class PlayerTracker {
    private PVPControl plugin = null;

    private Map<String, Integer> taggedPlayers = null;

    /**
     * Returns a list of pvp enabled players
     * @return a list of pvp enabled players
     */
    @Getter
    private List<String> pvpEnabled = null;

    public PlayerTracker(PVPControl plugin) {
        this.plugin = plugin;

        taggedPlayers = new HashMap<String, Integer>();

        pvpEnabled = new ArrayList<String>();
    }

    /**
     * Tags a player for pvp
     *
     * @param UUID   the UUID of the player to tag
     * @param worldUUID    the UUID of the world to tag them in
     * @param informPlayer should the player be notified of being tagged?
     */
    public void tagPlayer(@NonNull UUID UUID, @NonNull UUID worldUUID, boolean informPlayer) {
        if (!taggedPlayers.containsKey(String.valueOf(UUID) + ":" + String.valueOf(worldUUID))) {
            taggedPlayers.put(String.valueOf(UUID) + ":" + String.valueOf(worldUUID), plugin.getConfig().getInt("settings.pvp.pvp-tag-length"));
        } else if (taggedPlayers.containsKey(String.valueOf(UUID) + ":" + String.valueOf(worldUUID))) {
            if (taggedPlayers.get(String.valueOf(UUID) + ":" + String.valueOf(worldUUID)) < plugin.getConfig().getInt("settings.pvp.pvp-tag-length")) {
                taggedPlayers.remove(String.valueOf(UUID) + ":" + String.valueOf(worldUUID));
                taggedPlayers.put(String.valueOf(UUID) + ":" + String.valueOf(worldUUID), plugin.getConfig().getInt("settings.pvp.pvp-tag-length"));
            }
        }

        if (informPlayer) {
            if (plugin.getServer().getPlayer(UUID) == null) {
                return;
            }

            plugin.getMessaging().sendMessage(plugin.getServer().getPlayer(UUID), true, plugin.getLanguage().getMessage("tagged"));
        }
    }

    /**
     * Untags a player for pvp
     *
     * @param UUID   the UUID of the player to untag
     * @param worldUUID    the UUID of the world to untag them in
     * @param informPlayer should the player be notified of being untagged?
     */
    public void untagPlayer(@NonNull UUID UUID, @NonNull UUID worldUUID, boolean informPlayer) {
        if (taggedPlayers.containsKey(String.valueOf(UUID) + ":" + String.valueOf(worldUUID))) {

            taggedPlayers.remove(String.valueOf(UUID) + ":" + String.valueOf(worldUUID));
        }

        if (informPlayer) {

            if (plugin.getServer().getPlayer(UUID) == null) {
                return;
            }

            plugin.getMessaging().sendMessage(plugin.getServer().getPlayer(UUID), true, plugin.getLanguage().getMessage("untagged"));
        }
    }

    /**
     * Checks a player for being pvp tagged
     *
     * @param UUID   the UUID to check
     * @param worldUUID  the UUID of the world to check
     * @return if a player is pvp tagged
     */
    public boolean isTagged(@NonNull UUID UUID, @NonNull UUID worldUUID) {
        return taggedPlayers.containsKey(String.valueOf(UUID) + ":" + String.valueOf(worldUUID));
    }

    /**
     * Sets the tag time for a player
     * @param UUID the UUID for the player
     * @param worldUUID the UUID of the world
     * @param time the time to set the tag for
     */
    public void setTagTime(@NonNull UUID UUID, @NonNull UUID worldUUID, int time) {
        if (!taggedPlayers.containsKey(String.valueOf(UUID) + ":" + String.valueOf(worldUUID))) {
            taggedPlayers.put(String.valueOf(UUID) + ":" + String.valueOf(worldUUID), time);
        } else if (taggedPlayers.containsKey(String.valueOf(UUID) + ":" + String.valueOf(worldUUID))) {
            taggedPlayers.remove(String.valueOf(UUID) + ":" + String.valueOf(worldUUID));
            taggedPlayers.put(String.valueOf(UUID) + ":" + String.valueOf(worldUUID), time);
        }
    }

    /**
     * Returns a read-only list of the tagged players
     * @return a read-only list of the tagged players
     */
    public List<String> getTaggedPlayers() {
        return new ArrayList<String>(taggedPlayers.keySet());
    }

    /**
     * Returns the remaining time for a tag
     * @param UUID the UUID to get the time for
     * @param worldUUID the UUID of the world to get
     * @return
     */
    public int remainingTagTime(@NonNull UUID UUID, @NonNull UUID worldUUID) {
        return taggedPlayers.get(String.valueOf(UUID) + ":" + String.valueOf(worldUUID));
    }

    /**
     * Returns if a player has pvp enabled
     *
     * @param UUID the UUID to check
     * @param worldUUID the UUID of the world to check
     * @return if the player has pvp enabled
     */
    public boolean pvpEnabled(@NonNull UUID UUID, @NonNull UUID worldUUID) {
        return pvpEnabled.contains(String.valueOf(UUID) + ":" + String.valueOf(worldUUID));
    }

    /**
     * Enables pvp for a player
     * @param UUID the UUID for the player
     * @param worldUUID the UUID for the world
     */
    public void enablePVP(@NonNull UUID UUID, @NonNull UUID worldUUID) {
        if (pvpEnabled.contains(String.valueOf(UUID) + ":" + String.valueOf(worldUUID))) {
            return;
        }

        pvpEnabled.add(String.valueOf(UUID) + ":" + String.valueOf(worldUUID));
    }

    /**
     * Disables pvp for a player
     * @param UUID the UUID for the player
     * @param worldUUID the UUID for the world
     */
    public void disablePVP(@NonNull UUID UUID, @NonNull UUID worldUUID) {
        if (!pvpEnabled.contains(String.valueOf(UUID) + ":" + String.valueOf(worldUUID))) {
            return;
        }

        pvpEnabled.remove(String.valueOf(UUID) + ":" + String.valueOf(worldUUID));
    }
}
