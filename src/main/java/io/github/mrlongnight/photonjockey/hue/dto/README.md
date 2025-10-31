# Hue DTOs (Data Transfer Objects)

This package contains Data Transfer Objects for the Philips Hue lighting system integration (TG3.2).

## DTOs

### XyColor
Represents a color in XY color space with brightness, compatible with Philips Hue's color model.
- `x`: X coordinate in CIE color space (0.0 to 1.0)
- `y`: Y coordinate in CIE color space (0.0 to 1.0)
- `brightness`: Brightness value (0 to 254)

### LightUpdate
Represents an update command for a single light, used for HTTP-based light control.
- `lightId`: Unique identifier of the light
- `color`: XyColor to set
- `transitionTime`: Optional transition time in milliseconds

### EffectFrame
Represents a frame of lighting effects for multiple lights, used for UDP-based fast entertainment mode.
- `updates`: List of LightUpdate objects
- `timestamp`: Frame timestamp in milliseconds
- `sequenceNumber`: Sequential frame number for UDP transmission

### LightMapConfig
Root configuration object for light mapping containing bridges and lights configuration.
- `bridges`: List of BridgeConfig objects
- `lights`: List of LightConfig objects

### BridgeConfig
Configuration for a Philips Hue bridge.
- `id`: Unique identifier
- `ip`: IP address
- `username`: Optional API username

### LightConfig
Configuration for a light with spatial and control information.
- `id`: Unique identifier
- `x`, `y`: 2D spatial coordinates
- `bridgeId`: Reference to bridge ID
- `controlType`: "FAST_UDP" or "LOW_HTTP"
- `name`: Optional human-readable name

## JSON Schema

The JSON schema for light mapping is located at `schemas/lightmap.schema.json`.
See `config/lightmap.json` for an example configuration.

## Usage

```java
// Create a color
XyColor color = new XyColor(0.5, 0.5, 127);

// Create a light update
LightUpdate update = new LightUpdate("light-1", color, 400);

// Create an effect frame
List<LightUpdate> updates = Arrays.asList(update);
EffectFrame frame = new EffectFrame(updates, System.currentTimeMillis(), 0);

// Load/save configuration
LightMapConfig config = JsonUtils.fromJsonFile("config/lightmap.json", LightMapConfig.class);
JsonUtils.toJsonFile(config, "config/lightmap.json");
```
