package emu.grasscutter.config;

import ch.qos.logback.classic.Level;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.utils.*;
import lombok.NoArgsConstructor;

import java.util.*;

import static emu.grasscutter.Grasscutter.*;

/**
 * *when your JVM fails*
 */
public class ConfigContainer {
    /*
     * Configuration changes:
     * Version  5 - 'questing' has been changed from a boolean
     *              to a container of options ('questOptions').
     *              This field will be removed in future versions.
     * Version  6 - 'questing' has been fully replaced with 'questOptions'.
     *              The field for 'legacyResources' has been removed.
     * Version  7 - 'regionKey' is being added for authentication
     *              with the new dispatch server.
     * Version  8 - 'server' is being added for enforcing handbook server
     *              addresses.
     * Version  9 - 'limits' was added for handbook requests.
     * Version 10 - 'trialCostumes' was added for enabling costumes
     *              on trial avatars.
     * Version 11 - 'server.fastRequire' was added for disabling the new
     *              Lua script require system if performance is a concern.
     * Version 12 - 'http.startImmediately' was added to control whether the
     *              HTTP server should start immediately.
     * Version 13 - 'game.useUniquePacketKey' was added to control whether the
     *              encryption key used for packets is a constant or randomly generated.
     */
    private static int version() {
        return 13;
    }

    /**
     * Attempts to update the server's existing configuration.
     */
    public static void updateConfig() {
        try { // Check if the server is using a legacy config.
            var configObject = JsonUtils.loadToClass(Grasscutter.configFile.toPath(), JsonObject.class);
            if (!configObject.has("version")) {
                Grasscutter.getLogger().info("Updating legacy config...");
                Grasscutter.saveConfig(null);
            }
        } catch (Exception ignored) { }

        var existing = config.version;
        var latest = version();

        if (existing == latest)
            return;

        // Create a new configuration instance.
        var updated = new ConfigContainer();
        // Update all configuration fields.
        var fields = ConfigContainer.class.getDeclaredFields();
        Arrays.stream(fields).forEach(field -> {
            try {
                field.set(updated, field.get(config));
            } catch (Exception exception) {
                Grasscutter.getLogger().error("Failed to update a configuration field.", exception);
            }
        }); updated.version = version();

        try { // Save configuration and reload.
            Grasscutter.saveConfig(updated);
            Grasscutter.loadConfig();
        } catch (Exception exception) {
            Grasscutter.getLogger().warn("Failed to save the updated configuration.", exception);
        }
    }

    public Structure folderStructure = new Structure();
    public Database databaseInfo = new Database();
    public Language language = new Language();
    public Account account = new Account();
    public Server server = new Server();

    // DO NOT. TOUCH. THE VERSION NUMBER.
    public int version = version();

    /* Option containers. */

    public static class Database {
        public DataStore server = new DataStore();
        public DataStore game = new DataStore();

        public static class DataStore {
            public String connectionUri = "mongodb://localhost:27017";
            public String collection = "grasscutter";
        }
    }

    public static class Structure {
        public String resources = "./resources/";
        public String data = "./data/";
        public String packets = "./packets/";
        public String scripts = "resources:Scripts/";
        public String plugins = "./plugins/";
        public String cache = "./cache/";

        // UNUSED (potentially added later?)
        // public String dumps = "./dumps/";
    }

    public static class Server {
        public Set<Integer> debugWhitelist = Set.of();
        public Set<Integer> debugBlacklist = Set.of();
        public ServerRunMode runMode = ServerRunMode.HYBRID;
        public boolean logCommands = false;

        /**
         * If enabled, the 'require' Lua function will load the script's compiled varient into the context. (faster; doesn't work as well)
         * If disabled, all 'require' calls will be replaced with the referenced script's source. (slower; works better)
         */
        public boolean fastRequire = true;

        public HTTP http = new HTTP();
        public Game game = new Game();

        public Dispatch dispatch = new Dispatch();
        public DebugMode debugMode = new DebugMode();
    }

    public static class Language {
        public Locale language = Locale.getDefault();
        public Locale fallback = Locale.US;
        public String document = "EN";
    }

