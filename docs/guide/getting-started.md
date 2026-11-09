# Installation

## Prerequisites

- A JetBrains IDE with build version >= 213 (IntelliJ IDEA 2021.3 or later)
- Java or Kotlin project

## Installing from JetBrains Marketplace

1. Open your IDE
2. Go to **Preferences / Settings** → **Plugins**
3. Click **Marketplace** tab
4. Search for **"GenerateAllSetter"**
5. Click **Install**
6. Restart the IDE

## Installing Manually

1. Download the latest plugin ZIP from the [GitHub Releases page](https://github.com/gejun123456/intellij-generateAllSetMethod/releases/latest)
2. Go to **Preferences / Settings** → **Plugins**
3. Click the gear icon ⚙️ → **Install Plugin from Disk...**
4. Select the downloaded ZIP file
5. Restart the IDE

## Verifying the Installation

After restart, open any Java file with a class that has setters. Type:

```java
User user = new User();
```

Place the cursor on `User`, press **Alt+Enter** (or **Option+Enter** on macOS), and you should see **Generate all setter** in the intention actions list.

## Updating

The plugin will be updated automatically if you have **Auto-detect plugin updates** enabled in **Settings → Appearance & Behavior → System Settings → Updates**.