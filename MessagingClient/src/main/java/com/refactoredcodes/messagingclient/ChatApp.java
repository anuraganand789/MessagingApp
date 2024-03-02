package com.refactoredcodes.messagingclient;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ChatApp extends Application {

    private final ObservableList<String> observableListOfUsers = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) {
        final HBox rootNode = new HBox();
        rootNode.getChildren().add(usersLayouts());
        rootNode.getChildren().add(chatContainer());

        final   Scene   mainScene   =   new Scene(rootNode);
        stage.setScene(mainScene);

        stage.setHeight(700);
        stage.setWidth(700);
        stage.show();
    }

    private Node chatContainer(){
        final VBox vboxMessageContainer = new VBox();
        vboxMessageContainer
                .getChildren()
                .addAll(
                            listOfMessagesPerUserContainer()
                        ,   sendMessageContainer()
                        );

        vboxMessageContainer.setSpacing(4);
        vboxMessageContainer.setBorder(Border.stroke(Paint.valueOf("#342424")));
        vboxMessageContainer.setBackground(Background.fill(Paint.valueOf("green")));
        vboxMessageContainer.maxHeight(Integer.MAX_VALUE);
        return
                vboxMessageContainer;
    }
    // need to create per user per chat
    private Node listOfMessagesPerUserContainer(){
        final ListView listViewMessages = new ListView();
        listViewMessages.setPlaceholder(new Label("Placeholder"));
        listViewMessages.setStyle("""
                -fx-padding         :   2;
                """);
        listViewMessages.setMinHeight(600);
        listViewMessages.setMinWidth(500);
        return listViewMessages;
    }

    private Node sendMessageContainer(){
        final TextArea textareaMessage    = new TextArea();
        textareaMessage.setMinWidth(180);
        textareaMessage.setMaxHeight(56);

        final Font font = new Font(14);
        textareaMessage.setFont(font);

        final Button    buttonSendMessage   = new Button("Send");
        buttonSendMessage.setMaxWidth(120);
        buttonSendMessage.setMinWidth(120);
        buttonSendMessage.setMaxHeight(40);
        buttonSendMessage.setCursor(Cursor.HAND);

        final Font buttonFont = new Font(14);
        buttonSendMessage.setFont(buttonFont);

        buttonSendMessage.setStyle("""
                -fx-text-fill           :   #fff;
                -fx-background-color    :   #0f6cbd;
                """);

        final HBox  hBoxMessageSendLayout = new HBox(textareaMessage, buttonSendMessage);
        hBoxMessageSendLayout.setSpacing(2);
        hBoxMessageSendLayout.setStyle("""
                -fx-padding         :   2;
                -fx-background-color    :   red;
                """);
        hBoxMessageSendLayout.setMaxHeight(240);
        hBoxMessageSendLayout.setAlignment(Pos.CENTER);

        hBoxMessageSendLayout.setAlignment(Pos.BOTTOM_CENTER);
        return hBoxMessageSendLayout;
    }

    private Node usersLayouts(){
        final ListView listView = new ListView();
        listView.setItems(observableListOfUsers);

        observableListOfUsers.addAll("user1", "user2", "user3");
        return listView;
    }

    public static void main(String[] args) {
        launch();
    }
}