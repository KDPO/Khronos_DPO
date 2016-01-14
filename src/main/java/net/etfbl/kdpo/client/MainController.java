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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Created by Stijak on 16.12.2015..
 */

public class MainController {
    public static MainController mainController;
    public static VirtualAlbum screenshotAlbum;

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
    private Button btnCheck;

    @FXML
    private Button btnAbort;

    @FXML
    private Button btnRemove;

    @FXML
    private Button btnDelete;

    @FXML
    private MenuItem menuSendSS;

    private Stage stage;

    //Pomocna promjenjliva koja samo pamti stanje da li se do FS doslo preko + dugmeta u VA
    private boolean fromAlbum = false;

    //Pomocna promjenjliva koja samo prati čekirane slike
    private int checked = 0;

    private boolean buttonsVisibleControl = true;

    private Parent imageViewCotrollerRoot;
    private ImageViewController imageViewController;

    //menu na desni klik
    private MenuItem menuNewTreeView;
    private MenuItem menuCopyTreeView;
    private MenuItem menuCutTreeView;
    private MenuItem menuPasteTreeView;
    private MenuItem menuDeleteTreeView;
    private MenuItem menuRenameTreeView;

    private MenuItem menuRemoveListView;
    private MenuItem menuRenameListView;

    @FXML
    void initialize() {
        mainController = this;
        menuNewTreeView = new MenuItem("New");
        menuCopyTreeView = new MenuItem("Copy");
        menuCutTreeView = new MenuItem("Cut");
        menuPasteTreeView = new MenuItem("Paste");
        menuDeleteTreeView = new MenuItem("Delete");
        menuRenameTreeView = new MenuItem("Rename");
        menuRemoveListView = new MenuItem("Remove");
        menuRenameListView = new MenuItem("Rename");
        // lista unutar koje će se nalaziti objekti Virtuelnih albuma za prikaz u ListView i kasnije korišćenje
        listViewData = FXCollections.observableArrayList();
        listView.setEditable(true);
        listView.setItems(listViewData);
        listView.setOnMouseClicked((MouseEvent) -> setImagesToFlowPane(listView.getSelectionModel().getSelectedItem().getImages()));
        listView.setContextMenu(new ContextMenu(menuRenameListView, menuRemoveListView));

        //ucitavanje serijalizovanih VA
        readSerializedAlbums();

        setFirstElementOfListViewSelected();

        lblMessages.setVisible(false);
        btnAddImages.setVisible(false);
        btnCheck.setVisible(false);
        btnAbort.setVisible(false);
        btnRemove.setVisible(false);
        btnDelete.setVisible(false);

        new Thread(this::setTreeView).start();

        menuSendSS.setOnAction(event -> showScreenShotSendWindow());

        tabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            if (newTab.equals(tabFS)) {
                treeView.getSelectionModel().clearSelection();
                btnAddImages.setVisible(false);
                flowPane.getChildren().clear();
                if (checked != 0) {
                    checked = 0;
                    btnRemove.setVisible(false);
                }
            }
            if (newTab.equals(tabAlbumi)) {
                //btnAddImages.setVisible(true);
                if (listViewData.isEmpty()) {
                    flowPane.getChildren().clear();
                } else {
                    if (listView.getSelectionModel().getSelectedItem() != null) {
                        setImagesToFlowPane(listView.getSelectionModel().getSelectedItem().getImages());
                    } else {
                        flowPane.getChildren().clear();
                    }
                }
                if (checked != 0) {
                    checked = 0;
                    btnDelete.setVisible(false);
                }
            }
        });

        //listener koji prikazuje ikonicu za dodavanje slika samo kad je selektovan neki album
        listView.getSelectionModel().selectedItemProperty().addListener((ov, oldAlbum, newAlbum) -> {
            if ((listView.getSelectionModel().getSelectedItem() != null) && (tabPane.getSelectionModel().getSelectedItem().equals(tabAlbumi))) {
                btnAddImages.setVisible(true);
            }
        });

        btnAddNewAlbum.setOnMouseClicked(event -> showCreateNewAlbumWindow());

        btnAddImages.setOnMouseClicked(event -> addImages());

        btnCheck.setOnMouseClicked(event -> addImagesToAlbum());

        btnAbort.setOnMouseClicked(event -> abortAddingImages());

        menuRemoveListView.setOnAction(event -> removeVA());

        menu.setOnAction(event -> {
            Main.showNotification("Proba");
        });

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
            if (listView.getSelectionModel().getSelectedItem().getImages() != null) {
                setImagesToFlowPane(listView.getItems().get(0).getImages());
            }
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

        //poziva metodu za serijalizovanje albuma pri gasenju aplikacije
        stage.setOnCloseRequest(we -> serializeAlbums());
    }

    // prelazak na ImageViewController
    public void showImageViewController(ObservableList<File> images, int index) {
        try {
            if (imageViewCotrollerRoot == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/imageView.fxml"));
                imageViewCotrollerRoot = loader.load();
                imageViewController = loader.getController();
                imageViewController.initParams(stage, stage.getScene().getRoot());
            }
            if (tabPane.getSelectionModel().getSelectedItem().equals(tabAlbumi))
                imageViewController.setVirtualAlbum(listView.getSelectionModel().getSelectedItem(), index);
            else {
                VirtualAlbum va = new VirtualAlbum("", "", true);
                va.getImages().setAll(images);
                imageViewController.setVirtualAlbum(va, index);
            }
            stage.getScene().setRoot(imageViewCotrollerRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTreeView() {
        /* TreeView initialization */
        treeView.setContextMenu(new ContextMenu(menuNewTreeView, menuCopyTreeView, menuCutTreeView, menuPasteTreeView, menuDeleteTreeView, menuRenameTreeView));
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
            if (newValue != null)
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

                iFrame.getCheckBox().setOnAction(event -> {
                    if (buttonsVisibleControl) {
                        if (iFrame.getCheckBox().isSelected()) {
                            if (checked == 0) {
                                btnAddImages.setVisible(true);
                                if (tabPane.getSelectionModel().getSelectedItem().equals(tabAlbumi)) {
                                    btnRemove.setVisible(true);
                                } else if (tabPane.getSelectionModel().getSelectedItem().equals(tabFS)) {
                                    btnDelete.setVisible(true);
                                }
                            }
                            checked++;
                        } else {
                            checked--;
                            if (checked == 0) {
                                if (tabPane.getSelectionModel().getSelectedItem().equals(tabFS))
                                    btnAddImages.setVisible(false);
                                btnDelete.setVisible(false);
                                btnRemove.setVisible(false);
                            }
                        }
                    }
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

    //provjerava na kojem se tabu nalazi i na osnovu toka vrsi potrebne akcije da dodavanje slika u album
    private void addImages() {
        buttonsVisibleControl = false;
        if (tabPane.getSelectionModel().getSelectedItem().equals(tabAlbumi)) {
            tabPane.getSelectionModel().select(tabFS);
            btnCheck.setVisible(true);
            btnAbort.setVisible(true);
            btnAddImages.setVisible(false);
            fromAlbum = true;
        } else {
            buttonsVisibleControl = true;
            // u slučaju prekida da ne prebaci na Tab Albumi
            if (!showAlbumList(getCheckedImagesFromFlowPane())) {
                tabPane.getSelectionModel().select(tabAlbumi);
                fromAlbum = false;
            }
        }
    }

    //vrsi ubacivanje slika u selektovani album
    private void addImagesToAlbum() {
        buttonsVisibleControl = true;
        listView.getSelectionModel().getSelectedItem().setImages(getCheckedImagesFromFlowPane());
        tabPane.getSelectionModel().select(tabAlbumi);
        fromAlbum = false;
        btnCheck.setVisible(false);
        btnAbort.setVisible(false);
        //btnAddImages.setVisible(true);
    }

    //omogucava abort
    private void abortAddingImages() {
        buttonsVisibleControl = true;
        tabPane.getSelectionModel().select(tabAlbumi);
        fromAlbum = false;
        btnCheck.setVisible(false);
        btnAbort.setVisible(false);
    }

    //otvara onovi prozor u kojem se bira album u koji je potrebno dodati slike ukoliko je izabrano dodavanje sa FS
    private boolean showAlbumList(ObservableList<File> images) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/albumsList.fxml"));
            Parent root = loader.load();
            AddImagesToAlbumWindowController controller = loader.getController();
            Stage stage = new Stage();
            controller.setStage(stage);
            controller.setAlbumList(listViewData);
            controller.setImagesList(images);
            stage.setScene(new Scene(root, 319, 208));
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.showAndWait();
            return controller.isCancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    //metoda koja cita serijalizovane albume i ubacuje ih u listViewData
    private void readSerializedAlbums() {
        String path = System.getProperty("user.home") + File.separator + "Khronos_DPO" + File.separator + "VirtualAlbums";
        File inputDirectory = new File(path);
        if (inputDirectory.isDirectory()) {
            File[] fileList = inputDirectory.listFiles();
            if (fileList != null) {
                for (File f : fileList) {
                    try {
                        ObjectInputStream object = new ObjectInputStream(new FileInputStream(f));
                        listViewData.addAll((ArrayList<VirtualAlbum>) object.readObject());
                        object.close();
                    } catch (Exception ex) {

                    }
                }
            }
        }
    }

    //metoda koja serijalizuje VA iz listViewData
    private void serializeAlbums() {
        String path = System.getProperty("user.home") + File.separator + "Khronos_DPO" + File.separator + "VirtualAlbums";
        File outputPath = new File(path);
        if (!outputPath.exists()) {
            outputPath.mkdirs();
        }
        String file = path + File.separator + "virtualalbums.kva";
        try {
            ArrayList<VirtualAlbum> list = new ArrayList<>(listViewData);
            ObjectOutputStream object = new ObjectOutputStream(new FileOutputStream(file));
            object.writeObject(list);
            object.close();
        } catch (Exception ex) {

        }
    }

    //ukoliko je potvrdjeno brisanje brise VA i prikazuje slike iz sledeceg
    private void removeVA() {
        if (!showRemoveVirtualAlbumWindow()) {
            listViewData.remove(listView.getSelectionModel().getSelectedItem());
            if (!listViewData.isEmpty()) {
                setImagesToFlowPane(listView.getSelectionModel().getSelectedItem().getImages());
            } else {
                flowPane.getChildren().clear();
            }
        }
    }

    //metoda koja prikazuje prozor u kojem se potcrdjuje brisanje VA
    private boolean showRemoveVirtualAlbumWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/removeVirtualAlbum.fxml"));
            Parent root = loader.load();
            RemoveVirtualAlbumWindowController controller = loader.getController();
            Stage stage = new Stage();
            controller.setStage(stage);
            stage.setScene(new Scene(root, 319, 98));
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.showAndWait();
            return controller.isCancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}