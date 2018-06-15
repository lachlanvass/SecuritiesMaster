package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.VBox;

public class ScrollingVBox {

    private Node Content;
    public ScrollingVBox(Node content) {
        Content = content;
    }

    public Group getScrollingVBox() {

        Group group_root = new Group();
        ScrollBar scrollBar = new ScrollBar();
        scrollBar.setMin(0);
        scrollBar.setOrientation(Orientation.VERTICAL);
        VBox vb_graphBox = new VBox();
        vb_graphBox.setSpacing(10);
        vb_graphBox.setPadding(new Insets(0, 0, 10, 20));
        vb_graphBox.getChildren().add(Content);

        group_root.getChildren().addAll(vb_graphBox, scrollBar);
        scrollBar.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                vb_graphBox.setLayoutY(-newValue.doubleValue());
            }
        });

        return group_root;
    }
}
