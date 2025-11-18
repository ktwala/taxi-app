package com.taxiservice.integration;

import com.taxiservice.dto.member.ApplicationReviewRequest;
import com.taxiservice.dto.member.AssocMemberRequest;
import com.taxiservice.dto.member.MembershipApplicationRequest;
import com.taxiservice.dto.user.RouteRequest;
import com.taxiservice.repository.*;
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
 * Integration test for complete member onboarding workflow.
 *
 * Tests the following journey:
 * 1. Create route
 * 2. Submit membership application
 * 3. Secretary reviews application
 * 4. Chairperson approves application
 * 5. Create association member
 *
 * This simulates a real-world scenario from application to membership.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class MemberOnboardingWorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private MembershipApplicationRepository applicationRepository;

    @Autowired
    private AssocMemberRepository memberRepository;

    @BeforeEach
    public void setUp() {
        // Clean up before each test
        memberRepository.deleteAll();
        applicationRepository.deleteAll();
        routeRepository.deleteAll();
    }

    @Test
    public void testCompleteMemberOnboardingWorkflow() throws Exception {
        // Step 1: Create a route for the applicant
        String routeJson = """
            {
                "name": "Test Route A",
                "startPoint": "Point A",
                "endPoint": "Point B",
                "isActive": true
            }
            """;

        String routeResponse = mockMvc.perform(post("/api/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(routeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.routeId", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract route ID (simplified - in real test, use JSON parser)
        Long routeId = 1L; // Assuming first route

        // Step 2: Submit membership application
        String applicationJson = String.format("""
            {
                "applicantName": "John Applicant",
                "contactNumber": "+27123456789",
                "routeId": %d,
                "applicationStatus": "Pending"
            }
            """, routeId);

        String applicationResponse = mockMvc.perform(post("/api/membership-applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(applicationJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.applicationId", notNullValue()))
                .andExpect(jsonPath("$.data.applicationStatus", is("Pending")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long applicationId = 1L; // Assuming first application

        // Step 3: Secretary reviews and approves application
        String secretaryReviewJson = """
            {
                "decision": "Approved",
                "comments": "All documents verified successfully"
            }
            """;

        mockMvc.perform(put("/api/membership-applications/" + applicationId + "/secretary-review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(secretaryReviewJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.secretaryDecision", is("Approved")));

        // Step 4: Chairperson gives final approval
        String chairpersonReviewJson = """
            {
                "decision": "Approved",
                "comments": "Approved for membership"
            }
            """;

        mockMvc.perform(put("/api/membership-applications/" + applicationId + "/chairperson-review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(chairpersonReviewJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.chairpersonDecision", is("Approved")))
                .andExpect(jsonPath("$.data.applicationStatus", is("Approved")));

        // Step 5: Create association member based on approved application
        String memberJson = """
            {
                "name": "John Applicant",
                "contactNumber": "+27123456789",
                "squadNumber": "SQ-001",
                "blacklisted": false
            }
            """;

        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(memberJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.assocMemberId", notNullValue()))
                .andExpect(jsonPath("$.data.name", is("John Applicant")))
                .andExpect(jsonPath("$.data.blacklisted", is(false)));

        // Verify: Check that member was created
        mockMvc.perform(get("/api/members/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))));
    }

    @Test
    public void testApplicationRejectionWorkflow() throws Exception {
        // Create application
        String applicationJson = """
            {
                "applicantName": "Jane Applicant",
                "contactNumber": "+27987654321",
                "applicationStatus": "Pending"
            }
            """;

        mockMvc.perform(post("/api/membership-applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(applicationJson))
                .andExpect(status().isOk());

        Long applicationId = 1L;

        // Secretary rejects application
        String secretaryReviewJson = """
            {
                "decision": "Rejected",
                "comments": "Incomplete documentation"
            }
            """;

        mockMvc.perform(put("/api/membership-applications/" + applicationId + "/secretary-review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(secretaryReviewJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.secretaryDecision", is("Rejected")))
                .andExpect(jsonPath("$.data.applicationStatus", is("Rejected")));

        // Verify no member was created
        mockMvc.perform(get("/api/members/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }
}
