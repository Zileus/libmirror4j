package fr.violet.mirror.lib4java.usb;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Logger;

public class UsbDevice implements Closeable {

    private static Logger LOGGER = Logger.getLogger(UsbDevice.class.getName());

    private final InputStream input;
    private final OutputStream output;

    public UsbDevice(String devname) throws IOException {
        LOGGER.info("New device: " + devname);
        Path path = Paths.get(devname);
        input = Files.newInputStream(path);
        output = Files.newOutputStream(path);
    }

    public byte[] read() throws IOException {
        synchronized (input) {
            byte[] packet = new byte[16];
            LOGGER.finest("Available : " + input.available());
            int read = input.read(packet);
            LOGGER.finer(() -> "Read : " + read + " " + Arrays.toString(packet));
            return packet;
        }
    }

    public void write(byte[] packet) throws IOException {
        synchronized (output) {
            output.write(packet);
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (input) {
            input.close();
        }
        synchronized (output) {
            output.close();
        }
    }

}
