package life.unwitting.SNMP;

import life.unwitting.SNMP.ManagementInformationBase.printer;
import life.unwitting.SNMP.model.supplies;
import life.unwitting.lib;
import life.unwitting.log4j;
import life.unwitting.thread4j;
import org.json.JSONObject;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

@SuppressWarnings({"unused", "rawtypes"})
public class snmp4j {
    public static final String Pub = "public";
    public static final String iso = "1.3.";
    public static final String ValueKey = "value";
    public static final String ReadyKey = "ready";
    public static final int NotExists = 0x5;
    public static final int Integer32 = 0x2;
    public static final int Counter32 = 0x41;
    public static final int StringType = 0x42;
    public static final int MAX_BUFFER = 1024;
    public static final int defaultPort = 161;
    public static final int defaultRetries = 1;
    public static final int defaultTimeout = 3000;

    public int timeout = snmp4j.defaultTimeout;
    protected String host;
    protected int port = snmp4j.defaultPort;
    protected int retries = 1;
    protected Object receivedData;

    public snmp4j(String host) {
        this.host = host;
    }

    public snmp4j(String host, int port) {
        this.host = host;
        this.port = port;
    }

    protected static CommunityTarget newCommunityTarget(String host) {
        return snmp4j.newCommunityTarget(host, snmp4j.defaultPort, snmp4j.defaultRetries, snmp4j.defaultTimeout);
    }

    protected static CommunityTarget newCommunityTarget(String host, int port) {
        return snmp4j.newCommunityTarget(host, port, snmp4j.defaultRetries, snmp4j.defaultTimeout);
    }

    protected static CommunityTarget newCommunityTarget(String host, int port, int retries, int timeout) {
        CommunityTarget community = null;
        if (lib.notNullOrEmpty(host)) {
            Address addr = GenericAddress.parse("udp:" + host + "/" + port);
            if (addr != null) {
                community = new CommunityTarget();
                community.setCommunity(new OctetString(snmp4j.Pub));
                community.setAddress(addr);
                community.setRetries(retries);
                community.setTimeout(timeout);
                community.setVersion(SnmpConstants.version2c);
            }
        }
        return community;
    }

    public int find(byte[] src, byte[] find) {
        return this.find(src, 0, find, 0);
    }

    public int find(byte[] src, byte[] find, int findOffset) {
        return this.find(src, 0, find, findOffset);
    }

    public int find(byte[] src, int srcOffset, byte[] find, int findOffset) {
        int position = -1;
        if (lib.of(src).isJsonString() && lib.of(find).isJsonString()) {
            for (int i = srcOffset; i < src.length; i++) {
                boolean hit = true;
                int k = 0;
                for (int j = findOffset; j < find.length; j++, k++) {
                    if (src[i + k] != find[j]) {
                        hit = false;
                        break;
                    }
                }
                if (hit) {
                    position = i;
                    break;
                }
            }
        }
        return position;
    }

