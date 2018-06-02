package pl.main;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import pl.main.values.PaneCanvasGcSet;
import pl.main.values.GroupPanesAndGcSet;
import pl.main.values.ScreenAndPaneDimensions;

public class Runner extends Application {

	static MenuState menuState;
	static SubmenuType submenuType;
	SubmenuType previousSubmenuType;

	MainMenu mainMenu;
	PlayMenu playMenu;
	ShopMenu shopMenu;
	AchievementsMenu achievementsMenu;
	HighscoresMenu highscoresMenu;
	OptionsMenu optionsMenu;
	CreditsMenu creditsMenu;
	
	Gameplay game;

	HashMap<String, KeyState> keysActive;
	
	boolean hasGameJustStarted;
	int exitAnimationPosition;
	int enterAnimationPosition;
	
	int money;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		// set stage
		GroupPanesAndGcSet gpgc = setPanes();
		Scene scene = setStage(stage, gpgc);
		createMenus(gpgc);

		keysActive = new HashMap<>(); // stores pressed keys
		exitAnimationPosition = 0;
		enterAnimationPosition = 1920;
		hasGameJustStarted = true;
		money = 0; //
		// main loop
		new AnimationTimer() { //

			@Override
			public void handle(long now) {
				update(gpgc);
				
				if(keysActive.containsKey("ESCAPE")) {
					exit();
				} //usun pozniej
			}

		}.start();

		// detects user input
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

