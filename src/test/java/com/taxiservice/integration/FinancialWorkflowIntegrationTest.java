package com.taxiservice.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Integration test for financial workflow.
 *
 * Tests the following journey:
 * 1. Create payment method
 * 2. Create member
 * 3. Create levy payment
 * 4. Issue fine for late payment
 * 5. Create disciplinary case
 * 6. Issue receipt
 * 7. Check outstanding balance
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class FinancialWorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCompleteFinancialWorkflow() throws Exception {
        // Step 1: Create payment method
        String paymentMethodJson = """
            {
                "methodName": "Cash",
                "description": "Cash payment at office"
            }
            """;

        mockMvc.perform(post("/api/payment-methods")
                .contentType(MediaType.APPLICATION_JSON)
                .content(paymentMethodJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.paymentMethodId", notNullValue()));

        Long paymentMethodId = 1L;

        // Step 2: Create member
        String memberJson = """
            {
                "name": "Test Member",
                "contactNumber": "+27123456789",
                "squadNumber": "SQ-TEST-001",
                "blacklisted": false
            }
            """;

        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(memberJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.assocMemberId", notNullValue()));

        Long memberId = 1L;

        // Step 3: Create levy payment
        String levyPaymentJson = String.format("""
            {
                "assocMemberId": %d,
                "weekStartDate": "2025-11-10",
                "weekEndDate": "2025-11-16",
                "amount": 500.00,
                "paymentStatus": "Pending",
                "paymentMethodId": %d
            }
            """, memberId, paymentMethodId);

        mockMvc.perform(post("/api/levy-payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(levyPaymentJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.levyPaymentId", notNullValue()))
                .andExpect(jsonPath("$.data.amount", is(500.0)));

        // Step 4: Issue fine for late payment
        String fineJson = String.format("""
            {
                "assocMemberId": %d,
                "fineAmount": 50.00,
                "fineReason": "Late payment of weekly levy",
                "fineStatus": "Unpaid"
            }
            """, memberId);

        mockMvc.perform(post("/api/levy-fines")
                .contentType(MediaType.APPLICATION_JSON)
                .content(fineJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fineId", notNullValue()))
                .andExpect(jsonPath("$.data.fineAmount", is(50.0)));

        Long fineId = 1L;

        // Step 5: Member creates disciplinary case
        String disciplinaryJson = String.format("""
            {
                "levyFineId": %d,
                "assocMemberId": %d,
                "caseStatement": "I was unable to pay on time due to unexpected expenses. Requesting payment plan."
            }
            """, fineId, memberId);

        mockMvc.perform(post("/api/disciplinary-workflows")
                .contentType(MediaType.APPLICATION_JSON)
                .content(disciplinaryJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.workflowId", notNullValue()));

        // Step 6: Create receipt (after payment)
        String receiptJson = String.format("""
            {
                "assocMemberId": %d,
                "amount": 500.00,
                "paymentMethodId": %d,
                "receiptNumber": "RCT-TEST-001",
                "issuedBy": "Test Cashier"
            }
            """, memberId, paymentMethodId);

        mockMvc.perform(post("/api/receipts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(receiptJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.receiptId", notNullValue()))
                .andExpect(jsonPath("$.data.receiptNumber", is("RCT-TEST-001")));

        // Step 7: Check outstanding balance
        mockMvc.perform(get("/api/member-finance/" + memberId + "/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.outstandingBalance", notNullValue()));

        // Verify: Get all member receipts
        mockMvc.perform(get("/api/receipts/member/" + memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))));

        // Verify: Get member's fines
        mockMvc.perform(get("/api/levy-fines/member/" + memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))));
    }

    @Test
    public void testOverduePaymentWorkflow() throws Exception {
        // Create member
        String memberJson = """
            {
                "name": "Overdue Member",
                "contactNumber": "+27111111111",
                "squadNumber": "SQ-OVERDUE-001",
                "blacklisted": false
            }
            """;

        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(memberJson))
                .andExpect(status().isOk());

        Long memberId = 1L;

        // Create overdue levy payment
        String levyPaymentJson = String.format("""
            {
                "assocMemberId": %d,
                "weekStartDate": "2025-10-01",
                "weekEndDate": "2025-10-07",
                "amount": 500.00,
                "paymentStatus": "Overdue"
            }
            """, memberId);

        mockMvc.perform(post("/api/levy-payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(levyPaymentJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.paymentStatus", is("Overdue")));

        // Send payment reminder notification
        String notificationJson = String.format("""
            {
                "assocMemberId": %d,
                "message": "Your levy payment is overdue. Please pay immediately to avoid further penalties.",
                "notificationType": "PAYMENT_REMINDER"
            }
            """, memberId);

        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(notificationJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.notificationId", notNullValue()));

        // Verify: Get overdue payments
        mockMvc.perform(get("/api/levy-payments/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))));
    }
}
