package com.uet.legacy;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class OrderBillingEngineDiscountTest {

    @TempDir
    Path tempDir;

    private OrderBillingEngine engine;
    private ByteArrayOutputStream out;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        engine = new OrderBillingEngine();
        // Redirect the log file so tests never write to the real order_audit.log
        engine.setLogFilePath(tempDir.resolve("test.log").toString());

        // Capture everything the engine prints to the console
        originalOut = System.out;
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    // ---------- helper methods ----------

    private Map<String, Object> item(double price, int quantity) {
        Map<String, Object> item = new HashMap<>();
        item.put("price", price);       // must be double
        item.put("quantity", quantity); // must be int
        return item;
    }

    /** One item, quantity 1, valid credit card, PK tax (default). */
    private Map<String, Object> order(String customerType, double price) {
        Map<String, Object> order = new HashMap<>();
        order.put("customerEmail", "ali@example.com");
        order.put("items", new ArrayList<>(List.of(item(price, 1))));
        order.put("paymentMethod", "CREDIT_CARD");
        order.put("cardNumber", "4111111111111111");
        if (customerType != null) {
            order.put("customerType", customerType);
        }
        return order;
    }

    private String console() {
        return out.toString();
    }

    /** Order must succeed and the confirmation message must show this payable amount. */
    private void assertPayable(Map<String, Object> order, String expectedPayable) {
        assertTrue(engine.processOrder(order), "Order should succeed");
        assertTrue(console().contains("PKR " + expectedPayable + " is confirmed!"),
                "Expected payable " + expectedPayable + " but console was:\n" + console());
    }

    // ---------- REGULAR customer ----------

    @ParameterizedTest(name = "REGULAR price {0} -> payable {1}")
    @CsvSource({
        "1000.0,  1170.00",   // no discount
        "10000.0, 11700.00",  // exactly 10000: NOT above the limit, no discount
        "12000.0, 13455.00"   // above 10000: flat 500 discount
    })
    void regularCustomerDiscount(double price, String expected) {
        assertPayable(order("REGULAR", price), expected);
    }

    @Test
    void missingCustomerTypeDefaultsToRegular() {
        assertPayable(order(null, 1000.0), "1170.00");
    }

    @Test
    void unknownCustomerTypeTreatedAsRegular() {
        assertPayable(order("GOLD", 1000.0), "1170.00");
    }

    // ---------- VIP customer ----------

    @ParameterizedTest(name = "VIP price {0} -> payable {1}")
    @CsvSource({
        "2000.0, 2106.00",  // under 5000: 10%
        "5000.0, 5265.00",  // exactly 5000: still 10% (boundary)
        "6000.0, 5967.00"   // over 5000: 15%
    })
    void vipCustomerDiscount(double price, String expected) {
        assertPayable(order("VIP", price), expected);
    }

    // ---------- STUDENT customer ----------

    @Test
    void studentGets8PercentDiscount() {
        assertPayable(order("STUDENT", 1000.0), "1076.40");
    }

    // ---------- CORPORATE customer ----------

    @Test
    void corporateWithFewItemsGets5Percent() {
        assertPayable(order("CORPORATE", 1000.0), "1111.50");
    }

    @Test
    void corporateWith10LineItemsGets20Percent() {
        Map<String, Object> order = order("CORPORATE", 100.0);
        List<Map<String, Object>> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            items.add(item(100.0, 1));   // 10 line items x 100 = 1000
        }
        order.put("items", items);
        assertPayable(order, "936.00");
    }

    @Test
    void corporateWith9LineItemsStillGets5Percent() {
        Map<String, Object> order = order("CORPORATE", 100.0);
        List<Map<String, Object>> items = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            items.add(item(100.0, 1));   // 9 line items x 100 = 900
        }
        order.put("items", items);
        assertPayable(order, "1000.35");
    }

    // ---------- TAX by country ----------

    @ParameterizedTest(name = "country {0} -> payable {1}")
    @CsvSource({
        "PK, 1170.00",   // 17%
        "US, 1080.00",   //  8%
        "UK, 1200.00",   // 20%
        "FR, 1150.00"    // unknown country: default 15%
    })
    void taxDependsOnCountry(String country, String expected) {
        Map<String, Object> order = order("REGULAR", 1000.0);
        order.put("country", country);
        assertPayable(order, expected);
    }
}