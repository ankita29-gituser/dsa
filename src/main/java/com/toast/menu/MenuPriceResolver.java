package com.toast.menu;

import java.util.Collection;

public class MenuPriceResolver {

    /**
     * Returns the effective price for the given MenuItem by walking up the menu
     * hierarchy. Price resolution order (first non-null wins):
     *   MenuItem.price → parent MenuGroup.price → ... → Menu.price
     *
     * Returns null if no price is found anywhere in the hierarchy.
     */
    public static Double getPrice(Collection<Menu> menus, MenuItem target) {
        for (Menu menu : menus) {
            Double resolved = searchInMenu(menu, target, menu.price);
            if (resolved != null) {
                return resolved;
            }
        }
        return null;
    }

    // Search within a Menu's top-level groups, passing the Menu's price as the
    // inherited price from the root.
    private static Double searchInMenu(Menu menu, MenuItem target, Double inheritedPrice) {
        for (MenuGroup group : menu.groups) {
            Double result = searchInGroup(group, target, inheritedPrice);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    // Depth-first search through a MenuGroup tree.
    // effectivePrice is the closest non-null price seen so far from ancestors.
    private static Double searchInGroup(MenuGroup group, MenuItem target, Double inheritedPrice) {
        // This group's own price overrides the inherited one if present.
        Double effectivePrice = group.price != null ? group.price : inheritedPrice;

        // Check direct items in this group.
        for (MenuItem item : group.items) {
            if (item == target) {
                return item.price != null ? item.price : effectivePrice;
            }
        }

        // Recurse into nested groups.
        for (MenuGroup nested : group.groups) {
            Double result = searchInGroup(nested, target, effectivePrice);
            if (result != null) {
                return result;
            }
        }

        return null;
    }
}
