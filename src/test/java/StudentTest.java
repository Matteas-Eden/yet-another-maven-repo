import org.junit.jupiter.api.Test;

import javax.swing.text.html.HTMLDocument;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class StudentTest {
    @Test
    void studentHasTestScore() {
        // GIVEN a test score
        var testScore = new HTMLDocument();
        // AND a student with this test score
        var student = new Student("John", "Smith", 25, Color.BLUE, Color.BLACK, testScore);

        // WHEN we ask the student for their test score
        var testScoreActual = student.getTestScore();

        // THEN we get the test score we passed in
        assertEquals(testScore, testScoreActual);
    }
}
