package fr.violet.mirror.lib4java.main;

import java.io.IOException;
import java.util.Arrays;

import fr.violet.mirror.lib4java.usb.UsbDevice;

public class DeviceSpy {
    public static void main(String[] args) throws InterruptedException, IOException {
        /*
         * final Level level = Level.WARNING;
         * Logger.getGlobal().setLevel(level);
         * Logger.getLogger("fr.violet.mirror").setLevel(level);
         * LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.FINE);
         * Logger rootLogger = Logger.getLogger("");
         * rootLogger.setLevel(level);
         *
         * Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
         *
         * Handler[] handlers = rootLogger.getHandlers();
         * for (Handler handler : handlers) {
         * handler.setLevel(level);
         * }
         *
         * logger.setLevel(level);
         */
        String devname = args[0];
        UsbDevice dev = new UsbDevice(devname);

        while (true) {
            byte[] packet = dev.read();
            System.out.println(Arrays.toString(packet));
        }

        // Closing
        // dev.close();
    }

}
