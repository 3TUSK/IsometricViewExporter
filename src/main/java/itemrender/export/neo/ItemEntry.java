package itemrender.export.neo;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

import net.minecraft.item.ItemStack;

public final class ItemEntry {
    
    public String type = "item";

    @SerializedName("registry_name")
    public String registryName;

    public Map<String, String> name;

    public Map<String, String> icon;

    @SerializedName("max_damage")
    public int maxDamage;
    @SerializedName("max_stack_size")
    public int maxStackSize;

    // Not serialized
    transient ItemStack example = ItemStack.EMPTY;

}