			public void handle(KeyEvent key) {
				String keyData = key.getCode().toString();

				if (!keysActive.containsKey(keyData)) {
					keysActive.put(keyData, KeyState.PRESSED);
				}
			}
		});

		scene.setOnKeyReleased(new EventHandler<KeyEvent>() {

			public void handle(KeyEvent key) {
				String keyData = key.getCode().toString();
				keysActive.remove(keyData);
			}
		});

		stage.show();
	}

	private void createMenus(GroupPanesAndGcSet gpgc) {
		mainMenu = new MainMenu(gpgc.getGc("mainMenu"));
		playMenu = new PlayMenu(gpgc.getGc("playMenu"));
		shopMenu = new ShopMenu(gpgc.getGc("shopMenu"));
		achievementsMenu = new AchievementsMenu(gpgc.getGc("achievementsMenu"));
		highscoresMenu = new HighscoresMenu(gpgc.getGc("highscoresMenu"));
		optionsMenu = new OptionsMenu(gpgc.getGc("optionsMenu"));
		creditsMenu = new CreditsMenu(gpgc.getGc("creditsMenu"));

		menuState = MenuState.PREPAREMENU;
		submenuType = SubmenuType.MAIN;

	}

	private void paintMenuBg(GraphicsContext bgGc, GraphicsContext moneyGc) {
		
		Image bgMenuImage = new Image("file:resources\\bg.jpg");
		bgGc.drawImage(bgMenuImage, 0, 0);
		
		Image coin = new Image("file:resources\\coin.png");
		moneyGc.drawImage(coin, 1700, 15);
	}
	private Scene setStage(Stage stage, GroupPanesAndGcSet gpgc) {
		stage.setTitle("Jetpack Joe");
		stage.setFullScreen(true);
		stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		Scene scene = new Scene(gpgc.getPane("root"), Color.BLACK);
		stage.setScene(scene);

		return scene;
	}

	private GroupPanesAndGcSet setPanes() {
		ScreenAndPaneDimensions dimensions = getDimensions();

		PaneCanvasGcSet pcgBg = setPaneComponents(dimensions);
		scaleCanvas(pcgBg.getCanvas(), dimensions);
		
		PaneCanvasGcSet pcgMoney = setPaneComponents(dimensions);
		scaleCanvas(pcgMoney.getCanvas(), dimensions);

		PaneCanvasGcSet pcgMainMenu = setPaneComponents(dimensions);
		scaleCanvas(pcgMainMenu.getCanvas(), dimensions);

		PaneCanvasGcSet pcgPlayMenu = setPaneComponents(dimensions);
		scaleCanvas(pcgPlayMenu.getCanvas(), dimensions);

		PaneCanvasGcSet pcgShopMenu = setPaneComponents(dimensions);
		scaleCanvas(pcgShopMenu.getCanvas(), dimensions);

		PaneCanvasGcSet pcgAchievementsMenu = setPaneComponents(dimensions);
		scaleCanvas(pcgAchievementsMenu.getCanvas(), dimensions);

		PaneCanvasGcSet pcgHighscoresMenu = setPaneComponents(dimensions);
		scaleCanvas(pcgHighscoresMenu.getCanvas(), dimensions);

		PaneCanvasGcSet pcgOptionsMenu = setPaneComponents(dimensions);
		scaleCanvas(pcgOptionsMenu.getCanvas(), dimensions);

		PaneCanvasGcSet pcgCreditsMenu = setPaneComponents(dimensions);
		scaleCanvas(pcgCreditsMenu.getCanvas(), dimensions);
		
		PaneCanvasGcSet pcgPlayer = setPaneComponents(dimensions);
		scaleCanvas(pcgPlayer.getCanvas(), dimensions);
		
		Pane gamePane = new Pane(pcgBg.getPane(), pcgMoney.getPane(), pcgPlayer.getPane());
		
		Pane menuPane = new Pane(pcgBg.getPane(), pcgMoney.getPane(), pcgMainMenu.getPane(), pcgPlayMenu.getPane(), pcgShopMenu.getPane(),
				pcgAchievementsMenu.getPane(), pcgHighscoresMenu.getPane(), pcgOptionsMenu.getPane(),
				pcgCreditsMenu.getPane());
		
		Pane rootPane = new Pane(menuPane, gamePane);

		HashMap<String, Pane> pMap = new HashMap<>();
		pMap.put("root", rootPane);
		pMap.put("menu", menuPane);
		pMap.put("game", gamePane);
		
		HashMap<String, GraphicsContext> gcMap = new HashMap<>();
		gcMap.put("bg", pcgBg.getGc());
		gcMap.put("money", pcgMoney.getGc());
		gcMap.put("mainMenu", pcgMainMenu.getGc());
		gcMap.put("playMenu", pcgPlayMenu.getGc());
		gcMap.put("shopMenu", pcgShopMenu.getGc());
		gcMap.put("achievementsMenu", pcgAchievementsMenu.getGc());
		gcMap.put("highscoresMenu", pcgHighscoresMenu.getGc());
		gcMap.put("optionsMenu", pcgOptionsMenu.getGc());
		gcMap.put("creditsMenu", pcgCreditsMenu.getGc());
		
		GroupPanesAndGcSet gpgc = new GroupPanesAndGcSet(pMap, gcMap);

		return gpgc;
	}
	
	private PaneCanvasGcSet setPaneComponents(ScreenAndPaneDimensions dim) {
		Pane pane = new Pane();
		Canvas canvas = new Canvas(dim.getPaneWidth(), dim.getPaneHeight());
		GraphicsContext gc = canvas.getGraphicsContext2D();
		pane.getChildren().add(canvas);

		return new PaneCanvasGcSet(pane, canvas, gc);
	}
	
	private void scaleCanvas(Canvas canvas, ScreenAndPaneDimensions dim) {
		canvas.setScaleX(dim.getScreenWidth() / dim.getPaneWidth());
		canvas.setScaleY(dim.getScreenHeight() / dim.getPaneHeight());
		canvas.setTranslateX(0 - Math.abs(dim.getScreenWidth() - dim.getPaneWidth()) / 2);
		canvas.setTranslateY(0 - Math.abs(dim.getScreenHeight() - dim.getPaneHeight()) / 2);
	}
	
	private ScreenAndPaneDimensions getDimensions() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double paneHeight = 1080;
		double paneWidth = 1920;
		double screenHeight = screenSize.getHeight();
		double screenWidth = screenSize.getWidth();

		return new ScreenAndPaneDimensions(paneHeight, paneWidth, screenHeight, screenWidth);
	}

	
	private void update(GroupPanesAndGcSet gpgc) { // TODO Auto-generated method stub
		
		switch (menuState) {
		case PREPAREMENU:
			paintMenuBg(gpgc.getGc("bg"), gpgc.getGc("money"));
			updateMoneyCounter(gpgc.getGc("money"));

			hasGameJustStarted = mainMenu.displayMainMenu(hasGameJustStarted);
			menuState = MenuState.SUBMENU;
			break;
		
		case PREPARESUBMENU:
			displayChosenSubmenu();

			menuState = MenuState.MENU_ENTERANIMATION;
			break;
			
		case MENU_ENTERANIMATION:
			if(exitAnimationPosition >= -1920) {
				menuExitAnimation();
				menuEnterAnimation();

			} else {
				exitAnimationPosition = 0;
				enterAnimationPosition = 1920;
				menuState = MenuState.SUBMENU;
			}
			break;

		case SUBMENU:
			userSelectMenuOption();
			userSelectParallelMenuOption();

			if (userPressed("ENTER")) {
				previousSubmenuType = submenuType;
				getSelectedOption();
			}
			break;

		case PREPAREGAMEPLAY:
		    fadeTransition(gpgc.getPane("menu"), 1, 0, 1000);
//			playMenu.getMode();
//			playMenu.getNumberOfPlayers();
		    menuState = MenuState.GAMEPLAY;
			//-----------------------------------------------------------------------------------------------------------TU PRZYGOTOWANIE ROZGRYWKI
			break;

		case GAMEPLAY:
			//-----------------------------------------------------------------------------------------------------------TU PETLA SAMEJ ROZGRYWKI
			break;

		case EXIT:
			exit();
		}
	}

	private void fadeTransition(Node node, double fromValue, double toValue, int duration) {
		FadeTransition ft = new FadeTransition(Duration.millis(duration), node);
	    ft.setFromValue(fromValue);
	    ft.setToValue(toValue);
//	    node.setOpacity(toValue);
	 
	    ft.play();		
	}

	private void updateMoneyCounter(GraphicsContext moneyGc) { 
		moneyGc.clearRect(1760, 0, 200, 200);
		moneyGc.setFill(Color.WHITE);
		moneyGc.setFont(Font.font("Consolas", 45));
		
		String moneyString = Integer.toString(money);
		while (moneyString.length() < 5) {
			moneyString = "0" + moneyString;
		}
		moneyGc.fillText(moneyString, 1760, 55);		
	}

	private void getSelectedOption() { 
		switch (submenuType) {
		case MAIN:
			mainMenu.getSelectedOption();
			break;
		case PLAY:
			playMenu.getSelectedOption();
			break;
		case SHOP:
			shopMenu.getSelectedOption();
			break;
		case ACHIEVEMENTS:
			achievementsMenu.getSelectedOption();
			break;
		case HIGHSCORES:
			highscoresMenu.getSelectedOption();
			break;
		case OPTIONS:
			optionsMenu.getSelectedOption();
			break;
		case CREDITS:
			creditsMenu.getSelectedOption();
		}		
	}

	private void menuEnterAnimation() {
		switch (submenuType) {
		case MAIN:
			mainMenu.getGc().getCanvas().setTranslateX(enterAnimationPosition);
			break;
		case PLAY:
			playMenu.getGc().getCanvas().setTranslateX(enterAnimationPosition);
			break;
		case SHOP:
			shopMenu.getGc().getCanvas().setTranslateX(enterAnimationPosition);
			break;
		case ACHIEVEMENTS:
			achievementsMenu.getGc().getCanvas().setTranslateX(enterAnimationPosition);
			break;
		case HIGHSCORES:
			highscoresMenu.getGc().getCanvas().setTranslateX(enterAnimationPosition);
			break;
		case OPTIONS:
			optionsMenu.getGc().getCanvas().setTranslateX(enterAnimationPosition);
			break;
		case CREDITS:
			creditsMenu.getGc().getCanvas().setTranslateX(enterAnimationPosition);
		}		
		enterAnimationPosition -= 25;
	}

	private void menuExitAnimation() {
			
			switch (previousSubmenuType) {
			case MAIN:
				mainMenu.getGc().getCanvas().setTranslateX(exitAnimationPosition);
				break;
				
			case PLAY:
				playMenu.getGc().getCanvas().setTranslateX(exitAnimationPosition);
				break;
				
			case SHOP:
				shopMenu.getGc().getCanvas().setTranslateX(exitAnimationPosition);
				break;
				
			case ACHIEVEMENTS:
				achievementsMenu.getGc().getCanvas().setTranslateX(exitAnimationPosition);
				break;
				
			case HIGHSCORES:
				highscoresMenu.getGc().getCanvas().setTranslateX(exitAnimationPosition);
				break;
				
			case OPTIONS:
				optionsMenu.getGc().getCanvas().setTranslateX(exitAnimationPosition);
				break;
				
			case CREDITS:
				creditsMenu.getGc().getCanvas().setTranslateX(exitAnimationPosition);
			}
			
			exitAnimationPosition -= 25;
	}

	private void exit() { // TODO Auto-generated method stub
		System.exit(0); // clean up before!
	}

	private void displayChosenSubmenu() {
		switch (submenuType) {
		case MAIN:
			mainMenu.displayMainMenu(hasGameJustStarted);
			break;
		case PLAY:
			playMenu.displayPlayMenu();
			break;
		case SHOP:
			shopMenu.displayShopMenu();
			break;
		case ACHIEVEMENTS:
			achievementsMenu.displayAchievementsMenu();
			break;
		case HIGHSCORES:
			highscoresMenu.displayHighscoresMenu();
			break;
		case OPTIONS:
			optionsMenu.displayOptionsMenu();
			break;
		case CREDITS:
			creditsMenu.displayCreditsMenu();
		}
	}

	private boolean userPressed(String button) {
		if (keysActive.containsKey(button)) {
			if (keysActive.get(button) == KeyState.PRESSED) {
				keysActive.put(button, KeyState.HELD);
				return true;
			}
		}
		return false;
	}
	
	private void userSelectMenuOption() {
		if (userPressed("W") || userPressed("UP")) {
			selectPreviousMenuOption();
		}
		if (userPressed("S") || userPressed("DOWN")) {
			selectNextMenuOption();
		}
	}
	
	private void userSelectParallelMenuOption() {
		if (userPressed("D") || userPressed("RIGHT")) {
			selectRightMenuOption();
		}
		if (userPressed("A") || userPressed("LEFT")) {
			selectLeftMenuOption();
		}
	}

	@SuppressWarnings("incomplete-switch")
	private void selectLeftMenuOption() {
		switch (submenuType) {
		case PLAY:
			playMenu.selectLeftOption();
			break;
		case OPTIONS:
			optionsMenu.selectLeftOption();
		}		
	}

	@SuppressWarnings("incomplete-switch")
	private void selectRightMenuOption() {
		switch (submenuType) {
		case PLAY:
			playMenu.selectRightOption();
			break;
		case OPTIONS:
			optionsMenu.selectRightOption();
		}		
	}

	@SuppressWarnings("incomplete-switch")
	private void selectPreviousMenuOption() {
		switch (submenuType) {
		case MAIN:
			mainMenu.selectPreviousOption();
			break;
		case PLAY:
			playMenu.selectPreviousOption();
			break;
		case SHOP:
			shopMenu.selectPreviousOption();
			break;
		case ACHIEVEMENTS:
			achievementsMenu.selectPreviousOption();
			break;
		case OPTIONS:
			optionsMenu.selectPreviousOption();
		}
	}

	@SuppressWarnings("incomplete-switch")
	private void selectNextMenuOption() {
		switch (submenuType) {
		case MAIN:
			mainMenu.selectNextOption();
			break;
		case PLAY:
			playMenu.selectNextOption();
			break;
		case SHOP:
			shopMenu.selectNextOption();
			break;
		case ACHIEVEMENTS:
			achievementsMenu.selectNextOption();
			break;
		case OPTIONS:
			optionsMenu.selectNextOption();
		}

	}

	public enum KeyState {
		PRESSED, HELD
	}

	public enum MenuState {
		PREPAREMENU, MENU_ENTERANIMATION, PREPARESUBMENU, SUBMENU, PREPAREGAMEPLAY, GAMEPLAY, PREPAREPAUSE, PAUSE, PLAYERDIED, NEWHIGHSCORE, GAMEOVER, EXIT
	}
	
	public enum SubmenuType {
		MAIN, PLAY, SHOP, ACHIEVEMENTS, HIGHSCORES, OPTIONS, CREDITS
	}

}
