package com.aremy.simplyREST.headerManagers;

import com.aremy.simplyREST.Controller;
import com.aremy.simplyREST.generated.Properties;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class PropertiesController extends HeaderManagerController {

    @FXML TextField proxyHost;
    @FXML TextField proxyPort;
    @FXML TextField proxyLogin;
    @FXML TextField proxyPassword;

    private final Logger slf4jLogger = LoggerFactory.getLogger(Controller.class);

    private final String PROPERTIES_FILE_NAME = "properties.xml";

    @FXML
    public void initialize() {
        File propertiesFile = new File(PROPERTIES_FILE_NAME);
        if (propertiesFile.exists() && propertiesFile.isFile()) {
            Properties.Proxy proxyConfiguration = loadPersonDataFromFile(propertiesFile);
            proxyHost.setText(proxyConfiguration.getHost());
            proxyPort.setText(String.valueOf(proxyConfiguration.getPort()));
            proxyLogin.setText(proxyConfiguration.getLogin());
            proxyPassword.setText(proxyConfiguration.getPassword());
        }

    }

    /**
     * Loads proxy properties
     *
     * @param file
     */
    protected Properties.Proxy loadPersonDataFromFile(File file) {
        Properties.Proxy result = null;
        try {
            JAXBContext context = JAXBContext
                    .newInstance(Properties.class);
            Unmarshaller um = context.createUnmarshaller();

            // Reading XML from the file and unmarshalling.
            Properties wrapper = (Properties) um.unmarshal(file);

            result = wrapper.getProxy();

        } // catches ANY exception
        catch (JAXBException e) {
            slf4jLogger.error("Error while loading properties");
        }
        return result;
    }

    @FXML
    public void saveProxyConfiguration() {
        try {
            Short.valueOf(proxyPort.getText());
        } catch(NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid port value");
            alert.setContentText("Please set a numeric value in the Port field.");

            alert.showAndWait();
            return;
        }

        try {
            JAXBContext context = JAXBContext
                    .newInstance(Properties.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            Properties wrapper = new Properties();
            Properties.Proxy proxyProperties = new Properties.Proxy();
            proxyProperties.setHost(proxyHost.getText());

            proxyProperties.setPort(Short.valueOf(proxyPort.getText()));
            proxyProperties.setLogin(proxyLogin.getText());
            proxyProperties.setPassword(proxyPassword.getText());
            wrapper.setProxy(proxyProperties);

            // Marshalling and saving XML to the file.
            m.marshal(wrapper, new File(PROPERTIES_FILE_NAME));
        } catch (JAXBException e) {
            slf4jLogger.error("Error while loading properties");
        }

    }
}
