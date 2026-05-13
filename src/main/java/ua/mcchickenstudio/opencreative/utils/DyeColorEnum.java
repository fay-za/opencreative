package ua.mcchickenstudio.opencreative.utils;

import org.bukkit.DyeColor;
import org.bukkit.Material;

public enum DyeColorEnum {

    WHITE(Material.WHITE_DYE, DyeColor.WHITE),
    ORANGE(Material.ORANGE_DYE, DyeColor.ORANGE),
    MAGENTA(Material.MAGENTA_DYE, DyeColor.MAGENTA),
    LIGHT_BLUE(Material.LIGHT_BLUE_DYE, DyeColor.LIGHT_BLUE),
    YELLOW(Material.YELLOW_DYE, DyeColor.YELLOW),
    LIME(Material.LIME_DYE, DyeColor.LIME),
    PINK(Material.PINK_DYE, DyeColor.PINK),
    GRAY(Material.GRAY_DYE, DyeColor.GRAY),
    LIGHT_GRAY(Material.LIGHT_GRAY_DYE, DyeColor.LIGHT_GRAY),
    CYAN(Material.CYAN_DYE, DyeColor.CYAN),
    PURPLE(Material.PURPLE_DYE, DyeColor.PURPLE),
    BLUE(Material.BLUE_DYE, DyeColor.BLUE),
    BROWN(Material.BROWN_DYE, DyeColor.BROWN),
    GREEN(Material.GREEN_DYE, DyeColor.GREEN),
    RED(Material.RED_DYE, DyeColor.RED),
    BLACK(Material.BLACK_DYE, DyeColor.BLACK);

    private DyeColorEnum(Material material, DyeColor dyeColor) {
        this.material = material;
        this.dyeColor = dyeColor;
    }

    private final Material material;
    private final DyeColor dyeColor;

    public Material getMaterial() {
        return material;
    }

    public DyeColor getColor() {
        return dyeColor;
    }

    public static DyeColor byMaterial(Material material) {
        for (DyeColorEnum colorEnum : DyeColorEnum.values()) {
            if (colorEnum.getMaterial() == material) {
                return colorEnum.getColor();
            }
        }
        return null;
    }
}
