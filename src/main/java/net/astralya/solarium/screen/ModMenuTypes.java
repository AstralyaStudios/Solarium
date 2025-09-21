package net.astralya.solarium.screen;

import net.astralya.solarium.SolariumMod;
import net.astralya.solarium.screen.custom.PhotosmelterMenu;
import net.astralya.solarium.screen.custom.SunflowerGeneratorMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, SolariumMod.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<SunflowerGeneratorMenu>> SUNFLOWER_GENERATOR_MENU =
            registerMenuType("sunflower_generator_menu", SunflowerGeneratorMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<PhotosmelterMenu>> PHOTOSMELTER =
            MENUS.register("photosmelter",
                    () -> IMenuTypeExtension.create((id, inv, buf) -> new PhotosmelterMenu(id, inv))
            );

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>,
                MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
