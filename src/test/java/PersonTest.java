import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PersonTest {

    @Test
    void personHasFirstName() {
        Person person = new Person("John", "Smith", 25, Color.BLUE, Color.BLACK);
        assertEquals("John", person.getFirstName());
    }

    @Test
    void personHasLastName() {
        Person person = new Person("John", "Smith", 25, Color.BLUE, Color.BLACK);
        assertEquals("Smith", person.getLastName());
    }

    @Test
    void personHasAge() {
        Person person = new Person("John", "Smith", 25, Color.BLUE, Color.BLACK);
        assertEquals(25, person.getAge());
    }

    @Test
    void personHasBirthdayAndGetsOlder() {
        Person person = new Person("John", "Smith", 25, Color.BLUE, Color.BLACK);
        person.haveBirthday();
        assertEquals(26, person.getAge());
    }

    @Test
    void personHasEyeColour() {
        Person person = new Person("John", "Smith", 25, Color.BLUE, Color.BLACK);
        assertEquals(Color.BLUE, person.getEyeColour());
    }

    @Test
    void personHasHairColour() {
        Person person = new Person("John", "Smith", 25, Color.BLUE, Color.BLACK);
        assertEquals(Color.BLACK, person.getHairColour());
    }

    @Test
    void personDyesHair() {
        Person person = new Person("John", "Smith", 25, Color.BLUE, Color.BLACK);
        person.dyeHair(Color.RED);
        assertEquals(Color.RED, person.getHairColour());
    }
}
