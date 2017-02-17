package fr.violet.mirror.lib4java.usb;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class UsbDeviceManager implements Closeable {

    private static Logger LOGGER = Logger.getLogger(UsbDeviceManager.class.getName());

    private final UsbDevice dev;
    private final Thread executor;
    private final List<UsbDeviceListener> listeners = new ArrayList<>();

    public UsbDeviceManager(final UsbDevice dev) {
        this.dev = dev;
        executor = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LOGGER.info("Thread started!");
                    while (true) {
                        process();
                    }
                } catch (InterruptedException e) {
                    // Stop looping
                    Thread.interrupted();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    LOGGER.info("Thread stopped!");
                }
            }
        });
    }

    public void register(UsbDeviceListener listener) {
        this.listeners.add(listener);
    }

    public void start() {
        executor.start();
    }

    public void stop() throws InterruptedException {
        executor.interrupt();
        executor.join();
    }

    private void process() throws InterruptedException, IOException {

        byte[] packet = dev.read();
        if (!isNull(packet)) {
            LOGGER.fine(() -> "Processing : " + Arrays.toString(packet));
            for (UsbDeviceListener listener : listeners) {
                listener.packet(packet);
            }
        }
    }

    private boolean isNull(byte[] packet) {
        return packet[0] == 0x00 && packet[1] == 0x00;
    }

    public void write(byte[] packet) throws IOException {
        dev.write(packet);
    }

    @Override
    public void close() throws IOException {
        try {
            stop();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        dev.close();
    }

}
