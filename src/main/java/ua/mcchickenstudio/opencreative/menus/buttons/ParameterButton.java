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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.messageExists;

/**
 * <h1>ParameterButton</h1>
 * This class represents a dynamic changeable item button,
 * that stores list of values, and it stores selected value.
 */
public class ParameterButton {

    private final ItemStack item;
    private final String name;
    private final String turnedPath;
    private final String localizationPath;
    private final List<String> originalLore;
    private final List<Object> valueList = new ArrayList<>();
    private final List<Material> materialList = new ArrayList<>();
    private Object currentValue;
    private int currentChoice;

    public ParameterButton(Object currentValue, List<Object> values, String name, String turnedPath, String itemPath, List<Material> materials) {
        this.turnedPath = turnedPath;
        this.localizationPath = itemPath;
        this.name = name;
        materialList.addAll(materials);
        valueList.addAll(values);
        if (currentChoice == materialList.size() || currentChoice == valueList.size()) {
            currentChoice = 1;
        }
        if (currentChoice == 0) {
            currentChoice = 1;
        }
        for (int i = 0; i < valueList.size(); i++) {
            if (currentValue.equals(valueList.get(i))) {
                currentChoice = (i + 1);
                break;
            }
        }
        this.currentValue = currentValue;
        this.item = materialList.size() == currentChoice - 1 ?
                createItem(Material.BARRIER, 1, localizationPath) :
                createItem(materialList.get(currentChoice - 1), 1, localizationPath);
        if (!messageExists(localizationPath + ".name")) {
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&fParameter: &6" + name));
            item.setItemMeta(meta);
        }
        if (!messageExists(localizationPath + ".lore")) {
            ItemMeta meta = item.getItemMeta();
            List<String> notFoundLore = new ArrayList<>();
            notFoundLore.add(ChatColor.translateAlternateColorCodes('&', "&6This parameter was not filled in localization,"));
            notFoundLore.add(ChatColor.translateAlternateColorCodes('&', "&6please tell administration to fill line."));
            notFoundLore.add(" ");
            notFoundLore.add(ChatColor.translateAlternateColorCodes('&', "&7" + localizationPath));
            notFoundLore.add(ChatColor.translateAlternateColorCodes('&', "&fValues:"));
            for (int i = 1; i < valueList.size() + 1; i++) {
                notFoundLore.add("%" + i + "%");
            }
            meta.setLore(notFoundLore);
            item.setItemMeta(meta);
        }
        setPersistentData(item, getItemTypeKey(), name);
        this.originalLore = item.getItemMeta().getLore();
        updateLore();
    }

    public ParameterButton(Object currentValue, List<Object> values, String name, String turnedPath, String itemPath, Material material) {
        this.turnedPath = turnedPath;
        this.localizationPath = itemPath;
        this.name = name;
        materialList.add(material);
        valueList.addAll(values);
        if (currentChoice == materialList.size() || currentChoice == valueList.size()) {
            currentChoice = 1;
        }
        if (currentChoice == 0) {
            currentChoice = 1;
        }
        for (int i = 0; i < valueList.size(); i++) {
            if (currentValue.equals(valueList.get(i))) {
                currentChoice = (i + 1);
                break;
            }
        }
        this.currentValue = currentValue;
        this.item = createItem(material, 1, localizationPath);
        if (!messageExists(localizationPath + ".name")) {
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&fParameter: &6" + name));
            item.setItemMeta(meta);
        }
        if (!messageExists(localizationPath + ".lore")) {
            ItemMeta meta = item.getItemMeta();
            List<String> notFoundLore = new ArrayList<>();
            notFoundLore.add(ChatColor.translateAlternateColorCodes('&', "&6This parameter was not filled in localization,"));
            notFoundLore.add(ChatColor.translateAlternateColorCodes('&', "&6please tell administration to fill line."));
            notFoundLore.add(" ");
            notFoundLore.add(ChatColor.translateAlternateColorCodes('&', "&7" + localizationPath));
            notFoundLore.add(ChatColor.translateAlternateColorCodes('&', "&fValues:"));
            for (int i = 1; i < valueList.size() + 1; i++) {
                notFoundLore.add("%" + i + "%");
            }
            meta.setLore(notFoundLore);
            item.setItemMeta(meta);
        }
        this.originalLore = item.getItemMeta().getLore();
        updateLore();
    }

    public void previous() {
        if (currentChoice <= 1) {
            currentChoice = valueList.size();
        } else {
            currentChoice--;
        }
        currentValue = valueList.get(currentChoice - 1);
        if (currentChoice <= materialList.size()) {
            item.setType(materialList.get(currentChoice - 1));
        }
        updateLore();
    }

    public void next() {
        if (currentChoice >= valueList.size()) {
            currentChoice = 1;
        } else {
            currentChoice++;
        }
        if (currentChoice - 1 == valueList.size()) return;
        currentValue = valueList.get(currentChoice - 1);
        if (currentChoice <= materialList.size()) {
            item.setType(materialList.get(currentChoice - 1));
        }
        updateLore();

    }

    public void updateLore() {
        String turnedOn = MessageUtils.getLocaleMessage(turnedPath + ".turned-on");
        String turnedOff = MessageUtils.getLocaleMessage(turnedPath + ".turned-off");
        List<String> newLore = new ArrayList<>();
        String turned;
        for (String loreLine : originalLore) {
            String content = loreLine;
            if (content.matches("%[0-9]+%")) {
                int choiceNumber = Integer.parseInt(content.replace("%", ""));
                if (choiceNumber > valueList.size()) continue;
                if (choiceNumber == currentChoice) {
                    turned = turnedOn;
                } else {
                    turned = turnedOff;
                }
                Object value = valueList.get(choiceNumber - 1);
                String choicePath = localizationPath + ".choices." + (value instanceof Integer i ? (i) : value).toString();
                String choiceMessage = (value instanceof Integer i ? (i) : value).toString();
                if (messageExists(choicePath)) {
                    choiceMessage = getLocaleMessage(choicePath, false);
                }
                content = content.replace("%" + choiceNumber + "%", turned + choiceMessage);
            }
            newLore.add(content);
        }

        ItemMeta meta = item.getItemMeta();
        meta.setLore(newLore);
        item.setItemMeta(meta);
        fixItem(item);
    }

    public ItemStack getItem() {
        return item;
    }

    public ItemStack getItem(boolean withoutFlags) {
        ItemStack itemStack = item.clone();
        for (ItemFlag flag : itemStack.getItemFlags()) {
            itemStack.removeItemFlags(flag);
        }
        return itemStack;
    }

    public String getName() {
        return name;
    }

    public int getCurrentChoice() {
        return currentChoice;
    }

    public Object getCurrentValue() {
        return valueList.get(currentChoice - 1);
    }
}
