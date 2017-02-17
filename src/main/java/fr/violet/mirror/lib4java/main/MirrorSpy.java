package fr.violet.mirror.lib4java.main;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import fr.violet.mirror.lib4java.Direction;
import fr.violet.mirror.lib4java.MirrorDevice;
import fr.violet.mirror.lib4java.Orientation;
import fr.violet.mirror.lib4java.usb.UsbDevice;

public class MirrorSpy {
    public static void main(String[] args) throws InterruptedException, IOException {

        final Level level = Level.FINE;
        Logger.getGlobal().setLevel(level);
        Logger.getLogger("fr.violet.mirror").setLevel(level);
        LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.FINE);
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(level);

        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            handler.setLevel(level);
        }

        logger.setLevel(level);

        String devname = args[0];
        MirrorDevice dev = new MirrorDevice(new UsbDevice(devname));
        dev.register((Orientation p) -> {
            System.out.println("Fliped " + p.toString());
        });
        dev.register((String t, Direction d) -> {
            System.out.println("Tag " + t + " " + d.toString());
        });

        // FIXME
        Thread.sleep(500);

        System.out.println("Id " + dev.getId());
        System.out.println("AppVersion " + dev.getAppVersion());
        System.out.println("BootVersion " + dev.getBootVersion());
        System.out.println("State " + dev.getState());
        for (String tag : dev.getTags()) {
            System.out.println("Tag " + tag + " " + Direction.IN);
        }

        final int timeout = Integer.parseInt(args[1]);
        Thread.sleep(timeout);

        // Closing
        dev.close();
    }

}
