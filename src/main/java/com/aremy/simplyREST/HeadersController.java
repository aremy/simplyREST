package com.aremy.simplyREST;

import com.sun.javafx.application.HostServicesDelegate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HeadersController extends HeaderManagerController {
    @FXML private TreeTableView<CommonHeader> commonHeadersTable;
    @FXML private TreeTableColumn headerColumn;
    @FXML private TreeTableColumn descriptionColumn;
    @FXML private TreeTableColumn exampleColumn;

    private Stage dialogStage;

    public void goToHeaderSource() {
        //String url = "https://en.wikipedia.org/wiki/List_of_HTTP_header_fields";
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


    protected ArrayList<CommonHeader> getCommonHeadersFromXml() {
        ArrayList<CommonHeader> commonHeadersFromXml = new ArrayList<>();
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();

            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource("headers.xml").getFile());

            Document doc = builder.parse(file);
            NodeList nList = doc.getElementsByTagName("header");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;
                    commonHeadersFromXml.add(new CommonHeader(eElement.getElementsByTagName("name").item(0).getTextContent(),
                    eElement.getElementsByTagName("description").item(0).getTextContent(),
                    eElement.getElementsByTagName("example").item(0).getTextContent()));
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return commonHeadersFromXml;
    }

    @FXML
    public void initialize() {
        //Creating tree items
        Collection<CommonHeader> extractedCommonHeaders = getCommonHeadersFromXml();

        //Creating the root element
        final TreeItem<CommonHeader> root = new TreeItem<>(new CommonHeader("Common Headers", "",""));
        root.setExpanded(true);

        //Adding tree items to the root
        for (CommonHeader extractedCommonHeader: extractedCommonHeaders) {
            final TreeItem<CommonHeader> childNode = new TreeItem<>(extractedCommonHeader);
            root.getChildren().add(childNode);
        }
        //root.getChildren().addAll(childNode1, childNode2, childNode3);

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
        commonHeadersTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

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



    @FXML
    public void closeDialog() {
        dialogStage.close();
    }

    @FXML
    public void addSelectedHeaders () {
        System.out.println("test");
        setHeaderForm("test");
    }

}