    public static class Account {
        public boolean autoCreate = false;
        public boolean EXPERIMENTAL_RealPassword = false;
        public String[] defaultPermissions = {};
        public int maxPlayer = -1;
    }

    /* Server options. */

    public static class HTTP {
        /* This starts the HTTP server before the game server. */
        public boolean startImmediately = false;

        public String bindAddress = "0.0.0.0";
        public int bindPort = 8088;

        /* This is the address used in URLs. */
        public String accessAddress = "127.0.0.1";
        /* This is the port used in URLs. */
        public int accessPort = 0;

        public Encryption encryption = new Encryption();
        public Policies policies = new Policies();
        public Files files = new Files();
    }

    public static class Game {
        public String bindAddress = "0.0.0.0";
        public int bindPort = 22101;

        /* This is the address used in the default region. */
        public String accessAddress = "127.0.0.1";
        /* This is the port used in the default region. */
        public int accessPort = 0;

        /* Enabling this will generate a unique packet encryption key for each player. */
        public boolean useUniquePacketKey = true;

        public boolean useXorEncryption = true;

        /* Entities within a certain range will be loaded for the player */
        public int loadEntitiesForPlayerRange = 300;
        /* Start in 'unstable-quests', Lua scripts will be enabled by default. */
        public boolean enableScriptInBigWorld = true;
        public boolean enableConsole = true;

        /*
         * How often the world is ticked, in milliseconds.
         *
         * This is the resolution of everything the server drives itself: region triggers, challenge
         * and domain timers, respawns, script waits. At 1000 a domain timer can be a whole second
         * out and walking into a trigger volume takes up to a second to register. Lower is closer
         * to the real thing; the tick is cheap, but every scene with players in it runs on it.
         */
        public int tickRateMs = 200;

        /* Kcp internal work interval (milliseconds) */
        public int kcpInterval = 20;
        /* Controls whether packets should be logged in console or not */
        public ServerDebugMode logPackets = ServerDebugMode.NONE;
        /* Show packet payload in console or no (in any case the payload is shown in encrypted view) */
        public boolean isShowPacketPayload = false;
        /* Show annoying loop packets or no */
        public boolean isShowLoopPackets = false;

        public boolean cacheSceneEntitiesEveryRun = false;

        public GameOptions gameOptions = new GameOptions();
        public JoinOptions joinOptions = new JoinOptions();
        public ConsoleAccount serverAccount = new ConsoleAccount();

        public VisionOptions[] visionOptions = new VisionOptions[] {
            new VisionOptions("VISION_LEVEL_NORMAL"         , 80    , 20),
            new VisionOptions("VISION_LEVEL_LITTLE_REMOTE"  , 16    , 40),
            new VisionOptions("VISION_LEVEL_REMOTE"         , 1000  , 250),
            new VisionOptions("VISION_LEVEL_SUPER"          , 4000  , 1000),
            new VisionOptions("VISION_LEVEL_NEARBY"         , 40    , 20),
            new VisionOptions("VISION_LEVEL_SUPER_NEARBY"   , 20    , 20)
        };
    }

    /* Data containers. */

    public static class Dispatch {
        /* An array of servers. */
        public List<Region> regions = List.of();

        /* The URL used to make HTTP requests to the dispatch server. */
        public String dispatchUrl = "ws://127.0.0.1:1111";
        /* A unique key used for encryption. */
        public byte[] encryptionKey = Crypto.createSessionKey(32);
        /* A unique key used for authentication. */
        public String dispatchKey = Utils.base64Encode(
            Crypto.createSessionKey(32));

        public String defaultName = "Grasscutter";

        /* Controls whether http requests should be logged in console or not */
        public ServerDebugMode logRequests = ServerDebugMode.NONE;
    }

    /* Debug options container, used when jar launch argument is -debug | -debugall and override default values
     *  (see StartupArguments.enableDebug) */
    public static class DebugMode {
        /* Log level of the main server code (works only with -debug arg) */
        public Level serverLoggerLevel = Level.DEBUG;

