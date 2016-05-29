package com.aremy.simplyREST.objects;

import com.aremy.simplyREST.Controller;
import com.aremy.simplyREST.generated.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class PropertiesManager {
    static private PropertiesManager _instance = null;

    private static final Logger slf4jLogger = LoggerFactory.getLogger(PropertiesManager.class);
    private static final String PROPERTIES_FILE_NAME = "properties.xml";

    public static String proxyHost;
    public static String proxyPort;
    public static String proxyLogin;
    public static String proxyPassword;

    public static String defaultPath;

    /**
     * Loads proxy properties
     */
    public PropertiesManager(File propertiesFile) {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(Properties.class);
            Unmarshaller um = context.createUnmarshaller();
            Properties wrapper = (Properties) um.unmarshal(propertiesFile);
            proxyHost = wrapper.getProxy().getHost();
            proxyPort = wrapper.getProxy().getPort();
            proxyLogin = wrapper.getProxy().getLogin();
            proxyPassword = wrapper.getProxy().getPassword();
            defaultPath = wrapper.getGeneral().getDefaultPath();
            if (defaultPath == null || defaultPath.isEmpty())
                defaultPath = System.getProperty("user.home");
            //

        } // catches ANY exception
        catch (JAXBException e) {
            slf4jLogger.error("Error while loading properties");
        }
    }

    static public PropertiesManager instance(){
        return instance(new File(PROPERTIES_FILE_NAME));
    }

    static public PropertiesManager instance(File propertiesFile){
        if (_instance == null) {
            _instance = new PropertiesManager(propertiesFile);
        }
        return _instance;
    }

    public static void save() {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(Properties.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            Properties wrapper = new Properties();

            Properties.Proxy proxyProperties = new Properties.Proxy();
            proxyProperties.setHost(proxyHost);

            proxyProperties.setPort(proxyPort);
            proxyProperties.setLogin(proxyLogin);
            proxyProperties.setPassword(proxyPassword);
            wrapper.setProxy(proxyProperties);
            Properties.General generalProperties = new Properties.General();
            generalProperties.setDefaultPath(defaultPath);
            wrapper.setGeneral(generalProperties);

            // Marshalling and saving XML to the file.
            m.marshal(wrapper, new File(PROPERTIES_FILE_NAME));
        } catch (JAXBException e) {
            slf4jLogger.error("Error while loading properties");
        }
    }
}
