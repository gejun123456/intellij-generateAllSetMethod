# Generate Converter

Generate a converter between two classes that have matching fields.

## Usage

Place your cursor inside a method, press **Alt+Enter** (or **Option+Enter** on macOS) and select **Generate setter getter from function**.

### Before

```java
public UserDto convert(User user) {
    // cursor here
}
```

### After

```java
public UserDto convert(User user) {
    UserDto userDto = new UserDto();
    userDto.setName(user.getName());
    userDto.setEmail(user.getEmail());
    userDto.setAge(user.getAge());
    return userDto;
}
```

## Demo

![Generate Converter](/screenshot/generate_the_conveter.gif)

## How It Works

1. The plugin detects the method parameter and return type
2. It matches fields between the two classes by name
3. It generates setter calls on the result object, pulling values from the source object's getters
4. Matching is case-insensitive and supports common field naming patterns

## Tips

- Works best when the source and target classes have similarly named fields
- You can have multiple method parameters — the plugin will find the best match
- After generation, you can refine the mapping manually for any fields that didn't match automatically