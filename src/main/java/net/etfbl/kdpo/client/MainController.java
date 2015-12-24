package net.etfbl.kdpo.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import javax.swing.filechooser.FileSystemView;
import java.io.File;

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
    private ListView<String> listView;

    @FXML
    private TreeView<File> treeView;

    private Stage stage;

    public MainController() {
    }

    @FXML
    void initialize() {
        for (int i = 1; i < 5; i++) {
            flowPane.getChildren().add(new ImageFrame(new File("/testImages/slika.jpg")));
        }

        ObservableList<String> data = FXCollections.observableArrayList();
        listView.setItems(data);
        data.addAll("Prvi album", "Drugi album", "Treći album", "Četvrti album");

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
            TreeItem<File> root = new TreeItem<>();
            treeView.setRoot(root);
            treeView.setShowRoot(false);
            TreeItem<File> desktop = new TreeItem<File>(FileSystemView.getFileSystemView().getHomeDirectory());
            root.getChildren().add(desktop);
            findChilds(desktop.getValue(), desktop);
            for (File file : File.listRoots())
                if (file.isDirectory()) {
                    TreeItem<File> node = new TreeItem<File>(file);
                    root.getChildren().add(node);
                    findChilds(file, node);
                }
            // za dinamičko učitavanje
            root.addEventHandler(TreeItem.branchExpandedEvent(), new EventHandler<TreeItem.TreeModificationEvent<File>>() {
                public void handle(TreeItem.TreeModificationEvent<File> event) {
                    TreeItem<File> expandedTreeItem = event.getTreeItem();
                    for (TreeItem<File> item : expandedTreeItem.getChildren())
                    /* Možda je bolje da svaki put traži ponovo foldere u slučaju da se napravi neki novi
                       Problem je u slučaju da se unutar particije napravi novi folder, neće biti vidljiv dok se ne restartuje app
                        if (item.getChildren().isEmpty()) */
                        findChilds(item.getValue(), item);
                }
            });

            // nije dobro, previše sporo
            treeView.setCellFactory(tv -> {
                TreeCell<File> cell = new TreeCell<File>() {
                    @Override
                    protected void updateItem(File item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty)
                            setText(null);
                        else
                            setText(FileSystemView.getFileSystemView().getSystemDisplayName(item));
                    }
                };

                cell.setOnMouseClicked((MouseEvent event) -> {
                    if (event.getClickCount() == 1)
                        System.out.println();
                });

                return cell;
            });
        }).start();
    }

    private void findChilds(File file, TreeItem<File> node) {
        if (file.isDirectory()) {
            for (File var : file.listFiles((File pathname) -> {
                return pathname.isDirectory() && !pathname.isHidden();
            }))
                node.getChildren().add(new TreeItem<>(var));
        }
    }
}