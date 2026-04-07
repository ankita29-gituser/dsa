package com.toast.menu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MenuPriceResolverTest {

    // ── Lunch menu items ──────────────────────────────────────────────────────
    MenuItem wings;
    MenuItem fries;
    MenuItem gardenSalad;
    MenuItem cobbSalad;
    MenuItem avocado;
    MenuItem pbj;
    MenuItem burger;

    // ── Drinks menu items ─────────────────────────────────────────────────────
    MenuItem icedLatte;
    MenuItem decafIcedCoffee;
    MenuItem americano;
    MenuItem cupOfJoe;
    MenuItem coke;
    MenuItem sprite;
    MenuItem gingerAle;

    Collection<Menu> menus;

    @BeforeEach
    void buildMenus() {
        // ── Lunch ─────────────────────────────────────────────────────────────
        wings         = new MenuItem("Wings", null);
        fries         = new MenuItem("Fries", 3.50);

        MenuGroup appetizers = new MenuGroup("Appetizers", 5.00,
                Collections.emptyList(),
                Arrays.asList(wings, fries));

        gardenSalad   = new MenuItem("Garden Salad", null);
        cobbSalad     = new MenuItem("Cobb Salad w/ Bacon", 9.00);

        MenuGroup salads = new MenuGroup("Salads", 7.50,
                Collections.emptyList(),
                Arrays.asList(gardenSalad, cobbSalad));

        avocado       = new MenuItem("Avocado", null);
        pbj           = new MenuItem("PB&J", null);

        MenuGroup toast = new MenuGroup("Toast", 6.50,
                Collections.emptyList(),
                Arrays.asList(avocado, pbj));

        burger        = new MenuItem("Burger", null);

        MenuGroup entrees = new MenuGroup("Entrees", null,
                Arrays.asList(salads, toast),
                Collections.singletonList(burger));

        Menu lunch = new Menu("Lunch", 11.50, Arrays.asList(appetizers, entrees));

        // ── Drinks ────────────────────────────────────────────────────────────
        icedLatte         = new MenuItem("Iced Latte", 6.50);
        decafIcedCoffee   = new MenuItem("Decaf Iced Coffee", null);

        MenuGroup iced = new MenuGroup("Iced", 5.00,
                Collections.emptyList(),
                Arrays.asList(icedLatte, decafIcedCoffee));

        americano  = new MenuItem("Americano", null);
        cupOfJoe   = new MenuItem("Cup of Joe", null);

        MenuGroup hot = new MenuGroup("Hot", 3.00,
                Collections.emptyList(),
                Arrays.asList(americano, cupOfJoe));

        MenuGroup coffees = new MenuGroup("Coffees", null,
                Arrays.asList(iced, hot),
                Collections.emptyList());

        coke       = new MenuItem("Coke", null);
        sprite     = new MenuItem("Sprite", null);
        gingerAle  = new MenuItem("Ginger ale", null);

        MenuGroup sodas = new MenuGroup("Sodas", 2.50,
                Collections.emptyList(),
                Arrays.asList(coke, sprite, gingerAle));

        Menu drinks = new Menu("Drinks", 5.00, Arrays.asList(coffees, sodas));

        menus = Arrays.asList(lunch, drinks);
    }

    // ── Lunch menu ────────────────────────────────────────────────────────────

    @Test
    void wings_inheritsFromAppetizers() {
        // Wings has null price → inherits Appetizers ($5.00)
        assertEquals(5.00, MenuPriceResolver.getPrice(menus, wings));
    }

    @Test
    void fries_hasOwnPrice() {
        // Fries has its own price ($3.50)
        assertEquals(3.50, MenuPriceResolver.getPrice(menus, fries));
    }

    @Test
    void gardenSalad_inheritsFromSalads() {
        // Garden Salad null → Salads ($7.50)
        assertEquals(7.50, MenuPriceResolver.getPrice(menus, gardenSalad));
    }

    @Test
    void cobbSalad_hasOwnPrice() {
        // Cobb Salad has its own price ($9.00)
        assertEquals(9.00, MenuPriceResolver.getPrice(menus, cobbSalad));
    }

    @Test
    void avocado_inheritsFromToast() {
        // Avocado null → Toast ($6.50)
        assertEquals(6.50, MenuPriceResolver.getPrice(menus, avocado));
    }

    @Test
    void pbj_inheritsFromToast() {
        // PB&J null → Toast ($6.50)
        assertEquals(6.50, MenuPriceResolver.getPrice(menus, pbj));
    }

    @Test
    void burger_skipsEntreesInheritsFromLunch() {
        // Burger null → Entrees null → Lunch ($11.50)
        assertEquals(11.50, MenuPriceResolver.getPrice(menus, burger));
    }

    // ── Drinks menu ───────────────────────────────────────────────────────────

    @Test
    void icedLatte_hasOwnPrice() {
        // Iced Latte has its own price ($6.50)
        assertEquals(6.50, MenuPriceResolver.getPrice(menus, icedLatte));
    }

    @Test
    void decafIcedCoffee_inheritsFromIced() {
        // Decaf Iced Coffee null → Iced ($5.00)
        assertEquals(5.00, MenuPriceResolver.getPrice(menus, decafIcedCoffee));
    }

    @Test
    void americano_inheritsFromHot() {
        // Americano null → Hot ($3.00)
        assertEquals(3.00, MenuPriceResolver.getPrice(menus, americano));
    }

    @Test
    void cupOfJoe_inheritsFromHot() {
        // Cup of Joe null → Hot ($3.00)
        assertEquals(3.00, MenuPriceResolver.getPrice(menus, cupOfJoe));
    }

    @Test
    void coke_inheritsFromSodas() {
        // Coke null → Sodas ($2.50)
        assertEquals(2.50, MenuPriceResolver.getPrice(menus, coke));
    }

    @Test
    void sprite_inheritsFromSodas() {
        assertEquals(2.50, MenuPriceResolver.getPrice(menus, sprite));
    }

    @Test
    void gingerAle_inheritsFromSodas() {
        assertEquals(2.50, MenuPriceResolver.getPrice(menus, gingerAle));
    }

    // ── Edge cases ────────────────────────────────────────────────────────────

    @Test
    void itemNotInAnyMenu_returnsNull() {
        MenuItem unknown = new MenuItem("Unknown", null);
        assertNull(MenuPriceResolver.getPrice(menus, unknown));
    }

    @Test
    void itemWithPriceNotInMenus_returnsNull() {
        // Item is not in the menus collection even though it has a price set;
        // getPrice must still return null because the item is not found.
        MenuItem ghost = new MenuItem("Ghost", 99.99);
        assertNull(MenuPriceResolver.getPrice(menus, ghost));
    }
}
