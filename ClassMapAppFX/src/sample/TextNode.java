package sample;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

import java.io.File;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

public class TextNode extends MapNode{

    private GridPane nodePane;
    private String contents;
    private Alert showString;

    public TextNode(String in)
    {
        this.contents = in;
        this.type = type.string;
        this.setUserVote(Boolean.TRUE);
        this.incrementVoteCounter();
        this.createdBy = DataConnection.loggedUser.getUser();
        this.nodePerm = DataConnection.loggedUser.getAccount();
        this.drawNode();

    }

    public TextNode(int id, int pid, String in, Timestamp date_created, int numVotes, String user, String accountType)
    {
        this.uniqueId = id;
        this.parent = pid;
        this.contents = in;
        this.timeCreated = date_created;
        this.votes = numVotes;
        this.type = type.string;
        this.createdBy = user;
        this.nodePerm = accountType;
        //this.drawNode();
    }

    public void drawNode() {
        Text text = new Text(contents);
        text.setBoundsType(TextBoundsType.VISUAL);
        text.setWrappingWidth(150.0f);

        double height = (text.getLayoutBounds().getHeight())*2/3;
        double width = (text.getLayoutBounds().getWidth())*2/3;
        double minHeight = 60.0f;
        double minWidth = 60.0f;

        if(width < minHeight)
        {
            width = minHeight;
        }
        if(height < minWidth)
        {
            height = minWidth;
        }

        Ellipse newNode = new Ellipse(0.0f, 0.0f, width, height);
        if(this.uniqueId == 1) {
            text.setStroke(Paint.valueOf("white"));
            newNode.setFill(Paint.valueOf("blue"));
            newNode.setStroke(Paint.valueOf("black"));
        }
        else if(this.nodePerm.equals("student")) {
            newNode.setFill(Paint.valueOf("white"));
            newNode.setStroke(Paint.valueOf("black"));
        }
        else {
            text.setStroke(Paint.valueOf("white"));
            newNode.setFill(Paint.valueOf("black"));
            newNode.setStroke(Paint.valueOf("black"));
        }

        StackPane stack = new StackPane();
        stack.getChildren().addAll(newNode, text);

        stack.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.SECONDARY)
                    showString.display("Text Node", contents);
            }
        });

        Image arrow;
        ImageView arrowView;
        if(this.getUserVote() == Boolean.TRUE)
        {
            File path = new File("./Images/arrow-up-icon_voted.png");
            arrow = new Image(path.toURI().toString());

        }
        else
        {
            File path = new File("./Images/arrow-up-icon.png");
            arrow = new Image(path.toURI().toString());
        }

        arrowView = new ImageView(arrow);
        arrowView.setPreserveRatio(Boolean.TRUE);
        arrowView.setFitHeight(20.0f);
        Text numberOfVotes = new Text(""+votes);
        numberOfVotes.setStroke(Color.WHITE);
        numberOfVotes.setFill(Color.WHITE);
        numberOfVotes.setStyle("-fx-font: 20 arial");
        HBox arr = new HBox();

        arr.getChildren().addAll(arrowView,numberOfVotes);

        /*
        Toggles upvote actions.
         */
        arr.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(getUserVote() == Boolean.TRUE)
                {
                    File path = new File("./Images/arrow-up-icon.png");
                    Image newArrow = new Image(path.toURI().toString());
                    ImageView newArrowView = new ImageView(newArrow);
                    newArrowView.setPreserveRatio(Boolean.TRUE);
                    newArrowView.setFitHeight(20.0f);
                    decrementVoteCounter();
                    Text numberOfVotes = new Text(""+(votes));
                    numberOfVotes.setStyle("-fx-font: 20 arial");
                    numberOfVotes.setStroke(Color.WHITE);
                    numberOfVotes.setFill(Color.WHITE);
                    arr.getChildren().remove(0, 2);
                    arr.getChildren().addAll(newArrowView, numberOfVotes);
                    setUserVote(false);
                    try {
                        sendSelf();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    //setVisible();

                }
                else
                {
                    File path = new File("./Images/arrow-up-icon_voted.png");
                    Image newArrow = new Image(path.toURI().toString());
                    ImageView newArrowView = new ImageView(newArrow);
                    newArrowView.setPreserveRatio(Boolean.TRUE);
                    newArrowView.setFitHeight(20.0f);
                    incrementVoteCounter();
                    Text numberOfVotes = new Text(""+(votes));
                    numberOfVotes.setStyle("-fx-font: 20 arial");
                    numberOfVotes.setStroke(Color.WHITE);
                    numberOfVotes.setFill(Color.WHITE);
                    arr.getChildren().remove(0, 2);
                    arr.getChildren().addAll(newArrowView, numberOfVotes);
                    setUserVote(true);
                    try {
                        sendSelf();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    //setVisible();
                }
            }
        });

        nodePane = new GridPane();
        nodePane.add(arr,0,0);
        nodePane.add(stack,0,1);

        nodePane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY) {
                    nodePane.setLayoutX(event.getSceneX() - nodePane.getWidth() / 2);
                    nodePane.setLayoutY(event.getSceneY() - nodePane.getHeight());
                }
            }
        });

        Tooltip tooltip;

        DictParser dict = new DictParser();
        dict.searchForWord(text.getText());
        if(dict.getCount() > 0)
        {
            tooltip = new Tooltip(dict.getExactDefinition() + "\n\n"
                + "Created By: " + this.createdBy);
        }
        else
        {
            tooltip = new Tooltip("Not a definable word" + "\n\n"
                + "Created By: " + this.createdBy);
        }

        Tooltip.install(nodePane,tooltip);
    }

    public void setTypeToText()
    {
        this.type = classification.string;
    }

    public GridPane getNodePane()
    {
        return nodePane;
    }

    public String getContents() {
        return this.contents;
    }

    public void sendSelf() throws SQLException { DataConnection.addUpvote(this); }

    public void makeVisible() {
        this.nodePane.setVisible(true);
    }

    public void setVisible() {
        this.nodePane.setVisible(false);
    }

    public String getAccountPerms() { return this.nodePerm; }

    public void makeNode() {
        this.drawNode();
    }
}