        /* Log level of the third-party services (works only with -debug arg):
           javalin, quartz, reflections, jetty, mongodb.driver */
        public Level servicesLoggersLevel = Level.INFO;

        /* Controls whether packets should be logged in console or not */
        public ServerDebugMode logPackets = ServerDebugMode.ALL;

        /* Show packet payload in console or no (in any case the payload is shown in encrypted view) */
        public boolean isShowPacketPayload = false;

        /* Show annoying loop packets or no */
        public boolean isShowLoopPackets = false;

        /* Controls whether http requests should be logged in console or not */
        public ServerDebugMode logRequests = ServerDebugMode.ALL;
    }

    public static class Encryption {
        public boolean useEncryption = false;
        /* Should 'https' be appended to URLs? */
        public boolean useInRouting = false;
        public String keystore = "./keystore.p12";
        public String keystorePassword = "123456";
    }

    public static class Policies {
        public Policies.CORS cors = new Policies.CORS();

        public static class CORS {
            public boolean enabled = true;
            public String[] allowedOrigins = new String[]{"*"};
        }
    }

    public static class GameOptions {
        public InventoryLimits inventoryLimits = new InventoryLimits();
        public AvatarLimits avatarLimits = new AvatarLimits();
        public int sceneEntityLimit = 1000; // Unenforced. TODO: Implement.

        /**
         * Caps entity loading to 500m around the player, in both the group grid and the spawn
         * blocks.
         *
         * <p>Without it a coarse grid cell - some are 1000m wide - hands back everything it holds,
         * so walking into a new block can burst thousands of distant entities at the client in one
         * scene sync. That is the shape of the (1,1,2) disconnect. Turn it off to get the stock
         * behaviour back.
         */
        public boolean isPreventEntityError = true;

        public boolean watchGachaConfig = false;
        public boolean enableShopItems = false;
        public ArtifactShopOptions artifactShop = new ArtifactShopOptions();
        public boolean staminaUsage = true;
        public boolean energyUsage = true;
        public boolean fishhookTeleport = true;
        public boolean trialCostumes = false;

        /** Cutscene played once, the first time an account reaches a scene. 0 disables it. */
        public int firstLoginCutscene = 0;

        /**
         * Stops the server sending any cutscene at all - the login one and the ones scene scripts
         * ask for alike. The /cutscene command still works, since that is asked for explicitly.
         *
         * <p>The client also plays cutscenes off its own quest state, which no server setting can
         * reach. This only guarantees none of them came from here.
         */
        public boolean disableCutscenes = false;

        /**
         * Marks every main quest finished on the client at login, as {@code /quest forcefinish all}
         * does by hand.
         *
         * <p>The client decides on its own to replay the opening cutscene while it believes the
         * prologue is unplayed, and no cutscene setting reaches that - the only lever the server has
         * is telling it the quests are done. It also opens quest-gated region barriers, so this is
         * the sandbox answer rather than the faithful one.
         */
        public boolean forceFinishMainQuestsOnLogin = false;

        /**
         * Lists every official 5-star artifact piece in a shop. Buying one rolls it the way an
         * artifact domain would - a main stat out of the slot's real pool and substats out of the
         * excel affix table - only with the odds leaning towards crit and damage.
         */
        public static class ArtifactShopOptions {
            public boolean enabled = true;

            /**
             * Where the pieces are listed. 1004 is the general goods store - Blanche's Second Life,
             * in Mondstadt - which is where this server already keeps its custom goods. 1001 is
             * Paimon's Bargains, reachable from the shop menu without walking anywhere.
             */
            public int shopId = 1004;

            public int costMora = 20000;
            public int costPrimogems = 0;
            /** An item to charge on top of the currencies above, e.g. 220007 for Sanctifying Unction. */
            public int costItemId = 0;
            public int costItemCount = 0;
            /** How many of each piece one player may buy. 0 for unlimited. */
            public int buyLimit = 0;

            /** The upgrade level the piece arrives at, 0 to 20. 20 is a fully levelled artifact. */
            public int artifactLevel = 20;

