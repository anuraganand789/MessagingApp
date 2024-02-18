module com.refactoredcodes.messagingclient {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.refactoredcodes.messagingclient to javafx.fxml;
    exports com.refactoredcodes.messagingclient;
}