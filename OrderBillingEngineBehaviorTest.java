package com.uet.legacy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
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
import org.junit.jupiter.params.provider.ValueSource;

class OrderBillingEngineBehaviorTest {

    @TempDir
    Path tempDir;

    private OrderBillingEngine engine;
    private Path logFile;
    private ByteArrayOutputStream out;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        engine = new OrderBillingEngine();
        logFile = tempDir.resolve("test.log");
        engine.setLogFilePath(logFile.toString());

        originalOut = System.out;
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    // ---------- helpers ----------

    private Map<String, Object> item(Object price, Object quantity) {
        Map<String, Object> item = new HashMap<>();
        item.put("price", price);
        item.put("quantity", quantity);
        return item;
    }

    /** Valid order: one item of 1000.0, credit card, PK tax, EMAIL notification (defaults). */
    private Map<String, Object> validOrder() {
        Map<String, Object> order = new HashMap<>();
        order.put("customerEmail", "ali@example.com");
        order.put("items", new ArrayList<>(List.of(item(1000.0, 1))));
        order.put("paymentMethod", "CREDIT_CARD");
        order.put("cardNumber", "4111111111111111");
        return order;
    }

    private Map<String, Object> orderWithItem(Map<String, Object> item) {
        Map<String, Object> order = validOrder();
        order.put("items", new ArrayList<>(List.of(item)));
        return order;
    }

    private String console() {
        return out.toString();
    }

    private void assertRejected(Map<String, Object> order, String expectedMessage) {
        assertFalse(engine.processOrder(order), "Order should be rejected");
        assertTrue(console().contains(expectedMessage),
                "Expected message '" + expectedMessage + "' but console was:\n" + console());
        assertFalse(Files.exists(logFile), "Rejected orders must not write a log file");
    }

    // ================= INPUT VALIDATION =================

    @Test
    void nullOrderIsRejected() {
        assertRejected(null, "Error: Order data is empty!");
    }

    @Test
    void emptyOrderIsRejected() {
        assertRejected(new HashMap<>(), "Error: Order data is empty!");
    }

    @Test
    void missingEmailIsRejected() {
        Map<String, Object> order = validOrder();
        order.remove("customerEmail");
        assertRejected(order, "Error: Invalid customer email!");
    }

    @Test
    void emailWithoutAtSignIsRejected() {
        Map<String, Object> order = validOrder();
        order.put("customerEmail", "aliexample.com");
        assertRejected(order, "Error: Invalid customer email!");
    }

    @Test
    void missingItemsIsRejected() {
        Map<String, Object> order = validOrder();
        order.remove("items");
        assertRejected(order, "Error: Cart has no items!");
    }

    @Test
    void emptyCartIsRejected() {
        Map<String, Object> order = validOrder();
        order.put("items", new ArrayList<Map<String, Object>>());
        assertRejected(order, "Error: Cart has no items!");
    }

    @Test
    void itemWithoutPriceIsRejected() {
        Map<String, Object> item = new HashMap<>();
        item.put("quantity", 1);
        assertRejected(orderWithItem(item), "Error: Missing item price or quantity!");
    }

    @Test
    void itemWithoutQuantityIsRejected() {
        Map<String, Object> item = new HashMap<>();
        item.put("price", 100.0);
        assertRejected(orderWithItem(item), "Error: Missing item price or quantity!");
    }

    @Test
    void zeroPriceIsRejected() {
        assertRejected(orderWithItem(item(0.0, 1)), "Error: Invalid price or quantity for item!");
    }

    @Test
    void negativePriceIsRejected() {
        assertRejected(orderWithItem(item(-50.0, 1)), "Error: Invalid price or quantity for item!");
    }

    @Test
    void zeroQuantityIsRejected() {
        assertRejected(orderWithItem(item(100.0, 0)), "Error: Invalid price or quantity for item!");
    }

    @Test
    void negativeQuantityIsRejected() {
        assertRejected(orderWithItem(item(100.0, -2)), "Error: Invalid price or quantity for item!");
    }

    @Test
    void oneBadItemRejectsWholeOrder() {
        Map<String, Object> order = validOrder();
        order.put("items", new ArrayList<>(List.of(item(100.0, 1), item(-1.0, 1))));
        assertRejected(order, "Error: Invalid price or quantity for item!");
    }

    // Legacy quirk: unsafe casts crash instead of returning false.
    @Test
    void integerPriceCausesClassCastException() {
        assertThrows(ClassCastException.class,
                () -> engine.processOrder(orderWithItem(item(100, 1))));
    }

    @Test
    void doubleQuantityCausesClassCastException() {
        assertThrows(ClassCastException.class,
                () -> engine.processOrder(orderWithItem(item(100.0, 2.0))));
    }

    // ================= CREDIT CARD =================

