package uk.co.conclipse.PhytoMining;


import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import uk.co.conclipse.PhytoMining.proxy.CommonProxy;

@Mod(name=PhytoMining.MODNAME, modid=PhytoMining.MODID, version=PhytoMining.VERSION)
public class PhytoMining {
    public static final String MODID = "phytomining";
    public static final String MODNAME = "Phyto Mining";
    public static final String VERSION = "0.1";

    @SidedProxy(clientSide = "uk.co.conclipse.PhytoMining.proxy.ClientProxy", serverSide = "uk.co.conclipse.PhytoMining.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static PhytoMining instance;

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }

}
