package com.focuswall.challenges;

/**
 * Interface for challenges that must be solved to unlock the "Stop" button.
 * Decouples the UI from the specific type of challenge (Math, Captcha, etc.).
 */
public interface IChallenge {
    String getQuestion();
    boolean checkAnswer(String answer);
}