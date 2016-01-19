//package net.etfbl.kdpo.client;

import net.etfbl.kdpo.client.VirtualAlbum;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Vladan on 1/19/2016.
 */
public class VirtualAlbumTest {

    @Test
    public void testGetName() throws Exception {
        VirtualAlbum va = new VirtualAlbum("Album", "Opis albuma");
        String expected = "Album";
        String actual = va.getName();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetDescription() throws Exception {
        VirtualAlbum va = new VirtualAlbum("Album", "Opis albuma");
        String expected = "Opis albuma";
        String actual = va.getDescription();
        assertEquals(expected, actual);
    }

    @Test
    public void testToString() throws Exception {
        VirtualAlbum va = new VirtualAlbum("Album", "Opis albuma");
        String expected = "Album";
        String actual = va.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testIsTemporary() throws Exception {
        VirtualAlbum va = new VirtualAlbum("Album", "Opis albuma");
        boolean expected = false;
        boolean actual = va.isTemporary();
        assertEquals(expected, actual);
        va = new VirtualAlbum("Album1", "Opis1", true);
        expected = true;
        actual = va.isTemporary();
        assertEquals(expected, actual);
    }

    @Test
    public void testEquals() throws Exception {
        VirtualAlbum va1 = new VirtualAlbum("Album", "Opis1");
        VirtualAlbum va2 = new VirtualAlbum("Album", "Opis2");
        VirtualAlbum va3 = new VirtualAlbum("Album1", "Opis3");
        boolean expected = true;
        boolean actual = va2.equals(va1);
        assertEquals(expected, actual);
        expected = false;
        actual = va3.equals(va2);
        assertEquals(expected, actual);
    }

    @Test
    public void testSetName() throws Exception {
        VirtualAlbum va = new VirtualAlbum("Album", "Opis");
        va.setName("Naziv");
        String expected = "Naziv";
        String actual = va.getName();
        assertEquals(expected, actual);
    }
}