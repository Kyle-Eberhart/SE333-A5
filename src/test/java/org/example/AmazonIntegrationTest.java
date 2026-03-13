package org.example;

import org.example.Amazon.Amazon;
import org.example.Amazon.Database;
import org.example.Amazon.Item;
import org.example.Amazon.ShoppingCartAdaptor;
import org.example.Amazon.Cost.DeliveryPrice;
import org.example.Amazon.Cost.ExtraCostForElectronics;
import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.PriceRule;
import org.example.Amazon.Cost.RegularCost;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AmazonIntegrationTest {

    private Database database;
    private ShoppingCartAdaptor cart;

    @BeforeEach
    void setUp() {
        database = new Database();
        database.resetDatabase();
        cart = new ShoppingCartAdaptor(database);
    }

    @AfterEach
    void tearDown() {
        database.close();
    }

    @Nested
    @DisplayName("specification-based")
    class SpecificationBased {

        @Test
        void calculateShouldReturnZeroWhenCartIsEmpty() {
            Amazon amazon = new Amazon(cart, defaultRules());

            assertThat(amazon.calculate()).isEqualTo(0.0);
        }

        @Test
        void calculateShouldPersistAndReadSingleRegularItemCorrectly() {
            cart.add(new Item(ItemType.OTHER, "Book", 1, 25.0));

            Amazon amazon = new Amazon(cart, defaultRules());

            assertThat(amazon.calculate()).isEqualTo(30.0);
        }

        @Test
        void calculateShouldPersistAndReadElectronicItemWithSurcharge() {
            cart.add(new Item(ItemType.ELECTRONIC, "Laptop", 1, 100.0));

            Amazon amazon = new Amazon(cart, defaultRules());

            assertThat(amazon.calculate()).isEqualTo(112.5);
        }
    }

    @Nested
    @DisplayName("structural-based")
    class StructuralBased {

        @Test
        void calculateShouldReadBackMultipleItemsFromDatabase() {
            cart.add(new Item(ItemType.OTHER, "Pen", 2, 3.0));
            cart.add(new Item(ItemType.OTHER, "Notebook", 1, 4.0));

            Amazon amazon = new Amazon(cart, defaultRules());

            assertThat(amazon.calculate()).isEqualTo(15.0);
        }

        @Test
        void calculateShouldApplyMediumDeliveryBandForFourPersistedItems() {
            cart.add(new Item(ItemType.OTHER, "A", 1, 1.0));
            cart.add(new Item(ItemType.OTHER, "B", 1, 1.0));
            cart.add(new Item(ItemType.OTHER, "C", 1, 1.0));
            cart.add(new Item(ItemType.OTHER, "D", 1, 1.0));

            Amazon amazon = new Amazon(cart, List.of(new DeliveryPrice()));

            assertThat(amazon.calculate()).isEqualTo(12.5);
        }

        @Test
        void calculateShouldApplyHighestDeliveryBandForMoreThanTenPersistedItems() {
            for (int i = 1; i <= 11; i++) {
                cart.add(new Item(ItemType.OTHER, "Item" + i, 1, 1.0));
            }

            Amazon amazon = new Amazon(cart, List.of(new DeliveryPrice()));

            assertThat(amazon.calculate()).isEqualTo(20.0);
        }
    }

    private List<PriceRule> defaultRules() {
        return List.of(
                new RegularCost(),
                new DeliveryPrice(),
                new ExtraCostForElectronics()
        );
    }
}