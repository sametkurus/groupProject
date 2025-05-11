package application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;


//  Represents the player in the tower defense game
//  Tracks money, lives, and game progress

public class Player {
	private int money;
	private int lives;
	private int currentLevel;
	private int score;
	private int totalEnemiesDefeated;


	//      Initializes a new player with default values  
	public Player() {
		this.money = 100; // Starting money
		this.lives = 5;  // Starting lives
		this.currentLevel = 1;
		this.score = 0;
		this.totalEnemiesDefeated = 0;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}

	//      Adds money to the player's balance  
	public void addMoney(int amount) {
		if (amount > 0) {
			this.money += amount;
		}
	}


	//      Attempts to spend money
	public boolean spendMoney(int amount) {
		if (hasEnoughMoney(amount)) {
			this.money -= amount;
			return true;
		}
		return false;
	}


	//      Checks if player has enough money
	public boolean hasEnoughMoney(int amount) {
		return this.money >= amount;
	}


	//     Decrements player's lives when an enemy reaches the end
	public void loseLife() {
		if (this.lives > 0) {
			this.lives -= 1;
		}
	}


	//      Increases player's score
	public void addScore(int points) {
		if (points > 0) {
			score += points;
		}
	}


	//     Increments enemy defeat counter
	public void incrementEnemiesDefeated() {
		totalEnemiesDefeated++;
	}


	//     Advances player to next level

	public void levelUp() {
		currentLevel++;
		// Carry over money to next level
	}

	// Getters and property accessors
	public int getMoney() {
		return this.money;
	}

	public int getLives() {
		return this.lives;
	}

	public int getCurrentLevel() {
		return currentLevel;
	}

	public int getScore() {
		return score;
	}

	public int getTotalEnemiesDefeated() {
		return totalEnemiesDefeated;
	}


	//    Resets player for a new game while keeping score
	public void reset() {
		this.money = 100;
		this.lives = 5;
		this.currentLevel = 1;
		this.totalEnemiesDefeated = 0;
	}

	public void setMoney(int amount) {
		this.money = amount;
	}

	//     Fully resets player including score
	public void fullReset() {
		reset();
		score = 0;
	}
}