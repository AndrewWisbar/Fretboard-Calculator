module com.example.guitar_fretboard {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.guitar_fretboard to javafx.fxml;
    exports com.example.guitar_fretboard;
}