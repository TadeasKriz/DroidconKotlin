# Minimal iOS Dev Instructions

The absolute bare minimum install instructions, for an iOS dev who maybe hasn't done any Android.

1. Download and install [Android SDK](https://developer.android.com/studio#downloads) (Command line tools only)

2. Unzip the contents of `commandlinetools-mac.zip`.

3. In terminal run the following commands:
```zsh
cd ~/Downloads/cmdline-tools
./bin/sdkmanager "platform-tools" "platforms;android-30" "tools" --sdk_root="$HOME/Library/Android/sdk"
```

4. Add these environment properties into your `~/.zshrc`
```zsh
export ANDROID_HOME=~/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
```

5. Finally run
```zsh
source ~/.zshrc
```

7. Download IntelliJ Community: https://www.jetbrains.com/idea/download/#section=mac

8. Install [Cocoapods](https://cocoapods.org/)

6.
```
git clone https://github.com/touchlab/DroidconKotlin
```

7. Navigate to "ios/Droidcon/" and run
```
pod install
open Droidcon.xcworkspace
```

9. Run the app!
