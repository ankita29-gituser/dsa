package com.toast.menu;

import java.util.Collection;

public class MenuGroup {
    String name;
    Collection<MenuGroup> groups;
    Collection<MenuItem> items;
    Double price;

    public MenuGroup(String name, Double price, Collection<MenuGroup> groups, Collection<MenuItem> items) {
        this.name = name;
        this.price = price;
        this.groups = groups != null ? groups : java.util.Collections.emptyList();
        this.items = items != null ? items : java.util.Collections.emptyList();
    }
}
