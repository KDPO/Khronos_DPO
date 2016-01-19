package net.etfbl.kdpo.client;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Stijak on 16.12.2015..
 */
public class PreferencesController {


	@FXML
	private ListView<String> listViewUsers;


	@FXML
	private Button btnConnectToServer;


	@FXML
	private Button btnUnblock;

	@FXML
	private Button btnBlockAll;

	@FXML
	private Button btnDisconnectFromServer;

	@FXML
	private Button btnBlock;

	@FXML
	private Label lblSelectUser;

	@FXML
	private Button btnOK;

	@FXML
	private Button btnCancel;

	@FXML
	private TabPane tabPane;

	@FXML
	private Tab tabHelp;

	@FXML
	private Label lblError;

	@FXML
	private TextFlow textFlow;

	private Stage stage;

	private StringBuilder stringBuilder;

	@FXML
	void initialize() {
		//TODO sve što treba :D
		btnCancel.setOnAction(event -> {
			stage.close();
		});

		new Thread(() -> {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(new File("/config/help.txt")));
				stringBuilder = new StringBuilder();
				String input = "";
				while ((input = reader.readLine()) != null)
					stringBuilder.append(input);
				Text text = new Text(stringBuilder.toString());
				Platform.runLater(() -> textFlow.getChildren().add(text));
			} catch (Exception e) {

			}
		}).start();
		btnConnectToServer.setOnMouseClicked(event -> btnConnectToServerFunction());
		btnDisconnectFromServer.setOnMouseClicked(event -> btnDisconnectFromServer());
		if (ClientServicesThread.clientServicesThread == null) {
			lblError.setOpacity(0.0);
		} else {
			lblError.setOpacity(1.0);
		}

		/*      --------------  */
		ObservableList<String> data = FXCollections.observableArrayList();
		listViewUsers.setItems(data);

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					Thread.sleep(2000);
					String response = ClientServicesThread.displayUsers();
					System.out.println("RESPONSE" + response);
					String[] users = response.split("#");
					for (int i = 1; i < users.length; ++i)
						data.add(users[i]);
				} catch (Exception e) {
					Platform.runLater(() -> {
						lblError.setOpacity(1.0);
					});
				}
				return null;
			}
		};
		if (ClientServicesThread.socket != null) {
			new Thread(task).start();
		} else {
			Main.showNotification("Not Connected!");
		}


		listViewUsers.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
			@Override
			public ObservableValue<Boolean> call(String item) {
				BooleanProperty observable = new SimpleBooleanProperty();
				observable.addListener((obs, wasSelected, isNowSelected) -> {
					if (wasSelected) {
						selectedForBlocking.remove(item);
					} else {
						selectedForBlocking.add(item);
					}
				});
				return observable;
			}
		}));


		btnCancel.setOnMouseClicked(event -> {
			task.cancel();
			stage.close();
		});

		btnBlockAll.setOnMouseClicked(event -> {
			if (ClientServicesThread.clientServicesThread != null) {
				StringBuilder toServer = new StringBuilder();
				toServer.append("CONTROL#BLOCKUSER");
				for (String username : selectedForBlocking) {
					toServer.append("#").append(username);
				}
				ClientServicesThread.out.println(toServer.toString());
			}
		});

	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}


	private ArrayList<String> selectedForBlocking = new ArrayList<>();

	private void btnConnectToServerFunction() {
		if (ClientServicesThread.clientServicesThread == null || ClientServicesThread.socket == null || ClientServicesThread.socket.isClosed()) {
			try {
				ClientServicesThread.startClientServicesThread();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void btnDisconnectFromServer() {
		if (ClientServicesThread.clientServicesThread != null) {
			try {
				ClientServicesThread.out.println("EXIT");
				ClientServicesThread.out.close();
				ClientServicesThread.in.close();
				ClientServicesThread.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
