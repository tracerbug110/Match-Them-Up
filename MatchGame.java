import java.util.Collections;
import java.util.Random;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class MatchGame extends Application
{
	private Stage stage;
	private HBox buttonContainer;
	private Label winner;
	private GridPane cards;
	private int cardValues[][] =
	{
		{0, 1, 2, 3, 4},
		{5, 6, 7, 8, 9},
		{0, 1, 2, 3, 4},
		{5, 6, 7, 8, 9} 
	};
	private int pairsMade = 0;
	private int cardsClicked = 1;
	private int card1;
	private int card2;
	private boolean isWaiting = false;

	public static void main(String[] args) 
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception 
	{
		stage = primaryStage;
		stage.setScene(createMainScene());
		stage.setTitle("Match Them Up");
		stage.setResizable(false);
		stage.show();
	}

	public Scene createMainScene()
	{
		Label title = new Label("Match Them Up!");
		title.setFont(setLabelStyle());
		title.setTextFill(Color.GOLD);
		
		Button playGame = new Button("Play");
		playGame.setOnAction(e -> { stage.setScene(playGame()); });
		
		Button howToPlay = new Button("How to Play");
		howToPlay.setOnAction(e -> { stage.setScene(createHowToPlayScene()); });
		
		VBox buttonContainer = new VBox(playGame, howToPlay);
		buttonContainer.setSpacing(5);
		buttonContainer.setAlignment(Pos.CENTER);
		
		VBox sceneContainer = new VBox(title, buttonContainer);
		sceneContainer.setStyle(setSceneStyle());
		sceneContainer.setAlignment(Pos.TOP_CENTER);
		sceneContainer.setPadding(new Insets(5));
		sceneContainer.setSpacing(35);
		
		Scene s = new Scene(sceneContainer, 200, 200);
		return s;
	}
	
	public Scene playGame() 
	{	
		winner = new Label("You win!");
		winner.setFont(setLabelStyle());
		winner.setTextFill(Color.GOLD);
		winner.setAlignment(Pos.TOP_CENTER);
		winner.setTextAlignment(TextAlignment.CENTER);
		winner.setVisible(false);
		
		shuffle(cardValues);
		cards = new GridPane(5, 5);
		for(int i = 0; i < 4; i++)
			for(int j = 0; j < 5; j++)	
				cards.add(createCard(Integer.toString(cardValues[i][j])), j, i);
		setUpCards();
		cards.setAlignment(Pos.CENTER);
		
		Button replay = new Button("Replay");
		replay.setOnAction(e -> 
		{
			ObservableList<Node> c = cards.getChildren();
			int tally = 0;
			
			pairsMade = 0;
			cardsClicked = 1;
			winner.setVisible(false);
			buttonContainer.setVisible(false);
			shuffle(cardValues);
			for(int i = 0; i < 4; i++)
				for(int j = 0; j < 5; j++)
				{
					flipCard((StackPane)c.get(tally));
					((Label)((StackPane)c.get(tally)).getChildren().get(0)).setText(Integer.toString(cardValues[i][j]));
					tally++;
				}
		});
		
		Button menu = new Button("Return to Menu");
		menu.setOnAction(e -> 
		{ 
			pairsMade = 0;
			cardsClicked = 1;
			stage.setScene(createMainScene()); 
		});
		
		buttonContainer = new HBox(replay, menu);
		buttonContainer.setAlignment(Pos.BOTTOM_CENTER);
		buttonContainer.setSpacing(5);
		buttonContainer.setVisible(false);
		
		VBox sceneContainer = new VBox(winner, cards, buttonContainer);
		sceneContainer.setStyle(setSceneStyle());
		sceneContainer.setAlignment(Pos.CENTER);
		sceneContainer.setPadding(new Insets(5));
		sceneContainer.setSpacing(10);
		
		Scene s = new Scene(sceneContainer, 715, 715);
		return s;
	}

	public Scene createHowToPlayScene()
	{
		Label title = new Label("How To Play");
		title.setFont(setLabelStyle());
		title.setTextFill(Color.GOLD);
		
		Label instructions = new Label("A 5x4 grid of cards will be placed in front of you.\n"
									 + "Each card has a number on it. Click the card to reveal it.\n"
									 + "The goal is to find the pairs of cards with matching numbers.\n"
									 + "Match all the cards to win.\n"
									 + "Good Luck!");
		instructions.setFont(setLabelStyle());
		instructions.setTextFill(Color.GOLD);
		
		Button exitScene = new Button("Okay");
		exitScene.setOnAction(e -> { stage.setScene(createMainScene()); });
		
		VBox sceneContainer = new VBox(title, instructions, exitScene);
		sceneContainer.setStyle(setSceneStyle());
		sceneContainer.setPadding(new Insets(5));
		sceneContainer.setSpacing(5);
		
		Scene s = new Scene(sceneContainer, 600, 200);
		return s;
	}
	
	public Font setLabelStyle()
	{
		return Font.font("Arial", FontWeight.BOLD, FontPosture.ITALIC, 20);
	}
	
	public String setSceneStyle()
	{
		return "-fx-background-color: #158232; -fx-border-width: 5px; -fx-border-color: #ffd700";
	}
	
	public StackPane createCard(String value)
	{
		StackPane card;
		Rectangle cardBase;
		Rectangle cardDecor;
		Label cardValue;
		
		cardBase = new Rectangle(100, 150, Color.WHITE);
		cardBase.setArcWidth(20);
		cardBase.setArcHeight(20);
		
		cardDecor = new Rectangle(75, 125, Color.BLUE);
		cardDecor.setArcWidth(20);
		cardDecor.setArcHeight(20);
			
		cardValue = new Label(value);
		cardValue.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 75));
		cardValue.setTextFill(Color.BLUE);
			
		card = new StackPane();
		card.getChildren().addAll(cardValue, cardBase, cardDecor);
		return card;
	}
	
	public void shuffle(int[][] values)
	{
		Random r = new Random();
		int randRow, randCol;
		int randRow2, randCol2;
		int tmp;
		
		for(int i = 0; i < 25; i++)
		{
			randRow = r.nextInt(0, 4);
			randCol = r.nextInt(0, 5);
			randRow2 = r.nextInt(0, 4);
			randCol2 = r.nextInt(0, 5);
			
			tmp = values[randRow][randCol];
			values[randRow][randCol] = values[randRow2][randCol2];
			values[randRow2][randCol2] = tmp;
		}
	}
	
	public void flipCard(StackPane s)
	{
		ObservableList<Node> workingCollection = FXCollections.observableArrayList(s.getChildren());
		
		Collections.swap(workingCollection, 0, 2);
		s.getChildren().setAll(workingCollection);
	}
	
    public void delay(long millis, Runnable continuation) 
    {
        Task<Void> sleeper = new Task<Void>() 
        {
            @Override
            protected Void call() throws Exception 
            {
                try { Thread.sleep(millis); }
                catch (InterruptedException e) {}
                isWaiting = false;
                return null;
            }
        };
        sleeper.setOnSucceeded(event -> continuation.run());
        new Thread(sleeper).start();
      }

    public void setUpCards()
    {
		cards.getChildren().forEach((node) -> 
		{
			node.setOnMouseClicked(e ->
			{
				ObservableList<Node> c = cards.getChildren();

				if(pairsMade != 10 && !(((StackPane)c.get(c.indexOf(e.getSource()))).getChildren().get(2) instanceof Label) && !isWaiting)
				{					  // checks if game is won, if card is not flipped
					isWaiting = true;
					if(cardsClicked % 2 == 1)
					{
						card1 = c.indexOf(e.getSource());
						flipCard(((StackPane)c.get(card1)));
						isWaiting = false;
					}
					else
					{
						card2 = c.indexOf(e.getSource());
						flipCard(((StackPane)c.get(card2)));
						if(((Label)((StackPane)c.get(card1)).getChildren().get(2)).getText()   // compares values of cards,
				   .equals(((Label)((StackPane)c.get(card2)).getChildren().get(2)).getText())) // obviously (sarcasm)
						{
							pairsMade++;
							isWaiting = false;
						}
						else
						{
							delay(2500, () -> 
							{
								flipCard(((StackPane)c.get(card1)));
								flipCard(((StackPane)c.get(card2)));
							});
						}
					}
					cardsClicked++;
				}
				if(pairsMade == 10)
				{
					winner.setVisible(true);
					buttonContainer.setVisible(true);
				}
			});
		});
    }
}





