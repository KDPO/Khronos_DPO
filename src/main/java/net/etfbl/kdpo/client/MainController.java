package net.etfbl.kdpo.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FilenameFilter;
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
    private Button btnSlideShow;

    @FXML
    private Label lblMessages;

    @FXML
    private FlowPane flowPane;

    @FXML
    private MenuButton menu;

    @FXML
    private ListView<VirtualAlbum> listView;

    @FXML
    private TreeView<MyFile> treeView;

    private Stage stage;

    public MainController() {
    }

    @FXML
    void initialize() {
        ObservableList<VirtualAlbum> data = FXCollections.observableArrayList();
        listView.setItems(data);
        data.add(new VirtualAlbum("Prvi album", "Prvi album"));
        data.get(0).getImages().addAll(new File("/testImages/slika.jpg"), new File("/testImages/slika.jpg"), new File("/testImages/slika.jpg"));
        listView.setOnMouseClicked((MouseEvent) -> {
            setImagesToFlowPane(listView.getSelectionModel().getSelectedItem().getImages());
        });

        lblMessages.setVisible(false);
        setTreeView();
    }

    public void addNewVirtualAlbum() {

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void showImageViewController(Image image) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/imageView.fxml"));
            Parent root = loader.load();
            ImageViewController controller = loader.getController();
            controller.setImage(image);
            controller.initParams(stage, stage.getScene());
            stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTreeView() {
        /* TreeView initialization */
        new Thread(() -> {
            TreeItem<MyFile> root = new TreeItem<>();
            treeView.setRoot(root);
            treeView.setShowRoot(false);
            TreeItem<MyFile> desktop = new TreeItem<>(new MyFile(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath()));
            root.getChildren().add(desktop);
            findChilds(desktop.getValue(), desktop);
            for (File file : File.listRoots())
                if (file.isDirectory()) {
                    TreeItem<MyFile> node = new TreeItem<>(new MyFile(file.getAbsolutePath()));
                    root.getChildren().add(node);
                    findChilds(file, node);
                }
            // za dinamičko učitavanje
            root.addEventHandler(TreeItem.branchExpandedEvent(), new EventHandler<TreeItem.TreeModificationEvent<MyFile>>() {
                public void handle(TreeItem.TreeModificationEvent<MyFile> event) {
                    TreeItem<MyFile> expandedTreeItem = event.getTreeItem();
                    for (TreeItem<MyFile> item : expandedTreeItem.getChildren())
                        if (item.getChildren().isEmpty())
                            findChilds(item.getValue(), item);
                    /* Možda je bolje da svaki put traži ponovo foldere u slučaju da se napravi neki novi
                       Problem je u slučaju da se unutar particije napravi novi folder, neće biti vidljiv dok se ne restartuje app
                        */
                }
            });

            treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                ObservableList<File> images = FXCollections.observableArrayList();
                images.setAll(newValue.getValue().listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith("jpg") || name.endsWith("png") || name.endsWith("jpeg");
                    }
                }));
                setImagesToFlowPane(images);
            });
        }).start();
    }

    private void findChilds(File file, TreeItem<MyFile> node) {
        if (file.isDirectory()) {
            for (File var : file.listFiles((File pathname) -> {
                return pathname.isDirectory() && !pathname.isHidden() && Files.isReadable(pathname.toPath()) && !Files.isSymbolicLink(pathname.toPath());
            }))
                node.getChildren().add(new TreeItem<>(new MyFile(var.getAbsolutePath())));
        }
    }

    // dodavanje slika u flowPane
    private void setImagesToFlowPane(ObservableList<File> images) {
        ObservableList<Node> childs = flowPane.getChildren();
        childs.clear();
        for (File file : images)
            Platform.runLater(() -> {
                ImageFrame iFrame = new ImageFrame(file);
                childs.add(iFrame);
            });
    }
}