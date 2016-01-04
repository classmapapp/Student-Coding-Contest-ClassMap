package sample;

import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;

import java.awt.*;
import java.awt.Rectangle;
import java.awt.event.*;
import java.io.*;
import java.security.Timestamp;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;




public class Controller {

    public TitledPane mainStage;
    public GridPane root;
    public Pane nodeStage;
    public Pane newNodeStage;
    public Pane newNodeStage2;
    public ScrollPane teacherPane;
    public Pane innerPane;
    int[] array = {0, 5, 1, 6, 2, 7, 3, 8, 4, 9};
    java.util.List<MapNode> masterNode = new ArrayList<>();
    java.util.List<java.util.List<MapNode>> nodeList = new ArrayList<>();
    java.util.List<Line> lineList = new ArrayList<>();
    public ArrayList<MapNode> masterNodeList = new ArrayList<MapNode>();
    public MapNode daroot;
    public Accordion topicMenuAccordion;
    private boolean firstTimePublic = true;
    int prevPaneCordX, prevPaneCordY, prevMouseCordX, prevMouseCordY,diffX, diffY;

    TextNode classNode;
    TextNode newNode;
    ImageNode newImageNode;
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;
    VideoNode videoNode;
    int randomNumber;
    double factor = 1;
    boolean nodedrag = false;

    int index =0 ;
    int layer = 0;
    File path = new File("./Images/Drag-icon.png");
    javafx.scene.image.Image dragPicture = new javafx.scene.image.Image(path.toURI().toString());
    ImageView newDragView = new ImageView(dragPicture);

    public void drawTeacherPanel(ActionEvent actionEvent) {
        if (DataConnection.loggedUser.getAccount().equals("teacher")) {
            GridPane displayGrid = new GridPane();
            displayGrid.setMaxWidth(200);

            teacherPane.setVisible(true);
            int numStudents = DataConnection.students.size();
            for (int i = 0; i < numStudents; i++) {

                Text text = new Text( "   First: " + DataConnection.students.get(i).getFirstName() + "\n" + "   Last: " + DataConnection.students.get(i).getLastName() + "\n" + "   UserName: " + DataConnection.students.get(i).getUserName() + "\n" + "   Email: " + DataConnection.students.get(i).getEmail());
                text.setBoundsType(TextBoundsType.LOGICAL);
                //text.setTextAlignment(TextAlignment.LEFT);
                //text.setWrappingWidth(180.0f);

                double height = (text.getLayoutBounds().getHeight())*8/9;
                //double width = (text.getLayoutBounds().getWidth())*8/9;

                javafx.scene.shape.Rectangle newNode = new javafx.scene.shape.Rectangle(0.0f, 0.0f, 200, height + 20.0f);
                newNode.setFill(Paint.valueOf("white"));
                newNode.setStroke(Paint.valueOf("black"));
                StackPane stack = new StackPane();
                stack.getChildren().addAll(newNode, text);
                stack.setAlignment(text, Pos.CENTER_LEFT);

                int j = i;
                stack.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        showReset(event);
                        showUserNodes(event, DataConnection.students.get(j).getUserName());
                    }
                });

                DataConnection.displayPanes.add(new GridPane());
                DataConnection.displayPanes.get(i).add(stack,0,0);
                DataConnection.displayPanes.get(i).setVisible(true);

