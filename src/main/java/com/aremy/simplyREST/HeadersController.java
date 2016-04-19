package com.aremy.simplyREST;

import com.sun.javafx.application.HostServicesDelegate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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


    protected ArrayList<TreeItem<CommonHeader>> getCommonHeadersFromXml() {
        ArrayList<TreeItem<CommonHeader>> result = new ArrayList<>();
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();

            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource("headers.xml").getFile());

            Document doc = builder.parse(file);
            NodeList categoriesNodes = doc.getElementsByTagName("category");
            for (int categoryindex = 0; categoryindex < categoriesNodes.getLength(); categoryindex++) {
                ArrayList<CommonHeader> extractedCommonHeaders = new ArrayList<>();
                Node node = categoriesNodes.item(categoryindex);
                NodeList nList = node.getChildNodes();
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeName() == "header" && nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        extractedCommonHeaders.add(new CommonHeader(eElement.getElementsByTagName("name").item(0).getTextContent(),
                                eElement.getElementsByTagName("description").item(0).getTextContent(),
                                eElement.getElementsByTagName("example").item(0).getTextContent()));
                    }
                }

                // create a root item for each category
                final TreeItem<CommonHeader> category = new TreeItem<>(new CommonHeader(node.getAttributes().getNamedItem("name").getTextContent(), "",""));
                category.setExpanded(true);

                //Adding tree items to the root
                for (CommonHeader extractedCommonHeader: extractedCommonHeaders) {
                    final TreeItem<CommonHeader> childNode = new TreeItem<>(extractedCommonHeader);
                    category.getChildren().add(childNode);
                }
                result.add(category);
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
        return result;
    }

    @FXML
    public void initialize() {
        //Collection<CommonHeader> extractedCommonHeaders = getCommonHeadersFromXml();

        final TreeItem<CommonHeader> root = new TreeItem<>(new CommonHeader("", "",""));
        root.setExpanded(true);

        //Adding tree items to the root
        for (TreeItem<CommonHeader> extractedCommonHeader: getCommonHeadersFromXml()) {
            root.getChildren().add(extractedCommonHeader);
        }
        headerColumn.setCellValueFactory(new TreeItemPropertyValueFactory("name"));
        descriptionColumn.setCellValueFactory(new TreeItemPropertyValueFactory("description"));
        exampleColumn.setCellValueFactory(new TreeItemPropertyValueFactory("example"));
        commonHeadersTable.setRoot(root);
        commonHeadersTable.setShowRoot(false);

        commonHeadersTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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
        final ObservableList<TreeItem<CommonHeader>> selectedItems = commonHeadersTable.getSelectionModel().getSelectedItems();
        for (TreeItem<CommonHeader> treeitem: selectedItems) {
            setHeaderForm(treeitem.getValue().getExample());
        }
    }
}
