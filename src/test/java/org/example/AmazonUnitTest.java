package org.example;

import org.example.Amazon.Amazon;
import org.example.Amazon.Item;
import org.example.Amazon.ShoppingCart;
import org.example.Amazon.Cost.DeliveryPrice;
import org.example.Amazon.Cost.ExtraCostForElectronics;
import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.PriceRule;
import org.example.Amazon.Cost.RegularCost;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AmazonUnitTest {

    @Nested
    @DisplayName("specification-based")
    class SpecificationBased {

        @Test
        void calculateShouldReturnZeroForEmptyCart() {
            ShoppingCart cart = mock(ShoppingCart.class);
            when(cart.getItems()).thenReturn(List.of());

            List<PriceRule> rules = List.of(
                    new RegularCost(),
                    new DeliveryPrice(),
                    new ExtraCostForElectronics()
            );

            Amazon amazon = new Amazon(cart, rules);

            assertThat(amazon.calculate()).isEqualTo(0.0);
        }

        @Test
        void calculateShouldReturnRegularItemPlusDeliveryForSingleNonElectronicItem() {
            ShoppingCart cart = mock(ShoppingCart.class);

            Item item = new Item(ItemType.OTHER, "Book", 1, 25.0);
            when(cart.getItems()).thenReturn(List.of(item));

            List<PriceRule> rules = List.of(
                    new RegularCost(),
                    new DeliveryPrice(),
                    new ExtraCostForElectronics()
            );

            Amazon amazon = new Amazon(cart, rules);

            assertThat(amazon.calculate()).isEqualTo(30.0);
        }

        @Test
        void calculateShouldIncludeElectronicSurchargeWhenElectronicItemExists() {
            ShoppingCart cart = mock(ShoppingCart.class);

            Item item = new Item(ItemType.ELECTRONIC, "Laptop", 1, 100.0);
            when(cart.getItems()).thenReturn(List.of(item));

            List<PriceRule> rules = List.of(
                    new RegularCost(),
                    new DeliveryPrice(),
                    new ExtraCostForElectronics()
            );

            Amazon amazon = new Amazon(cart, rules);

            assertThat(amazon.calculate()).isEqualTo(112.5);
        }
    }

    @Nested
    @DisplayName("structural-based")
    class StructuralBased {

        @Test
        void calculateShouldTraverseRulesAndAggregateAllCharges() {
            ShoppingCart cart = mock(ShoppingCart.class);
            List<Item> items = List.of(
                    new Item(ItemType.OTHER, "Pen", 2, 3.0),
                    new Item(ItemType.OTHER, "Notebook", 1, 4.0)
            );

            when(cart.getItems()).thenReturn(items);

            PriceRule rule1 = mock(PriceRule.class);
            PriceRule rule2 = mock(PriceRule.class);

            when(rule1.priceToAggregate(items)).thenReturn(10.0);
            when(rule2.priceToAggregate(items)).thenReturn(5.0);

            Amazon amazon = new Amazon(cart, List.of(rule1, rule2));

            assertThat(amazon.calculate()).isEqualTo(15.0);
            verify(rule1).priceToAggregate(items);
            verify(rule2).priceToAggregate(items);
        }

        @Test
        void calculateShouldApplyMediumDeliveryBandForFourItems() {
            ShoppingCart cart = mock(ShoppingCart.class);
            List<Item> items = List.of(
                    new Item(ItemType.OTHER, "A", 1, 1.0),
                    new Item(ItemType.OTHER, "B", 1, 1.0),
                    new Item(ItemType.OTHER, "C", 1, 1.0),
                    new Item(ItemType.OTHER, "D", 1, 1.0)
            );

            when(cart.getItems()).thenReturn(items);

            Amazon amazon = new Amazon(cart, List.of(new DeliveryPrice()));

            assertThat(amazon.calculate()).isEqualTo(12.5);
        }

        @Test
        void calculateShouldApplyHighestDeliveryBandForMoreThanTenItems() {
            ShoppingCart cart = mock(ShoppingCart.class);
            List<Item> items = List.of(
                    new Item(ItemType.OTHER, "1", 1, 1.0),
                    new Item(ItemType.OTHER, "2", 1, 1.0),
                    new Item(ItemType.OTHER, "3", 1, 1.0),
                    new Item(ItemType.OTHER, "4", 1, 1.0),
                    new Item(ItemType.OTHER, "5", 1, 1.0),
                    new Item(ItemType.OTHER, "6", 1, 1.0),
                    new Item(ItemType.OTHER, "7", 1, 1.0),
                    new Item(ItemType.OTHER, "8", 1, 1.0),
                    new Item(ItemType.OTHER, "9", 1, 1.0),
                    new Item(ItemType.OTHER, "10", 1, 1.0),
                    new Item(ItemType.OTHER, "11", 1, 1.0)
            );

            when(cart.getItems()).thenReturn(items);

            Amazon amazon = new Amazon(cart, List.of(new DeliveryPrice()));

            assertThat(amazon.calculate()).isEqualTo(20.0);
        }
    }
}