import javax.swing.text.html.HTMLDocument;

public class Student extends Person {
    public final HTMLDocument testScore;

    public Student(String firstName, String lastName, int age, Color eyeColour, Color hairColour, HTMLDocument testScore) {
        super(firstName, lastName, age, eyeColour, hairColour);
        this.testScore = testScore;
    }
}

