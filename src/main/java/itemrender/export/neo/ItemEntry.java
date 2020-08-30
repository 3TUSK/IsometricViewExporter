package itemrender.export.neo;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

import net.minecraft.item.ItemStack;

public final class ItemEntry {
    
    public String type = "item";

    @SerializedName("registry_name")
    public String registryName;

    public Map<String, String> name;

    public Map<String, String> icon;

    /**
     * The value of this field shall be obtained by concatenating values of {@link #tags} field, 
     * joining on {@code ,}, as if executing the following code:
     * <pre>
     * ItemEntry entry = ...;
     * entry.tags = ....;
     * entry.ores = String.join(",", entry.tags);
     * </pre>
     * @deprecated Exist only for backward compatibility. Under most cases, using {@link #tags}
     *             is preferred.
     */
    @Deprecated
    public String ores;

    /**
     * All known tags in {@link String} form.
     * 
     * @see net.minecraft.tags.ItemTags
     * @see net.minecraft.tags.Tag
     */
    public List<String> tags;

    @SerializedName("max_damage")
    public int maxDamage;
    @SerializedName("max_stack_size")
    public int maxStackSize;

    // Not serialized
    transient ItemStack example = ItemStack.EMPTY;

}