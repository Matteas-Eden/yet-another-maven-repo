import java.awt.*;

public class Person {
    private final String firstName;
    private final String lastName;
    private int age;
    private Color eyeColour;
    private Color hairColour;

    public Person(String firstName, String lastName, int age, Color eyeColour, Color hairColour) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.eyeColour = eyeColour;
        this.hairColour = hairColour;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    public void haveBirthday() {
        age++;
    }

    public Color getEyeColour() {
        return eyeColour;
    }

    public Color getHairColour() {
        return hairColour;
    }

    public void dyeHair(Color colour) {
        this.hairColour = colour;
    }

}