                displayGrid.add(DataConnection.displayPanes.get(i), 0, i);


            }     //displayGrid.setStyle("-fx-background-color: #000000;");
            teacherPane.setContent(displayGrid);
        }
    }

    public void showNew(ActionEvent actionEvent) {
        if(!firstTimePublic) {
            showHome(actionEvent);
            recursiveShowNew(daroot, DataConnection.loggedUser.getSQLLog());
        }
    }

    public boolean recursiveShowNew(MapNode rootNode, java.sql.Timestamp sqlLog) {

        if(rootNode.getTimeCreated()==null) {
            return true;
        }
        rootNode.previousVote = false;
        int children = rootNode.children.size();

        if (children == 0) {
            rootNode.previousVote = (rootNode.timeCreated.after(sqlLog));
            if (rootNode.previousVote == false) {
                rootNode.getParentLine().setVisible(false);
                if (rootNode.type.toString().equals("string"))
                    ((TextNode) (rootNode)).setVisible();

                if (rootNode.type.toString().equals("image"))
                    ((ImageNode) (rootNode)).setVisible();

                if (rootNode.type.toString().equals("link"))
                    ((VideoNode) (rootNode)).setVisible();
            }
        }
        else {
            for (int i = 0; i < children; i++) {
                if(rootNode.previousVote == false)
                    rootNode.previousVote = recursiveShowNew(rootNode.children.get(i), sqlLog);
                else
                    recursiveShowNew(rootNode.children.get(i), sqlLog);
            }
        }

        if (rootNode.getTimeCreated().after(sqlLog)) {
            rootNode.previousVote = true;
        }
        else {
            if(rootNode.uniqueId == 1){

            }
            else {
                if (rootNode.getTimeCreated().after(sqlLog) == false && rootNode.previousVote == false) {
                    rootNode.getParentLine().setVisible(false);
                    if (rootNode.type.toString().equals("string"))
                        ((TextNode) (rootNode)).setVisible();

                    if (rootNode.type.toString().equals("image"))
                        ((ImageNode) (rootNode)).setVisible();

                    if (rootNode.type.toString().equals("link"))
                        ((VideoNode) (rootNode)).setVisible();
                }
            }
        }

        return rootNode.previousVote;
    }


    private class ZoomHandler implements EventHandler<ScrollEvent> {

        private Node nodeToZoom;

        private ZoomHandler(Node nodeToZoom) {
            this.nodeToZoom = nodeToZoom;
        }

        @Override
        public void handle(ScrollEvent scrollEvent) {
            //if (scrollEvent.isControlDown()) {
                    if(factor<=0.45)
                        newDragView.setVisible(false);
                    else
                        newDragView.setVisible(true);
                    final double scale = calculateScale(scrollEvent);
                    nodeToZoom.setScaleX(scale);
                    nodeToZoom.setScaleY(scale);
                    scrollEvent.consume();
            //}
        }

        private double calculateScale(ScrollEvent scrollEvent) {
            double scale = nodeToZoom.getScaleX() + scrollEvent.getDeltaY() / 400;
            factor = scale;
            if(scale<=0.1 ) {
                scale = 0.1;
            }
            if(scale>1)
                scale = 1;
            return scale;
        }
    }

    @FXML
    protected void initialize() {

        nodeStage.addEventFilter(ScrollEvent.ANY, new ZoomHandler(newNodeStage));
        nodeStage.addEventFilter(ScrollEvent.ANY, new ZoomHandler(newNodeStage2));
    }


    public void createTextNode(ActionEvent actionEvent){

        if(!firstTimePublic) {

            TextInputDialog dialog = new TextInputDialog("Enter the text for the node");
            dialog.setTitle("Create Text Node");
            dialog.setHeaderText("Enter the text below.");
            dialog.setContentText("Text: ");
            Optional<String> result = dialog.showAndWait();

            newNode = new TextNode(result.get());

            newNode.setTypeToText();
            //masterNodeList.add(newNode); I think this is still necessary but not for the database
            newNode.getNodePane().setOnMousePressed(OnMousePressedEventHandler);
            newNode.getNodePane().setOnMouseDragged(OnMouseDraggedEventHandler);
            newNode.getNodePane().setOnMouseReleased(OnMouseReleasedEventHandler);
            newNodeStage.getChildren().add(newNode.getNodePane());
        }

    }

    public void printMasterList()
    {
        int size = masterNodeList.size();

        for(int x = 0; x < size; x++)
        {
            System.out.println(masterNodeList.get(x).getSeconds());
        }
    }


    public void createImageNodeURL(ActionEvent actionEvent) {
        if(!firstTimePublic) {
            TextInputDialog dialog = new TextInputDialog("http://");
            dialog.setTitle("Create Image Node from URL");
            dialog.setHeaderText("Enter the URL below.");
            dialog.setContentText("URL: ");
            Optional<String> result = dialog.showAndWait();

            newImageNode = new ImageNode(result.get());
            newImageNode.setTypeToImage();
            //masterNodeList.add(newNode); I think this is still necessary but not for the database

            newImageNode.getNodePane().setOnMousePressed(OnMousePressedEventHandler);
            newImageNode.getNodePane().setOnMouseDragged(OnMouseDraggedEventHandler);
            newImageNode.getNodePane().setOnMouseReleased(ImageOnMouseReleasedEventHandler);
            newNodeStage.getChildren().add(newImageNode.getNodePane());
        }
    }

    public void createImageNodeFile(ActionEvent actionEvent) throws FileNotFoundException {
        if(!firstTimePublic) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open an image file");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp"));
            File openedFile = fileChooser.showOpenDialog(null);

            newImageNode = new ImageNode(openedFile);
            newImageNode.setTypeToImage();

            //masterNodeList.add(newNode); I think this is still necessary but not for the database

            newImageNode.getNodePane().setOnMousePressed(OnMousePressedEventHandler);
            newImageNode.getNodePane().setOnMouseDragged(OnMouseDraggedEventHandler);
            newImageNode.getNodePane().setOnMouseReleased(ImageOnMouseReleasedEventHandler);
            newNodeStage.getChildren().add(newImageNode.getNodePane());
        }
    }

    public void drawWorld(ActionEvent actionEvent) throws InterruptedException {
        if(firstTimePublic) {
            daroot = DataConnection.populate();
            DataConnection.getStudents();
            recursiveDisplay(daroot);
            firstTimePublic = false;
            populateList();
            nodeStage.getChildren().add(newDragView);
        }

    }

    public void showHome(ActionEvent actionEvent){
        if(!firstTimePublic) {
            recursiveShow(daroot);
            //newNodeStage2.setVisible(true);
        }
    }

    public void showReset(MouseEvent actionEvent){
        if(!firstTimePublic) {
            recursiveShow(daroot);
            //newNodeStage2.setVisible(true);
        }
    }

    EventHandler<MouseEvent> doNothing =
            new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent t) {
                }
            };

    private void recursiveDisplay(MapNode rootNode) {

        int children = rootNode.children.size();

        if (rootNode.uniqueId == 1 )
        {
            rootNode.setLayer(0);
            newNodeStage.getChildren().add(((TextNode)(rootNode)).getNodePane());
            ((TextNode)(rootNode)).getNodePane().setTranslateX(410);
            ((TextNode)(rootNode)).getNodePane().setTranslateY(225);
            nodeList.add(masterNode);
            ((TextNode) rootNode).getNodePane().setOnMousePressed(doNothing);
            ((TextNode) rootNode).getNodePane().setOnMouseDragged(doNothing);
            ((TextNode) rootNode).getNodePane().setOnMouseReleased(doNothing);
            classNode = (TextNode) rootNode;

        }

        else if (rootNode.getLayer() == 1) {

            if (rootNode.getParentNode().getNoOfChildren() == ClassMap.noOfCircle*10){
                expand();

            }

            if (ClassMap.circleX.size() % 2 == 0) {
                randomNumber = 0;

            } else randomNumber = ClassMap.circleX.size() / 2;

            double newTranslateX = ClassMap.circleX.get(randomNumber);
            double newTranslateY = ClassMap.circleY.get(randomNumber);
            ClassMap.circleX.remove(randomNumber);
            ClassMap.circleY.remove(randomNumber);

            if(rootNode.type.toString().equals("string")){
                newNodeStage.getChildren().add(((TextNode)(rootNode)).getNodePane());
                ((TextNode)(rootNode)).getNodePane().setTranslateX(newTranslateX+410);
                ((TextNode)(rootNode)).getNodePane().setTranslateY(newTranslateY+225);

                if(rootNode.getParentNode().getType().toString().equals("string")){
                    Line line = new Line();
                    line.setStroke(javafx.scene.paint.Color.BLACK);
                    TextNode textNode =(TextNode) rootNode.getParentNode();

                    line.startXProperty().bind(textNode.getNodePane().layoutXProperty().add((textNode.getNodePane().translateXProperty())));
                    line.startYProperty().bind(textNode.getNodePane().layoutYProperty().add(textNode.getNodePane().translateYProperty()));
                    line.endXProperty().bind(((TextNode)(rootNode)).getNodePane().layoutXProperty().add(((TextNode)(rootNode)).getNodePane().translateXProperty()));
                    line.endYProperty().bind(((TextNode)(rootNode)).getNodePane().layoutXProperty().add(((TextNode)(rootNode)).getNodePane().translateYProperty()));

                    line.setStrokeWidth(4);
                    rootNode.setParentLine(line);
                    newNodeStage2.getChildren().addAll(line);
                }
                if(rootNode.getParentNode().getType().toString().equals("image")){
                    Line line = new Line();
                    line.setStroke(javafx.scene.paint.Color.BLACK);
                    ImageNode imageNode =(ImageNode) rootNode.getParentNode();

                    line.startXProperty().bind(imageNode.getNodePane().layoutXProperty().add((imageNode.getNodePane().translateXProperty())));
                    line.startYProperty().bind(imageNode.getNodePane().layoutYProperty().add(imageNode.getNodePane().translateYProperty()));
                    line.endXProperty().bind(((TextNode)(rootNode)).getNodePane().layoutXProperty().add(((TextNode)(rootNode)).getNodePane().translateXProperty()));
                    line.endYProperty().bind(((TextNode)(rootNode)).getNodePane().layoutXProperty().add(((TextNode)(rootNode)).getNodePane().translateYProperty()));

                    line.setStrokeWidth(4);
                    rootNode.setParentLine(line);
                    newNodeStage2.getChildren().addAll(line);
                }
                if(rootNode.getParentNode().getType().toString().equals("link")){
                    Line line = new Line();
                    line.setStroke(javafx.scene.paint.Color.BLACK);
                    VideoNode videoNode =(VideoNode) rootNode.getParentNode();

                    line.startXProperty().bind(videoNode.getNodePane().layoutXProperty().add((videoNode.getNodePane().translateXProperty())));
                    line.startYProperty().bind(videoNode.getNodePane().layoutYProperty().add(videoNode.getNodePane().translateYProperty()));
                    line.endXProperty().bind(((TextNode)(rootNode)).getNodePane().layoutXProperty().add(((TextNode)(rootNode)).getNodePane().translateXProperty()));
                    line.endYProperty().bind(((TextNode)(rootNode)).getNodePane().layoutXProperty().add(((TextNode)(rootNode)).getNodePane().translateYProperty()));

                    line.setStrokeWidth(4);
                    rootNode.setParentLine(line);
                    newNodeStage2.getChildren().addAll(line);
                }
                ((TextNode) rootNode).getNodePane().setOnMousePressed(doNothing);
                ((TextNode) rootNode).getNodePane().setOnMouseDragged(doNothing);
                ((TextNode) rootNode).getNodePane().setOnMouseReleased(doNothing);
                rootNode.setA(newTranslateX+410);
                rootNode.setB(newTranslateY+225);

            }

            //System.out.println(rootNode.uniqueId);


            if(rootNode.type.toString().equals("image")){
                newNodeStage.getChildren().add(((ImageNode)(rootNode)).getNodePane());
                ((ImageNode)(rootNode)).getNodePane().setTranslateX(newTranslateX+410);
                ((ImageNode)(rootNode)).getNodePane().setTranslateY(newTranslateY+225);

                if(rootNode.getParentNode().getType().toString().equals("string")){
                    Line line = new Line();
                    line.setStroke(javafx.scene.paint.Color.BLACK);
                    TextNode textNode =(TextNode) rootNode.getParentNode();

                    line.startXProperty().bind(textNode.getNodePane().layoutXProperty().add((textNode.getNodePane().translateXProperty())));
                    line.startYProperty().bind(textNode.getNodePane().layoutYProperty().add(textNode.getNodePane().translateYProperty()));
                    line.endXProperty().bind(((ImageNode)(rootNode)).getNodePane().layoutXProperty().add(((ImageNode)(rootNode)).getNodePane().translateXProperty()));
                    line.endYProperty().bind(((ImageNode)(rootNode)).getNodePane().layoutXProperty().add(((ImageNode)(rootNode)).getNodePane().translateYProperty()));

                    line.setStrokeWidth(4);
                    rootNode.setParentLine(line);
                    newNodeStage2.getChildren().addAll(line);
                }
                if(rootNode.getParentNode().getType().toString().equals("image")){
                    Line line = new Line();
                    line.setStroke(javafx.scene.paint.Color.BLACK);
                    ImageNode imageNode =(ImageNode) rootNode.getParentNode();

                    line.startXProperty().bind(imageNode.getNodePane().layoutXProperty().add((imageNode.getNodePane().translateXProperty())));
                    line.startYProperty().bind(imageNode.getNodePane().layoutYProperty().add(imageNode.getNodePane().translateYProperty()));
                    line.endXProperty().bind(((ImageNode)(rootNode)).getNodePane().layoutXProperty().add(((ImageNode)(rootNode)).getNodePane().translateXProperty()));
                    line.endYProperty().bind(((ImageNode)(rootNode)).getNodePane().layoutXProperty().add(((ImageNode)(rootNode)).getNodePane().translateYProperty()));

                    line.setStrokeWidth(4);
                    rootNode.setParentLine(line);
                    newNodeStage2.getChildren().addAll(line);
                }
                if(rootNode.getParentNode().getType().toString().equals("link")){
                    Line line = new Line();
                    line.setStroke(javafx.scene.paint.Color.BLACK);
                    VideoNode videoNode =(VideoNode) rootNode.getParentNode();

                    line.startXProperty().bind(videoNode.getNodePane().layoutXProperty().add((videoNode.getNodePane().translateXProperty())));
                    line.startYProperty().bind(videoNode.getNodePane().layoutYProperty().add(videoNode.getNodePane().translateYProperty()));
                    line.endXProperty().bind(((ImageNode)(rootNode)).getNodePane().layoutXProperty().add(((ImageNode)(rootNode)).getNodePane().translateXProperty()));
                    line.endYProperty().bind(((ImageNode)(rootNode)).getNodePane().layoutXProperty().add(((ImageNode)(rootNode)).getNodePane().translateYProperty()));

                    line.setStrokeWidth(4);
                    rootNode.setParentLine(line);
                    newNodeStage2.getChildren().addAll(line);
                }

                ((ImageNode) rootNode).getNodePane().setOnMousePressed(doNothing);
                ((ImageNode) rootNode).getNodePane().setOnMouseDragged(doNothing);
                ((ImageNode) rootNode).getNodePane().setOnMouseReleased(doNothing);
                rootNode.setA(newTranslateX+410);
                rootNode.setB(newTranslateY+225);

            }

            if(rootNode.type.toString().equals("link")) {
                newNodeStage.getChildren().add(((VideoNode) (rootNode)).getNodePane());
                ((VideoNode)(rootNode)).getNodePane().setTranslateX(newTranslateX+410);
                ((VideoNode)(rootNode)).getNodePane().setTranslateY(newTranslateY+225);

                if(rootNode.getParentNode().getType().toString().equals("string")){
                    Line line = new Line();
                    line.setStroke(javafx.scene.paint.Color.BLACK);
                    TextNode textNode =(TextNode) rootNode.getParentNode();

                    line.startXProperty().bind(textNode.getNodePane().layoutXProperty().add((textNode.getNodePane().translateXProperty())));
                    line.startYProperty().bind(textNode.getNodePane().layoutYProperty().add(textNode.getNodePane().translateYProperty()));
                    line.endXProperty().bind(((VideoNode)(rootNode)).getNodePane().layoutXProperty().add(((VideoNode)(rootNode)).getNodePane().translateXProperty()));
                    line.endYProperty().bind(((VideoNode)(rootNode)).getNodePane().layoutXProperty().add(((VideoNode)(rootNode)).getNodePane().translateYProperty()));

                    line.setStrokeWidth(4);
                    rootNode.setParentLine(line);
                    newNodeStage2.getChildren().addAll(line);
                }
                if(rootNode.getParentNode().getType().toString().equals("image")){
                    Line line = new Line();
                    line.setStroke(javafx.scene.paint.Color.BLACK);
                    ImageNode imageNode =(ImageNode) rootNode.getParentNode();

                    line.startXProperty().bind(imageNode.getNodePane().layoutXProperty().add((imageNode.getNodePane().translateXProperty())));
                    line.startYProperty().bind(imageNode.getNodePane().layoutYProperty().add(imageNode.getNodePane().translateYProperty()));
                    line.endXProperty().bind(((VideoNode)(rootNode)).getNodePane().layoutXProperty().add(((VideoNode)(rootNode)).getNodePane().translateXProperty()));
                    line.endYProperty().bind(((VideoNode)(rootNode)).getNodePane().layoutXProperty().add(((VideoNode)(rootNode)).getNodePane().translateYProperty()));

                    line.setStrokeWidth(4);
                    rootNode.setParentLine(line);
                    newNodeStage2.getChildren().addAll(line);
                }
                if(rootNode.getParentNode().getType().toString().equals("link")){
                    Line line = new Line();
                    line.setStroke(javafx.scene.paint.Color.BLACK);
                    VideoNode videoNode =(VideoNode) rootNode.getParentNode();

                    line.startXProperty().bind(videoNode.getNodePane().layoutXProperty().add((videoNode.getNodePane().translateXProperty())));
                    line.startYProperty().bind(videoNode.getNodePane().layoutYProperty().add(videoNode.getNodePane().translateYProperty()));
                    line.endXProperty().bind(((VideoNode)(rootNode)).getNodePane().layoutXProperty().add(((VideoNode)(rootNode)).getNodePane().translateXProperty()));
                    line.endYProperty().bind(((VideoNode)(rootNode)).getNodePane().layoutXProperty().add(((VideoNode)(rootNode)).getNodePane().translateYProperty()));

                    line.setStrokeWidth(4);
                    rootNode.setParentLine(line);
                    newNodeStage2.getChildren().addAll(line);
                }


                rootNode.setA(newTranslateX+410);
                rootNode.setB(newTranslateY+225);
            }

            rootNode.setQuadrant(array[masterNode.size()%10] + 1);
            rootNode.setCircleNo(ClassMap.noOfCircle);
            rootNode.setExpansion(2*rootNode.getParentNode().getExpansionconst());
            rootNode.setChildLimit(2*rootNode.getParentNode().getExpansionconst());
            if (masterNode.size() >= 10){
                rootNode.setOffset(1);
            }
            masterNode.add(rootNode);
            rootNode.getParentNode().setNoOfChildren(rootNode.getParentNode().getNoOfChildren()+1);

        }

        else {
            if (rootNode.getParentNode().getNoOfChildren()== rootNode.getParentNode().getChildLimit()){
                expandChildren(rootNode.getParentNode());
            }
            List<Double> X = newCalculateX(rootNode.getParentNode().getCircleNo()*rootNode.getParentNode().getExpansion());
            List<Double> Y = newCalculateY(rootNode.getParentNode().getCircleNo()*rootNode.getParentNode().getExpansion());

            double newTranslateX = X.get(((rootNode.getQuadrant()-1)*rootNode.getParentNode().getCircleNo()*rootNode.getParentNode().getExpansion())+rootNode.getParentNode().getNoOfChildren()+(rootNode.getParentNode().getOffset()*rootNode.getParentNode().getExpansion())+(rootNode.getParentNode().getChildno()*rootNode.getParentNode().getExpansion()));
            double newTranslateY = Y.get(((rootNode.getQuadrant()-1)*rootNode.getParentNode().getCircleNo()*rootNode.getParentNode().getExpansion())+rootNode.getParentNode().getNoOfChildren()+(rootNode.getParentNode().getOffset()*rootNode.getParentNode().getExpansion())+(rootNode.getParentNode().getChildno()*rootNode.getParentNode().getExpansion()));

            if(rootNode.type.toString().equals("string")){
                newNodeStage.getChildren().add(((TextNode)(rootNode)).getNodePane());
                ((TextNode)(rootNode)).getNodePane().setTranslateX(newTranslateX+410);
                ((TextNode)(rootNode)).getNodePane().setTranslateY(newTranslateY+225);

                if(rootNode.getParentNode().getType().toString().equals("string")){
                    Line line = new Line();
                    line.setStroke(javafx.scene.paint.Color.BLACK);
                    TextNode textNode =(TextNode) rootNode.getParentNode();

                    line.startXProperty().bind(textNode.getNodePane().layoutXProperty().add((textNode.getNodePane().translateXProperty())));
                    line.startYProperty().bind(textNode.getNodePane().layoutYProperty().add(textNode.getNodePane().translateYProperty()));
                    line.endXProperty().bind(((TextNode)(rootNode)).getNodePane().layoutXProperty().add(((TextNode)(rootNode)).getNodePane().translateXProperty()));
                    line.endYProperty().bind(((TextNode)(rootNode)).getNodePane().layoutXProperty().add(((TextNode)(rootNode)).getNodePane().translateYProperty()));

                    line.setStrokeWidth(4);
                    rootNode.setParentLine(line);
                    newNodeStage2.getChildren().addAll(line);
                }
                if(rootNode.getParentNode().getType().toString().equals("image")){
                    Line line = new Line();
                    line.setStroke(javafx.scene.paint.Color.BLACK);
                    ImageNode imageNode =(ImageNode) rootNode.getParentNode();

                    line.startXProperty().bind(imageNode.getNodePane().layoutXProperty().add((imageNode.getNodePane().translateXProperty())));
                    line.startYProperty().bind(imageNode.getNodePane().layoutYProperty().add(imageNode.getNodePane().translateYProperty()));
                    line.endXProperty().bind(((TextNode)(rootNode)).getNodePane().layoutXProperty().add(((TextNode)(rootNode)).getNodePane().translateXProperty()));
                    line.endYProperty().bind(((TextNode)(rootNode)).getNodePane().layoutXProperty().add(((TextNode)(rootNode)).getNodePane().translateYProperty()));

                    line.setStrokeWidth(4);
                    rootNode.setParentLine(line);
                    newNodeStage2.getChildren().addAll(line);
                }
                if(rootNode.getParentNode().getType().toString().equals("link")){
                    Line line = new Line();
                    line.setStroke(javafx.scene.paint.Color.BLACK);
                    VideoNode videoNode =(VideoNode) rootNode.getParentNode();

                    line.startXProperty().bind(videoNode.getNodePane().layoutXProperty().add((videoNode.getNodePane().translateXProperty())));
                    line.startYProperty().bind(videoNode.getNodePane().layoutYProperty().add(videoNode.getNodePane().translateYProperty()));
                    line.endXProperty().bind(((TextNode)(rootNode)).getNodePane().layoutXProperty().add(((TextNode)(rootNode)).getNodePane().translateXProperty()));
                    line.endYProperty().bind(((TextNode)(rootNode)).getNodePane().layoutXProperty().add(((TextNode)(rootNode)).getNodePane().translateYProperty()));

                    line.setStrokeWidth(4);
                    rootNode.setParentLine(line);
                    newNodeStage2.getChildren().addAll(line);
                }
                ((TextNode) rootNode).getNodePane().setOnMousePressed(doNothing);
                ((TextNode) rootNode).getNodePane().setOnMouseDragged(doNothing);
                ((TextNode) rootNode).getNodePane().setOnMouseReleased(doNothing);
                rootNode.setA(newTranslateX+410);
                rootNode.setB(newTranslateY+225);
            }
            // System.out.println(rootNode.uniqueId);


            if(rootNode.type.toString().equals("image")){
                newNodeStage.getChildren().add(((ImageNode)(rootNode)).getNodePane());
                ((ImageNode)(rootNode)).getNodePane().setTranslateX(newTranslateX+410);
                ((ImageNode)(rootNode)).getNodePane().setTranslateY(newTranslateY+225);

                if(rootNode.getParentNode().getType().toString().equals("string")){
                    Line line = new Line();
                    line.setStroke(javafx.scene.paint.Color.BLACK);
                    TextNode textNode =(TextNode) rootNode.getParentNode();

                    line.startXProperty().bind(textNode.getNodePane().layoutXProperty().add((textNode.getNodePane().translateXProperty())));
                    line.startYProperty().bind(textNode.getNodePane().layoutYProperty().add(textNode.getNodePane().translateYProperty()));
                    line.endXProperty().bind(((ImageNode)(rootNode)).getNodePane().layoutXProperty().add(((ImageNode)(rootNode)).getNodePane().translateXProperty()));
                    line.endYProperty().bind(((ImageNode)(rootNode)).getNodePane().layoutXProperty().add(((ImageNode)(rootNode)).getNodePane().translateYProperty()));

                    line.setStrokeWidth(4);
                    rootNode.setParentLine(line);
                    newNodeStage2.getChildren().addAll(line);
                }
                if(rootNode.getParentNode().getType().toString().equals("image")){
                    Line line = new Line();
                    line.setStroke(javafx.scene.paint.Color.BLACK);
                    ImageNode imageNode =(ImageNode) rootNode.getParentNode();

                    line.startXProperty().bind(imageNode.getNodePane().layoutXProperty().add((imageNode.getNodePane().translateXProperty())));
                    line.startYProperty().bind(imageNode.getNodePane().layoutYProperty().add(imageNode.getNodePane().translateYProperty()));
                    line.endXProperty().bind(((ImageNode)(rootNode)).getNodePane().layoutXProperty().add(((ImageNode)(rootNode)).getNodePane().translateXProperty()));
                    line.endYProperty().bind(((ImageNode)(rootNode)).getNodePane().layoutXProperty().add(((ImageNode)(rootNode)).getNodePane().translateYProperty()));

                    line.setStrokeWidth(4);
                    rootNode.setParentLine(line);
                    newNodeStage2.getChildren().addAll(line);
                }
                if(rootNode.getParentNode().getType().toString().equals("link")){
                    Line line = new Line();
                    line.setStroke(javafx.scene.paint.Color.BLACK);
                    VideoNode videoNode =(VideoNode) rootNode.getParentNode();

                    line.startXProperty().bind(videoNode.getNodePane().layoutXProperty().add((videoNode.getNodePane().translateXProperty())));
                    line.startYProperty().bind(videoNode.getNodePane().layoutYProperty().add(videoNode.getNodePane().translateYProperty()));
                    line.endXProperty().bind(((ImageNode)(rootNode)).getNodePane().layoutXProperty().add(((ImageNode)(rootNode)).getNodePane().translateXProperty()));
                    line.endYProperty().bind(((ImageNode)(rootNode)).getNodePane().layoutXProperty().add(((ImageNode)(rootNode)).getNodePane().translateYProperty()));

                    line.setStrokeWidth(4);
                    rootNode.setParentLine(line);
                    newNodeStage2.getChildren().addAll(line);
                }

                rootNode.setA(newTranslateX+410);
                rootNode.setB(newTranslateY+225);
            }

            if(rootNode.type.toString().equals("link")) {
                newNodeStage.getChildren().add(((VideoNode) (rootNode)).getNodePane());
                ((VideoNode)(rootNode)).getNodePane().setTranslateX(newTranslateX+410);
                ((VideoNode)(rootNode)).getNodePane().setTranslateY(newTranslateY+225);


                if(rootNode.getParentNode().getType().toString().equals("string")){
                    Line line = new Line();
                    line.setStroke(javafx.scene.paint.Color.BLACK);
                    TextNode textNode =(TextNode) rootNode.getParentNode();

                    line.startXProperty().bind(textNode.getNodePane().layoutXProperty().add((textNode.getNodePane().translateXProperty())));
                    line.startYProperty().bind(textNode.getNodePane().layoutYProperty().add(textNode.getNodePane().translateYProperty()));
                    line.endXProperty().bind(((VideoNode)(rootNode)).getNodePane().layoutXProperty().add(((VideoNode)(rootNode)).getNodePane().translateXProperty()));
                    line.endYProperty().bind(((VideoNode)(rootNode)).getNodePane().layoutXProperty().add(((VideoNode)(rootNode)).getNodePane().translateYProperty()));

                    line.setStrokeWidth(4);
                    rootNode.setParentLine(line);
                    newNodeStage2.getChildren().addAll(line);
                }
                if(rootNode.getParentNode().getType().toString().equals("image")){
                    Line line = new Line();
                    line.setStroke(javafx.scene.paint.Color.BLACK);
                    ImageNode imageNode =(ImageNode) rootNode.getParentNode();

                    line.startXProperty().bind(imageNode.getNodePane().layoutXProperty().add((imageNode.getNodePane().translateXProperty())));
                    line.startYProperty().bind(imageNode.getNodePane().layoutYProperty().add(imageNode.getNodePane().translateYProperty()));
                    line.endXProperty().bind(((VideoNode)(rootNode)).getNodePane().layoutXProperty().add(((VideoNode)(rootNode)).getNodePane().translateXProperty()));
                    line.endYProperty().bind(((VideoNode)(rootNode)).getNodePane().layoutXProperty().add(((VideoNode)(rootNode)).getNodePane().translateYProperty()));

                    line.setStrokeWidth(4);
                    rootNode.setParentLine(line);
                    newNodeStage2.getChildren().addAll(line);
                }
                if(rootNode.getParentNode().getType().toString().equals("link")){
                    Line line = new Line();
                    line.setStroke(javafx.scene.paint.Color.BLACK);
                    VideoNode videoNode =(VideoNode) rootNode.getParentNode();

                    line.startXProperty().bind(videoNode.getNodePane().layoutXProperty().add((videoNode.getNodePane().translateXProperty())));
                    line.startYProperty().bind(videoNode.getNodePane().layoutYProperty().add(videoNode.getNodePane().translateYProperty()));
                    line.endXProperty().bind(((VideoNode)(rootNode)).getNodePane().layoutXProperty().add(((VideoNode)(rootNode)).getNodePane().translateXProperty()));
                    line.endYProperty().bind(((VideoNode)(rootNode)).getNodePane().layoutXProperty().add(((VideoNode)(rootNode)).getNodePane().translateYProperty()));

                    line.setStrokeWidth(4);
                    rootNode.setParentLine(line);
                    newNodeStage2.getChildren().addAll(line);
                }


                rootNode.setA(newTranslateX+410);
                rootNode.setB(newTranslateY+225);
            }
            if (nodeList.size() == rootNode.getLayer()-1) {
                List<MapNode> node = new ArrayList<>();
                node.add(rootNode);
                nodeList.add(node);
            } else {
                nodeList.get(rootNode.getLayer()-1).add(rootNode);
            }
            rootNode.setQuadrant(rootNode.getParentNode().getQuadrant());
            rootNode.setCircleNo(rootNode.getParentNode().getCircleNo()*rootNode.getParentNode().getExpansion());
            rootNode.setExpansion(2*rootNode.getParentNode().getExpansionconst());
            rootNode.setChildLimit(2*rootNode.getParentNode().getExpansionconst());
            rootNode.setChildno((rootNode.getParentNode().getNoOfChildren()+rootNode.getParentNode().getChildno()) * 2);
            if(rootNode.getParentNode().getOffset()!=0)
            {
                rootNode.setOffset((rootNode.getParentNode().getNoOfChildren()+rootNode.getParentNode().getOffset()) * 2);
            }
            rootNode.getParentNode().setNoOfChildren(rootNode.getParentNode().getNoOfChildren()+1);
        }
        // System.out.println("Layer "+ rootNode.getLayer());

        for (int i = 0; i < children; i++) {

            if (!rootNode.children.isEmpty()){
                rootNode.children.get(i).setLayer(rootNode.getLayer()+1);
                rootNode.children.get(i).setQuadrant(rootNode.getQuadrant());
                recursiveDisplay(rootNode.children.get(i));
            }
        }
    }

    public List newCalculateX(int a) {
        List<Double> newcircleX = new ArrayList<>();
        double add = 6.28319 / (a * 10);
        for (double i = 0; i < 6.2831; i = i + add) {
            double x = Math.cos(i) * ClassMap.radius * a;
            newcircleX.add(x);
        }
        return newcircleX;
    }

    public List newCalculateY(int a) {
        List<Double> newcircleY = new ArrayList<>();
        double add = 6.28319 / (a * 10);
        for (double i = 0; i < 6.2831; i = i + add) {
            double y = Math.sin(i) * ClassMap.radius * a;
            newcircleY.add(y);

        }
        return newcircleY;
    }

    public void expand(){
        ClassMap.noOfCircle++;

        ClassMap.calculate();
        int j = 0;
        for (int i = 0; i < nodeList.get(0).size(); i++) {

            if (i % 10 == 0 && i != 0) {
                j++;
            }
            if (nodeList.get(0).get(i).getType().equals("string")){
                ((TextNode)nodeList.get(0).get(i)).getNodePane().setTranslateX(ClassMap.circleX.get(((nodeList.get(0).get(i).getQuadrant() - 1) * ClassMap.noOfCircle) + j) + 410);
                ((TextNode)nodeList.get(0).get(i)).getNodePane().setTranslateY(ClassMap.circleY.get(((nodeList.get(0).get(i).getQuadrant() - 1) * ClassMap.noOfCircle) + j) + 225);
                nodeList.get(0).get(i).setCircleNo(ClassMap.noOfCircle);
                nodeList.get(0).get(i).setA(ClassMap.circleX.get(((nodeList.get(0).get(i).getQuadrant() - 1) * ClassMap.noOfCircle) + j) + 410);
                nodeList.get(0).get(i).setB(ClassMap.circleY.get(((nodeList.get(0).get(i).getQuadrant() - 1) * ClassMap.noOfCircle) + j) + 225);
            }
            else if (nodeList.get(0).get(i).getType().equals("image")){
                ((ImageNode)nodeList.get(0).get(i)).getNodePane().setTranslateX(ClassMap.circleX.get(((nodeList.get(0).get(i).getQuadrant() - 1) * ClassMap.noOfCircle) + j) + 410);
                ((ImageNode)nodeList.get(0).get(i)).getNodePane().setTranslateY(ClassMap.circleY.get(((nodeList.get(0).get(i).getQuadrant() - 1) * ClassMap.noOfCircle) + j) + 225);
                nodeList.get(0).get(i).setCircleNo(ClassMap.noOfCircle);
                nodeList.get(0).get(i).setA(ClassMap.circleX.get(((nodeList.get(0).get(i).getQuadrant() - 1) * ClassMap.noOfCircle) + j) + 410);
                nodeList.get(0).get(i).setB(ClassMap.circleY.get(((nodeList.get(0).get(i).getQuadrant() - 1) * ClassMap.noOfCircle) + j) + 225);

            }
            else if (nodeList.get(0).get(i).getType().equals("link")){
                ((VideoNode)nodeList.get(0).get(i)).getNodePane().setTranslateX(ClassMap.circleX.get(((nodeList.get(0).get(i).getQuadrant() - 1) * ClassMap.noOfCircle) + j) + 410);
                ((VideoNode)nodeList.get(0).get(i)).getNodePane().setTranslateY(ClassMap.circleY.get(((nodeList.get(0).get(i).getQuadrant() - 1) * ClassMap.noOfCircle) + j) + 225);
                nodeList.get(0).get(i).setCircleNo(ClassMap.noOfCircle);
                nodeList.get(0).get(i).setA(ClassMap.circleX.get(((nodeList.get(0).get(i).getQuadrant() - 1) * ClassMap.noOfCircle) + j) + 410);
                nodeList.get(0).get(i).setB(ClassMap.circleY.get(((nodeList.get(0).get(i).getQuadrant() - 1) * ClassMap.noOfCircle) + j) + 225);
            }
        }

        for (int i = 0; i < ClassMap.circleX.size(); i++) {
            for (int k = 0; k < ClassMap.noOfCircle - 1; k++) {
                ClassMap.circleX.remove(i);
                ClassMap.circleY.remove(i);
            }

        }
        expandChildren();
    }
    public  void expandChildren(){

        int add;
        for (int j =1 ; j< nodeList.size();j++) {

            for (int i = 0; i < nodeList.get(j).size(); i++) {

                List<Double> X = newCalculateX(nodeList.get(j).get(i).getParentNode().getCircleNo()*nodeList.get(j).get(i).getParentNode().getExpansion());
                List<Double> Y = newCalculateY(nodeList.get(j).get(i).getParentNode().getCircleNo()*nodeList.get(j).get(i).getParentNode().getExpansion());

                if ( nodeList.get(j).get(i).getChildno() > 0) {
                    add = nodeList.get(j).get(i).getChildno() / 2;
                } else add = 0;

                double newTranslateX = X.get(((nodeList.get(j).get(i).getQuadrant() - 1) * nodeList.get(j).get(i).getParentNode().getCircleNo() * nodeList.get(j).get(i).getParentNode().getExpansion()) + add+(nodeList.get(j).get(i).getParentNode().getOffset()*nodeList.get(j).get(i).getParentNode().getExpansion()));
                double newTranslateY = Y.get(((nodeList.get(j).get(i).getQuadrant() - 1) * nodeList.get(j).get(i).getParentNode().getCircleNo() * nodeList.get(j).get(i).getParentNode().getExpansion()) + add+(nodeList.get(j).get(i).getParentNode().getOffset()*nodeList.get(j).get(i).getParentNode().getExpansion()));

                if (nodeList.get(j).get(i).type.toString().equals("string")) {
                    ((TextNode) (nodeList.get(j).get(i))).getNodePane().setTranslateX(newTranslateX + 410);
                    ((TextNode) (nodeList.get(j).get(i))).getNodePane().setTranslateY(newTranslateY + 225);
                    nodeList.get(j).get(i).setCircleNo(nodeList.get(j).get(i).getParentNode().getCircleNo()*nodeList.get(j).get(i).getParentNode().getExpansion());
                    nodeList.get(j).get(i).setA(newTranslateX + 410);
                    nodeList.get(j).get(i).setB(newTranslateY + 225);

                }
                //System.out.println(nodeList.get(j).get(i).uniqueId);


                if (nodeList.get(j).get(i).type.toString().equals("image")) {
                    ((ImageNode) (nodeList.get(j).get(i))).getNodePane().setTranslateX(newTranslateX + 410);
                    ((ImageNode) (nodeList.get(j).get(i))).getNodePane().setTranslateY(newTranslateY + 225);
                    nodeList.get(j).get(i).setCircleNo(nodeList.get(j).get(i).getParentNode().getCircleNo()*nodeList.get(j).get(i).getParentNode().getExpansion());
                    nodeList.get(j).get(i).setA(newTranslateX + 410);
                    nodeList.get(j).get(i).setB(newTranslateY + 225);
                }

                if (nodeList.get(j).get(i).type.toString().equals("link")) {
                    ((VideoNode) (nodeList.get(j).get(i))).getNodePane().setTranslateX(newTranslateX + 410);
                    ((VideoNode) (nodeList.get(j).get(i))).getNodePane().setTranslateY(newTranslateY + 225);
                    nodeList.get(j).get(i).setCircleNo(nodeList.get(j).get(i).getParentNode().getCircleNo()*nodeList.get(j).get(i).getParentNode().getExpansion());
                    nodeList.get(j).get(i).setA(newTranslateX + 410);
                    nodeList.get(j).get(i).setB(newTranslateY + 225);
                }
            }
        }
    }
    public void expandChildren(MapNode node){
        int add;
        if (node.getLayer()==1){
            node.getParentNode().setExpansionconst(node.getParentNode().getExpansionconst()*2);
            for (int i =0;i<nodeList.get(node.getLayer()-1).size();i++){
                nodeList.get(node.getLayer()-1).get(i).setExpansion(2*node.getParentNode().getExpansionconst());
                nodeList.get(node.getLayer()-1).get(i).setChildLimit(2*node.getParentNode().getExpansionconst());

            }
        }
        else{
            for (int i =0;i<nodeList.get(node.getLayer()-2).size();i++ ){
                nodeList.get(node.getLayer()-2).get(i).setExpansionconst(nodeList.get(node.getLayer()-2).get(i).getExpansionconst()*2);
            }

            for (int i =0;i<nodeList.get(node.getLayer()-1).size();i++){
                nodeList.get(node.getLayer()-1).get(i).setExpansion(2*node.getParentNode().getExpansionconst());
                nodeList.get(node.getLayer()-1).get(i).setChildLimit(2*node.getParentNode().getExpansionconst());

            }

        }

        for (int j =node.getLayer() ; j< nodeList.size();j++) {

            for (int i = 0; i < nodeList.get(j).size(); i++) {

                List<Double> X = newCalculateX(nodeList.get(j).get(i).getParentNode().getCircleNo()*nodeList.get(j).get(i).getParentNode().getExpansion());
                List<Double> Y = newCalculateY(nodeList.get(j).get(i).getParentNode().getCircleNo()*nodeList.get(j).get(i).getParentNode().getExpansion());

                if ( nodeList.get(j).get(i).getChildno() > 0) {
                    add = nodeList.get(j).get(i).getChildno() / 2;
                } else add = 0;

                double newTranslateX = X.get(((nodeList.get(j).get(i).getQuadrant() - 1) * nodeList.get(j).get(i).getParentNode().getCircleNo() * nodeList.get(j).get(i).getParentNode().getExpansion()) + add+(nodeList.get(j).get(i).getParentNode().getOffset()*nodeList.get(j).get(i).getParentNode().getExpansion()));
                double newTranslateY = Y.get(((nodeList.get(j).get(i).getQuadrant() - 1) * nodeList.get(j).get(i).getParentNode().getCircleNo() * nodeList.get(j).get(i).getParentNode().getExpansion()) + add+(nodeList.get(j).get(i).getParentNode().getOffset()*nodeList.get(j).get(i).getParentNode().getExpansion()));

                if (nodeList.get(j).get(i).type.toString().equals("string")) {
                    ((TextNode) (nodeList.get(j).get(i))).getNodePane().setTranslateX(newTranslateX + 410);
                    ((TextNode) (nodeList.get(j).get(i))).getNodePane().setTranslateY(newTranslateY + 225);
                    nodeList.get(j).get(i).setCircleNo(nodeList.get(j).get(i).getParentNode().getCircleNo()*nodeList.get(j).get(i).getParentNode().getExpansion());
                    nodeList.get(j).get(i).setA(newTranslateX + 410);
                    nodeList.get(j).get(i).setB(newTranslateY + 225);
                }
                //System.out.println(nodeList.get(j).get(i).uniqueId);


                if (nodeList.get(j).get(i).type.toString().equals("image")) {
                    ((ImageNode) (nodeList.get(j).get(i))).getNodePane().setTranslateX(newTranslateX + 410);
                    ((ImageNode) (nodeList.get(j).get(i))).getNodePane().setTranslateY(newTranslateY + 225);
                    nodeList.get(j).get(i).setCircleNo(nodeList.get(j).get(i).getParentNode().getCircleNo()*nodeList.get(j).get(i).getParentNode().getExpansion());
                    nodeList.get(j).get(i).setA(newTranslateX + 410);
                    nodeList.get(j).get(i).setB(newTranslateY + 225);
                }

                if (nodeList.get(j).get(i).type.toString().equals("link")) {
                    ((VideoNode) (nodeList.get(j).get(i))).getNodePane().setTranslateX(newTranslateX + 410);
                    ((VideoNode) (nodeList.get(j).get(i))).getNodePane().setTranslateY(newTranslateY + 225);
                    nodeList.get(j).get(i).setCircleNo(nodeList.get(j).get(i).getParentNode().getCircleNo()*nodeList.get(j).get(i).getParentNode().getExpansion());
                    nodeList.get(j).get(i).setA(newTranslateX + 410);
                    nodeList.get(j).get(i).setB(newTranslateY + 225);
                    //System.out.println(((VideoNode) (nodeList.get(j).get(i))).getContents());
                }
            }
        }
    }

    public void populateList() {

        if(DataConnection.getTopic == Boolean.FALSE)
        {
            DataConnection.setTopic();
            for(int x = 0; x < DataConnection.topicNameList.size(); x++)
            {
                WebView web = new WebView();
                WebEngine engine = web.getEngine();
                engine.loadContent(DataConnection.htmlList.get(x).toString());
                TitledPane temp = new TitledPane(DataConnection.topicNameList.get(x).toString(),web);
                topicMenuAccordion.getPanes().addAll(temp);
            }
        }
    }

    public void hideNodes(ActionEvent actionEvent) {
        if(!firstTimePublic) {
            showHome(actionEvent);
            recursiveHide(daroot);
        }
    }

    public void showUserNodes(MouseEvent actionEvent, String user) {
        if(!firstTimePublic) {
            recursiveShowUser(daroot, user);
        }
    }

    public boolean recursiveShowUser(MapNode rootNode, String user) {

        rootNode.previousVote = false;
        int children = rootNode.children.size();

        if (children == 0) {
            rootNode.previousVote = (rootNode.getCreatedBy().equals(user));
            if (rootNode.previousVote == false) {
                rootNode.getParentLine().setVisible(false);
                if (rootNode.type.toString().equals("string"))
                    ((TextNode) (rootNode)).setVisible();

                if (rootNode.type.toString().equals("image"))
                    ((ImageNode) (rootNode)).setVisible();

                if (rootNode.type.toString().equals("link"))
                    ((VideoNode) (rootNode)).setVisible();
            }
        }
        else {
            for (int i = 0; i < children; i++) {
                if(rootNode.previousVote == false)
                    rootNode.previousVote = recursiveShowUser(rootNode.children.get(i), user);
                else
                    recursiveShowUser(rootNode.children.get(i), user);
            }
        }

        if (rootNode.getCreatedBy().equals(user)) {
            rootNode.previousVote = true;
        }
        else {
            if(rootNode.uniqueId == 1){

            }
            else {
                if (rootNode.getCreatedBy().equals(user) == false && rootNode.previousVote == false) {
                    rootNode.getParentLine().setVisible(false);
                    if (rootNode.type.toString().equals("string"))
                        ((TextNode) (rootNode)).setVisible();

                    if (rootNode.type.toString().equals("image"))
                        ((ImageNode) (rootNode)).setVisible();

                    if (rootNode.type.toString().equals("link"))
                        ((VideoNode) (rootNode)).setVisible();
                }
            }
        }

        return rootNode.previousVote;
    }


    public boolean recursiveHide(MapNode rootNode) {

        rootNode.previousVote = false;
        int children = rootNode.children.size();

        if (children == 0) {
            rootNode.previousVote = rootNode.getUserVote();
            if (rootNode.getUserVote() == false) {
                rootNode.getParentLine().setVisible(false);
                if (rootNode.type.toString().equals("string"))
                    ((TextNode) (rootNode)).setVisible();

                if (rootNode.type.toString().equals("image"))
                    ((ImageNode) (rootNode)).setVisible();

                if (rootNode.type.toString().equals("link"))
                    ((VideoNode) (rootNode)).setVisible();
            }
        }
        else {
            for (int i = 0; i < children; i++) {
                if(rootNode.previousVote == false)
                    rootNode.previousVote = recursiveHide(rootNode.children.get(i));
                else
                    recursiveHide(rootNode.children.get(i));
            }
        }

        if (rootNode.getUserVote() == true) {
            rootNode.previousVote = true;
        }
        else {
            if(rootNode.uniqueId == 1){

            }
            else {
                if (rootNode.getUserVote() == false && rootNode.previousVote == false) {
                    rootNode.getParentLine().setVisible(false);
                    if (rootNode.type.toString().equals("string"))
                        ((TextNode) (rootNode)).setVisible();

                    if (rootNode.type.toString().equals("image"))
                        ((ImageNode) (rootNode)).setVisible();

                    if (rootNode.type.toString().equals("link"))
                        ((VideoNode) (rootNode)).setVisible();
                }
            }
        }

        return rootNode.previousVote;
    }

    public void recursiveShow(MapNode rootNode) {

        int children = rootNode.children.size();

        if(rootNode.type.toString().equals("string")) {
            ((TextNode) (rootNode)).makeVisible();
        }

        if(rootNode.type.toString().equals("image")) {
            ((ImageNode) (rootNode)).makeVisible();
        }

        if(rootNode.type.toString().equals("link")) {
            ((VideoNode) (rootNode)).makeVisible();
        }

        for (int i = 0; i < children; i++) {
            if (!rootNode.children.isEmpty())
                recursiveShow(rootNode.children.get(i));
        }
        if(rootNode.getParentLine() != null)
            (rootNode).getParentLine().setVisible(true);

    }

    public void nodeDragMousePressed(MouseEvent m)
    {
        if (!nodedrag){
            prevPaneCordX= (int) newNodeStage.getLayoutX();
            prevPaneCordY= (int) newNodeStage.getLayoutY();
            prevMouseCordX= (int) m.getX();
            prevMouseCordY= (int) m.getY();}
    }

    // set this method on Mouse Drag event for newNodeStage
    public void nodeDragMouseDragged(MouseEvent m)
    {
        if (!nodedrag)
        {
            diffX= (int) (m.getX()- prevMouseCordX);
            diffY= (int) (m.getY()-prevMouseCordY );
            int x = (int) (diffX+newNodeStage.getLayoutX()-root.getLayoutX());
            int y = (int) (diffY+newNodeStage.getLayoutY()-root.getLayoutY());
            if(m.getSceneX() > 0 && m.getSceneY() > 0) {
                if(factor<0.45) {

                }
                else {
                    newNodeStage.setLayoutX(x);
                    newNodeStage.setLayoutY(y);
                    newNodeStage2.setLayoutX(x);
                    newNodeStage2.setLayoutY(y);
                }
            }
        }
    }

    public void createVideoNode(ActionEvent actionEvent) {
        if(!firstTimePublic) {

            TextInputDialog dialog = new TextInputDialog("Enter YouTube URL");
            dialog.setTitle("Create Video Node");
            dialog.setHeaderText("Enter the URL below.");
            dialog.setContentText("URL: ");
            Optional<String> result = dialog.showAndWait();

            videoNode = new VideoNode(result.get());
            videoNode.getNodePane().setOnMousePressed(OnMousePressedEventHandler);
            videoNode.getNodePane().setOnMouseDragged(OnMouseDraggedEventHandler);
            videoNode.getNodePane().setOnMouseReleased(VideoOnMouseReleasedEventHandler);
            newNodeStage.getChildren().add(videoNode.getNodePane());
        }
    }


    public boolean intersection(GridPane pane) {
        boolean intersect = false;

        for (int j = 0; j < nodeList.size(); j++) {


            for (int i = 0; i < nodeList.get(j).size(); i++) {

                if (nodeList.get(j).get(i).getType() == "string") {
                    TextNode textNode = (TextNode) nodeList.get(j).get(i);
                    Bounds bounds = pane.getBoundsInParent();
                    double a = textNode.getA();
                    double b = textNode.getB();
                    if (textNode.getNodePane().intersects(bounds.getMinX() - a, bounds.getMinY() - b, 100, 100)) {
                        intersect = true;
                        index = i;
                        layer = j;
                        break;
                    }
                } else if (nodeList.get(j).get(i).getType() == "image") {
                    ImageNode imageNode = (ImageNode) nodeList.get(j).get(i);
                    Bounds bounds = pane.getBoundsInParent();
                    double a = imageNode.getA();
                    double b = imageNode.getB();
                    if (imageNode.getNodePane().intersects(bounds.getMinX() - a, bounds.getMinY() - b, 100, 100)) {
                        intersect = true;
                        index = i;
                        layer = j;
                        break;
                    }
                }
                else if (nodeList.get(j).get(i).getType() == "link") {
                    VideoNode videoNode = (VideoNode) nodeList.get(j).get(i);
                    Bounds bounds = pane.getBoundsInParent();
                    double a = videoNode.getA();
                    double b = videoNode.getB();
                    if (videoNode.getNodePane().intersects(bounds.getMinX() - a, bounds.getMinY() - b, 100, 100)) {
                        intersect = true;
                        index = i;
                        layer = j;
                        break;
                    }
                }
            }

        }
        return intersect;

    }


    EventHandler<MouseEvent> OnMouseReleasedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {

            if (nodeList.get(layer).get(index).getType().equals("string") && nodeList.get(layer).get(index) != null ){
                TextNode textNode = (TextNode) nodeList.get(layer).get(index);
                textNode.getNodePane().setStyle(null);
            }
            else if (nodeList.get(layer).get(index).getType().equals("image") && nodeList.get(layer).get(index) != null)
            {
                ImageNode imageNode = (ImageNode) nodeList.get(layer).get(index);

                imageNode.getNodePane().setStyle(null);
            }
            else  if (nodeList.get(layer).get(index).getType().equals("link") && nodeList.get(layer).get(index) != null)
            {
                VideoNode videoNode = (VideoNode) nodeList.get(layer).get(index);

                videoNode.getNodePane().setStyle(null);
            }

            nodedrag = false;
            Bounds bounds = newNode.getNodePane().getBoundsInParent();
            if (classNode.getNodePane().intersects(bounds.getMinX() - 410, bounds.getMinY() - 225, 100, 100) && masterNode.size() == (ClassMap.noOfCircle * 10)) {
                expand();
            }
            if (classNode.getNodePane().intersects(bounds.getMinX() - 410, bounds.getMinY() - 225, 100, 100)) {

                if (ClassMap.circleX.size() % 2 == 0) {
                    randomNumber = 0;

                } else randomNumber = ClassMap.circleX.size() / 2;

                double newTranslateX = ClassMap.circleX.get(randomNumber);
                double newTranslateY = ClassMap.circleY.get(randomNumber);
                ClassMap.circleX.remove(randomNumber);
                ClassMap.circleY.remove(randomNumber);

                newNode.getNodePane().setTranslateX(newTranslateX + 410);
                newNode.getNodePane().setTranslateY(newTranslateY + 225);

                Line line = new Line();
                line.setStroke(javafx.scene.paint.Color.BLACK);

                line.startXProperty().bind(classNode.getNodePane().layoutXProperty().add(classNode.getNodePane().translateXProperty()));
                line.startYProperty().bind(classNode.getNodePane().layoutYProperty().add(classNode.getNodePane().translateYProperty()));
                line.endXProperty().bind(newNode.getNodePane().layoutXProperty().add(newNode.getNodePane().translateXProperty()));
                line.endYProperty().bind(newNode.getNodePane().layoutXProperty().add(newNode.getNodePane().translateYProperty()));

                line.setStrokeWidth(4);
                newNode.setParentLine(line);
                newNodeStage2.getChildren().addAll(line);
                newNode.setA(newTranslateX + 410);
                newNode.setB(newTranslateY + 225);
                newNode.setQuadrant(array[masterNode.size()%10] + 1);
                newNode.setCircleNo(ClassMap.noOfCircle);
                newNode.setParentNode(classNode);
                newNode.setParent(newNode.getParentNode().getUniqueId());
                newNode.setChildLimit(2*classNode.getExpansionconst());
                newNode.setLayer(0);
                newNode.setExpansion(2*classNode.getExpansionconst());
                if (masterNode.size() >= 10){
                    newNode.setOffset(1);
                }

                if (nodeList.size() == 0) {

                    masterNode.add(newNode);
                    nodeList.add(masterNode);
                } else {
                    nodeList.get(0).add(newNode);
                }

                classNode.setNoOfChildren(classNode.getNoOfChildren()+1);


                newNode.getNodePane().setOnMousePressed(doNothing);
                newNode.getNodePane().setOnMouseDragged(doNothing);
                newNode.getNodePane().setOnMouseReleased(doNothing);
                DataConnection.addTextNode(newNode);
            } else if (intersection(newNode.getNodePane())) {

                if (nodeList.get(layer).get(index).getNoOfChildren() == nodeList.get(layer).get(index).getChildLimit()){
                    expandChildren(nodeList.get(layer).get(index));

                }

                List<Double> X = newCalculateX(nodeList.get(layer).get(index).getCircleNo() * nodeList.get(layer).get(index).getExpansion());
                List<Double> Y = newCalculateY(nodeList.get(layer).get(index).getCircleNo() * nodeList.get(layer).get(index).getExpansion());

                double newTranslateX = X.get(((nodeList.get(layer).get(index).getQuadrant() - 1) * (nodeList.get(layer).get(index).getCircleNo())*nodeList.get(layer).get(index).getExpansion()) + nodeList.get(layer).get(index).getNoOfChildren() +nodeList.get(layer).get(index).getChildno() +(nodeList.get(layer).get(index).getOffset()*nodeList.get(layer).get(index).getExpansion()));
                double newTranslateY = Y.get(((nodeList.get(layer).get(index).getQuadrant() - 1) * (nodeList.get(layer).get(index).getCircleNo())*nodeList.get(layer).get(index).getExpansion()) + nodeList.get(layer).get(index).getNoOfChildren() +nodeList.get(layer).get(index).getChildno() +(nodeList.get(layer).get(index).getOffset()*nodeList.get(layer).get(index).getExpansion()));
                newNode.getNodePane().setTranslateX(newTranslateX + 410);
                newNode.getNodePane().setTranslateY(newTranslateY + 225);

                Line line = new Line();
                line.setStroke(javafx.scene.paint.Color.BLACK);

                MapNode parentNode = nodeList.get(layer).get(index);
                parentNode.children.add(newNode);

                if (parentNode.getType() == "string") {
                    TextNode textNode = (TextNode) parentNode;
                    line.startXProperty().bind(textNode.getNodePane().layoutXProperty().add(textNode.getNodePane().translateXProperty()));
                    line.startYProperty().bind(textNode.getNodePane().layoutYProperty().add(textNode.getNodePane().translateYProperty()));
                } else if (parentNode.getType() == "image") {

                    ImageNode imageNode = (ImageNode) parentNode;
                    line.startXProperty().bind(imageNode.getNodePane().layoutXProperty().add(imageNode.getNodePane().translateXProperty()));
                    line.startYProperty().bind(imageNode.getNodePane().layoutYProperty().add(imageNode.getNodePane().translateYProperty()));
                }
                else if (parentNode.getType() == "link") {

                    VideoNode videoNode = (VideoNode) parentNode;
                    line.startXProperty().bind(videoNode.getNodePane().layoutXProperty().add(videoNode.getNodePane().translateXProperty()));
                    line.startYProperty().bind(videoNode.getNodePane().layoutYProperty().add(videoNode.getNodePane().translateYProperty()));
                }

                line.setStrokeWidth(4);

                line.endXProperty().bind(newNode.getNodePane().layoutXProperty().add(newNode.getNodePane().translateXProperty()));
                line.endYProperty().bind(newNode.getNodePane().layoutXProperty().add(newNode.getNodePane().translateYProperty()));
                newNode.setParentLine(line);
                newNodeStage2.getChildren().addAll(line);


                newNode.setChildno((nodeList.get(layer).get(index).getNoOfChildren()+nodeList.get(layer).get(index).getChildno()) * 2);
                newNode.setA(newTranslateX + 435);
                newNode.setB(newTranslateY + 260);
                newNode.setQuadrant(nodeList.get(layer).get(index).getQuadrant());
                newNode.setCircleNo(nodeList.get(layer).get(index).getCircleNo() *nodeList.get(layer).get(index).getExpansion());
                newNode.setLayer(nodeList.get(layer).get(index).getLayer() + 1);
                newNode.setExpansion(2*nodeList.get(layer).get(index).getExpansionconst());
                newNode.setChildLimit(2*nodeList.get(layer).get(index).getExpansionconst());
                newNode.setParentNode(nodeList.get(layer).get(index));
                newNode.setParent(newNode.getParentNode().getUniqueId());

                if(nodeList.get(layer).get(index).getOffset()!=0)
                {
                    newNode.setOffset((nodeList.get(layer).get(index).getNoOfChildren()+nodeList.get(layer).get(index).getOffset()) * 2);
                }

                nodeList.get(layer).get(index).setNoOfChildren(nodeList.get(layer).get(index).getNoOfChildren() + 1);

                if (nodeList.size() == layer + 1) {
                    List<MapNode> node = new ArrayList<>();
                    node.add(newNode);
                    nodeList.add(node);
                } else {
                    nodeList.get(layer + 1).add(newNode);
                }

                newNode.getNodePane().setOnMousePressed(doNothing);
                newNode.getNodePane().setOnMouseDragged(doNothing);
                newNode.getNodePane().setOnMouseReleased(doNothing);

                DataConnection.addTextNode(newNode);
            }
        }

    };

    EventHandler<MouseEvent> OnMousePressedEventHandler =
            new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent t) {
                    nodedrag = true;
                    orgSceneX = t.getSceneX();
                    orgSceneY = t.getSceneY();
                    orgTranslateX = ((GridPane) (t.getSource())).getTranslateX();
                    orgTranslateY = ((GridPane) (t.getSource())).getTranslateY();
                }
            };

    EventHandler<MouseEvent> OnMouseDraggedEventHandler =
            new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent t) {

                    if (nodeList.get(layer).get(index).getType().equals("string") && nodeList.get(layer).get(index) != null){
                        TextNode textNode = (TextNode) nodeList.get(layer).get(index);
                        textNode.getNodePane().setStyle(null);
                    }
                    else if (nodeList.get(layer).get(index).getType().equals("image") && nodeList.get(layer).get(index) != null)
                    {
                        ImageNode imageNode = (ImageNode) nodeList.get(layer).get(index);

                        imageNode.getNodePane().setStyle(null);
                    }
                    else  if (nodeList.get(layer).get(index).getType().equals("link") && nodeList.get(layer).get(index) != null)
                    {
                        VideoNode videoNode = (VideoNode) nodeList.get(layer).get(index);

                        videoNode.getNodePane().setStyle(null);
                    }

                    nodedrag = true;
                    double offsetX = t.getSceneX() - orgSceneX;
                    double offsetY = t.getSceneY() - orgSceneY;
                    double newTranslateX = orgTranslateX + offsetX;
                    double newTranslateY = orgTranslateY + offsetY;
                    ((GridPane) (t.getSource())).setTranslateX(newTranslateX * (1/factor));
                    ((GridPane) (t.getSource())).setTranslateY(newTranslateY * (1/factor));
                    if (intersection((GridPane)t.getSource())){

                        if (nodeList.get(layer).get(index).getType().equals("string") && nodeList.get(layer).get(index) != null){
                            TextNode textNode = (TextNode) nodeList.get(layer).get(index);
                            textNode.getNodePane().setStyle("-fx-background-color: #4D4DFF;");
                        }
                        else if (nodeList.get(layer).get(index).getType().equals("image") && nodeList.get(layer).get(index) != null)
                        {
                            ImageNode imageNode = (ImageNode) nodeList.get(layer).get(index);

                            imageNode.getNodePane().setStyle("-fx-background-color: #4D4DFF;");
                        }
                        else  if (nodeList.get(layer).get(index).getType().equals("link") && nodeList.get(layer).get(index) != null)
                        {
                            VideoNode videoNode = (VideoNode) nodeList.get(layer).get(index);

                            videoNode.getNodePane().setStyle("-fx-background-color: #4D4DFF;");
                        }
                    }

                }
            };

    EventHandler<MouseEvent> ImageOnMouseReleasedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {

            if (nodeList.get(layer).get(index).getType().equals("string") && nodeList.get(layer).get(index) != null){
                TextNode textNode = (TextNode) nodeList.get(layer).get(index);
                textNode.getNodePane().setStyle(null);
            }
            else if (nodeList.get(layer).get(index).getType().equals("image") && nodeList.get(layer).get(index) != null)
            {
                ImageNode imageNode = (ImageNode) nodeList.get(layer).get(index);

                imageNode.getNodePane().setStyle(null);
            }
            else  if (nodeList.get(layer).get(index).getType().equals("link") && nodeList.get(layer).get(index) != null)
            {
                VideoNode videoNode = (VideoNode) nodeList.get(layer).get(index);

                videoNode.getNodePane().setStyle(null);
            }
            nodedrag = false;
            Bounds bounds = newImageNode.getNodePane().getBoundsInParent();
            if (classNode.getNodePane().intersects(bounds.getMinX() - 410, bounds.getMinY() - 225, 100, 100) && masterNode.size() == (ClassMap.noOfCircle * 10)) {
                expand();
            }
            if (classNode.getNodePane().intersects(bounds.getMinX() - 410, bounds.getMinY() - 225, 100, 100)) {

                if (ClassMap.circleX.size() % 2 == 0) {
                    randomNumber = 0;

                } else randomNumber = ClassMap.circleX.size() / 2;

                double newTranslateX = ClassMap.circleX.get(randomNumber);
                double newTranslateY = ClassMap.circleY.get(randomNumber);
                ClassMap.circleX.remove(randomNumber);
                ClassMap.circleY.remove(randomNumber);

                newImageNode.getNodePane().setTranslateX(newTranslateX + 410);
                newImageNode.getNodePane().setTranslateY(newTranslateY + 225);

                Line line = new Line();
                line.setStroke(javafx.scene.paint.Color.BLACK);

                line.startXProperty().bind(classNode.getNodePane().layoutXProperty().add(classNode.getNodePane().translateXProperty()));
                line.startYProperty().bind(classNode.getNodePane().layoutYProperty().add(classNode.getNodePane().translateYProperty()));
                line.endXProperty().bind(newImageNode.getNodePane().layoutXProperty().add(newImageNode.getNodePane().translateXProperty()));
                line.endYProperty().bind(newImageNode.getNodePane().layoutXProperty().add(newImageNode.getNodePane().translateYProperty()));

                line.setStrokeWidth(4);
                newImageNode.setParentLine(line);
                newNodeStage2.getChildren().addAll(line);
                newImageNode.setA(newTranslateX + 410);
                newImageNode.setB(newTranslateY + 225);
                newImageNode.setQuadrant(array[masterNode.size()%10] + 1);
                newImageNode.setCircleNo(ClassMap.noOfCircle);
                newImageNode.setParentNode(classNode);
                newImageNode.setParent(newImageNode.getParentNode().getUniqueId());
                newImageNode.setChildLimit(2*classNode.getExpansionconst());
                newImageNode.setLayer(0);
                newImageNode.setExpansion(2*classNode.getExpansionconst());
                if (masterNode.size() >= 10){
                    newImageNode.setOffset(1);
                }

                if (nodeList.size() == 0) {

                    masterNode.add(newImageNode);
                    nodeList.add(masterNode);
                } else {
                    nodeList.get(0).add(newImageNode);
                }

                classNode.setNoOfChildren(classNode.getNoOfChildren()+1);


                newImageNode.getNodePane().setOnMousePressed(doNothing);
                newImageNode.getNodePane().setOnMouseDragged(doNothing);
                newImageNode.getNodePane().setOnMouseReleased(doNothing);
                DataConnection.addImageNode(newImageNode);
            } else if (intersection(newImageNode.getNodePane())) {

                if (nodeList.get(layer).get(index).getNoOfChildren() == nodeList.get(layer).get(index).getChildLimit()){
                    expandChildren(nodeList.get(layer).get(index));

                }

                List<Double> X = newCalculateX(nodeList.get(layer).get(index).getCircleNo() * nodeList.get(layer).get(index).getExpansion());
                List<Double> Y = newCalculateY(nodeList.get(layer).get(index).getCircleNo() * nodeList.get(layer).get(index).getExpansion());

                double newTranslateX = X.get(((nodeList.get(layer).get(index).getQuadrant() - 1) * (nodeList.get(layer).get(index).getCircleNo())*nodeList.get(layer).get(index).getExpansion()) + nodeList.get(layer).get(index).getNoOfChildren()+nodeList.get(layer).get(index).getChildno() +(nodeList.get(layer).get(index).getOffset()*nodeList.get(layer).get(index).getExpansion()));
                double newTranslateY = Y.get(((nodeList.get(layer).get(index).getQuadrant() - 1) * (nodeList.get(layer).get(index).getCircleNo())*nodeList.get(layer).get(index).getExpansion()) + nodeList.get(layer).get(index).getNoOfChildren() +nodeList.get(layer).get(index).getChildno()+(nodeList.get(layer).get(index).getOffset()*nodeList.get(layer).get(index).getExpansion()));
                newImageNode.getNodePane().setTranslateX(newTranslateX + 410);
                newImageNode.getNodePane().setTranslateY(newTranslateY + 225);

                Line line = new Line();
                line.setStroke(javafx.scene.paint.Color.BLACK);

                MapNode parentNode = nodeList.get(layer).get(index);
                parentNode.children.add(newImageNode);

                if (parentNode.getType() == "string") {
                    TextNode textNode = (TextNode) parentNode;
                    line.startXProperty().bind(textNode.getNodePane().layoutXProperty().add(textNode.getNodePane().translateXProperty()));
                    line.startYProperty().bind(textNode.getNodePane().layoutYProperty().add(textNode.getNodePane().translateYProperty()));
                } else if (parentNode.getType() == "image") {

                    ImageNode imageNode = (ImageNode) parentNode;
                    line.startXProperty().bind(imageNode.getNodePane().layoutXProperty().add(imageNode.getNodePane().translateXProperty()));
                    line.startYProperty().bind(imageNode.getNodePane().layoutYProperty().add(imageNode.getNodePane().translateYProperty()));
                }
                else if (parentNode.getType() == "link") {

                    VideoNode videoNode = (VideoNode) parentNode;
                    line.startXProperty().bind(videoNode.getNodePane().layoutXProperty().add(videoNode.getNodePane().translateXProperty()));
                    line.startYProperty().bind(videoNode.getNodePane().layoutYProperty().add(videoNode.getNodePane().translateYProperty()));
                }

                line.setStrokeWidth(4);

                line.endXProperty().bind(newImageNode.getNodePane().layoutXProperty().add(newImageNode.getNodePane().translateXProperty()));
                line.endYProperty().bind(newImageNode.getNodePane().layoutXProperty().add(newImageNode.getNodePane().translateYProperty()));
                newImageNode.setParentLine(line);
                newNodeStage2.getChildren().addAll(line);


                newImageNode.setChildno((nodeList.get(layer).get(index).getNoOfChildren()+nodeList.get(layer).get(index).getChildno()) * 2);
                newImageNode.setA(newTranslateX + 435);
                newImageNode.setB(newTranslateY + 260);
                newImageNode.setQuadrant(nodeList.get(layer).get(index).getQuadrant());
                newImageNode.setCircleNo(nodeList.get(layer).get(index).getCircleNo() *nodeList.get(layer).get(index).getExpansion());
                newImageNode.setLayer(nodeList.get(layer).get(index).getLayer() + 1);
                newImageNode.setExpansion(2*nodeList.get(layer).get(index).getExpansionconst());
                newImageNode.setChildLimit(2*nodeList.get(layer).get(index).getExpansionconst());
                newImageNode.setParentNode(nodeList.get(layer).get(index));
                newImageNode.setParent(newImageNode.getParentNode().getUniqueId());

                if(nodeList.get(layer).get(index).getOffset()!=0)
                {
                    newImageNode.setOffset((nodeList.get(layer).get(index).getNoOfChildren()+nodeList.get(layer).get(index).getOffset()) * 2);
                }

                nodeList.get(layer).get(index).setNoOfChildren(nodeList.get(layer).get(index).getNoOfChildren() + 1);

                if (nodeList.size() == layer + 1) {
                    List<MapNode> node = new ArrayList<>();
                    node.add(newImageNode);
                    nodeList.add(node);
                } else {
                    nodeList.get(layer + 1).add(newImageNode);
                }

                newImageNode.getNodePane().setOnMousePressed(doNothing);
                newImageNode.getNodePane().setOnMouseDragged(doNothing);
                newImageNode.getNodePane().setOnMouseReleased(doNothing);

                DataConnection.addImageNode(newImageNode);
            }
        }

    };


    EventHandler<MouseEvent> VideoOnMouseReleasedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {

            if (nodeList.get(layer).get(index).getType().equals("string") && nodeList.get(layer).get(index) != null){
                TextNode textNode = (TextNode) nodeList.get(layer).get(index);
                textNode.getNodePane().setStyle(null);
            }
            else if (nodeList.get(layer).get(index).getType().equals("image") && nodeList.get(layer).get(index) != null)
            {
                ImageNode imageNode = (ImageNode) nodeList.get(layer).get(index);

                imageNode.getNodePane().setStyle(null);
            }
            else  if (nodeList.get(layer).get(index).getType().equals("link") && nodeList.get(layer).get(index) != null)
            {
                VideoNode videoNode = (VideoNode) nodeList.get(layer).get(index);

                videoNode.getNodePane().setStyle(null);
            }

            nodedrag = false;
            Bounds bounds = videoNode.getNodePane().getBoundsInParent();
            if (classNode.getNodePane().intersects(bounds.getMinX() - 410, bounds.getMinY() - 225, 100, 100) && masterNode.size() == (ClassMap.noOfCircle * 10)) {
                expand();
            }
            if (classNode.getNodePane().intersects(bounds.getMinX() - 410, bounds.getMinY() - 225, 100, 100)) {

                if (ClassMap.circleX.size() % 2 == 0) {
                    randomNumber = 0;

                } else randomNumber = ClassMap.circleX.size() / 2;

                double newTranslateX = ClassMap.circleX.get(randomNumber);
                double newTranslateY = ClassMap.circleY.get(randomNumber);
                ClassMap.circleX.remove(randomNumber);
                ClassMap.circleY.remove(randomNumber);

                videoNode.getNodePane().setTranslateX(newTranslateX + 410);
                videoNode.getNodePane().setTranslateY(newTranslateY + 225);

                Line line = new Line();
                line.setStroke(javafx.scene.paint.Color.BLACK);

                line.startXProperty().bind(classNode.getNodePane().layoutXProperty().add(classNode.getNodePane().translateXProperty()));
                line.startYProperty().bind(classNode.getNodePane().layoutYProperty().add(classNode.getNodePane().translateYProperty()));
                line.endXProperty().bind(videoNode.getNodePane().layoutXProperty().add(videoNode.getNodePane().translateXProperty()));
                line.endYProperty().bind(videoNode.getNodePane().layoutXProperty().add(videoNode.getNodePane().translateYProperty()));

                line.setStrokeWidth(4);
                videoNode.setParentLine(line);
                newNodeStage2.getChildren().addAll(line);
                videoNode.setA(newTranslateX + 410);
                videoNode.setB(newTranslateY + 225);
                videoNode.setQuadrant(array[masterNode.size()%10] + 1);
                videoNode.setCircleNo(ClassMap.noOfCircle);
                videoNode.setParentNode(classNode);
                videoNode.setParent(videoNode.getParentNode().getUniqueId());
                videoNode.setChildLimit(2*classNode.getExpansionconst());
                videoNode.setLayer(0);
                videoNode.setExpansion(2*classNode.getExpansionconst());
                if (masterNode.size() >= 10){
                    videoNode.setOffset(1);
                }

                if (nodeList.size() == 0) {

                    masterNode.add(videoNode);
                    nodeList.add(masterNode);
                } else {
                    nodeList.get(0).add(videoNode);
                }

                classNode.setNoOfChildren(classNode.getNoOfChildren()+1);


                videoNode.getNodePane().setOnMousePressed(doNothing);
                videoNode.getNodePane().setOnMouseDragged(doNothing);
                videoNode.getNodePane().setOnMouseReleased(doNothing);
                DataConnection.addVideoNode(videoNode);
            } else if (intersection(videoNode.getNodePane())) {

                if (nodeList.get(layer).get(index).getNoOfChildren() == nodeList.get(layer).get(index).getChildLimit()){
                    expandChildren(nodeList.get(layer).get(index));

                }

                List<Double> X = newCalculateX(nodeList.get(layer).get(index).getCircleNo() * nodeList.get(layer).get(index).getExpansion());
                List<Double> Y = newCalculateY(nodeList.get(layer).get(index).getCircleNo() * nodeList.get(layer).get(index).getExpansion());

                double newTranslateX = X.get(((nodeList.get(layer).get(index).getQuadrant() - 1) * (nodeList.get(layer).get(index).getCircleNo())*nodeList.get(layer).get(index).getExpansion()) + nodeList.get(layer).get(index).getNoOfChildren()+nodeList.get(layer).get(index).getChildno() +(nodeList.get(layer).get(index).getOffset()*nodeList.get(layer).get(index).getExpansion()));
                double newTranslateY = Y.get(((nodeList.get(layer).get(index).getQuadrant() - 1) * (nodeList.get(layer).get(index).getCircleNo())*nodeList.get(layer).get(index).getExpansion()) + nodeList.get(layer).get(index).getNoOfChildren() +nodeList.get(layer).get(index).getChildno()+(nodeList.get(layer).get(index).getOffset()*nodeList.get(layer).get(index).getExpansion()));

                videoNode.getNodePane().setTranslateX(newTranslateX + 410);
                videoNode.getNodePane().setTranslateY(newTranslateY + 225);

                Line line = new Line();
                line.setStroke(javafx.scene.paint.Color.BLACK);

                MapNode parentNode = nodeList.get(layer).get(index);
                parentNode.children.add(videoNode);

                if (parentNode.getType() == "string") {
                    TextNode textNode = (TextNode) parentNode;
                    line.startXProperty().bind(textNode.getNodePane().layoutXProperty().add(textNode.getNodePane().translateXProperty()));
                    line.startYProperty().bind(textNode.getNodePane().layoutYProperty().add(textNode.getNodePane().translateYProperty()));
                } else if (parentNode.getType() == "image") {

                    ImageNode imageNode = (ImageNode) parentNode;
                    line.startXProperty().bind(imageNode.getNodePane().layoutXProperty().add(imageNode.getNodePane().translateXProperty()));
                    line.startYProperty().bind(imageNode.getNodePane().layoutYProperty().add(imageNode.getNodePane().translateYProperty()));
                }
                else if (parentNode.getType() == "link") {

                    VideoNode videoNode = (VideoNode) parentNode;
                    line.startXProperty().bind(videoNode.getNodePane().layoutXProperty().add(videoNode.getNodePane().translateXProperty()));
                    line.startYProperty().bind(videoNode.getNodePane().layoutYProperty().add(videoNode.getNodePane().translateYProperty()));
                }

                line.setStrokeWidth(4);

                line.endXProperty().bind(videoNode.getNodePane().layoutXProperty().add(videoNode.getNodePane().translateXProperty()));
                line.endYProperty().bind(videoNode.getNodePane().layoutXProperty().add(videoNode.getNodePane().translateYProperty()));
                videoNode.setParentLine(line);
                newNodeStage2.getChildren().addAll(line);


                videoNode.setChildno((nodeList.get(layer).get(index).getNoOfChildren()+nodeList.get(layer).get(index).getChildno()) * 2);
                videoNode.setA(newTranslateX + 435);
                videoNode.setB(newTranslateY + 260);
                videoNode.setQuadrant(nodeList.get(layer).get(index).getQuadrant());
                videoNode.setCircleNo(nodeList.get(layer).get(index).getCircleNo() *nodeList.get(layer).get(index).getExpansion());
                videoNode.setLayer(nodeList.get(layer).get(index).getLayer() + 1);
                videoNode.setExpansion(2*nodeList.get(layer).get(index).getExpansionconst());
                videoNode.setChildLimit(2*nodeList.get(layer).get(index).getExpansionconst());
                videoNode.setParentNode(nodeList.get(layer).get(index));
                videoNode.setParent(videoNode.getParentNode().getUniqueId());

                if(nodeList.get(layer).get(index).getOffset()!=0)
                {
                    videoNode.setOffset((nodeList.get(layer).get(index).getNoOfChildren()+nodeList.get(layer).get(index).getOffset()) * 2);
                }

                nodeList.get(layer).get(index).setNoOfChildren(nodeList.get(layer).get(index).getNoOfChildren() + 1);

                if (nodeList.size() == layer + 1) {
                    List<MapNode> node = new ArrayList<>();
                    node.add(videoNode);
                    nodeList.add(node);
                } else {
                    nodeList.get(layer + 1).add(videoNode);
                }

                videoNode.getNodePane().setOnMousePressed(doNothing);
                videoNode.getNodePane().setOnMouseDragged(doNothing);
                videoNode.getNodePane().setOnMouseReleased(doNothing);

                DataConnection.addVideoNode(videoNode);
            }
        }

    };


}

