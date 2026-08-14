package io.grasscutter;

import static org.junit.jupiter.api.Assertions.*;

import com.google.protobuf.CodedOutputStream;
import emu.grasscutter.utils.ProtoRead;
import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pins the schema-free reader against the shape a real 7.0.0 client put on the wire on 2026-08-12.
 *
 * <p>The field numbers here are the capture, not a guess, so these cases double as the record of
 * it: if a later dump disagrees with one of them, the dump is what needs re-checking.
 */
public class ProtoReadTest {

    /** The captured GetPlayerTokenReq: account id, key slot, RSA blob and version, in wire order. */
    private static byte[] tokenRequest() throws Exception {
        var baos = new ByteArrayOutputStream();
        var cos = CodedOutputStream.newInstance(baos);
        cos.writeString(2, "10008"); // account_uid
        cos.writeUInt32(3, 3); // platform_type
        cos.writeString(6, "e6351edecc2e59d407bead96".repeat(2)); // account_token, 64 hex
        cos.writeUInt32(7, 1);
        cos.writeUInt32(9, 1);
        cos.writeString(116, "csc");
        cos.writeUInt32(476, 2);
        cos.writeUInt32(578, 1);
        cos.writeUInt32(588, 5); // key_id
        cos.writeUInt32(726, 1);
        cos.writeString(932, "PjvDek0WUoEs90OfSt/K0Djf"); // client_rand_key
        cos.writeString(1397, "OSRELWin7.0.0");
        cos.flush();
        return baos.toByteArray();
    }

    @Test
    @DisplayName("reads the four token fields the handshake needs")
    public void readsTokenRequest() throws Exception {
        var payload = tokenRequest();
        assertEquals("10008", ProtoRead.string(payload, 2));
        assertEquals("e6351edecc2e59d407bead96".repeat(2), ProtoRead.string(payload, 6));
        assertEquals(5, ProtoRead.varint(payload, 588));
        assertEquals("PjvDek0WUoEs90OfSt/K0Djf", ProtoRead.string(payload, 932));
        assertEquals("OSRELWin7.0.0", ProtoRead.string(payload, 1397));
    }

    @Test
    @DisplayName("an absent field reads as empty rather than throwing")
    public void absentFieldsAreEmpty() throws Exception {
        var payload = tokenRequest();
        assertEquals(0, ProtoRead.varint(payload, 41)); // the 6.7 key_id number, gone in 7.0
        assertEquals("", ProtoRead.string(payload, 1475)); // the 6.7 client_rand_key number
        assertEquals(0, ProtoRead.varint(payload, 99999));
    }

    @Test
    @DisplayName("skips a fixed32 to reach the fields behind it")
    public void skipsFixedWidthFields() throws Exception {
        // The captured PingReq is #1 varint, #3 fixed32, #7 varint, #13 varint - so every field the
        // ping handler wants sits BEHIND a fixed-width one. Mis-measuring that skip would not throw,
        // it would silently read garbage from the middle of the next field.
        var baos = new ByteArrayOutputStream();
        var cos = CodedOutputStream.newInstance(baos);
        cos.writeUInt32(1, 1786577216); // client_time
        cos.writeFixed32(3, 1069547520);
        cos.writeUInt32(7, 60); // seq
        cos.writeUInt64(13, 2581401600L);
        cos.flush();
        var payload = baos.toByteArray();

        assertEquals(1786577216L, ProtoRead.varint(payload, 1));
        assertEquals(60, ProtoRead.varint(payload, 7));
        assertEquals(2581401600L, ProtoRead.varint(payload, 13));
    }

    @Test
    @DisplayName("skips a fixed64 and a nested message the same way")
    public void skipsLongAndNestedFields() throws Exception {
        var inner = new ByteArrayOutputStream();
        var icos = CodedOutputStream.newInstance(inner);
        icos.writeUInt32(7, 999); // same number as the field we want, one level down
        icos.flush();

        var baos = new ByteArrayOutputStream();
        var cos = CodedOutputStream.newInstance(baos);
        cos.writeFixed64(2, Long.MAX_VALUE);
        cos.writeByteArray(4, inner.toByteArray());
        cos.writeUInt32(7, 60);
        cos.flush();

        // 999 lives inside the nested message, so a reader that walked into it would return that.
        assertEquals(60, ProtoRead.varint(baos.toByteArray(), 7));
    }

    @Test
    @DisplayName("a truncated payload gives up instead of throwing")
    public void truncatedPayloadIsSafe() throws Exception {
        var payload = tokenRequest();
        var cut = new byte[12];
        System.arraycopy(payload, 0, cut, 0, cut.length);
        assertDoesNotThrow(() -> ProtoRead.string(cut, 932));
        assertDoesNotThrow(() -> ProtoRead.varint(new byte[] {(byte) 0xFF}, 1));
        assertEquals("", ProtoRead.string(new byte[0], 2));
        assertEquals(0, ProtoRead.varint(null, 2));
    }
}
