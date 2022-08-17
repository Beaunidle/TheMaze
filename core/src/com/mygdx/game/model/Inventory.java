package com.mygdx.game.model;

import com.mygdx.game.model.environment.blocks.Wall;
import com.mygdx.game.model.items.Item;
import com.mygdx.game.model.items.Magic;
import com.mygdx.game.model.items.Material;

import java.util.HashMap;
import java.util.Map;

public class Inventory {

    private int size;
    private Map<Integer, Object> slots;

    public Inventory(int size) {
        this.size = size;
        this.slots = new HashMap<>();
        for (int i = 0; i<size; i++) {
            slots.put(i, null);
        }
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Map<Integer, Object> getSlots() {
        return slots;
    }

    public void setSlots(Map<Integer, Object> slots) {
        this.slots = slots;
    }

    public int addInventory(Object object) {
        if (object instanceof Item) {
            return addItemToInventory((Item) object);
        } else if ((object instanceof Material)) {
            return addMaterialToInventory((Material) object);

        }
        System.out.println("(Inventory full. Item not added");
        return 0;
    }

    public int removeInventory(Material material) {
        if (material instanceof Magic) {
            return removeMagic((Magic) material) ? 1: 0;
        } else if (material instanceof Item) {
            return removeItem((Item) material) ? 1 : 0;
        } else if ((material instanceof Material)) {
            return removeMaterial((Material) material) ? 1 : 0;
        }
        return 0;
    }

    public int addMaterialToInventory(Material material) {
        int excess = 0;
        Integer slotToFill = findAvailableMaterialSlot(material);
        if (slotToFill == null) {
            Integer newSlot = findEmptySlot();
            if (newSlot != null) {
                slots.put(newSlot, material);
                return 2;
            } else {
                return 0;
            }
        }
        Material inventoryMaterial = (Material)slots.get(slotToFill);
        if (inventoryMaterial.getQuantity() < inventoryMaterial.getMaxPerStack()) {
            inventoryMaterial.setQuantity(inventoryMaterial.getQuantity() + material.getQuantity());
            if (inventoryMaterial.getQuantity() > inventoryMaterial.getMaxPerStack()) {
                excess = inventoryMaterial.getQuantity() - inventoryMaterial.getMaxPerStack();
                inventoryMaterial.setQuantity(inventoryMaterial.getMaxPerStack());
            }
            if (excess == 0) {
                return 1;
            } else {
                material.setQuantity(excess);
                Integer newSlot = findEmptySlot();
                if (newSlot != null) {
                    slots.put(newSlot, material);
                    return 2;
                }
            }
        }
        return 0;
    }

    public int addItemToInventory(Item item) {
        int excess = 0;
        Integer slotToFill = findAvailableItemSlot(item);
        if (slotToFill == null) {
            Integer newSlot = findEmptySlot();
            if (newSlot != null) {
                slots.put(newSlot, item);
                return 2;
            } else {
                return 0;
            }
        }
        Item inventoryItem = (Item)slots.get(slotToFill);
        if (inventoryItem.getQuantity() < inventoryItem.getMaxPerStack()) {
            inventoryItem.setQuantity(inventoryItem.getQuantity() + item.getQuantity());
            if (inventoryItem.getQuantity() > inventoryItem.getMaxPerStack()) {
                excess = inventoryItem.getQuantity() - inventoryItem.getMaxPerStack();
                inventoryItem.setQuantity(inventoryItem.getMaxPerStack());
            }
            if (excess == 0) {
                return 1;
            } else {
                item.setQuantity(excess);
                Integer newSlot = findEmptySlot();
                if (newSlot != null) {
                    slots.put(newSlot, item);
                    return 2;
                }
            }
        }

        return 0;
    }

    public Integer findAvailableMaterialSlot(Material material) {
        for (Integer i : slots.keySet()) {
            if (slots.get(i) instanceof Material) {
                Material inventoryMaterial = (Material) slots.get(i);
                if (inventoryMaterial.getType().equals(material.getType()) && inventoryMaterial.getQuantity() < inventoryMaterial.getMaxPerStack()) {
                    return i;
                }
            }
        }
        return null;
    }

    private Integer findAvailableItemSlot(Item item) {
        for (Integer i : slots.keySet()) {
            if (slots.get(i) instanceof Item) {
                Item inventoryItem = (Item) slots.get(i);
                if (inventoryItem.getItemType().equals(item.getItemType()) && inventoryItem.getQuantity() < inventoryItem.getMaxPerStack()) {
                    return i;
                }
            }
        }
        return null;
    }

    private Integer findEmptySlot() {
        for (Integer i : slots.keySet()) {
            if (slots.get(i) == null) return i;
        }
        return null;
    }

    public boolean checkInventory(Object object) {
        if (object instanceof Magic) {
            Magic magic = (Magic) object;
            for (Object o : slots.values()) {
                if (o instanceof Magic && ((Magic) o).getMagicType().equals(magic.getMagicType())) {
                    return true;
                }
            }
        } else if (object instanceof Item) {
            Item item = (Item) object;
            for (Object o : slots.values()) {
                if (o instanceof Item && ((Item) o).getItemType().equals(item.getItemType()) && ((Material) o).getQuantity() >= item.getQuantity()) {
                    return true;
                }
            }
        } else if (object instanceof Material) {
            Material material = (Material) object;
            for (Object o : slots.values()) {
                if (o instanceof Material && ((Material) o).getType().equals(material.getType()) && ((Material) o).getQuantity() >= material.getQuantity()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkItem(Item item) {
        for (Object o : slots.values()) {
            if (o instanceof Item && ((Item) o).getItemType().equals(item.getItemType())) {
                return true;
            }
        }
        return false;
    }

    public boolean checkMaterial(Material material) {
        for (Object o : slots.values()) {
            if (o instanceof Material && ((Material) o).getType().equals(material.getType())) {
                return true;
            }
        }
        return false;
    }

    public boolean removeMaterial(Material material) {
        for (Integer i  : slots.keySet()) {
            Material inventoryMaterial = (Material) slots.get(i);
            if (inventoryMaterial != null && material.getType().equals(inventoryMaterial.getType()) && material.getQuantity() >= material.getQuantity()) {
//                inventoryMaterial.setQuantity(inventoryMaterial.getQuantity() - material.getQuantity());
                if (inventoryMaterial.getQuantity() - material.getQuantity() <= 0) {
                    removeItem(i);
                }
                return true;
            }
        }
        return false;
    }

    public boolean removeItem(Item item) {
        for (Integer i  : slots.keySet()) {
            if (slots.get(i) instanceof Item) {
                Item inventoryItem = (Item) slots.get(i);
                if (inventoryItem != null && item.getItemType().equals(inventoryItem.getItemType()) && item.getQuantity() >= item.getQuantity()) {
//                    inventoryItem.setQuantity(inventoryItem.getQuantity() - item.getQuantity());
                    if (inventoryItem.getQuantity() - item.getQuantity() == 0) {
                        removeItem(i);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean removeMagic(Magic magic) {
        for (Integer i  : slots.keySet()) {
            if (slots.get(i) instanceof Magic) {
                Magic inventoryMagic = (Magic) slots.get(i);
                if (inventoryMagic != null && magic.getMagicType().equals(inventoryMagic.getMagicType())) {
                    removeItem(i);
                    return true;
                }
            }
        }
        return false;
    }

    public void removeItem(int slot) {
        if (slots.get(slot) != null) {
            slots.put(slot, null);
        }
    }
}
