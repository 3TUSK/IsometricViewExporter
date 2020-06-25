/*
 * Copyright (c) 2015 Jerrell Fang
 *
 * This project is Open Source and distributed under The MIT License (MIT)
 * (http://opensource.org/licenses/MIT)
 *
 * You should have received a copy of the The MIT License along with
 * this project.   If not, see <http://opensource.org/licenses/MIT>.
 */

package itemrender.client.export;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import itemrender.ItemRenderConfig;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityType;

/**
 * Created by Meow J on 8/17/2015.
 *
 * @author Meow J
 */
public class MobData {

    private static final Logger LOGGER = LogManager.getLogger("Item Render");
    private static final Marker MARKER = MarkerManager.getMarker("EntityData");

    private String name;
    private String Englishname;
    private String mod;
    private String registerName;
    private String Icon;
    private transient EntityType<?> mob;
   
    public MobData(EntityType<?> type){

        if (ItemRenderConfig.debugMode.get())
            LOGGER.info(MARKER, I18n.format("itemrender.msg.processing", type.getName()));
        name = null;
        Englishname = null;
        mod = type.getRegistryName().getNamespace();
        registerName = type.getRegistryName().toString();    
        Icon = ExportUtils.INSTANCE.getEntityIcon(type);
        this.mob = type;
    }

    public EntityType<?> getMob() {
        return mob;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setEnglishname(String name) {
        this.Englishname = name;
    }
}
