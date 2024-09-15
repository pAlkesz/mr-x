This is a Kotlin Multiplatform project targeting Android, iOS.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
    - `commonMain` is for code that’s common for all targets.
    - Other folders are for Kotlin code that will be compiled for only the platform indicated in the
      folder name.
      For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
      `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for
  your project.

Learn more
about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…

The project implements the game "Mr. X".
Rules:
    - A player (Mr. X) thinks of a name of a well-known person (e.g., Bruce Willis), thus becoming 
"Mr. W".
    - Other players ask questions regarding another famous person, for example: "Are you a 
writer?" (The players must have an answer to their question.)
    - Mr. W then tries to answer the question with a name that fits the criteria. (Being a 
writer and last name starts with W.) Something like: "No, I am not Oscar Wilde!".
    - If the player, who asked that question finds this answer fitting, than he or she can 
specify his or her question: "Are you a living writer?". From this point, the questions and 
answers go to the point when Mr. W finds out who the original person was (the person the player 
thought of), or not, and gives up.
    - In case the host can not answer a question asked by a player, other players have a chance 
to answer that question too. If they succeed, they get to ask a barkochba question from Mr. X, 
which he or she has to answer.
    - The game ends when a player guesses the name of Mr. W.

Project setup:
Setup Android Studio according to the official documentation: [Create a Compose Multiplatform 
app — tutorial](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-getting-started.html)
Build and launch the application on a simulator or physical device