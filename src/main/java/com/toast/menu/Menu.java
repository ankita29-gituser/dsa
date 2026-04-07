package com.toast.menu;

import java.util.Collection;

public class Menu {
    String name;
    Collection<MenuGroup> groups;
    Double price;

    public Menu(String name, Double price, Collection<MenuGroup> groups) {
        this.name = name;
        this.price = price;
        this.groups = groups != null ? groups : java.util.Collections.emptyList();
    }
}