            /** Weight multiplier for CRIT Rate and CRIT DMG. 1 rolls them as the game does. */
            public double critWeight = 8;
            /** Weight multiplier for ATK%, Elemental Mastery and the DMG bonuses. */
            public double damageWeight = 3;
            /**
             * How hard each stat leans towards the top of its four possible values. 0 picks between
             * them evenly, the way the game does.
             */
            public double highRollBias = 3;
        }

        public NewAccountIntro newAccountIntro = new NewAccountIntro();

        /**
         * Hands a brand new account to the client's own character creation - the twin stars, the
         * fight, and the choice of Traveler - instead of silently making one Lumine.
         *
         * <p>Off by default because two of the three packets in that handshake have no known 7.0
         * CmdId. They are both sent EMPTY, so only the numbers are missing: fill them in below and
         * the flow completes. At 0 they are not sent at all, which is still worth trying first -
         * the client may open creation on its own once the server stops pre-empting it.
         */
        public static class NewAccountIntro {
            public boolean enabled = false;
            public int doSetPlayerBornDataNotify = 0;
            public int setPlayerBornDataRsp = 0;

            /** Seconds to wait for creation before making the Traveler anyway. 0 waits forever. */
            public int fallbackSeconds = 15;
        }

        @SerializedName(value = "questing", alternate = "questOptions")
        public Questing questing = new Questing();
        public ResinOptions resinOptions = new ResinOptions();
        public Rates rates = new Rates();
        public TowerOptions tower = new TowerOptions();

        public HandbookOptions handbook = new HandbookOptions();
        public BirthdayMailOptions birthdayMail = new BirthdayMailOptions();
        public WatermarkOptions watermark = new WatermarkOptions();

        public static class InventoryLimits {
            public int weapons = 2000;
            public int relics = 2000;
            public int materials = 2000;
            public int furniture = 2000;
            public int all = 30000;
        }

        public static class AvatarLimits {
            public int singlePlayerTeam = 4;
            public int multiplayerTeam = 4;
        }

        public static class Rates {
            public float adventureExp = 1.0f;
            public float mora = 1.0f;
            public float leyLines = 1.0f;
        }

        /** Spiral Abyss. */
        public static class TowerOptions {
            /**
             * Which rotation to serve, or 0 to rotate on the 1st and the 16th like the game does.
             *
             * <p>Only rotations this server can actually build are ever chosen: a rotation whose
             * floors point at dungeon scenes with no group scripts would open onto empty rooms, so
             * those are skipped. Setting an id here serves it whether or not it passes that check.
             */
            public int scheduleId = 0;

            /**
             * Cycle through rotations on the 1st and the 16th instead of always serving the newest.
             *
             * <p>Off by default: the newest rotation this server can build is the closest it gets to
             * the live game, and rotating only ever moves backwards from it.
             */
            public boolean rotate = false;

            /** How many of the newest playable rotations to cycle through. 0 uses every one. */
            public int rotationPool = 12;

            /** Hand floors 1-8 over already cleared. Off plays the corridor for real. */
            public boolean skipEntranceFloors = true;
        }

        public static class ResinOptions {
            public boolean resinUsage = false;
            /* Increased to 200 in version 4.8 */
            public int cap = 200;
            public int rechargeTime = 480;
        }

        public static class Questing {
            /* Should questing behavior be used? */
            public boolean enabled = false;
        }

