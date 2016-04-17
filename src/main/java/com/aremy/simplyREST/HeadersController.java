package com.aremy.simplyREST;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class HeadersController {
    @FXML private TreeTableView<CommonHeader> commonHeadersTable;
    @FXML private TreeTableColumn headerColumn;
    @FXML private TreeTableColumn descriptionColumn;
    @FXML private TreeTableColumn exampleColumn;

    private Stage dialogStage;

    public void goToHeaderSource() {
        // https://en.wikipedia.org/wiki/List_of_HTTP_header_fields
    }

    public class CommonHeader {
        private String name;
        private String description;
        private String example;

        public CommonHeader(String name, String description, String example) {
            this.name = name;
            this.description = description;
            this.example = example;
        }


        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getExample() {
            return example;
        }
    }
    @FXML
    public void initialize() {
        //Creating tree items
        List<CommonHeader> list = new ArrayList<>();
        list.add(new CommonHeader("A", "B", "C"));
        list.add(new CommonHeader("A1", "B1", "C1"));
        list.add(new CommonHeader("A2", "B2", "C2"));
        ObservableList<CommonHeader> teams = FXCollections.observableArrayList();



        final TreeItem<CommonHeader> childNode1 = new TreeItem<>(new CommonHeader("Child Node 1", "x","y"));
        final TreeItem<CommonHeader> childNode2 = new TreeItem<>(new CommonHeader("Child Node 2", "x","y"));
        final TreeItem<CommonHeader> childNode3 = new TreeItem<>(new CommonHeader("Child Node 3", "x","y"));

        //Creating the root element
        final TreeItem<CommonHeader> root = new TreeItem<>(new CommonHeader("Common Headers", "",""));
        root.setExpanded(true);

        //Adding tree items to the root
        root.getChildren().setAll(childNode1, childNode2, childNode3);


        //Defining cell content

        //Creating a column
        //TreeTableColumn<String,String> column = new TreeTableColumn<>("Column");

        headerColumn.setCellValueFactory(new TreeItemPropertyValueFactory("name"));
        descriptionColumn.setCellValueFactory(new TreeItemPropertyValueFactory("description"));
        exampleColumn.setCellValueFactory(new TreeItemPropertyValueFactory("example"));



        /*column.setCellValueFactory((TreeTableColumn.CellDataFeatures<String, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue()));*/


        //Defining cell content
        /*column.setCellValueFactory((TreeTableColumn.CellDataFeatures<String, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue()));*/

        commonHeadersTable.setRoot(root);

        //Creating a tree table view
/*        final TreeTableView<String> treeTableView = new TreeTableView<>(root);
        treeTableView.getColumns().add(column);
        treeTableView.setPrefWidth(152);
        treeTableView.setShowRoot(true);
        sceneRoot.getChildren().add(treeTableView);
        stage.setScene(scene);
        stage.show();*/
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}
