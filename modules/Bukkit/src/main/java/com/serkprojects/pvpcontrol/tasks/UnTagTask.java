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

package com.serkprojects.pvpcontrol.tasks;

import com.serkprojects.pvpcontrol.trackers.PlayerTracker;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class UnTagTask extends BukkitRunnable {
    private PlayerTracker tracker;

    public UnTagTask(PlayerTracker tracker) {
        this.tracker = tracker;
    }

    public void run() {
        for (String s : tracker.getTaggedPlayers()) {
            String[] array = s.split(":");

            if (tracker.remainingTagTime(UUID.fromString(array[0]), UUID.fromString(array[1])) == 0) {
                tracker.untagPlayer(UUID.fromString(array[0]), UUID.fromString(array[1]), true);
                continue;
            }

            if (tracker.remainingTagTime(UUID.fromString(array[0]), UUID.fromString(array[1])) > 0) {
                int time = tracker.remainingTagTime(UUID.fromString(array[0]), UUID.fromString(array[1]));
                time = time - 1;

                tracker.setTagTime(UUID.fromString(array[0]), UUID.fromString(array[1]), time);

                if (tracker.remainingTagTime(UUID.fromString(array[0]), UUID.fromString(array[1])) == 0) {
                    tracker.untagPlayer(UUID.fromString(array[0]), UUID.fromString(array[1]), true);
                }
            }
        }
    }
}