        public static class WatermarkOptions {
            /* Replace the client's beta watermark text for everyone on this server. */
            public boolean enabled = true;
            /* Text to show. Capped at 254 bytes by the payload format; longer values are ignored. */
            public String text = "CapyGC";
            /* Colour as hex (#RRGGBB or #RGB). Leave blank to keep the client's default white. */
            public String color = "#FFFFFF";
            /* Set to fade from "color" to this one across the text. Blank means a flat colour.
             * A gradient costs ~24 bytes per character, so it fits roughly 10 characters. */
            public String gradientTo = "#6032a8";
            /* CmdId to send the wind seed notify under. 0 uses PacketOpcodes.WindSeedClientNotify,
             * which the 7.0 dump gives as 226. Set to -1 to send nothing at all.
             *
             * If the client ever starts crashing a second or two after login, set this to -1 first:
             * this is the one packet whose payload the client EXECUTES as Lua, so a payload it does
             * not accept takes the game down instead of being ignored. It did exactly that once,
             * when area_notify was sent with its two uint32s left at zero. */
            public int cmdId = 0;
            /* Protobuf field number the Lua payload is written to, flat at the top level.
             * 0 uses the built-in default (6, which is `payload` on 7.0's message). */
            public int payloadField = 0;
            /* Try several candidates in one login instead of one per restart. Each entry is
             * "cmdId:payloadField"; the watermark is sent once under each. The client ignores a
             * CmdId it does not know, so the wrong ones are inert - if the text appears, bisect
             * this list to find which one landed. Empty means just use cmdId/payloadField above.
             * Never put 8191 or 9250 in here: those are PlayerLoginRsp and GetPlayerTokenRsp, and
             * a Lua payload sent under them breaks login rather than the watermark. */
            public String[] sweep = {};
        }

        public static class BirthdayMailOptions {
            /* Should players receive a mail with a gift on their in-game birthday? */
            public boolean enabled = true;
            /* How many days the birthday mail stays claimable before it expires. */
            public int expireDays = 7;
            /* The items attached to the birthday mail. Defaults to Mora and Primogems. */
            public GiftItem[] gifts =
                    new GiftItem[] {
                        new GiftItem(202, 10000000), // Mora
                        new GiftItem(201, 600000) // Primogem
                    };

            public static class GiftItem {
                public int itemId;
                public int count;

                public GiftItem() {
                    this(202, 1);
                }

                public GiftItem(int itemId, int count) {
                    this.itemId = itemId;
                    this.count = count;
                }
            }
        }

        public static class HandbookOptions {
            public boolean enable = false;
            public boolean allowCommands = true;

            public Limits limits = new Limits();
            public Server server = new Server();

            public static class Limits {
                /* Are rate limits checked? */
                public boolean enabled = false;
                /* The time for limits to expire. */
                public int interval = 3;

                /* The maximum amount of normal requests. */
                public int maxRequests = 10;
                /* The maximum amount of entities to be spawned in one request. */
                public int maxEntities = 25;
            }

            public static class Server {
                /* Are the server settings sent to the handbook? */
                public boolean enforced = false;
                /* The default server address for the handbook's authentication. */
                public String address = "127.0.0.1";
                /* The default server port for the handbook's authentication. */
                public int port = 443;
                /* Should the defaults be enforced? */
                public boolean canChange = true;
            }
        }
    }

    public static class VisionOptions {
        public String name;
        public int visionRange;
        public int gridWidth;

        public VisionOptions(String name, int visionRange, int gridWidth) {
            this.name = name;
            this.visionRange = visionRange;
            this.gridWidth = gridWidth;
        }
    }

    public static class JoinOptions {
        public int[] welcomeEmotes = {2007, 1002, 4010};
        public String welcomeMessage = "Welcome to LunaGC 6.6.0";
        public JoinOptions.Mail welcomeMail = new JoinOptions.Mail();

        public static class Mail {
            public String title = "Welcome to LunaGC 6.6.0";
            public String content = """
                    Hi there!\r\nWelcome to LunaGC!
                    """;
            public String sender = "Kei-Luna and pmagixc";
            public emu.grasscutter.game.mail.Mail.MailItem[] items = {
            };
        }
    }

    public static class ConsoleAccount {
        public int avatarId = 10000007;
        public int nameCardId = 210001;
        public int adventureRank = 1;
        public int worldLevel = 0;

        public String nickName = "LunaGC";
        public String signature = "Welcome to LunaGC";
    }

    public static class Files {
        public String indexFile = "./index.html";
        public String errorFile = "./404.html";
    }

    /* Objects. */

    @NoArgsConstructor
    public static class Region {
        public String Name = "os_usa";
        public String Title = "Grasscutter";
        public String Ip = "127.0.0.1";
        public int Port = 22102;

        public Region(
            String name, String title,
            String address, int port
        ) {
            this.Name = name;
            this.Title = title;
            this.Ip = address;
            this.Port  = port;
        }
    }
}
