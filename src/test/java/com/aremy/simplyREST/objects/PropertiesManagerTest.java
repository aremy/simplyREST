package com.aremy.simplyREST.objects;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class PropertiesManagerTest {
    @Test
    public void loadPersonDataFromFile() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("properties.xml").getFile());
        PropertiesManager propertiesManager = PropertiesManager.instance(file);

        assertEquals("localhost", propertiesManager.proxyHost);
        assertEquals("3128", propertiesManager.proxyPort);
        assertEquals("mylogin", propertiesManager.proxyLogin);
        assertEquals("mypassword", propertiesManager.proxyPassword);
    }
}