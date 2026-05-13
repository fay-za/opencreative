/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
 *
 * OpenCreative+ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenCreative+ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package ua.mcchickenstudio.opencreative.menus.buttons;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h1>RadioButton</h1>
 * This class represents RadioButton that has multiple choices
 * and actions for changing current choice.
 */
public class RadioButton {

    static final Map<ItemStack, RadioButton> radioButtonList = new HashMap<>();
    private int currentChoice;
    private int maxChoicesAmount;
    private List<Runnable> choiceActions;
    private ItemStack buttonItem;
    private List<String> originalLore;
    private String turnedPath;
    private String itemLocalePath;

    /**
     * Creates RadioButton with specified parameters.
     *
     * @param material         Item's material
     * @param name             Display name of item
     * @param lore             Lore of item
     * @param currentChoice    Current choice
     * @param maxChoicesAmount Limit of choices
     * @param choicesActions   Runnables that are executing on choice change
     * @param itemLocalePath   Path of item in localization file
     * @param turnedPath       Path of 'turnedOn' 'turnedOff' messages
     */
    public RadioButton(Material material, String name, List<String> lore, int currentChoice,
                       int maxChoicesAmount, List<Runnable> choicesActions, String itemLocalePath,
                       String turnedPath) {
        setChoices(currentChoice, maxChoicesAmount, choicesActions);
        setItemButton(material, name, lore, itemLocalePath, turnedPath);
        radioButtonList.put(getButtonItem(), this);
    }

    public static RadioButton getRadioButtonByItemStack(ItemStack itemStack) {
        return radioButtonList.get(itemStack);
    }

    private void setChoices(int currentChoice, int maxChoicesAmount, List<Runnable> choicesActions) {
        this.currentChoice = currentChoice;
        this.maxChoicesAmount = maxChoicesAmount;
        this.choiceActions = choicesActions;
    }

    private void setItemButton(Material material, String name, List<String> lore, String itemLocalePath, String chosenLocalePath) {
        this.originalLore = lore;
        this.itemLocalePath = itemLocalePath;
        this.turnedPath = chosenLocalePath;

        ItemStack buttonItem = new ItemStack(material, 1);
        ItemMeta buttonItemMeta = buttonItem.getItemMeta();
        buttonItemMeta.setDisplayName(name);
        buttonItem.setItemMeta(buttonItemMeta);
        this.buttonItem = buttonItem;
        updateItem();
    }

    public void updateItem() {

        ItemMeta buttonItemMeta = buttonItem.getItemMeta();
        List<String> lore = new ArrayList<>();

        String turnedOn = MessageUtils.getLocaleMessage(turnedPath + ".turned-on");
        String turnedOff = MessageUtils.getLocaleMessage(turnedPath + ".turned-off");
        String turned;

        for (String loreLine : originalLore) {
            if (loreLine.matches("%[0-9]+%")) {
                int choiceNumber = Integer.parseInt((loreLine.replace("%", "")));
                if (choiceNumber == currentChoice) turned = turnedOn;
                else turned = turnedOff;
                loreLine = loreLine.replace("%" + choiceNumber + "%", turned + MessageUtils.getLocaleMessage(itemLocalePath + "." + choiceNumber, false));
            }
            lore.add(loreLine);
        }

        buttonItemMeta.setLore(lore);
        buttonItem.setItemMeta(buttonItemMeta);
    }

    public int getCurrentChoice() {
        return currentChoice;
    }

    public void onChoice() {


        if (this.currentChoice == 0) this.currentChoice = 1;
        int nextChoice = this.currentChoice + 1;
        if (nextChoice > this.maxChoicesAmount) {
            this.currentChoice = 1;
            nextChoice = 1;
        }

        if (nextChoice - 1 < choiceActions.size()) {
            Runnable actions = this.choiceActions.get(nextChoice - 1);
            actions.run();
        }

        this.currentChoice = nextChoice;
        radioButtonList.remove(buttonItem);
        updateItem();
        radioButtonList.put(buttonItem, this);
    }

    public ItemStack getButtonItem() {
        return buttonItem;
    }
}
