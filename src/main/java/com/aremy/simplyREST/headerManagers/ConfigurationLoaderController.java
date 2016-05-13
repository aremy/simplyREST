package com.aremy.simplyREST.headerManagers;

import com.aremy.simplyREST.generated.Savedpresets;
import com.aremy.simplyREST.objects.PresetsManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationLoaderController extends HeaderManagerController {

    @FXML private ListView presetList;
    @FXML private TextArea presetDescription;

    private final Logger slf4jLogger = LoggerFactory.getLogger(ConfigurationLoaderController.class);

    @FXML
    private void initialize() {
        PresetsManager presetsManager = PresetsManager.instance();
        List<Savedpresets.Session> savedSessions = presetsManager.sessionList;

        if (savedSessions != null) {
            presetList.setItems(FXCollections.observableList(savedSessions));
        } else {

        }
        presetList.setCellFactory(new Callback<ListView<Savedpresets.Session>, ListCell<Savedpresets.Session>>(){
            @Override
            public ListCell<Savedpresets.Session> call(ListView<Savedpresets.Session> p) {
                ListCell<Savedpresets.Session> cell = new ListCell<Savedpresets.Session>(){
                    @Override
                    protected void updateItem(Savedpresets.Session t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t.getName());
                        }
                    }
                };
                return cell;
            }
        });

        presetList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Savedpresets.Session>() {
            @Override
            public void changed(ObservableValue<? extends Savedpresets.Session> observable, Savedpresets.Session oldValue, Savedpresets.Session newValue) {
                System.out.println("ListView selection changed from oldValue = "
                        + (oldValue != null ?oldValue.getName():"") + " to newValue = " + newValue.getName());

            }
        });
    }

    @FXML
    private void updateDescription() {
        Savedpresets.Session selectedItem = (Savedpresets.Session) presetList.getSelectionModel().getSelectedItem();
        presetDescription.setText("Url :\n" + selectedItem.getUrl() +
                                  "\n\nMethod :\n" + selectedItem.getMethod() +
                                  "\n\nHeaders:\n" + selectedItem.getHeaders());
    }

    @FXML
    private void loadPresets() {
        Savedpresets.Session selectedItem = (Savedpresets.Session) presetList.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            setHeaderForm(selectedItem.getHeaders());
            setUrlForm(selectedItem.getUrl());
            setBodyForm(selectedItem.getBody());
            setMethodForm(selectedItem.getMethod());
        }
        dialogStage.close();
    }

}
