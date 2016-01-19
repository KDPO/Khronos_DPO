//package net.etfbl.kdpo.server;

import net.etfbl.kdpo.server.KeyGen;

import static org.junit.Assert.*;

/**
 * Created by Vladan on 1/19/2016.
 */
public class KeyGenTest {

    @org.junit.Test
    public void testCheckKey() throws Exception {
        KeyGen kg = new KeyGen();
        kg.generateNewKeys(5);
        String key = kg.getKey();
        boolean expected = true;
        boolean actual = kg.checkKey(key);
        assertEquals(expected, actual);
        expected = false;
        actual = kg.checkKey("1234");
        assertEquals(expected, actual);
    }

    @org.junit.Test
    public void testSetKeyAsUsed() throws Exception {
        KeyGen kg = new KeyGen();
        boolean expected = false;
        boolean actual = kg.setKeyAsUsed("123456");
        assertEquals(expected, actual);
        kg.generateNewKeys(5);
        String key = kg.getKey();
        expected = true;
        actual = kg.setKeyAsUsed(key);
        assertEquals(expected, actual);
        expected = false;
        actual = kg.setKeyAsUsed("1234");
        assertEquals(expected, actual);
    }

    @org.junit.Test
    public void testGenerateNewKeys() throws Exception {
        KeyGen kg = new KeyGen();
        String key = kg.getKey();
        assertNull(key);
        kg.generateNewKeys(3);
        key = kg.getKey();
        assertNotNull(key);
    }

    @org.junit.Test
    public void testGetKey() throws Exception {
        KeyGen kg = new KeyGen();
        String key = kg.getKey();
        assertNull(key);
        kg.generateNewKeys(3);
        key = kg.getKey();
        assertNotNull(key);
    }
}