# Configuration

You can customize the plugin's behavior through the settings panel.

## Accessing Settings

Go to **Preferences / Settings** → **Tools** → **GenerateAllSetter**.

or search for "GenerateAllSetter" in the Settings dialog.

## Configuration Options

### Default Value Templates

You can configure custom default values for different types. See the [Template Customization](#template-customization) section.

### Guava Code Generation

When enabled, the plugin will use Guava collection types (e.g., `Lists.newArrayList()`) instead of standard Java collection constructors.

This option can be toggled in the settings panel under **Guava Support**.

### Test Engine Auto-Detection

The plugin automatically detects which testing framework your project uses for the assert getter feature:

- AssertJ
- JUnit 4
- JUnit 5
- TestNG
- JDK assert

You can see which engine is detected in the settings panel.

## Template Customization

The plugin supports custom templates via Velocity templates. You can define templates for generating code in specific patterns.

### Using Templates

1. Go to **Settings → Tools → GenerateAllSetter**
2. Create or modify templates in the template editor
3. Use the **Generate by template** intention action when cursor is on a variable

### Template Variables

| Variable | Description |
|----------|-------------|
| `$className` | The class name of the variable |
| `$fields` | List of fields with their names and types |
| `$variableName` | The name of the variable |