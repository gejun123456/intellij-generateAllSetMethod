# Complex Return Types

Generate default return values for methods with complex return types: List, Set, Map, and Optional.

## Usage

Place your cursor inside a method body, press **Alt+Enter** (or **Option+Enter** on macOS) and select **Generate all setter**.

### List Return Type

```java
public List<User> getUsers() {
    // cursor here
}
```

After generation:

```java
public List<User> getUsers() {
    return new ArrayList<>();
}
```

### Set Return Type

```java
public Set<String> getNames() {
    // cursor here
}
```

After generation:

```java
public Set<String> getNames() {
    return new HashSet<>();
}
```

### Map Return Type

```java
public Map<String, User> getUserMap() {
    // cursor here
}
```

After generation:

```java
public Map<String, User> getUserMap() {
    return new HashMap<>();
}
```

### Optional Return Type

```java
public Optional<User> findUser(String id) {
    // cursor here
}
```

After generation:

```java
public Optional<User> findUser(String id) {
    return Optional.empty();
}
```

## Demo

![Complex Return Types](/screenshot/generate_list_default_value.gif)

## Tips

- The plugin automatically inserts the correct generic type parameters
- Imports are added automatically
- Works with any depth of generics (e.g., `Map<String, List<User>>`)