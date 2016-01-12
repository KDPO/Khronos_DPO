package net.etfbl.kdpo.client;

import com.sun.javafx.robot.FXRobot;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;

/**
 * Created by Stijak on 16.12.2015..
 */

public class MainController {
    @FXML
    private Label lblAlbumDescription;

    @FXML
    private Button btnAddImages;

    @FXML
    private Label lblMessages;

    @FXML
    private FlowPane flowPane;

    @FXML
    private MenuButton menu;

    @FXML
    private Tab tabFS;

    @FXML
    private Tab tabAlbumi;

    @FXML
    private ListView<VirtualAlbum> listView;
    private ObservableList<VirtualAlbum> listViewData;

    @FXML
    private TreeView<MyFile> treeView;

    @FXML
    private TabPane tabPane;

    @FXML
    private Button btnAddNewAlbum;

    @FXML
    private MenuItem menuSendSS;

    private Stage stage;


    @FXML
    void initialize() {
        // lista unutar koje će se nalaziti objekti Virtuelnih albuma za prikaz u ListView i kasnije korišćenje
        listViewData = FXCollections.observableArrayList();
        listView.setItems(listViewData);

        // listener za prikaz slika albuma u flowPane-u
        listView.setOnMouseClicked((MouseEvent) -> setImagesToFlowPane(listView.getSelectionModel().getSelectedItem().getImages()));

        // prvi album selektovan prilikom pokretanja
        setFirstElementOfListViewSelected();

        lblMessages.setVisible(false);

        new Thread(this::setTreeView).start();

        menuSendSS.setOnAction(event -> showScreenShotSendWindow());

        tabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            if (newTab.equals(tabFS))
                btnAddImages.setVisible(false);
            if (newTab.equals(tabAlbumi))
                btnAddImages.setVisible(true);

        });

        btnAddNewAlbum.setOnMouseClicked(event -> showCreateNewAlbumWindow());

    }

    // dodavanje novog albuma nakon klika na dugne Add new album
    private void addNewVirtualAlbum(String name, String description) {
        listViewData.add(new VirtualAlbum(name, description));
    }

    // dodavanje novog albuma nakon čekiranja slika tokom pregleda fs
    private void addNewVirtualAlbum(String name, String description, ObservableList<File> images) {
        VirtualAlbum va = new VirtualAlbum(name, description);
        va.getImages().setAll(images);
        listViewData.add(va);
    }

    // u slučaju da postoje VA potrebno je prvi selektovati
    private void setFirstElementOfListViewSelected() {
        if (!listViewData.isEmpty()) {
            listView.getSelectionModel().selectFirst();
            setImagesToFlowPane(listView.getItems().get(0).getImages());
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        Platform.runLater(() -> {
            stage.getScene().getWindow().addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
                if (event.getCode().equals(KeyCode.PRINTSCREEN))
                    showScreenShotSendWindow();
            });
        });
    }

    // prelazak na ImageViewController
    private void showImageViewController(ObservableList<File> images, int index) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/imageView.fxml"));
            Parent root = loader.load();
            ImageViewController controller = loader.getController();
            // privremeno
            VirtualAlbum va = new VirtualAlbum("", "", true);
            va.getImages().setAll(images);
            controller.setVirtualAlbum(va, index);
            controller.initParams(stage, stage.getScene().getRoot());
            stage.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTreeView() {
        /* TreeView initialization */
        TreeItem<MyFile> root = new TreeItem<>();
        treeView.setRoot(root);
        treeView.setShowRoot(false);
        // desktop
        setChild(root, new TreeItem<>(new MyFile(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath())));
        // particije
        for (File file : File.listRoots())
            if (file.isDirectory())
                setChild(root, new TreeItem<>(new MyFile(file.getAbsolutePath())));
        // za dinamičko učitavanje
        root.addEventHandler(TreeItem.branchExpandedEvent(), new EventHandler<TreeItem.TreeModificationEvent<MyFile>>() {
            public void handle(TreeItem.TreeModificationEvent<MyFile> event) {
                findChilds(event.getTreeItem().getValue(), event.getTreeItem());
            }
        });

        // listener za prikaz slika u flowPane na sleketovanje foldera
        // treba izbaciti odavde
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<File> images = FXCollections.observableArrayList();
            images.setAll(newValue.getValue().listFiles((dir, name) -> name.endsWith("jpg") || name.endsWith("png") || name.endsWith("jpeg")));
            setImagesToFlowPane(images);
        });
    }

    // pronalazi podfoldere
    private void findChilds(File file, TreeItem<MyFile> node) {
        if (file.isDirectory()) {
            node.getChildren().clear();
            for (File var : file.listFiles((File pathname) -> pathname.isDirectory() && !pathname.isHidden() && Files.isReadable(pathname.toPath()) && !Files.isSymbolicLink(pathname.toPath())))
                setChild(node, new TreeItem<>(new MyFile(var.getAbsolutePath())));
        }
    }

    // Don't repeat yourself  :D
    private void setChild(TreeItem<MyFile> parent, TreeItem<MyFile> child) {
        child.getChildren().add(new TreeItem<>()); // kako bi se pojavlia strelica za expand
        parent.getChildren().add(child);
    }

    // dodavanje slika u flowPane
    private void setImagesToFlowPane(ObservableList<File> images) {
        ObservableList<Node> childs = flowPane.getChildren();
        childs.clear();
        for (File file : images)
            Platform.runLater(() -> {
                ImageFrame iFrame = new ImageFrame(file);
                childs.add(iFrame);
                /*  onClick na ImageFrame treba da pređe u prikaz slike */
                iFrame.setOnMouseClicked((MouseEvent event) -> {
                    if (event.getButton().equals(MouseButton.PRIMARY))
                        showImageViewController(getImagesFromFlowPane(), flowPane.getChildren().indexOf(iFrame));
                });
            });
    }

    // objekti slika su već napravljeni pa zašto ih ne iskoristiti
    private ObservableList<File> getImagesFromFlowPane() {
        ObservableList<File> images = FXCollections.observableArrayList();
        for (Node node : flowPane.getChildren())
            images.add(((ImageFrame) node).getFile());
        return images;
    }

    // kada budemo dodavali slike u album
    private ObservableList<File> getCheckedImagesFromFlowPane() {
        ObservableList<File> images = FXCollections.observableArrayList();
        for (Node node : flowPane.getChildren())
            if (((ImageFrame) node).getCheckBox().isSelected())
                images.add(((ImageFrame) node).getFile());

        return images;
    }

    private void showCreateNewAlbumWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/newAlbum.fxml"));
            Parent root = loader.load();
            CreateVirtualAlbumWindowController controller = loader.getController();
            Stage stage = new Stage();
            controller.setStage(stage);
            controller.setAlbumList(listViewData);
            stage.setScene(new Scene(root, 319, 208));
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // prikaz prozora za slanje SS
    private void showScreenShotSendWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ssSend.fxml"));
            Parent root = loader.load();
            ScreenShotSendWindowController controller = loader.getController();
            Stage stage = new Stage();
            controller.setStage(stage);
            stage.setScene(new Scene(root, 531, 367));
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}