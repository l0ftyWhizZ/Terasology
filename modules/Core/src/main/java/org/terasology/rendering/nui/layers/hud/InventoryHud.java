/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.rendering.nui.layers.hud;

import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.logic.delay.DelayManager;
import org.terasology.logic.inventory.SelectedInventorySlotComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.In;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.layers.ingame.inventory.InventoryCell;
import org.terasology.rendering.nui.widgets.UIText;

public class InventoryHud extends CoreHudWidget {

    @In
    private LocalPlayer localPlayer;

    @In
    private Time time;

    @In
    private DelayManager delayManager;

    private UICrosshair crosshair;
    private UIText toolTipText;

    @Override
    public void initialise() {
        for (InventoryCell cell : findAll(InventoryCell.class)) {
            cell.bindSelected(new SlotSelectedBinding(cell.getTargetSlot(), localPlayer));
            cell.bindTargetInventory(new ReadOnlyBinding<EntityRef>() {
                @Override
                public EntityRef get() {
                    return localPlayer.getCharacterEntity();
                }
            });
        }

        crosshair = find("crosshair", UICrosshair.class);
        toolTipText = find("toolTipText", UIText.class);
        toolTipText.bindText(new CurrentSlotItem(localPlayer));
        toolTipText.bindVisible(new CurrentSlotEmpty((localPlayer)));
    }

    public void setChargeAmount(float amount) {
        crosshair.setChargeAmount(amount);
    }


    private static final class SlotSelectedBinding extends ReadOnlyBinding<Boolean> {

        private int slot;
        private LocalPlayer localPlayer;

        private SlotSelectedBinding(int slot, LocalPlayer localPlayer) {
            this.slot = slot;
            this.localPlayer = localPlayer;
        }

        @Override
        public Boolean get() {
            SelectedInventorySlotComponent component = localPlayer.getCharacterEntity().getComponent(SelectedInventorySlotComponent.class);
            return component != null && component.slot == slot;
        }
    }

    private final class CurrentSlotItem extends ReadOnlyBinding<String> {
        private LocalPlayer localPlayer;

        private CurrentSlotItem (LocalPlayer localPlayer) {
            this.localPlayer = localPlayer;
        }
        @Override
        public String get() {
            SelectedInventorySlotComponent component = localPlayer.getCharacterEntity().getComponent(SelectedInventorySlotComponent.class);
            for (InventoryCell cell : findAll(InventoryCell.class)) {
                if (cell.getTargetItem().getComponent(DisplayNameComponent.class) != null && cell.getTargetSlot() == component.slot) {
                    return cell.getTargetItem().getComponent(DisplayNameComponent.class).name;
                }
            }
            return "";
        }
    }

    private final class CurrentSlotEmpty extends ReadOnlyBinding<Boolean> {
        private LocalPlayer localPlayer;

        private CurrentSlotEmpty (LocalPlayer localPlayer) {
            this.localPlayer = localPlayer;
        }
        @Override
        public Boolean get() {
            SelectedInventorySlotComponent component = localPlayer.getCharacterEntity().getComponent(SelectedInventorySlotComponent.class);
            for (InventoryCell cell : findAll(InventoryCell.class)) {
                if (cell.getTargetItem().getComponent(DisplayNameComponent.class) != null && cell.getTargetSlot() == component.slot) {
                    return true;
                }
            }
            return false;
        }
    }
}
