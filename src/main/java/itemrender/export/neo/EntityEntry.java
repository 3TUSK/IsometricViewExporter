package itemrender.export.neo;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

import net.minecraft.entity.EntityType;

public class EntityEntry {

    public String type = "entity";

    @SerializedName("registry_name")
    public String registryName;

    public Map<String, String> name;

    public String icon;

    /**
     * A marker field to denote whether this entity
     * is "living entity".
     * Technical-wise, this implies `instaceof LivingEntity`.
     */
    public boolean living;

	public transient EntityType<?> entityType;
}