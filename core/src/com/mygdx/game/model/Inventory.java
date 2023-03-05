package com.mygdx.game.model;

import com.mygdx.game.model.environment.blocks.Wall;
import com.mygdx.game.model.items.Consumable;
import com.mygdx.game.model.items.Item;
import com.mygdx.game.model.items.Magic;
import com.mygdx.game.model.items.Material;
import com.mygdx.game.model.items.Placeable;
import com.mygdx.game.model.items.Swingable;
import com.mygdx.game.model.items.Throwable;

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
        if (object instanceof Consumable) {
            return addConsumableToInventory(new Consumable(((Consumable) object).getConsumableType(), ((Consumable) object).getQuantity()));
        }
        if (object instanceof  Swingable) {
            return addSwingableToInventory((Swingable) object);
        } else if (object instanceof Placeable) {
            return addPlacableToInventory(new Placeable(((Placeable) object).getPlaceableType(), ((Placeable) object).getQuantity()));
        } else if (object instanceof Item) {
            return addItemToInventory((Item) object);
        } else if ((object instanceof Material)) {
            return addMaterialToInventory(new Material(((Material) object).getType(), ((Material) object).getQuantity()));
        }
        System.out.println("(Inventory full. Item not added");
        return 0;
    }

    public int removeInventory(Material material) {
        if (material instanceof Magic) {
            return removeMagic((Magic) material) ? 1: 0;
        } else if (material instanceof Item) {
            return removeItem((Item) material) ? 1 : 0;
        } else if ((material != null)) {
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

    public Integer addConsumableToInventory(Consumable consumable) {
        int excess = 0;
        Integer slotToFill = findAvailableMaterialSlot(consumable);
        if (slotToFill == null) {
            Integer newSlot = findEmptySlot();
            if (newSlot != null) {
                slots.put(newSlot, consumable);
                return 2;
            } else {
                return 0;
            }
        }
        Material inventoryMaterial = (Material)slots.get(slotToFill);
        if (inventoryMaterial.getQuantity() < inventoryMaterial.getMaxPerStack()) {
            inventoryMaterial.setQuantity(inventoryMaterial.getQuantity() + consumable.getQuantity());
            if (inventoryMaterial.getQuantity() > inventoryMaterial.getMaxPerStack()) {
                excess = inventoryMaterial.getQuantity() - inventoryMaterial.getMaxPerStack();
                inventoryMaterial.setQuantity(inventoryMaterial.getMaxPerStack());
            }
            if (excess == 0) {
                return 1;
            } else {
                consumable.setQuantity(excess);
                Integer newSlot = findEmptySlot();
                if (newSlot != null) {
                    slots.put(newSlot, consumable);
                    return 2;
                }
            }
        }
        return 0;
    }

    public Integer addPlacableToInventory(Placeable placeable) {
        int excess = 0;
        Integer slotToFill = findAvailableItemSlot(placeable);
        if (slotToFill == null) {
            Integer newSlot = findEmptySlot();
            if (newSlot != null) {
                slots.put(newSlot, placeable);
                return 2;
            } else {
                return 0;
            }
        }
        Material inventoryItem = (Material) slots.get(slotToFill);
        if (inventoryItem.getQuantity() < inventoryItem.getMaxPerStack()) {
            inventoryItem.setQuantity(inventoryItem.getQuantity() + placeable.getQuantity());
            if (inventoryItem.getQuantity() > inventoryItem.getMaxPerStack()) {
                excess = inventoryItem.getQuantity() - inventoryItem.getMaxPerStack();
                inventoryItem.setQuantity(inventoryItem.getMaxPerStack());
            }
            if (excess == 0) {
                return 1;
            } else {
                placeable.setQuantity(excess);
                Integer newSlot = findEmptySlot();
                if (newSlot != null) {
                    slots.put(newSlot, placeable);
                    return 2;
                }
            }
        }
        return 0;
    }

    public Integer addSwingableToInventory(Swingable swingable) {
        int excess = 0;
        Integer slotToFill = findAvailableItemSlot(swingable);
        if (slotToFill == null) {
            Integer newSlot = findEmptySlot();
            if (newSlot != null) {
                slots.put(newSlot, swingable);
                return 2;
            } else {
                return 0;
            }
        }
        Item inventoryItem = (Item)slots.get(slotToFill);
        if (inventoryItem.getQuantity() < inventoryItem.getMaxPerStack()) {
            inventoryItem.setQuantity(inventoryItem.getQuantity() + swingable.getQuantity());
            if (inventoryItem.getQuantity() > inventoryItem.getMaxPerStack()) {
                excess = inventoryItem.getQuantity() - inventoryItem.getMaxPerStack();
                inventoryItem.setQuantity(inventoryItem.getMaxPerStack());
            }
            if (excess == 0) {
                return 1;
            } else {
                swingable.setQuantity(excess);
                Integer newSlot = findEmptySlot();
                if (newSlot != null) {
                    slots.put(newSlot, swingable);
                    return 2;
                }
            }
        }
        return 0;
    }

    public Integer findAvailableMaterialSlot(Material material) {
        //todo think we need to put all the different material types here
        for (Integer i : slots.keySet()) {
            if (material instanceof Consumable && slots.get(i) instanceof Consumable) {
                Consumable inventoryItem = (Consumable) slots.get(i);
                if (inventoryItem.getName().equals(((Consumable) material).getName()) && inventoryItem.getQuantity() < inventoryItem.getMaxPerStack()) {
                    return i;
                }
            } else if (slots.get(i) instanceof Material) {
                Material inventoryMaterial = (Material) slots.get(i);
                if (inventoryMaterial.getName().equals(material.getName()) && inventoryMaterial.getQuantity() < inventoryMaterial.getMaxPerStack()) {
                    return i;
                }
            }
        }
        return null;
    }

    private Integer findAvailableItemSlot(Item item) {
        for (Integer i : slots.keySet()) {
            if (item instanceof Placeable && slots.get(i) instanceof Placeable) {
                Placeable inventoryItem = (Placeable) slots.get(i);
                if (inventoryItem.getPlaceableType().equals(((Placeable) item).getPlaceableType()) && inventoryItem.getQuantity() < inventoryItem.getMaxPerStack()) {
                    return i;
                }
            } else if (item instanceof Swingable && slots.get(i) instanceof Swingable) {
                Swingable inventoryItem = (Swingable) slots.get(i);
                if (inventoryItem.getSwingableType().equals(((Swingable) item).getSwingableType()) && inventoryItem.getQuantity() < inventoryItem.getMaxPerStack()) {
                    return i;
                }
            }
//            } else if (item instanceof Throwable && slots.get(i) instanceof Throwable) {
//                Throwable inventoryItem = (Throwable) slots.get(i);
//                if (inventoryItem.getThrowableType().equals(((Throwable) item).getThrowableType()) && inventoryItem.getQuantity() < inventoryItem.getMaxPerStack()) {
//                    return i;
//                }
//            } else if (slots.get(i) instanceof Item) {
//                Item inventoryItem = (Item) slots.get(i);
//                if (inventoryItem.getItemType().equals(item.getItemType()) && inventoryItem.getQuantity() < inventoryItem.getMaxPerStack()) {
//                    return i;
//                }
//            }

            else {
                Material inventoryMaterial = (Material) slots.get(i);
                if (inventoryMaterial != null && inventoryMaterial.getName().equals(item.getName()) && inventoryMaterial.getQuantity() < inventoryMaterial.getMaxPerStack()) {
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
        } else if (object instanceof Swingable) {
            Swingable item = (Swingable) object;
            for (Object o : slots.values()) {
                if (o instanceof Swingable && ((Swingable) o).getName().equals(item.getName()) && ((Material) o).getQuantity() >= item.getQuantity()) {
                    return true;
                }
            }
        } else if (object instanceof Item) {
            Item item = (Item) object;
            for (Object o : slots.values()) {
                if (o instanceof Item && ((Item) o).getName().equals(item.getName()) && ((Material) o).getQuantity() >= item.getQuantity()) {
                    return true;
                }
            }
        } else if (object instanceof Material) {
            Material material = (Material) object;
            for (Object o : slots.values()) {
                if (o instanceof Material && ((Material) o).getName().equals(material.getName()) && ((Material) o).getQuantity() >= material.getQuantity()) {
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
            if (o instanceof Material && ((Material) o).getName().equals(material.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean removeMaterial(Material material) {
        for (Integer i  : slots.keySet()) {
            Material inventoryMaterial = (Material) slots.get(i);
            if (inventoryMaterial != null && material.getName().equals(inventoryMaterial.getName()) && material.getQuantity() >= 0) {
                inventoryMaterial.setQuantity(inventoryMaterial.getQuantity() - material.getQuantity());
                if (inventoryMaterial.getQuantity() <= 0) {
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
                if (inventoryItem != null && item.getName().equals(inventoryItem.getName()) && item.getQuantity() >= item.getQuantity()) {
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
