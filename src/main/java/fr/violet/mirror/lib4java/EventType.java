package fr.violet.mirror.lib4java;

public enum EventType {

    Unspecified(new byte[] { 0x00, 0x00 }),
    GetMirrorId(new byte[] { 0x01, 0x01 }),
    MirrorId(new byte[] { 0x01, 0x02 }),
    GetOrientation(new byte[] { 0x01, 0x03 }),
    OrientationUp(new byte[] { 0x01, 0x04 }),
    OrientationDown(new byte[] { 0x01, 0x05 }),
    EchoRequestResponse(new byte[] { 0x01, (byte) 0xFF }), // From http://blog.nomzit.com/archives/274
    ShowTag(new byte[] { 0x02, 0x01 }),
    HideTag(new byte[] { 0x02, 0x02 }),

    // Choreo commands
    SetChoreoOff(new byte[] { 0x03, 0x01 }), // Returns 0xff32 if fails
    UnknownChoreoCommand02(new byte[] { 0x03, 0x02 }),
    PlayChoreo(new byte[] { 0x03, 0x03 }), // This also reactivates choreos if them are being set off
    SetChoreoPowerOnly(new byte[] { 0x03, 0x04 }),
    UnknownChoreoCommand05(new byte[] { 0x03, 0x05 }),
    UnknownChoreoCommand06(new byte[] { 0x03, 0x06 }),
    GetChoreoInfo(new byte[] { 0x03, 0x07 }), // This could be Get Choreo Info where the data would be volume + heap?
    ChoreoInfo(new byte[] { 0x03, 0x08 }), // Choreo info data where first two bytes are volume and second two are heap

    // Firmware commands
    UsbUpdate(new byte[] { 0x04, 0x01 }),
    GetApplicationVersion(new byte[] { 0x04, 0x04 }),
    ApplicationVersion(new byte[] { 0x04, 0x05 }),
    GetBootloaderVersion(new byte[] { 0x04, 0x06 }),
    BootloaderVersion(new byte[] { 0x04, 0x07 }),
    EnterUsbUpdate(new byte[] { 0x04, 0x09 }),

    // Status messages
    InvalidParameters(new byte[] { (byte) 0xff, 0x01 }),
    UnknownCommand(new byte[] { (byte) 0xff, 0x02 }),
    Error3(new byte[] { (byte) 0xff, 0x03 }),
    Error4(new byte[] { (byte) 0xff, 0x32 }), // This message comes for example when trying to call set choreo off and
                                              // it fails
    ExceptionInCalledMethod(new byte[] { (byte) 0xff, 0x33 }); // From http://blog.nomzit.com/archives/274

    private byte[] header;

    private EventType(byte[] header) {
        this.header = header;
    }

    public boolean match(byte[] packet) {
        for (int i = 0; i < header.length; i++) {
            if ((i >= packet.length) || header[i] != packet[i]) {
                return false;
            }
        }
        return true;
    }

    public byte[] toBytes() {
        return header;
    }

    public static EventType valueOf(byte[] packet) {
        for (EventType type : values()) {
            if (type.match(packet)) {
                return type;
            }
        }
        return null;
    }
}
