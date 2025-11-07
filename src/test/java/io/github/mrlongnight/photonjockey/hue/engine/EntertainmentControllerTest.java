package io.github.mrlongnight.photonjockey.hue.engine;

import io.github.mrlongnight.photonjockey.hue.dto.EntertainmentGroupInfo;
import io.github.mrlongnight.photonjockey.hue.dto.EntertainmentLightInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EntertainmentControllerTest {

    private EntertainmentController controller;
    private IFastEffectController mockFastController;
    private EntertainmentGroupInfo testGroup;

    @BeforeEach
    void setUp() {
        controller = new EntertainmentController();
        mockFastController = Mockito.mock(IFastEffectController.class);
        
        List<EntertainmentLightInfo> lights = Arrays.asList(
                new EntertainmentLightInfo("1", "Light 1", "COLOR", new double[]{0.5, 0.5, 0.0}),
                new EntertainmentLightInfo("2", "Light 2", "COLOR", new double[]{-0.5, 0.5, 0.0})
        );
        testGroup = new EntertainmentGroupInfo("group1", "Test Group", lights, "192.168.1.100");
    }

    @Test
    void testInitialStateIsInactive() {
        assertFalse(controller.isEntertainmentModeActive());
        assertNull(controller.getActiveGroup());
    }

    @Test
    void testActivateEntertainmentMode() {
        controller.activateEntertainmentMode(testGroup, mockFastController);

        assertTrue(controller.isEntertainmentModeActive());
        assertEquals(testGroup, controller.getActiveGroup());
        verify(mockFastController, times(1)).startSession();
    }

    @Test
    void testActivateEntertainmentModeWithNullGroupThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            controller.activateEntertainmentMode(null, mockFastController);
        });
    }

    @Test
    void testActivateEntertainmentModeWithNullControllerThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            controller.activateEntertainmentMode(testGroup, null);
        });
    }

    @Test
    void testActivateEntertainmentModeWhenAlreadyActiveThrowsException() {
        controller.activateEntertainmentMode(testGroup, mockFastController);

        assertThrows(IllegalStateException.class, () -> {
            controller.activateEntertainmentMode(testGroup, mockFastController);
        });
    }

    @Test
    void testDeactivateEntertainmentMode() {
        controller.activateEntertainmentMode(testGroup, mockFastController);
        controller.deactivateEntertainmentMode();

        assertFalse(controller.isEntertainmentModeActive());
        assertNull(controller.getActiveGroup());
        verify(mockFastController, times(1)).stopSession();
    }

    @Test
    void testDeactivateWhenNotActiveDoesNothing() {
        // Should not throw exception
        controller.deactivateEntertainmentMode();
        assertFalse(controller.isEntertainmentModeActive());
    }

    @Test
    void testIsLightInEntertainmentMode() {
        assertFalse(controller.isLightInEntertainmentMode("1"));
        
        controller.activateEntertainmentMode(testGroup, mockFastController);
        
        assertTrue(controller.isLightInEntertainmentMode("1"));
        assertTrue(controller.isLightInEntertainmentMode("2"));
        assertFalse(controller.isLightInEntertainmentMode("3"));
    }

    @Test
    void testIsLightInEntertainmentModeAfterDeactivation() {
        controller.activateEntertainmentMode(testGroup, mockFastController);
        assertTrue(controller.isLightInEntertainmentMode("1"));
        
        controller.deactivateEntertainmentMode();
        
        assertFalse(controller.isLightInEntertainmentMode("1"));
        assertFalse(controller.isLightInEntertainmentMode("2"));
    }

    @Test
    void testSendFrameWhenActive() {
        controller.activateEntertainmentMode(testGroup, mockFastController);
        
        EffectFrame frame = new EffectFrame(
                Arrays.asList(new LightUpdateDTO("1", 254, 0.5, 1.0, 0)),
                System.currentTimeMillis()
        );
        
        controller.sendFrame(frame);
        
        verify(mockFastController, times(1)).sendFrame(frame);
    }

    @Test
    void testSendFrameWhenNotActiveThrowsException() {
        EffectFrame frame = new EffectFrame(
                Arrays.asList(new LightUpdateDTO("1", 254, 0.5, 1.0, 0)),
                System.currentTimeMillis()
        );
        
        assertThrows(IllegalStateException.class, () -> {
            controller.sendFrame(frame);
        });
    }

    @Test
    void testGetEntertainmentLightIds() {
        assertTrue(controller.getEntertainmentLightIds().isEmpty());
        
        controller.activateEntertainmentMode(testGroup, mockFastController);
        
        assertEquals(2, controller.getEntertainmentLightIds().size());
        assertTrue(controller.getEntertainmentLightIds().contains("1"));
        assertTrue(controller.getEntertainmentLightIds().contains("2"));
    }

    @Test
    void testGetEntertainmentLightIdsReturnsUnmodifiableSet() {
        controller.activateEntertainmentMode(testGroup, mockFastController);
        
        var lightIds = controller.getEntertainmentLightIds();
        
        // Modifying returned set should not affect internal state
        lightIds.clear();
        
        assertEquals(2, controller.getEntertainmentLightIds().size());
    }
}
