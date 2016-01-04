package sample;

import javafx.application.Application;
import static javafx.application.Application.launch;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.scene.Scene;
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
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;


/**
 * Created by acous on 12/30/2015.
 */
public class VideoNode extends MapNode{
    private GridPane nodePane;
    private byte[] imgToByte;
    private Image image;
    private String contents;
    private String content_Url;

    public VideoNode(String in)
    {
        this.contents = in;
        this.contents = getContent();
        this.type = type.link;
        this.setUserVote(Boolean.TRUE);
        this.incrementVoteCounter();
        this.createdBy = DataConnection.loggedUser.getUser();
        this.nodePerm = DataConnection.loggedUser.getAccount();
        content_Url = "<iframe width=\"560\" height=\"315\" src=\"" + contents
                + "\" frameborder=\"0\" allowfullscreen></iframe>";

        this.drawNode();

    }

    public VideoNode(int id, int pid, String in, Timestamp date_created, int numVotes, String user, String accountType)
    {
        this.uniqueId = id;
        this.parent = pid;
        this.contents = in;
        this.timeCreated = date_created;
        this.votes = numVotes;
        this.type = type.link;
        this.createdBy = user;
        this.nodePerm = accountType;

        content_Url = "<iframe width=\"560\" height=\"315\" src=\"" + contents
                + "\" frameborder=\"0\" allowfullscreen></iframe>";

        //this.drawNode();
    }


    public void drawNode() {
        File icon = new File("./Images/youtube-icon.png");
        image = new Image(icon.toURI().toString());
        ImageView viewer = new ImageView(image);
        viewer.setPreserveRatio(Boolean.TRUE);
        viewer.setFitHeight(80.0f);
        double height = viewer.getBoundsInParent().getHeight() * 4/5;
        double width = viewer.getBoundsInParent().getWidth() * 4/5;

        Ellipse newNode = new Ellipse(0.0f, 0.0f, width, height);
        if(this.nodePerm.equals("student")) {
            newNode.setFill(Paint.valueOf("white"));
            newNode.setStroke(Paint.valueOf("black"));
        }
        else {
            newNode.setFill(Paint.valueOf("black"));
            newNode.setStroke(Paint.valueOf("black"));
        }

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
        Text numberOfVotes = new Text(" "+votes);
        numberOfVotes.setStyle("-fx-font: 20 arial");
        numberOfVotes.setStroke(Color.WHITE);
        numberOfVotes.setFill(Color.WHITE);
        HBox arr = new HBox();
        arr.getChildren().addAll(arrowView,numberOfVotes);


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

        StackPane stack = new StackPane();
        stack.getChildren().addAll(newNode, viewer);
        stack.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    Image logo = new Image("sample/OrangeIcon.png");
                    Stage newStage = new Stage();
                    newStage.initModality(Modality.APPLICATION_MODAL);
                    newStage.setTitle("Video Viewer");
                    newStage.getIcons().add(logo);
                    newStage.setResizable(true);

                    WebView webView = new WebView();
                    WebEngine webEngine = webView.getEngine();
                    webEngine.loadContent(content_Url);

                    StackPane root = new StackPane();
                    root.getChildren().add(webView);
                    Scene scene = new Scene(root, 600, 330);
                    newStage.setScene(scene);
                    newStage.centerOnScreen();
                    newStage.show();


                    newStage.setOnCloseRequest(new EventHandler<javafx.stage.WindowEvent>() {
                        @Override
                        public void handle(javafx.stage.WindowEvent event) {
                            webView.getEngine().load(null);
                        }
                    });

                }
            }
        });
        nodePane = new GridPane();
        nodePane.add(arr,0,0);
        nodePane.add(stack,0,1);

        nodePane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent m) {
                if(m.getButton() == MouseButton.PRIMARY) {
                    nodePane.setLayoutX(m.getSceneX() - nodePane.getWidth() / 2);
                    nodePane.setLayoutY(m.getSceneY() - nodePane.getHeight());
                }
            }
        });

        Tooltip tooltip;
        tooltip = new Tooltip("Created By: " + this.createdBy);
        Tooltip.install(nodePane,tooltip);
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

    public GridPane getNodePane()
    {
        return nodePane;
    }

    private String getContent() {
        String temp;
        temp = contents.replace("watch?v=", "embed/");
        return temp;
    }

    public void makeNode() {
        this.drawNode();
    }
}
