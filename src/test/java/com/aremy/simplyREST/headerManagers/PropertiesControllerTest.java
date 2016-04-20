package com.aremy.simplyREST.headerManagers;

import com.aremy.simplyREST.generated.Properties;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class PropertiesControllerTest {
    @Test
    public void loadPersonDataFromFile() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("properties.xml").getFile());

        PropertiesController propertiesController = new PropertiesController();
        Properties.Proxy proxyConfiguration = propertiesController.loadPersonDataFromFile(file);
        assertEquals("localhost", proxyConfiguration.getHost());
        assertEquals(3128, proxyConfiguration.getPort());
        assertEquals("mylogin", proxyConfiguration.getLogin());
        assertEquals("mypassword", proxyConfiguration.getPassword());
    }

}