    @Test
    void validCreditCardIsAccepted() {
        assertTrue(engine.processOrder(validOrder()));
        assertTrue(console().contains("Processing credit card payment of PKR"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"411111111111111", "41111111111111111", "411111111111111a", "abcdefghijklmnop"})
    void invalidCardNumbersAreDeclined(String card) {
        Map<String, Object> order = validOrder();
        order.put("cardNumber", card);
        assertRejected(order, "Card declined: Must be 16 digits.");
    }

    @Test
    void missingCardNumberIsDeclined() {
        Map<String, Object> order = validOrder();
        order.remove("cardNumber");
        assertRejected(order, "Card declined: Must be 16 digits.");
    }

    // ================= EASYPAISA =================

    private Map<String, Object> easyPaisaOrder(String mobile) {
        Map<String, Object> order = validOrder();
        order.put("paymentMethod", "EASYPAISA");
        order.remove("cardNumber");
        if (mobile != null) {
            order.put("mobileNumber", mobile);
        }
        return order;
    }

    @Test
    void validEasyPaisaIsAccepted() {
        assertTrue(engine.processOrder(easyPaisaOrder("03001234567")));
        assertTrue(console().contains("Sending EasyPaisa OTP push to 03001234567 for PKR"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0300123456", "030012345678", "04001234567", "+9230012345"})
    void invalidEasyPaisaNumbersAreRejected(String mobile) {
        assertRejected(easyPaisaOrder(mobile), "EasyPaisa failed: Invalid mobile number.");
    }

    @Test
    void missingEasyPaisaNumberIsRejected() {
        assertRejected(easyPaisaOrder(null), "EasyPaisa failed: Invalid mobile number.");
    }

    // ================= CASH ON DELIVERY =================

    private Map<String, Object> codOrder(double price) {
        Map<String, Object> order = validOrder();
        order.put("paymentMethod", "CASH_ON_DELIVERY");
        order.remove("cardNumber");
        order.put("items", new ArrayList<>(List.of(item(price, 1))));
        return order;
    }

    @Test
    void codUnderLimitIsAccepted() {
        // 21000 -> regular discount 500 -> net 20500 -> +17% = 23985 (<= 25000)
        assertTrue(engine.processOrder(codOrder(21000.0)));
    }

    @Test
    void codOverLimitIsRejected() {
        // 22000 -> net 21500 -> +17% = 25155 (> 25000)
        assertRejected(codOrder(22000.0),
                "Cash on delivery not allowed for orders above PKR 25,000.");
    }

    // ================= UNSUPPORTED PAYMENT =================

    @Test
    void unsupportedPaymentMethodIsRejected() {
        Map<String, Object> order = validOrder();
        order.put("paymentMethod", "BITCOIN");
        assertRejected(order, "Error: Unsupported payment method: BITCOIN");
    }

    @Test
    void missingPaymentMethodDefaultsToUnsupportedCash() {
        Map<String, Object> order = validOrder();
        order.remove("paymentMethod");
        assertRejected(order, "Error: Unsupported payment method: CASH");
    }

    // ================= NOTIFICATIONS =================

    @Test
    void defaultNotificationIsEmail() {
        assertTrue(engine.processOrder(validOrder()));
        assertTrue(console().contains(
                "[EMAIL DISPATCHED] To: ali@example.com | Your order of PKR 1170.00 is confirmed!"));
    }

    @Test
    void smsNotificationUsesDefaultPhone() {
        Map<String, Object> order = validOrder();
        order.put("notifyChannel", "SMS");
        assertTrue(engine.processOrder(order));
        assertTrue(console().contains(
                "[SMS DISPATCHED] To: 0300-0000000 | Order confirmed! Paid: PKR 1170.00"));
        assertFalse(console().contains("[EMAIL DISPATCHED]"));
    }

    @Test
    void smsNotificationUsesGivenPhone() {
        Map<String, Object> order = validOrder();
        order.put("notifyChannel", "SMS");
        order.put("phone", "0345-1112223");
        assertTrue(engine.processOrder(order));
        assertTrue(console().contains("[SMS DISPATCHED] To: 0345-1112223"));
    }

    @Test
    void unknownNotificationChannelSendsNothingButSucceeds() {
        Map<String, Object> order = validOrder();
        order.put("notifyChannel", "WHATSAPP");
        assertTrue(engine.processOrder(order));
        assertFalse(console().contains("DISPATCHED"));
    }

    // ================= LOG FILE =================

    @Test
    void successfulPaidOrderIsLogged() throws IOException {
        assertTrue(engine.processOrder(validOrder()));
        String log = Files.readString(logFile);
        assertTrue(log.contains("Order for: ali@example.com"));
        assertTrue(log.contains("Payable: PKR "));
        assertTrue(log.contains("Status: PAID"));
        assertTrue(log.endsWith("\n"));
    }

    @Test
    void codOrderIsLoggedAsPendingCash() throws IOException {
        assertTrue(engine.processOrder(codOrder(1000.0)));
        assertTrue(Files.readString(logFile).contains("Status: PENDING_CASH"));
    }

    @Test
    void logFileIsAppendedNotOverwritten() throws IOException {
        assertTrue(engine.processOrder(validOrder()));
        assertTrue(engine.processOrder(validOrder()));
        List<String> lines = Files.readAllLines(logFile);
        assertEquals(2, lines.size());
    }

    @Test
    void logWriteFailureDoesNotFailTheOrder() {
        engine.setLogFilePath(tempDir.resolve("no_such_folder/x.log").toString());
        assertTrue(engine.processOrder(validOrder()));
        assertTrue(console().contains("Failed to write order log:"));
    }

    @Test
    void logFilePathGetterAndSetterWork() {
        engine.setLogFilePath("custom.log");
        assertEquals("custom.log", engine.getLogFilePath());
    }
}