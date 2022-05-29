package io.github.matteas;

import java.awt.Color;
import javax.swing.text.html.HTMLDocument;

public class Student extends Person {
    private final HTMLDocument testScore;

    public Student(String firstName, String lastName, int age, Color eyeColour,
                   Color hairColour, HTMLDocument testScore) {
        super(firstName, lastName, age, eyeColour, hairColour);
        this.testScore = testScore;
    }

    public HTMLDocument getTestScore() {
        return this.testScore;
    }
}
