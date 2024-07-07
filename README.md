![presentazione deck check - GitHub](https://github.com/AntoKay/DeckCheck/assets/34346522/b18b42c1-af9b-4019-9eda-810387dfd709)


A simple Android app that, given a Steam game ID, retrieves the Steam Deck compatibility status.

Since the Steam web API don't give any information about the Steam Deck compatibility, this app implements the Jsoup library (https://jsoup.org) to web scrape the Steam store page of the game to retrieve its compatibility status with Deck, using coroutines when needed (https://developer.android.com/kotlin/coroutines?hl=it).

The app is built with Android 9.0 (API 28) SDK and the UI is optimized for devices with 1080x1920 screen resolution (like Google Pixel 2) in portrait mode.


Here are some game IDs for testing:

VERIFIED: 220 (Half-Life 2), 620 (Portal 2), 1888930 (The Last of Us: Part I)

PLAYABLE: 1222700 (A Way Out), 2395210 (Tony Hawk's Pro Skater 1+2), 1172470 (Apex Legends)

NOT SUPPORTED: 1774580 (Star Wars Jedi Survivor), 552440 (The Talos Principle VR), 1225560 (Unravel)

UNKNOWN: 2556990 (Beyond Good & Evil 20th Anniversary Edition), 2172010 (Until Dawn), 1687960 (The Wreck)
