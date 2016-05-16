package com.aremy.simplyREST.objects;

import com.aremy.simplyREST.generated.Savedpresets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.List;

public class PresetsManager {
    static private PresetsManager _instance = null;
    private static final Logger slf4jLogger = LoggerFactory.getLogger(PresetsManager.class);
    private static final String PRESETS_FILE_NAME = "presets.xml";

    public static List<Savedpresets.Session> sessionList;

/*    public List<Savedpresets.Session> getSessionList() {
        return sessionList;
    }  */

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
            } catch (JAXBException e) {
                e.printStackTrace();
                slf4jLogger.error("Error while loading presets");
            }
        }
    }

    static public PresetsManager instance(){
        if (_instance == null) {
            _instance = new PresetsManager();
        }
        return _instance;
    }

    public static void save() {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(Savedpresets.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            Savedpresets wrapper = new Savedpresets();
            wrapper.getSession().addAll(sessionList);

            // Marshalling and saving XML to the file.
            m.marshal(wrapper, new File(PRESETS_FILE_NAME));
        } catch (JAXBException e) {
            slf4jLogger.error("Error while loading properties");
        }
    }
}
