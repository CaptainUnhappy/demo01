package com.example;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;

import static com.example.kazuya.isPortInUse;
import static com.example.kazuya.startLiveServer;

public final class Demo extends JavaPlugin {
    public static final Demo INSTANCE = new Demo();

    private Demo() {
        super(new JvmPluginDescriptionBuilder("com.example.demo", "0.5.0")
                .name("Demo")
                .author("Administrator")
                .build());
    }

    @Override
    public void onEnable() {
        getLogger().info("demo01 loaded!");
//        Bot bot = BotFactory.INSTANCE.newBot(3091325189l,"test2011140!");
//        bot.login();
            GlobalEventChannel.INSTANCE.registerListenerHost(new test());

        GlobalEventChannel.INSTANCE.registerListenerHost(new test01());
        GlobalEventChannel.INSTANCE.registerListenerHost(new test02Fix02());
        GlobalEventChannel.INSTANCE.registerListenerHost(new test03());
        GlobalEventChannel.INSTANCE.registerListenerHost(new test04());
        GlobalEventChannel.INSTANCE.registerListenerHost(new kazuya());
        GlobalEventChannel.INSTANCE.registerListenerHost(new rise());

//        GlobalEventChannel.INSTANCE.subscribeAlways(MessageEvent.class,g -> {
//
//        });

        if (!isPortInUse(9000)) {
            startLiveServer();
            getLogger().info("startLiveServer!");
        }
    }
}