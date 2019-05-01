package com.tchristofferson.pagedinventories.utils;

import com.google.common.base.Preconditions;
import com.sun.istack.internal.NotNull;
import com.tchristofferson.pagedinventories.NavigationType;

public class InventoryUtil {

    public static boolean isValidSlot(int slot, int inventorySize) {
        if (slot < 0)
            return false;
        return slot < inventorySize - 9;
    }

    public static int getNavigationSlot(@NotNull NavigationType navigationType, int size) {
        Preconditions.checkArgument(navigationType != null);
        Preconditions.checkArgument(size % 9 == 0);

        switch (navigationType) {

            case NEXT:
                return size - 1;
            case PREVIOUS:
                return size - 9;
            case CLOSE:
                return size - 5;

        }

        return -1;
    }

}
