package emu.grasscutter.command.commands;

import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.data.GameData;
import emu.grasscutter.game.player.Player;
import java.util.List;
import java.util.Locale;

@Command(
        label = "dailytask",
        aliases = {"dt"},
        usage = {
			"list",
			"load",
			"reset",
			"city <random|cityId|region>",
			"finish <taskId>",
			"support",
			"bonus"
		},
        permission = "player.dailytask",
        permissionTargeted = "player.dailytask.others",
        targetRequirement = Command.TargetRequirement.ONLINE)
public final class DailyTaskCommand implements CommandHandler {
    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {
        if (args.isEmpty()) {
            this.sendUsageMessage(sender);
            return;
        }

        targetPlayer.loadDailyTaskManager();

        var manager = targetPlayer.getDailyTaskManager();

        String action = args.get(0).toLowerCase();

        switch (action) {
            case "list" -> {
				CommandHandler.sendMessage(
						sender,
						"Daily commissions: date=%08d, filter=%s (%d), activeRegion=%s (%d), finished=%d/4, scoreReward=%d, bonusTaken=%s"
								.formatted(
										manager.getLastGenerationDate(),
										getCityName(manager.getCityId()),
										manager.getCityId(),
										getCityName(manager.getActiveCityId()),
										manager.getActiveCityId(),
										manager.getFinishedCount(),
										manager.getScoreRewardId(),
										manager.isScoreRewardTaken()));

                if (manager.getDailyTasks().isEmpty()) {
                    CommandHandler.sendMessage(
                            sender,
                            "No daily commissions are currently active.");
                    return;
                }

                for (var task : manager.getDailyTasks()) {
                    var data =
                            GameData.getDailyTaskDataMap()
                                    .get(task.getTaskId());

                    String groups =
                            data == null
                                            || data.getNewGroupVec() == null
                                    ? "[]"
                                    : data.getNewGroupVec().toString();

                    CommandHandler.sendMessage(
                            sender,
                            "Task %d: progress=%d/%d, finished=%s, reward=%d, groups=%s"
                                    .formatted(
                                            task.getTaskId(),
                                            task.getProgress(),
                                            task.getFinishProgress(),
                                            task.isFinished(),
                                            task.getRewardId(),
                                            groups));
                }
            }
			
			case "load" -> {
				if (targetPlayer.getScene() == null) {
					CommandHandler.sendMessage(
							sender,
							"The target player has no active scene.");
					return;
				}

				int ready =
						manager.loadActiveGroups(
								targetPlayer.getScene());

				CommandHandler.sendMessage(
						sender,
						"Ready daily commission groups: %d/%d in scene %d."
								.formatted(
										ready,
										manager.getActiveGroupIds().size(),
										targetPlayer.getSceneId()));
			}

            case "reset" -> {
                int count = manager.resetDailyTasks();

                CommandHandler.sendMessage(
                        sender,
                        "Generated %d daily commissions for city %d."
                                .formatted(
                                        count,
                                        manager.getCityId()));
            }

			case "city" -> {
				if (args.size() < 2) {
					this.sendUsageMessage(sender);
					return;
				}

				Integer cityId =
						parseCityId(args.get(1));

				if (cityId == null) {
					CommandHandler.sendMessage(
							sender,
							"Invalid region. Use random, Mondstadt, Liyue, Inazuma, Sumeru, Fontaine, Natlan, Nod-Krai, or a city ID.");
					return;
				}

				if (!manager.setCityIdAndReset(cityId)) {
					CommandHandler.sendMessage(
							sender,
							"Region %s (%d) does not contain at least four supported combat commissions."
									.formatted(
											getCityName(cityId),
											cityId));

					CommandHandler.sendMessage(
							sender,
							"Supported city IDs: "
									+ manager.getSupportedCityIds());

					return;
				}

				CommandHandler.sendMessage(
						sender,
						"Daily commission filter: %s (%d). Today's active region: %s (%d)."
								.formatted(
										getCityName(manager.getCityId()),
										manager.getCityId(),
										getCityName(manager.getActiveCityId()),
										manager.getActiveCityId()));
			}

            case "finish" -> {
                if (args.size() < 2) {
                    this.sendUsageMessage(sender);
                    return;
                }

                int taskId;

                try {
                    taskId = Integer.parseInt(args.get(1));
                } catch (NumberFormatException ignored) {
                    CommandHandler.sendMessage(
                            sender,
                            "Invalid daily task ID.");
                    return;
                }

                if (!manager.finishDailyTask(taskId)) {
                    CommandHandler.sendMessage(
                            sender,
                            "Daily task %d was not active or was already finished."
                                    .formatted(taskId));
                    return;
                }

                CommandHandler.sendMessage(
                        sender,
                        "Daily task %d completed."
                                .formatted(taskId));
            }

			case "support" -> {
				var cityIds =
						GameData.getDailyTaskDataMap()
								.values()
								.stream()
								.map(data -> data.getCityId())
								.distinct()
								.sorted()
								.toList();

				CommandHandler.sendMessage(
						sender,
						"Daily commission Lua resource coverage:");

				for (int cityId : cityIds) {
					long defined =
							manager.getDefinedCombatTaskCount(cityId);

					long resourceBacked =
							manager.getResourceBackedTaskCount(cityId);

					CommandHandler.sendMessage(
							sender,
							"%s (%d): %d/%d combat commissions have usable encounter resources."
									.formatted(
											getCityName(cityId),
											cityId,
											resourceBacked,
											defined));
				}
			}

            case "bonus" -> {
                if (!manager.claimScoreReward()) {
                    CommandHandler.sendMessage(
                            sender,
                            "The four-commission bonus cannot be claimed yet, was already claimed, or its reward data was unavailable.");
                    return;
                }

                CommandHandler.sendMessage(
                        sender,
                        "Daily commission completion bonus claimed.");
            }

            default -> this.sendUsageMessage(sender);
        }
    }

	private static String getCityName(int cityId) {
		return switch (cityId) {
			case 0 -> "Random";
			case 1 -> "Mondstadt";
			case 2 -> "Liyue";
			case 3 -> "Inazuma";
			case 4 -> "Sumeru";
			case 5 -> "Fontaine";
			case 6 -> "Natlan";
			case 7 -> "Nod-Krai";
			default -> "City " + cityId;
		};
	}

	private static Integer parseCityId(String value) {
		String normalized =
				value.toLowerCase(Locale.ROOT);

		return switch (normalized) {
			case "random" -> 0;
			case "mondstadt" -> 1;
			case "liyue" -> 2;
			case "inazuma" -> 3;
			case "sumeru" -> 4;
			case "fontaine" -> 5;
			case "natlan" -> 6;

			case "nodkrai",
					"nod-krai",
					"nod_krai" -> 7;

			default -> {
				try {
					yield Integer.parseInt(value);
				} catch (NumberFormatException ignored) {
					yield null;
				}
			}
		};
	}
}