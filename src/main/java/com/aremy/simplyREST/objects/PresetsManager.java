package com.aremy.simplyREST.objects;

import com.aremy.simplyREST.generated.Savedpresets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.List;

public class PresetsManager {

    private static final Logger slf4jLogger = LoggerFactory.getLogger(PresetsManager.class);
    private static final String PRESETS_FILE_NAME = "presets.xml";

    public List<Savedpresets.Session> getSessionList() {
        return sessionList;
    }

    public List<Savedpresets.Session> sessionList;

    public PresetsManager() {
        File propertiesFile = new File(PRESETS_FILE_NAME);
        if (propertiesFile.exists()) {
            try {
                JAXBContext context = JAXBContext
                        .newInstance(Savedpresets.class);
                Unmarshaller um = context.createUnmarshaller();
                Savedpresets wrapper = (Savedpresets) um.unmarshal(propertiesFile);
                sessionList = wrapper.getSession();

/*            proxyHost = wrapper.getProxy().getHost();
            proxyPort = wrapper.getProxy().getPort();
            proxyLogin = wrapper.getProxy().getLogin();
            proxyPassword = wrapper.getProxy().getPassword();*/
            } // catches ANY exception
            catch (JAXBException e) {
                e.printStackTrace();
                slf4jLogger.error("Error while loading presets");
            }
        }
    }
/*
    static public PresetsManager instance(){
        return instance(new File(PROPERTIES_FILE_NAME));
    }

    static public PresetsManager instance(File propertiesFile){
        if (_instance == null) {
            _instance = new PresetsManager(propertiesFile);
        }
        return _instance;
    }
*/
    /*
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

            // Marshalling and saving XML to the file.
            m.marshal(wrapper, new File(PROPERTIES_FILE_NAME));
        } catch (JAXBException e) {
            slf4jLogger.error("Error while loading properties");
        }
    }
    */
}
