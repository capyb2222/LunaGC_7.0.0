# LunaGC-6.5.0 WIP

## Note from the maintainer
Might update to latest occasionally, depends on how I'm feeling and my situation. Of course, I post the protocol buffer definitions on [GitLab](https://gitlab.com/kitkat-multiverse/genshin-protocol) and translations. Contact me at my [Discord](https://discord.gg/5Rfyjrt5aB)

## Updated version of Grasscutters, with some new features implemented.
Old Discord for LunaGC https://discord.gg/7D5gkyJR5Y (don't ask for support there as it's been taken over by other people (...), instead create an issue in this repository)

Features and functionality of the ps is not guaranteed, try it yourself to see what works and what doesnt.
This is possibly the only public PS with updated mob and gadget spawns! (Up to Version 5.4)

Contribute if you want/can...

# Read the [handbook](handbook.md)!

# Setup Guide
- Read it below, its just enough to get the server up and running along with the client.

## Main Requirements

- Get [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- Get [MongoDB Community Server](https://www.mongodb.com/try/download/community)
- Get [NodeJS](https://nodejs.org/dist/v20.15.0/node-v20.15.0-x64.msi) (For handbook generation)
- Get game version REL6.5.0
- Make sure to install java and set the environment variables.
- Build the server (refer to "Compile the actual server" in this guide.)
- Download the [Resources](https://github.com/kitkat033/LunaGC-Resources), make a new folder called `resources` in the downloaded LunaGC folder and then extract the resources in that new folder.
- Set useEncryption, Questing and useInRouting to false (it should be false by default, if not then change it)
- [Patch the game](#patching-the-game)
- Start the server and the game, make sure to also create an account in the LunaGC console!
- Have fun (or don't)

### Patching the game
- Install [**Rust**](https://rust-lang.org/learn/get-started/) and **Cargo** (comes with rustup)
- Go to the `patch/` folder (make sure you have cloned this repository with the `--recurse-submodules` flag)
- Run `cargo build --release` to build the DLL at `target/release`
- Inject the DLL into the game. You can do this by renaming the patch to `Astrolabe.dll` and putting it in the game folder at `GenshinImpact_Data/Plugins`. Make sure you back up the old `Astrolabe.dll` in the plugins folder.

### Getting started

- Clone the repository (install [Git](https://git-scm.com) first )

  ```
  git clone --recurse-submodules https://github.com/kitkat033/LunaGC.git
  ```

- Now you can continue with the steps below.


### Compile the actual Server

**Requirements**:

[Java Development Kit 17 | JDK](https://oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or higher

- **Sidenote**: Handbook generation may fail on some systems. To disable handbook generation, append `-PskipHandbook=1` to the `gradlew jar` command.

- **For Windows**:

  ```shell
  .\gradlew.bat
  .\gradlew.bat jar
  ```

- **For Linux**:

  ```bash
  chmod +x gradlew
  ./gradlew
  ./gradlew jar
  ```

### You can find the output JAR in the project root folder.

### Manually compile the handbook

```shell
./gradlew generateHandbook
```

## Troubleshooting

- Make sure to set useEncryption and useInRouting both to false otherwise you might encounter errors.
- To use windy make sure that you put your luac files in C:\Windy (make the folder if it doesnt exist)
- If you get an error related to MongoDB connection timeout, check if the mongodb service is running. On windows: Press windows key and r then type `services.msc`, look for mongodb server and if it's not started then start it by right clicking on it and start. On linux, you can do `systemctl status mongod` to see if it's running, if it isn't then type `systemctl start mongod`. However, if you get error 14 on linux change the owner of the mongodb folder and the .sock file (`sudo chown -R mongodb:mongodb /var/lib/mongodb` and `sudo chown mongodb:mongodb /tmp/mongodb-27017.sock` then try to start the service again.)

## Credit

proto Repository [hk4e-protos](https://gitlab.com/kitkat-multiverse/genshin-protocol)

patch Repository [hk4e-patch-universal](https://github.com/kitkat033/hk4e-patch-universal)
