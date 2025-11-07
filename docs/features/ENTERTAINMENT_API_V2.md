# Entertainment API V2 Integration

## Overview

PhotonJockey now supports Philips Hue Entertainment API V2 for high-performance light effects using DTLS/UDP communication. This provides significantly faster updates compared to the traditional HTTPS API, enabling more responsive and synchronized light shows.

## Features

### 1. Entertainment Group Selection
- Query and display all available entertainment groups from connected Hue bridges
- View detailed information about entertainment groups including:
  - Group name and ID
  - List of lights in the group
  - Light types and positions
  - Bridge IP address

### 2. Entertainment Mode Control
- **Activation/Deactivation**: Toggle entertainment mode on/off via UI button or checkbox
- **Status Indication**: Visual feedback showing when entertainment mode is active
- **Group Selection**: Choose which entertainment group to use for fast effects

### 3. Dual Effect System

#### Slow Effects (HTTPS)
- Traditional effects using the Hue API over HTTPS
- Suitable for ambient lighting and slower transitions
- Configurable parameters:
  - `BRIGHTNESS_MIN`: Minimum brightness (0-254)
  - `BRIGHTNESS_MAX`: Maximum brightness (0-254)
  - `HUE_MAX_FADE_TIME`: Maximum fade time in 100ms steps

#### Fast Effects (UDP/DTLS)
- High-performance effects using Entertainment API V2
- Significantly lower latency for beat-synchronized effects
- Configurable parameters:
  - `HUE_FAST_EFFECT_PORT`: UDP port (default: 2100)
  - `HUE_FAST_EFFECT_BRIGHTNESS_MIN`: Minimum brightness for fast effects
  - `HUE_FAST_EFFECT_BRIGHTNESS_MAX`: Maximum brightness for fast effects
  - `HUE_FAST_EFFECT_TRANSITION_TIME`: Transition time in 100ms steps

### 4. Light Selection Protection
When entertainment mode is active:
- Lights in the active entertainment group are **disabled** for slow effect selection
- UI clearly indicates which lights are in entertainment mode
- Prevents conflicts between slow HTTPS and fast UDP updates
- Once entertainment mode is deactivated, all lights become available again

## Configuration

### Config Nodes

New configuration nodes have been added:

```java
HUE_ENTERTAINMENT_GROUP         // Selected entertainment group name
HUE_ENTERTAINMENT_MODE_ENABLED  // Whether entertainment mode is active
HUE_FAST_EFFECT_PORT           // UDP port for entertainment API (default: 2100)
HUE_FAST_EFFECT_BRIGHTNESS_MIN // Min brightness for fast effects
HUE_FAST_EFFECT_BRIGHTNESS_MAX // Max brightness for fast effects
HUE_FAST_EFFECT_TRANSITION_TIME // Transition time for fast effects
```

## UI Integration

### Light Controller Dashboard

The Light Controller Dashboard has been updated with:

1. **Entertainment Group ComboBox**: Select from available entertainment groups
2. **Entertainment Mode CheckBox**: Shows current mode status (disabled for user interaction)
3. **Toggle Button**: Activate/Deactivate entertainment mode
4. **Light Selection**: Automatically disables lights that are in entertainment mode

### Usage Flow

1. Connect to your Hue Bridge
2. Select an entertainment group from the dropdown
3. Click "Activate Entertainment Mode"
4. Lights in the entertainment group will be marked as unavailable for slow effects
5. Fast effects will now be sent via UDP for these lights
6. Click "Deactivate Entertainment Mode" to return to normal operation

## Architecture

### Components

#### EntertainmentController
Manages the entertainment mode state and coordinates between the UI and effect controllers.

**Key Methods:**
- `activateEntertainmentMode(group, controller)`: Start entertainment mode
- `deactivateEntertainmentMode()`: Stop entertainment mode
- `isLightInEntertainmentMode(lightId)`: Check if a light is in entertainment mode
- `sendFrame(frame)`: Send effect frame to active entertainment group

#### EntertainmentGroupInfo
DTO containing entertainment group metadata:
- Group ID and name
- List of lights in the group
- Bridge IP address

#### EntertainmentLightInfo
DTO containing individual light information:
- Light ID, name, and type
- 3D position in entertainment area (when available)

#### FastEffectController
Handles UDP communication for Entertainment API V2:
- Starts/stops UDP sessions
- Sends effect frames with sequence numbers
- Tracks packet statistics and loss

### Effect Routing

Effects are routed based on entertainment mode state:

```
┌─────────────────┐
│  Beat Detector  │
└────────┬────────┘
         │
         ▼
┌─────────────────────────┐
│  Effect Generator       │
└────────┬────────────────┘
         │
         ├──────────────────────────┐
         ▼                          ▼
┌─────────────────┐      ┌──────────────────┐
│  Slow Effects   │      │  Fast Effects    │
│  (HTTPS API)    │      │  (UDP/DTLS)      │
└─────────────────┘      └──────────────────┘
         │                          │
         ▼                          ▼
┌─────────────────┐      ┌──────────────────┐
│ Non-Ent. Lights │      │ Ent. Group Lights│
└─────────────────┘      └──────────────────┘
```

## Testing

Comprehensive unit tests have been added:

- `EntertainmentGroupInfoTest`: Tests for entertainment group DTO
- `EntertainmentLightInfoTest`: Tests for light information DTO
- `EntertainmentControllerTest`: Tests for entertainment mode controller

Run tests with:
```bash
./gradlew test --tests "*Entertainment*"
```

## Performance Considerations

- **UDP Packet Size**: Limited to 1400 bytes for network compatibility
- **Sequence Numbers**: Each frame includes a sequence number for ordering
- **Latency**: UDP communication has significantly lower latency than HTTPS
- **Update Rate**: Entertainment API supports up to 60 updates per second

## Troubleshooting

### Entertainment Group Not Showing
- Ensure your Hue lights are assigned to an entertainment group in the Hue app
- Check that the bridge connection is active
- Verify you're using a compatible Hue Bridge (V2 or newer)

### Entertainment Mode Fails to Activate
- Check that the UDP port (default 2100) is not blocked by firewall
- Verify the bridge IP is correct
- Ensure lights in the group are powered on and reachable

### Lights Still Responding to Slow Effects
- Verify entertainment mode is actually active (check the checkbox)
- Confirm the lights are part of the selected entertainment group
- Try refreshing the light list

## Future Enhancements

Potential future improvements:
- Support for multiple simultaneous entertainment groups
- Advanced choreography with position-based effects
- Recording and playback of light sequences
- Integration with music visualization patterns
- Custom entertainment area configuration

## References

- [Philips Hue Entertainment API Documentation](https://developers.meethue.com/develop/hue-entertainment/)
- [DTLS Protocol Specification](https://tools.ietf.org/html/rfc6347)
- [Yet Another Hue API Library](https://github.com/ZeroOne3010/yetanotherhueapi)
