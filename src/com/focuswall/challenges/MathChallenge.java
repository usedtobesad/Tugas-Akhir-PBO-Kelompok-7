package com.focuswall.challenges;

import java.util.Random;

/**
 * A math challenge that scales in difficulty.
 * - Digits increase based on the difficulty level (extension count).
 * - Randomly swaps between Addition and Subtraction.
 */
public class MathChallenge implements IChallenge {
    private int a;
    private int b;
    private String operator;
    private String expectedAnswer;

    /**
     * @param difficultyLevel 0 = Easy (2 digits), 1 = Medium (3 digits), etc.
     */
    public MathChallenge(int difficultyLevel) {
        generateNew(difficultyLevel);
    }

    public void generateNew(int level) {
        Random rand = new Random();

        // Calculate range based on level.
        // Level 0: 10 - 99 (2 digits)
        // Level 1: 100 - 999 (3 digits)
        // Level 2: 1000 - 9999 (4 digits)
        int min = (int) Math.pow(10, 1 + level);
        int max = (int) Math.pow(10, 2 + level) - 1;

        this.a = rand.nextInt((max - min) + 1) + min;
        this.b = rand.nextInt((max - min) + 1) + min;

        // Randomly choose Addition (+) or Subtraction (-)
        boolean isAddition = rand.nextBoolean();

        if (isAddition) {
            this.operator = "+";
            this.expectedAnswer = String.valueOf(a + b);
        } else {
            this.operator = "-";
            this.expectedAnswer = String.valueOf(a - b);
        }
    }

    @Override
    public String getQuestion() {
        return a + " " + operator + " " + b + " = ?";
    }

    @Override
    public boolean checkAnswer(String input) {
        if (input == null) return false;
        try {
            // Trim whitespace to be forgiving
            return input.trim().equals(expectedAnswer);
        } catch (Exception e) {
            return false;
        }
    }
}