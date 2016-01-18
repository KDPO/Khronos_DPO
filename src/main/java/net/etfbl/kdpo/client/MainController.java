package net.etfbl.kdpo.client;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;

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
    private ScrollPane scrollPane;

    @FXML
    private Label lblAlbumDescription;

    @FXML
    private Button btnAddImages;

    @FXML
    private Label copyText;

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
    private MenuItem menuAcceptSS;

    @FXML
    private MenuItem menuPreferences;

    @FXML
    private MenuItem menuSendSS;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private ProgressIndicator treeViewProgressIndicator;

    @FXML
    private ProgressIndicator copyProgress;

    @FXML
    private AnchorPane anchorPaneButtonContainer;

    @FXML
    private HBox hBoxMainContainer;

    private Stage stage;

    //Pomocna promjenjliva koja samo pamti stanje da li se do FS doslo preko + dugmeta u VA
    private boolean fromAlbum = false;

    //Pomocna promjenjliva koja samo prati čekirane slike
    private int checked = 0;

    private boolean buttonsVisibleControl = true;

    private Parent imageViewCotrollerRoot;
    private ImageViewController imageViewController;
    private ContextMenu iFrameContextMenu;
    private ContextMenu flowPaneContextMenu;
    private MenuItem copy;
    private MenuItem cut;
    private MenuItem delete;
    private MenuItem paste;

    //dodatne promjenljive za copy, cut, paste
    private String clipboard;
    private String name;
    private BooleanProperty isThereSomethigToPaste;
    private boolean isCutted = false;
    // kada će delete i rename biti prikazano
    private BooleanProperty isDisabled;
    //task za ubacivanje slika u flowPane
    private Task<Void> task;
    private boolean canAdd = true;

    @FXML
    void initialize() {
        mainController = this;
        //TODO dodati Tooltip na svkai btn
        menu.setTooltip(new Tooltip("Menu"));
        btnAddImages.setVisible(false);
        btnAddImages.setTooltip(new Tooltip("Add new images"));
        btnCheck.setVisible(false);
        btnAbort.setVisible(false);
        btnAbort.setTooltip(new Tooltip("Abort"));
        btnRemove.setVisible(false);
        btnDelete.setVisible(false);
        progressIndicator.setVisible(false);
        treeViewProgressIndicator.setVisible(false);

        menuPreferences.setOnAction(event -> showPreferencesWindow());

        copy = new MenuItem("Copy");
        copy.setOnAction(event -> {
            System.out.println(event.getSource());
            //TODO kopiranje slika
            // obratiti pažnju na kopiranje između tabAlbumi i tabFS
            // obratiti pažnju kada je više slika čakirano i kada nije ni jedna
            // implementirati po želji :D
        });
        cut = new MenuItem("Cut");
        cut.setOnAction(event -> {
            //TODO premještanje slika
            // obratiti pažnju na premještanje između tabAlbumi i tabFS
            // obratiti pažnju kada je više slika čakirano i kada nije ni jedna
            // implementirati po želji :D
        });
        delete = new MenuItem("Delete");
        delete.setOnAction(event -> {
            //TODO brisanje slika
            // iz albuma se izbacuju slike
            // sa FS se skroz brišu
            // dodati popup za ok
            // obratiti pažnju kada je više slika čakirano i kada nije ni jedna
            //implementirati po želji
        });
        iFrameContextMenu = new ContextMenu(copy, cut, delete);
        iFrameContextMenu.setAutoHide(true);
        paste = new MenuItem("Paste");
        paste.setOnAction(event -> {
            //TODO paste
            // obratiti pažnju na kopiranje između tabAlbumi i tabFS
            // obratiti pažnju na duplikate
            // implementirati disableProperty()
            // implementirati po želji :D
        });
        flowPaneContextMenu = new ContextMenu(paste);
        flowPaneContextMenu.setAutoHide(true);
        scrollPane.setOnContextMenuRequested(event -> {
            if (!iFrameContextMenu.isShowing())
                flowPaneContextMenu.show(scrollPane, event.getScreenX(), event.getScreenY());
        });
        scrollPane.setOnMousePressed(event -> {
            if (flowPaneContextMenu.isShowing())
                flowPaneContextMenu.hide();
        });

        // lista unutar koje će se nalaziti objekti Virtuelnih albuma za prikaz u ListView i kasnije korišćenje
        listViewData = FXCollections.observableArrayList();
        listView.setEditable(true);
        treeView.setEditable(true);
        listView.setItems(listViewData);
        listView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                setImagesToFlowPane(newValue.getImages());
                btnAddImages.setVisible(true);
                lblAlbumDescription.setText(newValue.getDescription());
            }
        }));
        listView.setCellFactory(new Callback<ListView<VirtualAlbum>, ListCell<VirtualAlbum>>() {
            @Override
            public ListCell<VirtualAlbum> call(ListView<VirtualAlbum> param) {
                return new TextFieldListCell<VirtualAlbum>() {
                    private TextField textField;
                    private MenuItem remove = new MenuItem("Remove");
                    MenuItem rename = new MenuItem("Rename");

                    {
                        remove.setOnAction(event -> removeVA());
                        rename.setOnAction(event -> {
                            setEditable(true);
                            startEdit();
                        });
                        setConverter(new StringConverter<VirtualAlbum>() {
                            @Override
                            public String toString(VirtualAlbum object) {
                                return object.getName();
                            }

                            @Override
                            public VirtualAlbum fromString(String string) {
                                // promjena imena albuma i čuvanje na FS
                                getItem().setName(string);
                                serializeAlbums();
                                return getItem();
                            }
                        });
                    }

                    @Override
                    public void startEdit() {
                        if (isEditable()) {
                            super.startEdit();
                            if (textField == null) {
                                textField = (TextField) getGraphic();
                                textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                                    if (!textField.isFocused())
                                        commitEdit(getItem());
                                });
                            }
                        }
                    }

                    @Override
                    public void updateItem(VirtualAlbum item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setEditable(false);
                            setText(item.getName());
                            setContextMenu(new ContextMenu(remove, rename));
                        }
                    }

                    @Override
                    public void commitEdit(VirtualAlbum newValue) {
                        super.commitEdit(newValue);
                        if (isEditable())
                            setEditable(false);
                        // TODO provjeriti da li je novi naziv ispravan (dostupan) i ako jeste promijeniti ime VA
                        // ako ime već postoji ja bih dodao broj (koji može) kraj toga imena
                    }

                    @Override
                    public void cancelEdit() {
                        super.cancelEdit();
                        if (isEditable())
                            setEditable(false);
                    }
                };
            }
        });

        //ucitavanje serijalizovanih VA
        readSerializedAlbums();

        setFirstElementOfListViewSelected();

        //new Thread(this::setTreeView).start();
        new Thread(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                setTreeView();
                return null;
            }
        }).start();

        menuSendSS.setOnAction(event -> showScreenShotSendWindow());

        tabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            if (newTab.equals(tabFS)) {
                treeView.getSelectionModel().clearSelection();
                lblAlbumDescription.setText("");
                btnAddImages.setVisible(false);
                flowPane.getChildren().clear();
                if (checked != 0) {
                    checked = 0;
                    btnRemove.setVisible(false);
                }
            }
            if (newTab.equals(tabAlbumi)) {
                if (listView.getSelectionModel().getSelectedIndex() >= 0)
                //if (!btnCheck.isVisible())
                {
                    btnAddImages.setVisible(true);
                }
                if (listViewData.isEmpty()) {
                    flowPane.getChildren().clear();
                } else {
                    if (listView.getSelectionModel().getSelectedItem() != null) {
                        lblAlbumDescription.setText(listView.getSelectionModel().getSelectedItem().getDescription());
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

        btnAddNewAlbum.setOnMouseClicked(event -> showCreateNewAlbumWindow());

        btnAddImages.setOnMouseClicked(event -> addImages());

        btnCheck.setOnMouseClicked(event -> addImagesToAlbum());

        btnAbort.setOnMouseClicked(event -> abortAddingImages());

        btnRemove.setOnMouseClicked(event -> removeImagesFromVA());


        // Notification test
        menu.setOnMouseClicked(event -> {
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
            btnAddImages.setVisible(true);
            listView.getSelectionModel().selectFirst();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        Platform.runLater(() -> {
            stage.getScene().getWindow().addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
                if (event.getCode().equals(KeyCode.PRINTSCREEN)) {
                    showScreenShotSendWindow();
                }
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
            if (tabPane.getSelectionModel().getSelectedItem().equals(tabAlbumi)) {
                imageViewController.setVirtualAlbum(listView.getSelectionModel().getSelectedItem(), index);
            } else {
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
        isThereSomethigToPaste = new SimpleBooleanProperty(false);
        isDisabled = new SimpleBooleanProperty(true);
        treeView.setCellFactory(new Callback<TreeView<MyFile>, TreeCell<MyFile>>() {
            @Override
            public TreeCell<MyFile> call(TreeView<MyFile> param) {
                return new TextFieldTreeCell<MyFile>() {
                    MenuItem menuNewFolderTreeView = new MenuItem("New folder");
                    MenuItem menuCopyTreeView = new MenuItem("Copy");
                    MenuItem menuCutTreeView = new MenuItem("Cut");
                    MenuItem menuPasteTreeView = new MenuItem("Paste");
                    MenuItem menuDeleteTreeView = new MenuItem("Delete");
                    MenuItem menuRenameTreeView = new MenuItem("Rename");

                    {
                        setConverter(new StringConverter<MyFile>() {
                            @Override
                            public String toString(MyFile object) {
                                return object.toString();
                            }

                            @Override
                            public MyFile fromString(String string) {
                                MyFile file = new MyFile(getItem().getParent(), string);
                                new Thread(() -> {
                                    try {
                                        Files.move(getItem().toPath(), getItem().toPath().resolveSibling(string));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }).start();
                                return file;
                            }
                        });
                        menuRenameTreeView.disableProperty().bind(isDisabled.not());
                        menuRenameTreeView.setOnAction(event -> startEdit());
                        menuNewFolderTreeView.setOnAction(event -> newFolderButtonFunction());
                        menuDeleteTreeView.disableProperty().bind(isDisabled.not());
                        menuDeleteTreeView.setOnAction(event -> {
                            if (!showRemoveVirtualAlbumWindow("Deleting \"" + treeView.getSelectionModel().getSelectedItem().getValue().getName() + "\"")) {
                                deleteFolder();
                            }
                        });
                        menuCopyTreeView.setOnAction(event -> {
                            isCutted = false;
                            copyToClipboard();
                        });
                        menuCutTreeView.setOnAction(event -> {
                            isCutted = true;
                            copyToClipboard();
                        });
                        menuPasteTreeView.disableProperty().bind(isThereSomethigToPaste.not());
                        menuPasteTreeView.setOnAction(event -> {
                            if (isThereSomethigToPaste.getValue()) {
                                treeView.getSelectionModel().getSelectedItem().getChildren().add(new TreeItem<>(new MyFile(name)));
                                pasteFromClipboard(new MyFile(clipboard), new MyFile(treeView.getSelectionModel().getSelectedItem().getValue().getAbsolutePath() + File.separator + name));
                                if (isCutted) {
                                    isCutted = false;
                                    deleteFromDisk(new File(clipboard));
                                }
                                isThereSomethigToPaste.set(false);
                            }
                            /* najbolje ako nema ništa da se pastuje da se ništa i ne desi
                            else {
                                Main.showNotification("Nothing to paste.");
                            } */
                        });
                        // onemogući rename ukoliko ima slika unutar foldera
                        // isDisabled može da kontroliše i ovo
                        setEditable(false);
                    }

                    @Override
                    public void updateItem(MyFile item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty && item != null) {
                            if ("".equals(item.getName()) || "Desktop".equals(item.getName())) {
                                setContextMenu(new ContextMenu(menuNewFolderTreeView, menuPasteTreeView));
                            } else {
                                editableProperty().bind(isDisabled);
                                setContextMenu(new ContextMenu(menuNewFolderTreeView, menuCopyTreeView, menuCutTreeView, menuPasteTreeView, menuDeleteTreeView, menuRenameTreeView));
                            }
                        }
                        if (empty) {
                            setContextMenu(null);
                        }
                    }
                };
            }
        });
        TreeItem<MyFile> root = new TreeItem<>();
        treeView.setRoot(root);
        treeView.setShowRoot(false);
        // desktop
        setChild(root, new TreeItem<>(new MyFile(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath())));
        // particije
        for (File file : File.listRoots())
            if (file.isDirectory()) {
                setChild(root, new TreeItem<>(new MyFile(file.getAbsolutePath())));
            }
        // za dinamičko učitavanje
        root.addEventHandler(TreeItem.branchExpandedEvent(), new EventHandler<TreeItem.TreeModificationEvent<MyFile>>() {
            public void handle(TreeItem.TreeModificationEvent<MyFile> event) {
                TreeItem<MyFile> item = event.getTreeItem();
                treeView.getSelectionModel().select(item);
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        TreeItem<MyFile> temp = new TreeItem<>();
                        findChilds(item.getValue(), temp);
                        Platform.runLater(() -> item.getChildren().setAll(temp.getChildren()));
                        return null;
                    }
                };
                treeViewProgressIndicator.progressProperty().bind(task.progressProperty());
                treeViewProgressIndicator.visibleProperty().bind(task.runningProperty());
                new Thread(task).start();
            }
        });

        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<File> images = FXCollections.observableArrayList();
            if (newValue != null) {
                images.setAll(newValue.getValue().listFiles((dir, name) -> name.endsWith("jpg") || name.endsWith("png") || name.endsWith("jpeg")));
                if (images.isEmpty())
                    isDisabled.set(true);
                else
                    isDisabled.set(false);
                lblAlbumDescription.setText(newValue.getValue().toString());
            }
            setImagesToFlowPane(images);
        });
    }

    private void newFolderButtonFunction() {
        TreeItem<MyFile> item = treeView.getSelectionModel().getSelectedItem();
        File itemFile = item.getValue();
        int count = 0;
        for (File dir : itemFile.listFiles()) {
            String name;
            if (count == 0) {
                name = "New folder";
            } else {
                name = "New folder (" + count + ")";
            }
            if (dir.isDirectory() && dir.getName().equals(name)) {
                count++;
            }
        }
        File newFile;
        if (count == 0) {
            newFile = new File(itemFile, "New folder");
        } else {
            newFile = new File(itemFile, "New folder (" + count + ")");
        }
        newFile.mkdir();
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                TreeItem<MyFile> temp = new TreeItem<>();
                findChilds(item.getValue(), temp);
                Platform.runLater(() -> item.getChildren().setAll(temp.getChildren()));
                return null;
            }
        };
        treeViewProgressIndicator.visibleProperty().bind(task.runningProperty());
        new Thread(task).start();
    }

    // pronalazi podfoldere
    private void findChilds(File file, TreeItem<MyFile> node) {
        if (file.isDirectory()) {
            node.getChildren().clear();
            for (File var : file.listFiles((File pathname) -> pathname.isDirectory() && !pathname.isHidden() && Files.isReadable(pathname.toPath()) && !Files.isSymbolicLink(pathname.toPath()) && !"Windows".equals(pathname.getName())))
                setChild(node, new TreeItem<>(new MyFile(var.getAbsolutePath())));
        }
    }

    // Don't repeat yourself  :D
    private void setChild(TreeItem<MyFile> parent, TreeItem<MyFile> child) {
        child.getChildren().add(new TreeItem<>(new MyFile("Loading..."))); // kako bi se pojavlia strelica za expand
        parent.getChildren().add(child);
    }

    // dodavanje slika u flowPane
    private void setImagesToFlowPane(ObservableList<File> images) {
        ObservableList<Node> childs = flowPane.getChildren();
        childs.clear();
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                /* code start here  */
                for (File file : images) {
                    ImageFrame iFrame = new ImageFrame(file);
                    Platform.runLater(() -> childs.add(iFrame));
                    iFrame.setOnMouseClicked((MouseEvent event) -> {
                        if (event.getButton().equals(MouseButton.PRIMARY)) {
                            if (checked == 0) {
                                showImageViewController(getImagesFromFlowPane(), flowPane.getChildren().indexOf(iFrame));
                                iFrameContextMenu.hide();
                            } else {
                                if (!iFrame.getCheckBox().isSelected()) {
                                    iFrame.getCheckBox().setSelected(true);
                                    checked++;
                                } else {
                                    iFrame.getCheckBox().setSelected(false);
                                    checked--;
                                    if (checked == 0) {
                                        if (tabPane.getSelectionModel().getSelectedItem().equals(tabFS)) {
                                            btnAddImages.setVisible(false);
                                        }
                                        btnDelete.setVisible(false);
                                        btnRemove.setVisible(false);
                                    }
                                }
                            }
                        }
                    });

                    iFrame.setOnContextMenuRequested(event -> {
                        //TODO napraviti nekako vezu sa ovim iFrame ako ni jedan nije čekiran
                        iFrameContextMenu.show(iFrame, event.getScreenX(), event.getScreenY());
                        //iFrame.getCheckBox().setSelected(true);
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
                                    if (tabPane.getSelectionModel().getSelectedItem().equals(tabFS)) {
                                        btnAddImages.setVisible(false);
                                    }
                                    btnDelete.setVisible(false);
                                    btnRemove.setVisible(false);
                                }
                            }
                        }
                    });
                }
                /* code end here  */
                return null;
            }
        };
        progressIndicator.visibleProperty().bind(task.runningProperty());
        //problem kada učitava mnogo slika
        //treeView.disableProperty().bind(task.runningProperty());
        new Thread(task).start();
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
            if (((ImageFrame) node).getCheckBox().isSelected()) {
                images.add(((ImageFrame) node).getFile());
            }

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
            if (!controller.isCancel()) {
                listView.getSelectionModel().selectLast();
                listView.scrollTo(listViewData.size() - 1);
                if (!flowPane.getChildren().isEmpty()) {
                    flowPane.getChildren().clear();
                }
            }
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
            stage.setScene(new Scene(root));
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
            tabAlbumi.setDisable(true);
            btnCheck.setVisible(true);
            btnAbort.setVisible(true);
            //btnAddImages.setVisible(false);
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
        if (tabAlbumi.isDisable()) {
            tabAlbumi.setDisable(false);
        }
        buttonsVisibleControl = true;
        listView.getSelectionModel().getSelectedItem().setImages(getCheckedImagesFromFlowPane());
        new Thread(this::serializeAlbums).start();
        tabPane.getSelectionModel().select(tabAlbumi);
        fromAlbum = false;
        btnCheck.setVisible(false);
        btnAbort.setVisible(false);
        btnAddImages.setVisible(true);
    }

    //omogucava abort
    private void abortAddingImages() {
        if (tabAlbumi.isDisable()) {
            tabAlbumi.setDisable(false);
        }
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
            stage.setScene(new Scene(root, 319, 149));
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.showAndWait();
            if (controller.getIndexOfVA() > 0) {
                listView.getSelectionModel().select(controller.getIndexOfVA());
                listView.scrollTo(controller.getIndexOfVA());
            }
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
        /* nema potrebe za ovim ?
        if (inputDirectory.isDirectory()) {
            File[] fileList = inputDirectory.listFiles();
            if (fileList != null) {
                for (File f : fileList) {
                    try {
                        ObjectInputStream object = new ObjectInputStream(new FileInputStream(f));
                        listViewData.addAll((ArrayList<VirtualAlbum>) object.readObject());
                        object.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } */
        if (inputDirectory.isDirectory()) {
            try {
                File file = new File(path + File.separator + "virtualalbums.kva");
                if (file.exists()) {
                    ObjectInputStream object = new ObjectInputStream(new FileInputStream(file));
                    listViewData.setAll((ArrayList<VirtualAlbum>) object.readObject());
                    //ako nema sačuvanog ovog albuma onda baca izuzetak, neće se dešavati nakon prvog čuvanja
                    screenshotAlbum = (VirtualAlbum) object.readObject();
                    object.close();
                } else
                    screenshotAlbum = new VirtualAlbum("", "");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    //metoda koja serijalizuje VA iz listViewData
    private void serializeAlbums() {
        new Thread(() -> {
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
                object.writeObject(screenshotAlbum);
                object.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    //ukoliko je potvrdjeno brisanje brise VA i prikazuje slike iz sledeceg
    private void removeVA() {
        if (!showRemoveVirtualAlbumWindow("Remove virtual album?")) {
            listViewData.remove(listView.getSelectionModel().getSelectedItem());
            if (listViewData.isEmpty()) {
                flowPane.getChildren().clear();
                btnAddImages.setVisible(false);
            }
        }
    }

    //metoda koja prikazuje prozor u kojem se potcrdjuje brisanje VA
    private boolean showRemoveVirtualAlbumWindow(String text) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/removeVirtualAlbum.fxml"));
            Parent root = loader.load();
            RemoveVirtualAlbumWindowController controller = loader.getController();
            Stage stage = new Stage();
            controller.setStage(stage);
            controller.setText(text);
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

    //metoda za uklanjanje slika iz virtuelnog album
    private void removeImagesFromVA() {
        ObservableList<File> temp = listView.getSelectionModel().getSelectedItem().getImages();
        temp.removeAll(getCheckedImagesFromFlowPane());
        setImagesToFlowPane(temp);
    }

    //kao sto pise, brise foldere kad se na FS izabere delete iz dropdown menija
    //rekurzivno brisanje
    private void deleteFolder() {
        TreeItem<MyFile> item = treeView.getSelectionModel().getSelectedItem();
        item.getParent().getChildren().remove(item);
        // kada radiš nešto u pozadini to treba prebaciti u neki background thread da se ne opterećuje UI thread
        new Thread(() -> deleteFromDisk(item.getValue())).start();
    }

    private void deleteFromDisk(File file) {
        for (File var : file.listFiles()) {
            if (var.isDirectory()) {
                deleteFromDisk(var);
            } else if (!var.delete()) {
                var.deleteOnExit();
            }
        }
        if (!file.delete()) {
            file.deleteOnExit();
        }
    }

    private void copyToClipboard() {
        clipboard = treeView.getSelectionModel().getSelectedItem().getValue().getAbsolutePath();
        name = treeView.getSelectionModel().getSelectedItem().getValue().getName();
        isThereSomethigToPaste.set(true);
    }

    private void pasteFromClipboard(File sourceLocation, File targetLocation) {
        //TODO reakcija na folder/file already exist, ne daj Bože nema mjesta i slične greške
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (sourceLocation.isDirectory()) {
                    copyDirectory(sourceLocation, targetLocation);
                } else {
                    copyFile(sourceLocation, targetLocation);
                }
                return null;
            }
        };
        if (isCutted)
            copyText.setText("Moving...");
        else
            copyText.setText("Copying...");
        copyProgress.visibleProperty().bind(task.runningProperty());
        copyText.visibleProperty().bind(task.runningProperty());
        treeView.disableProperty().bind(task.runningProperty());
        new Thread(task).start();
    }

    private void copyDirectory(File source, File target) {
        if (!target.exists()) {
            target.mkdir();
        }
        for (String f : source.list()) {
            pasteFromClipboard(new File(source, f), new File(target, f));
        }
    }

    private void copyFile(File source, File target) {
        try {
            InputStream in = new FileInputStream(source);
            OutputStream out = new FileOutputStream(target);
            byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showPreferencesWindow() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/preferences.fxml"));
            Stage stage = new Stage(StageStyle.UNIFIED);
            stage.setScene(new Scene(root, 600, 400));
            stage.getIcons().add(new Image("/images/khronos.png"));
            stage.setTitle("Preferences");
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}