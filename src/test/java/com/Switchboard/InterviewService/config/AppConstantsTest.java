package com.Switchboard.InterviewService.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppConstantsTest {

    @Test
    void pageNumber_ShouldBeZero() {
        // Assert
        assertEquals("0", AppConstants.PAGE_NUMBER);
    }

    @Test
    void pageSize_ShouldBeTen() {
        // Assert
        assertEquals("10", AppConstants.PAGE_SIZE);
    }

    @Test
    void sortBy_ShouldBeUpdatedAt() {
        // Assert
        assertEquals("updatedAt", AppConstants.SORT_BY);
    }

    @Test
    void sortDir_ShouldBeAsc() {
        // Assert
        assertEquals("asc", AppConstants.SORT_DIR);
    }

    @Test
    void pathVariable_ShouldBeInterviewExperience() {
        // Assert
        assertEquals("interview-experience", AppConstants.PATH_VARIABLE);
    }

    @Test
    void appConstants_ShouldBeInstantiable() {
        // Act
        AppConstants constants = new AppConstants();

        // Assert
        assertNotNull(constants);
    }
}
