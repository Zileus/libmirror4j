package fr.violet.mirror.lib4java;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import fr.violet.mirror.lib4java.usb.UsbDevice;
import fr.violet.mirror.lib4java.usb.UsbDeviceListener;
import fr.violet.mirror.lib4java.usb.UsbDeviceManager;

public class MirrorDevice implements Closeable, UsbDeviceListener {

    private static Logger LOGGER = Logger.getLogger(MirrorDevice.class.getName());

    private String id;
    private String app_version;
    private String boot_version;
    private Orientation state = Orientation.Up;
    private HashSet<String> tagList = new HashSet<String>();
    private List<TagListener> tagListeners = new ArrayList<>();
    private List<OrientationListener> orientationListeners = new ArrayList<>();

    private final UsbDeviceManager device;

    public MirrorDevice(UsbDevice device) throws IOException {
        this.device = new UsbDeviceManager(device);
        this.device.register(this);
        this.device.start();

        // Force refreshing info
        this.device.write(EventType.GetMirrorId.toBytes());
        this.device.write(EventType.GetApplicationVersion.toBytes());
        this.device.write(EventType.GetBootloaderVersion.toBytes());
        this.device.write(EventType.GetOrientation.toBytes());
    }

    public String getId() {
        return id;
    }

    public String getAppVersion() {
        return app_version;
    }

    public String getBootVersion() {
        return boot_version;
    }

    public Orientation getState() {
        return state;
    }

    public String getStateAsString() {
        return state.toString();
    }

    public String[] getTags() {
        return tagList.toArray(new String[0]);
    }

    public void reset() {
        tagList.clear();
    }

    public void register(TagListener listener) {
        tagListeners.add(listener);
    }

    public void register(OrientationListener listener) {
        orientationListeners.add(listener);
    }

    @Override
    public void close() throws IOException {
        try {
            device.stop();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        device.close();
    }

    @Override
    public void packet(byte[] packet) {
        EventType type = EventType.valueOf(packet);
        LOGGER.fine(() -> "Processing: " + type.name());
        switch (type) {
            case MirrorId:
                setId(packet);
                break;
            case OrientationDown:
                setOrientation(Orientation.Down);
                break;
            case OrientationUp:
                setOrientation(Orientation.Up);
                break;
            case ApplicationVersion:
                setAppVersion(packet);
                break;
            case BootloaderVersion:
                setBootVersion(packet);
                break;
            case HideTag:
                tag(Direction.OUT, packet);
                break;
            case ShowTag:
                tag(Direction.IN, packet);
                break;
            default:
                throw new EnumConstantNotPresentException(EventType.class, packet.toString());
        }
    }

    private void setBootVersion(byte[] packet) {
        LOGGER.info("Bootloader version");
        this.boot_version = toString(getPayload(packet));
    }

    private void setAppVersion(byte[] packet) {
        LOGGER.info("Application version");
        this.app_version = toString(getPayload(packet));
    }

    private void setOrientation(Orientation orient) {
        LOGGER.info("Orientation: " + orient);
        this.state = orient;
    }

    private void tag(Direction dir, byte[] packet) {
        LOGGER.info("Tag: " + dir);
        String tag = toString(getPayload(packet));
        switch (dir) {
            case IN:
                tagList.add(tag);
                break;
            case OUT:
                tagList.remove(tag);
                break;

            default:
                throw new EnumConstantNotPresentException(Direction.class, dir.toString());
        }
    }

    private void setId(byte[] packet) {
        LOGGER.info("setId");
        this.id = toString(getPayload(packet));
    }

    private byte[] getPayload(byte[] packet) {
        // Header: 2 bytes
        //
        int len = packet[4];
        LOGGER.fine("Len=" + len);

        byte[] payload = new byte[len];
        System.arraycopy(packet, 5, payload, 0, len);

        return payload;
    }

    private String toString(byte[] packet) {
        StringBuffer buf = new StringBuffer(packet.length * 2);
        for (byte b : packet) {
            buf.append(String.format("%x02", b));
        }
        return buf.toString();
    }
}