    @SuppressWarnings("UnusedAssignment")
    public String invokeRaw(final String oid, final boolean isGetNext) {
        String value = null;
        String src = oid;
        try {
            ArrayList<Byte> raw = new ArrayList<Byte>();
            byte[] head = new byte[]{
                    0x30, 0x31, 0x02, 0x01, 0x00, 0x04, 0x06, 0x70, 0x75, 0x62, 0x6c, 0x69, 0x63, (byte) 0xa0, 0x24, 0x02,
                    0x04, 0x1c, 0x62, 0x17, (byte) 0x9a, 0x02, 0x01, 0x00, 0x02, 0x01, 0x00, 0x30, 0x16, 0x30, 0x14, 0x06,
            };
            if (isGetNext) {
                head = new byte[]{
                        0x30, 0x2b, 0x02, 0x01, 0x00, 0x04, 0x06, 0x70, 0x75, 0x62, 0x6c, 0x69, 0x63, (byte) 0xa1, 0x1e, 0x02,
                        0x04, 0x00, 0x00, 0x00, 0x00, 0x02, 0x01, 0x00, 0x02, 0x01, 0x00, 0x30, 0x10, 0x30, 0x0e, 0x06};
            }
            byte[] requestId = lib.of(new Random().nextInt()).ToBytes(true);
            int requestOffset = 17;
            head[requestOffset++] = requestId[0];
            head[requestOffset++] = requestId[1];
            head[requestOffset++] = requestId[2];
            head[requestOffset++] = requestId[3];
            for (byte e : head) {
                raw.add(e);
            }
            if (src.startsWith(snmp4j.iso)) {
                src = src.substring(snmp4j.iso.length());
            }
            byte[] serialOid = null;
            String[] parts = src.split("\\.");
            if (parts.length > 0) {
                ArrayList<Byte> tmp = new ArrayList<Byte>();
                tmp.add((byte) 0);
                tmp.add((byte) 0x2b);
                for (String e : parts) {
                    int i = Integer.parseInt(e);
                    if (i > 127) {
                        tmp.add((byte) (128 + (i / 128)));
                        tmp.add((byte) (i - ((i / 128) * 128)));
                    } else {
                        tmp.add((byte) i);
                    }
                }
                tmp.set(0, (byte) (tmp.size() - 1));
                Byte[] bytes = tmp.toArray(new Byte[]{});
                serialOid = new byte[bytes.length];
                for (int i = 0; i < serialOid.length; i++) {
                    serialOid[i] = bytes[i];
                }
            }
            if (serialOid != null) {
                for (byte e : serialOid) {
                    raw.add(e);
                }
                char[] tail = new char[]{0x05, 0x00};
                for (char e : tail) {
                    raw.add((byte) e);
                }
                Byte[] boxed = raw.toArray(new Byte[]{});
                byte[] data = new byte[boxed.length];
                for (int i = 0; i < data.length; i++) {
                    data[i] = boxed[i];
                }
                DatagramSocket udpSocket = new DatagramSocket();
                JSONObject jsonObject = new JSONObject();
                final snmp4j inst = this;
                Thread thread = new Thread(new thread4j(udpSocket, serialOid, jsonObject, requestId) {
                    @Override
                    public void run() {
                        try {
                            DatagramSocket udpSocket = (DatagramSocket) this.parameter;
                            byte[] serialOid = (byte[]) this.parameter2;
                            JSONObject jsonObject = (JSONObject) this.parameter3;
                            byte[] buffer = new byte[snmp4j.MAX_BUFFER];
                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                            jsonObject.put(snmp4j.ReadyKey, true);
                            while (true) {
                                udpSocket.receive(packet);
                                String ip = packet.getAddress().getHostAddress();
                                if (packet.getAddress().getHostAddress().equalsIgnoreCase(inst.host) &&
                                        packet.getPort() == snmp4j.defaultPort) {
                                    if (packet.getLength() > 0) {
                                        int requestId = inst.find(buffer, new byte[]{
                                                0x02, 0x04,
                                                ((byte[]) this.parameter4)[0],
                                                ((byte[]) this.parameter4)[1],
                                                ((byte[]) this.parameter4)[2],
                                                ((byte[]) this.parameter4)[3]});
                                        if (requestId >= 0) {
                                            int position = inst.find(buffer, serialOid, 1);
                                            if (position >= 0) {
                                                int oidLength = buffer[position - 1];
                                                int valueType = buffer[position + oidLength];
                                                int valueLength = buffer[position + oidLength + 1];
                                                byte[] value = new byte[valueLength];
                                                int idx = 0;
                                                for (int i = position + oidLength + 2; i < position + oidLength + 2 + valueLength; i++) {
                                                    value[idx++] = buffer[i];
                                                }
                                                switch (valueType) {
                                                    case snmp4j.Integer32:
                                                    case snmp4j.Counter32: {
                                                        jsonObject.put(snmp4j.ValueKey, (int) lib.of(value).toLong(true));
                                                    }
                                                    break;
                                                    case snmp4j.NotExists: {
                                                        log4j.info(String.format("snmp4j.invokeRaw, %s: %s not exists", inst.host, oid));
                                                    }
                                                    break;
                                                    default: {
                                                        log4j.info(String.format("snmp4j.invokeRaw, Can't convert from type %s", valueType));
                                                    }
                                                    break;
                                                }
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            log4j.err(e);
                        }
                    }
                });
                thread.start();
                while (!jsonObject.has(snmp4j.ReadyKey)) {
                    Thread.sleep(10);
                }
                udpSocket.send(new DatagramPacket(data, data.length, InetAddress.getByName(this.host), snmp4j.defaultPort));
                thread.join(this.timeout);
                if (jsonObject.has(snmp4j.ValueKey)) {
                    value = String.valueOf(jsonObject.getInt(snmp4j.ValueKey));
                }
                udpSocket.close();
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return value;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public String invoke(String oid, boolean isTryNextOidWhenOidMissing) {
        String value = null;
        try {
            if (lib.notNullOrEmpty(oid) &&
                    lib.notNullOrEmpty(this.host) &&
                    lib.of(this.port).ToPort().isJsonString()) {
                CommunityTarget communityTarget = snmp4j.newCommunityTarget(this.host, this.port, this.retries, this.timeout);
                if (communityTarget != null) {
                    TransportMapping transport = new DefaultUdpTransportMapping();
                    Snmp snmp = new Snmp(transport);
                    transport.listen();
                    PDU pdu = new PDU();
                    pdu.add(new VariableBinding(new OID(oid)));
                    for (int i = 1; i <= (isTryNextOidWhenOidMissing ? 2 : 1); i++) {
                        ResponseEvent e;
                        if (i == 1) {
                            e = snmp.get(pdu, communityTarget);
                        } else {
                            e = snmp.getNext(pdu, communityTarget);
                        }
                        if (e != null && e.getResponse() != null) {
                            Vector recVBs = e.getResponse().getVariableBindings();
                            for (int j = 0; j < recVBs.size(); j++) {
                                VariableBinding recVB = (VariableBinding) recVBs.elementAt(j);
                                if (!recVB.isException()) {
                                    Variable var = recVB.getVariable();
                                    value = recVB.getVariable().toString();
                                    if (Null.class.isAssignableFrom(var.getClass())) {
                                        assert (false);
                                    } else if (Counter64.class.isAssignableFrom(var.getClass())) {
                                        assert (false);
                                    } else if (Integer32.class.isAssignableFrom(var.getClass())) {
                                        assert (false);
                                    } else if (OID.class.isAssignableFrom(var.getClass())) {
                                        assert (false);
                                    } else if (UnsignedInteger32.class.isAssignableFrom(var.getClass())) {

                                    } else if (SMIAddress.class.isAssignableFrom(var.getClass())) {
                                        assert (false);
                                    } else if (VariantVariable.class.isAssignableFrom(var.getClass())) {
                                        assert (false);
                                    } else if (OctetString.class.isAssignableFrom(var.getClass())) {

                                    } else {
                                        assert (false);
                                    }
                                    break;
                                }
                            }
                        }
                        if (value != null) {
                            break;
                        }
                    }
                    transport.close();
                    snmp.close();
                }
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return value;
    }

    @SuppressWarnings("rawtypes")
    public static LinkedHashMap<String, String> walk(String host, String tableOid) {
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
        try {
            TransportMapping transport = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(transport);
            transport.listen();
            TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
            List events = treeUtils.getSubtree(snmp4j.newCommunityTarget(host), new OID(tableOid));
            if (events != null) {
                for (Object e : events) {
                    if (e instanceof TreeEvent) {
                        TreeEvent event = (TreeEvent) e;
                        if (!event.isError()) {
                            VariableBinding[] varBindings = event.getVariableBindings();
                            if (lib.notNullOrZeroLength(varBindings)) {
                                for (VariableBinding varBinding : varBindings) {
                                    if (varBinding != null) {
                                        result.put("." + varBinding.getOid().toString(), varBinding.getVariable().toString());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            transport.close();
            snmp.close();
        } catch (Exception e) {
            log4j.err(e);
        }
        return result;
    }

    public LinkedHashMap<String, String> meters() {
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
        result.put(printer.prtMarkerLifeCount, this.invokeRaw(printer.prtMarkerLifeCount, true));
        result.put(printer.prtMarkerLifeCount1, this.invokeRaw(printer.prtMarkerLifeCount1, false));
        result.put(printer.prtMarkerLifeCount2, this.invokeRaw(printer.prtMarkerLifeCount2, false));
        result.put(printer.prtMarkerLifeCount3, this.invokeRaw(printer.prtMarkerLifeCount3, false));
        result.put(printer.prtMarkerLifeCount4, this.invokeRaw(printer.prtMarkerLifeCount4, false));
        result.put(printer.prtMarkerLifeCount5, this.invokeRaw(printer.prtMarkerLifeCount5, false));
        return result;
    }

    @SuppressWarnings("DuplicatedCode")
    public supplies supplies() {
        supplies supplies = null;
        if (lib.notNullOrEmpty(this.host)) {
            supplies = new supplies();
            for (Map.Entry<String, String> e : snmp4j.walk(this.host, printer.prtMarkerSuppliesLevel).entrySet()) {
                if (lib.notNullOrEmpty(e.getKey())) {
                    switch (lib.of(e.getKey().substring(e.getKey().lastIndexOf('.') + 1)).toInt()) {
                        case 1:
                            supplies.Key = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.Key: " + supplies.Key);
                            break;
                        case 30:
                            supplies.Key1 = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.Key1: " + supplies.Key1);
                            break;
                        case 31:
                            supplies.Key2 = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.Key2: " + supplies.Key2);
                            break;
                        case 2:
                            supplies.Yellow = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.Yellow: " + supplies.Yellow);
                            break;
                        case 3:
                            supplies.Magenta = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.Magenta: " + supplies.Magenta);
                            break;
                        case 4:
                            supplies.Cyan = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.Cyan: " + supplies.Cyan);
                            break;
                        case 6:
                            supplies.DurmKey = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.DurmKey: " + supplies.DurmKey);
                            break;
                        case 7:
                            supplies.DurmYellow = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.DurmYellow: " + supplies.DurmYellow);
                            break;
                        case 8:
                            supplies.DurmMagenta = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.DurmMagenta: " + supplies.DurmMagenta);
                            break;
                        case 9:
                            supplies.DurmCyan = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.DurmCyan: " + supplies.DurmCyan);
                            break;
                    }
                }
            }
            for (Map.Entry<String, String> e : snmp4j.walk(this.host, printer.prtMarkerSuppliesMaxCapacity).entrySet()) {
                if (lib.notNullOrEmpty(e.getKey())) {
                    switch (lib.of(e.getKey().substring(e.getKey().lastIndexOf('.') + 1)).toInt()) {
                        case 1:
                            supplies.MaxKey = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.MaxKey: " + supplies.MaxKey);
                            break;
                        case 30:
                            supplies.MaxKey1 = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.MaxKey1: " + supplies.MaxKey1);
                            break;
                        case 31:
                            supplies.MaxKey2 = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.MaxKey2: " + supplies.MaxKey2);
                            break;
                        case 2:
                            supplies.MaxYellow = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.MaxYellow: " + supplies.MaxYellow);
                            break;
                        case 3:
                            supplies.MaxMagenta = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.MaxMagenta: " + supplies.MaxMagenta);
                            break;
                        case 4:
                            supplies.MaxCyan = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.MaxCyan: " + supplies.MaxCyan);
                            break;
                        case 6:
                            supplies.MaxDurmKey = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.MaxDurmKey: " + supplies.MaxDurmKey);
                            break;
                        case 7:
                            supplies.MaxDurmYellow = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.MaxDurmYellow: " + supplies.MaxDurmYellow);
                            break;
                        case 8:
                            supplies.MaxDurmMagenta = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.MaxDurmMagenta: " + supplies.MaxDurmMagenta);
                            break;
                        case 9:
                            supplies.MaxDurmCyan = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.MaxDurmCyan: " + supplies.MaxDurmCyan);
                            break;
                    }
                }
            }
            for (Map.Entry<String, String> e : snmp4j.walk(this.host, printer.prtMarkerSuppliesSupplyUnit).entrySet()) {
                if (lib.notNullOrEmpty(e.getKey())) {
                    switch (lib.of(e.getKey().substring(e.getKey().lastIndexOf('.') + 1)).toInt()) {
                        case 1:
                            supplies.KeyUnit = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.KeyUnit: " + supplies.KeyUnit);
                            break;
                        case 30:
                            supplies.KeyUnit1 = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.KeyUnit1: " + supplies.KeyUnit1);
                            break;
                        case 31:
                            supplies.KeyUnit2 = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.KeyUnit2: " + supplies.KeyUnit2);
                            break;
                        case 2:
                            supplies.YellowUnit = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.YellowUnit: " + supplies.YellowUnit);
                            break;
                        case 3:
                            supplies.MagentaUnit = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.MagentaUnit: " + supplies.MagentaUnit);
                            break;
                        case 4:
                            supplies.CyanUnit = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.CyanUnit: " + supplies.CyanUnit);
                            break;
                        case 6:
                            supplies.DurmKeyUnit = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.DurmKeyUnit: " + supplies.DurmKeyUnit);
                            break;
                        case 7:
                            supplies.DurmYellowUnit = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.DurmYellowUnit: " + supplies.DurmYellowUnit);
                            break;
                        case 8:
                            supplies.DurmMagentaUnit = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.DurmMagentaUnit: " + supplies.DurmMagentaUnit);
                            break;
                        case 9:
                            supplies.DurmCyanUnit = lib.of(e.getValue()).toInt();
                            log4j.info("supplies.DurmCyanUnit: " + supplies.DurmCyanUnit);
                            break;
                    }
                }
            }
        }
        return supplies;
    }

    public static String Get(String host, String oid) {
        return new snmp4j(host).invoke(oid, false);
    }

    public static String GetNext(String host, String oid) {
        return new snmp4j(host).invoke(oid, true);
    }
}
