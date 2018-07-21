package net.thomas.portfolio.web;

//import javafx.application.Application;
//import javafx.geometry.HPos;
//import javafx.geometry.VPos;
//import javafx.scene.Node;
//import javafx.scene.Scene;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.Priority;
//import javafx.scene.layout.Region;
//import javafx.scene.paint.Color;
//import javafx.scene.web.WebEngine;
//import javafx.scene.web.WebView;
//import javafx.stage.Stage;

public class Tester { // extends Application {
	// private Scene scene;
	//
	// @Override
	// public void start(Stage stage) {
	// // create the scene
	// stage.setTitle("Web View");
	// scene = new Scene(new Browser(), 750, 500, Color.web("#666970"));
	// stage.setScene(scene);
	// scene.getStylesheets().add("webviewsample/BrowserToolbar.css");
	// stage.show();
	// }
	//
	// public static void main(String[] args) {
	// launch(args);
	// }
	// }
	//
	// class Browser extends Region {
	//
	// final WebView browser = new WebView();
	// final WebEngine webEngine = browser.getEngine();
	//
	// public Browser() {
	// // apply the styles
	// getStyleClass().add("browser");
	// // load the web page
	// webEngine.load("http://www.google.com/");
	// // add the web view to the scene
	// getChildren().add(browser);
	//
	// }
	//
	// private Node createSpacer() {
	// final Region spacer = new Region();
	// HBox.setHgrow(spacer, Priority.ALWAYS);
	// return spacer;
	// }
	//
	// @Override
	// protected void layoutChildren() {
	// final double w = getWidth();
	// final double h = getHeight();
	// layoutInArea(browser, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
	// }
	//
	// @Override
	// protected double computePrefWidth(double height) {
	// return 750;
	// }
	//
	// @Override
	// protected double computePrefHeight(double width) {
	// return 500;
	// }
}

// public class Tester extends JFrame {
// private static final long serialVersionUID = 1L;
//
// public Tester() {
// final WebView browser = new WebView();
// final WebEngine webEngine = browser.getEngine();
//
// final String url = "https://eclipse.org";
//
// webEngine.load(url);
//
// setLayout(new BorderLayout());
// add(new Browser(browser), CENTER);
// setSize(1000, 600);
// setDefaultCloseOperation(DISPOSE_ON_CLOSE);
// setVisible(true);
// }
//
// class Browser extends JFXPanel {
// private final WebView browser;
//
// public Browser(WebView browser) {
// this.browser = browser;
// }
// }
//
// public static void main(String[] args) throws InterruptedException {
// final Tester tester = new Tester();
// }
// }
