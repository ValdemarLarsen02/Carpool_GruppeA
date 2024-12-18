import app.utils.DropdownOptions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DropDownOptionsTest {

    // Tester genereringen af dropdown-valgmuligheder med gyldig rækkevidde og trin
    @Test
    void testGenerateOptionsValidRangeAndStep() {
        // Test gyldig rækkevidde (1-10) og trin på 2
        String[] options = DropdownOptions.generateOptions(1, 10, 2);
        assertArrayEquals(new String[]{"2", "4", "6", "8", "10"}, options); // Forventer et array med de rigtige trin
    }

    // Tester genereringen af dropdown-valgmuligheder, når start og slut er den samme værdi
    @Test
    void testGenerateOptionsSingleValue() {
        // Test hvor start og slut er den samme (10)
        String[] options = DropdownOptions.generateOptions(10, 10, 5);
        assertArrayEquals(new String[]{"10"}, options); // Forventer kun én værdi i resultatet, da start og slut er ens
    }

    // Tester genereringen af dropdown-valgmuligheder, når intervallet er tomt (ingen værdier)
    @Test
    void testGenerateOptionsNoValues() {
        // Test hvor intervallet ikke indeholder nogen værdier (start=1, slut=10, trin=20)
        String[] options = DropdownOptions.generateOptions(1, 10, 20);
        assertArrayEquals(new String[]{}, options); // Forventer et tomt array, da der ikke er nogen værdier i intervallet
    }

    // Tester genereringen af dropdown-valgmuligheder med negative værdier i intervallet
    @Test
    void testGenerateOptionsNegativeRange() {
        // Test med negative værdier i rækkevidden (start=-10, slut=-1, trin=3)
        String[] options = DropdownOptions.generateOptions(-10, -1, 3);
        assertArrayEquals(new String[]{"-9", "-6", "-3"}, options); // Forventer et array med negative værdier og trin på 3
    }

    // Tester genereringen af dropdown-valgmuligheder, når start er større end slut (omvendt interval)
    @Test
    void testGenerateOptionsReversedRange() {
        // Test hvor start (10) er større end slut (1), hvilket resulterer i et omvendt interval
        String[] options = DropdownOptions.generateOptions(10, 1, 2);
        assertArrayEquals(new String[]{}, options); // Forventer et tomt array, da rækkefølgen er omvendt og ingen værdier genereres
    }

    // Tester genereringen af dropdown-valgmuligheder med trin=1 (alle værdier skal medtages)
    @Test
    void testGenerateOptionsEdgeCaseZeroStep() {
        // Test hvor trin er 1, hvilket betyder at alle værdier fra 1 til 5 skal medtages
        String[] options = DropdownOptions.generateOptions(1, 5, 1);
        assertArrayEquals(new String[]{"1", "2", "3", "4", "5"}, options); // Forventer alle værdier i intervallet fra 1 til 5
    }
}
