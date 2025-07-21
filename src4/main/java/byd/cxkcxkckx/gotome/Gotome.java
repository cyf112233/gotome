package byd.cxkcxkckx.gotome;

import byd.cxkcxkckx.gotome.client.GotomeClient;
import net.fabricmc.api.ClientModInitializer;

public class Gotome implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        GotomeClient.init();
    }
}
